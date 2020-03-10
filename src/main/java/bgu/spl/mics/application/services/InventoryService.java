package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.checkBookEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	/**
	 *constructor
	 *
	 *@param name of this inventory microservice
	 *
	 */
	public InventoryService(String name) {
		super(name);
	}

	/**
	 * needs to solve check book events
	 *
	 <p>
	 *
	 * check if the book available in the inventory and get it's price
	 * check if the customer has enough money to buy the book
	 * if customer has enough money and the book available : order the book
	 * else : return -1
	 <p>
	 * if the duration ends- terminate gracefully
	 */
	protected void initialize() {
		subscribeEvent(checkBookEvent.class, event-> {
					int price = Inventory.getInstance().checkAvailabiltyAndGetPrice(event.getBookTitle());
					if (price != -1 && event.getCreditAmount() >= price &&
							OrderResult.SUCCESSFULLY_TAKEN==Inventory.getInstance().take(event.getBookTitle())) {
						complete(event, price);
					}else{
						complete(event,-1);
					}
				});

		subscribeBroadcast(TickBroadcast.class ,(broadcast)->{
			if(broadcast.getDuration() == broadcast.getTick()){
				terminate();
			}
		});
	}

}
