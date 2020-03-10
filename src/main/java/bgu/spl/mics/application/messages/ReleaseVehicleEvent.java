package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Event (Message object) that should release the vehicle that finished the delivery.
 */
public class ReleaseVehicleEvent implements Event {
    private DeliveryVehicle vehicle;

    /**
     * Constructor
     *
     * @param vehicle of type DeliveryVehicle that had delivered the book and needs to be released
     *
     * */
    public ReleaseVehicleEvent(DeliveryVehicle vehicle){
        this.vehicle = vehicle;
    }

    /**
     * Retrieves the vehicle
     *
     <p>

     * @return {@link DeliveryVehicle} this vehicle
     */
    public DeliveryVehicle getVehicle() {
        return vehicle;
    }
}
