package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 * Event (Message object) representing a delivery of book from the store to the address of the customer.
 */
public class DeliveryEvent implements Event {
    private String address;
    private int distance;

    /**
     * Constructor
     *
     * @param address the address of the customer that ordered the book
     * @param distance the distance from the customers house
     *
     */
    public DeliveryEvent( String address, int distance){
        this.address = address;
        this.distance = distance;
    }

    /**
     * Retrieves the address of the customer
     <p>
     * @return String address
     */
    public String getAddress(){
        return address;
    }

    /**
     ** Retrieves the distance of the customer
     <p>
     * @return int distance
     */
    public int getDistance(){
        return distance;
    }
}
