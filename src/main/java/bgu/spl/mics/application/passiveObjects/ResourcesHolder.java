package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private LinkedBlockingQueue<DeliveryVehicle> vehicles;
	private LinkedBlockingQueue<Future<DeliveryVehicle>> futures ;

	/**
	 * Constructor
	 */
	private ResourcesHolder(){
		vehicles = new LinkedBlockingQueue<>();
		futures= new LinkedBlockingQueue<>() ;

	}
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return ResourcesHolderH.instance;
	}

	/**
	 * creates this resource holder singelton in a thread safe manner
	 */
	private static class ResourcesHolderH{
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> future = new Future();
		synchronized (vehicles) {	//that no one won't put future while its empty
			if (vehicles.isEmpty()) {
				try {
					futures.put(future);
				} catch (InterruptedException e) {
				}
			} else {
				future.resolve(vehicles.poll());
			}
		}
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (vehicles) {
			if (futures.isEmpty()) {
				try {
					vehicles.put(vehicle);
				} catch (InterruptedException e) {
				}
			} else {
				futures.poll().resolve(vehicle);
			}
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for( DeliveryVehicle vehicle: vehicles) {
			try {
				this.vehicles.put(vehicle);
			} catch (InterruptedException e) {
			}
		}
	}

}
