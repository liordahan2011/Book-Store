package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.checkBookEvent;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private int globalTick;

	/**
	 *
	 * @param name of this selling microservice
	 */
	public SellingService(String name) {
		super(name);
	}

	/**
	 * retrives the global tick of the store
	 *
	 <p>
	 *
	 * @return int global tick of the store
	 */
	public int getGlobalTick() {
		return globalTick;
	}

	/**
	 * takes care about the selling process of the book
	 *
	 <p>
	 *
	 * if there is no available book | no such book at all | the customer does not have enough money to purchase - resolve with null
	 * else - order the book, charge the customer, and deliver the book to the customer, at the end create the receipt and resolve
	 <p>
	 *
	 * if the duration ends- terminate gracefully
	 *
	 */
	protected void initialize() {
		subscribeEvent(BookOrderEvent.class, event-> {
			synchronized (event.getCustomer()) {
				Future<Integer> bookPrice = sendEvent(new checkBookEvent(event.getBookTitle(),event.getCustomer().getAvailableCreditAmount()));
				if (bookPrice== null || bookPrice.get()==null|| bookPrice.get() == -1 )
					complete(event, null);
				else {
					MoneyRegister.getInstance().chargeCreditCard(event.getCustomer(), bookPrice.get());
					sendEvent(new DeliveryEvent(event.getCustomer().getAddress(), event.getCustomer().getDistance()));
					OrderReceipt r = new OrderReceipt(0, getName(), event.getCustomer().getId(), event.getBookTitle(),
							bookPrice.get(), event.getOrderTick(), getGlobalTick(), globalTick);
					MoneyRegister.getInstance().file(r);
					complete(event, r);
				}
			}});
			subscribeBroadcast(TickBroadcast.class,broadcast-> {
				if (broadcast.getTick() != broadcast.getDuration())
					globalTick = broadcast.getTick();
				else {
					terminate();
				}
			});
	}

}
