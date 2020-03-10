package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.Pair;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
	Customer customer;
	private AtomicReferenceArray<Pair> orderSchedule;

	/**
	 *constructor
	 *
	 <p>
	 *
	 * @param name the name of this API
	 * @param customer the customer that this API connects to the store
	 * @param orderSchedule the orders od the customer
	 */
	public APIService(String name, Customer customer, AtomicReferenceArray orderSchedule) {
		super("API");
		this.customer = customer;
		this.orderSchedule = orderSchedule;
	}

	/**
	 * manages the orders of the customer due to it's order schedule
	 *
	 <p>
	 * sends selling event to the selling service and waits for a receipt
	 * for each order- gets the receipt and adds it to the total receipt of the customer
	 <p>
	 * if the duration ends terminates gracefully
	 */
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, (broadcast) -> {
			LinkedBlockingQueue<Future<OrderReceipt>> futures = new LinkedBlockingQueue<>();
			if (broadcast.getTick() != broadcast.getDuration()) {
				for (int i = 0; i < orderSchedule.length(); i++) {
					if (orderSchedule.get(i).getSecond() == broadcast.getTick()) {
						Future<OrderReceipt> future = sendEvent(new BookOrderEvent(orderSchedule.get(i).getFirst(), customer, broadcast.getTick()));
						if (future != null) {
							try {
								futures.put(future);
							} catch (InterruptedException e) {
							}
						}
					}
				}
				while (!futures.isEmpty()) {
					OrderReceipt f = futures.poll().get();
					if (f != null)
						customer.getCustomerReceiptList().add(f);
				}
			} else {
				terminate();
			}
		});
	}
}
