package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
	private int orderId;
	private String seller;
	private int customer;
	private String bookTitle;
	private int price;
	private int issuedTick;
	private int orderTick;
	private int processTick;

	/**
	 * constructor of the receipt
	 * @param orderId the ID number of the order
	 * @param seller the name of the seller
	 * @param customer the customer that made the order
	 * @param bookTitle the name of the book
	 * @param price the price of the book
	 * @param orderTick the tick in which the customer purchased the book
	 * @param processTick the tick in which treating the order started
	 * @param issuedTick the tick in which the receipt was issued
	 */
	public OrderReceipt(int orderId, String seller, int customer, String bookTitle, int price, int orderTick, int processTick, int issuedTick){
		this.orderId = orderId;
		this.seller = seller;
		this.customer = customer;
		this.bookTitle = bookTitle;
		this.price = price;
		this.orderTick = orderTick;
		this.processTick = processTick;
		this.issuedTick = issuedTick;
	}

	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return orderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return customer;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     *
	 <p>
	 *
	 * @return int the tick in which the receipt was issued.
	 */
	public int getIssuedTick() {
		return issuedTick;
	}

	/**
	 * sets the tick in which this receipt was issued.
	 */
	public void setIssuedTick(int tick) {
		this.issuedTick = tick;
	}

	/**
	 * Retrieves the tick in which the customer sent the purchase request
	 *
	 <p>
	 *
	 * @return int tick in which the customer sent the purchase request
	 */
	public int getOrderTick() {
		return orderTick;
	}

	/**
	 * Retrieves the tick in which the treating selling service started
	 * processing the order.
	 *
	 <p>
	 *
     * @return int tick in which the selling service started
     */
	public int getProcessTick() {
		return processTick;
	}

	/**
	 * sets the tick in which this receipt was issued.
	 */
	public void setProcessTick(int tick) {
		this.processTick = tick;
	}
}
