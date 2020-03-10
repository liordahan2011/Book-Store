package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {
	private BookInventoryInfo[] books;

	/**
	 * constructor
	 */
	private Inventory(){ }

	/**
     * creates this singelton in a thread safe manner
     */
	private static class InventoryHolder{
		private static Inventory instance = new Inventory();
	}

	/**
	 * Retrieves the single instance of this class.
	 *
	 <p>
	 *
	 * @return refernce to the inventory
	 */
	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[] inventory ) {
		this.books = inventory;
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		for (int i=0; i<books.length; i++){
			if (books[i].getBookTitle().equals(book)){
				int amount = books[i].getAmountInInventory();
				if (amount > 0) {
					books[i].setAmountInInventory(amount - 1);
					return OrderResult.SUCCESSFULLY_TAKEN;
				}
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		for (int i=0; i<books.length; i++) {
			if (books[i].getBookTitle().equals(book))
				if (books[i].getAmountInInventory() > 0)
					return books[i].getPrice();
		}
		return -1;
	}
	
	/**
     *
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory.
	 * The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) {
		HashMap<String, Integer> hashInventory = new HashMap<>();
		for (int i = 0; i < books.length; i++) {
			int amount = books[i].getAmountInInventory();
			hashInventory.put(books[i].getBookTitle(), amount);
		}
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(hashInventory);
			oos.close();
			fos.close();
		} catch (IOException ioe) { }
	}

	/**
	 * Retrieves array of books that exist in the Inventory
	 *
	 <p>
	 *
	 * @return {@link BookInventoryInfo[]} array of books that exist in the Inventory
	 */
	public BookInventoryInfo[] getBooks(){
		return books;
	}
}
