package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	LinkedBlockingQueue<Future<DeliveryVehicle>> futures;

	/**
	 * constructor
	 *
	 * @param name of this inventory microservice
	 */
	public ResourceService(String name) {
		super(name);
		futures = new LinkedBlockingQueue<>();
	}

	/**
	 *sends the available vehicle to its delivery and saves a reference to it's future result
	 *
	 <p>
	 *
	 * gets the information about vehicles that done the delivery
	 *
	 <p>
	 *
	 * if the duration ends- terminates gracefully and resolves all the futures that are still in process / waiting
	 */
	protected void initialize() {
		subscribeEvent(AcquireVehicleEvent.class, (event)->{
			Future<DeliveryVehicle> future =ResourcesHolder.getInstance().acquireVehicle();
			try{
				futures.put(future);
			}catch (InterruptedException e){}
			complete(event,future);
		});

		subscribeEvent(ReleaseVehicleEvent.class, event-> ResourcesHolder.getInstance().releaseVehicle(event.getVehicle()));

		subscribeBroadcast(TickBroadcast.class ,(broadcast)->{
			if(broadcast.getDuration() == broadcast.getTick()){
				terminate();
				while (!futures.isEmpty()){
					Future<DeliveryVehicle> future = futures.poll();
					future.resolve(null);
				}
			}
		});
	}

}
