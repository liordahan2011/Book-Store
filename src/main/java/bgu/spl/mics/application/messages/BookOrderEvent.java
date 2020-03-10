package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * Event (Message object) representing an order of book at the store.
 */
public class BookOrderEvent implements Event {
    String bookTitle;
    Customer customer;
    int orderTick;

    /**
     * Constructor
     *
     * @param bookTitle represents the title of the book that should be ordered
     * @param customer represents the customer that ordered the book
     * @param orderTick represents the tick in which the book was ordered
     *
     */
    public BookOrderEvent (String bookTitle, Customer customer, int orderTick){
        this.bookTitle = bookTitle;
        this.customer = customer;
        this.orderTick= orderTick;
    }

    /**
     * Retrieves the customer that ordered the book.
     <p>
     * @return {@link Customer} customer that ordered the book
     */
    public Customer getCustomer(){
        return customer;
    }

    /**
     * Retrieves the book title.
     <p>
     * @return String book title
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Retrieves the order tick.
     <p>
     * @return int order tick
     */
    public int getOrderTick() {
        return orderTick;
    }


}
