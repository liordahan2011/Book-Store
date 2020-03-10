package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	/**
	 * constructor
	 *
	 * @param name of this logistic microservice
	 */
	public LogisticsService(String name) {
		super(name);
	}

	/**
	 * manages the delivery of the ordered book to the customer that made the order
	 *
	 <p>
	 *
	 * acquires vehicle from the Resource holder
	 * waits till there is an available vehicle
	 * delivers the book to the customer and releases the vehicle when it arrived
	 * if there is no car - do nothing
	 *
	 <p>
	 * when duration ends- terminate gracefully
	 */
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, event ->{
			Future<Future<DeliveryVehicle>> future = sendEvent(new AcquireVehicleEvent());
			if (future != null){
				Future<DeliveryVehicle> vehicle = future.get();
				if (vehicle!=null && vehicle.get()!=null){
					vehicle.get().deliver(event.getAddress(), event.getDistance());
					sendEvent(new ReleaseVehicleEvent(vehicle.get()));
				}
			}
		});

		subscribeBroadcast(TickBroadcast.class ,(broadcast)->{
			if(broadcast.getDuration() == broadcast.getTick()){
				terminate();
			}
		});
	}

}
