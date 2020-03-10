package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
/**
 * Event (Message object) that is sent from selling service before customer buys the book
 * with details about the book and amount of money in the customers credit card
 */
public class checkBookEvent implements Event {
    private String bookTitle;
    private int creditAmount;

    /**
     * Constructor
     *
     * @param bookTitle the name of the book we are checking
     * @param creditAmount the amount in the credit card of the customer
     *
     */
    public checkBookEvent(String bookTitle, int creditAmount){
        this.bookTitle = bookTitle;
        this.creditAmount = creditAmount;
    }

    /**
     * Retrieves the book name.
     <p>
     * @return String book name
     */
    public String getBookTitle(){
        return bookTitle;
    }

    /**
     * Retrieves the amount the customer has in credit.
     <p>
     * @return int amount the customer has in credit
     */
    public int getCreditAmount(){
        return creditAmount;
    }

}
