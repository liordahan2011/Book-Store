package bgu.spl.mics.application.passiveObjects;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import static org.junit.Assert.*;


public class InventoryTest {

    @Before
    public void setUp()throws SecurityException, NoSuchFieldException, IllegalArgumentException,IllegalAccessException{
        Field instance = Inventory.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null,null);
    }

    @Test
    public void getInstance() {
        Inventory instance = Inventory.getInstance();
        instance.getInstance();
        assertNotNull(instance.getInstance());
        assertEquals(instance.getInstance(),instance);
    }

    @Test
    public void load() {
        Inventory instance = Inventory.getInstance();
        BookInventoryInfo[] books = {new BookInventoryInfo("book1", 1, 100)
                                    ,new BookInventoryInfo("book2", 1, 200)};
        instance.load(books);
        assertEquals(instance.take("book1"), OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(instance.take("book2"), OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(instance.take("book1"),instance.take("book2"));
    }

    @Test
    public void take() {
        Inventory instance = Inventory.getInstance();
        BookInventoryInfo[] books = {new BookInventoryInfo("book1", 1, 100)
                ,new BookInventoryInfo("book2", 1, 200)};
        instance.load(books);
        assertEquals(instance.take("book1"), OrderResult.SUCCESSFULLY_TAKEN);
        assertNotEquals(instance.take("book1"), OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(instance.take("book1"), OrderResult.NOT_IN_STOCK);
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        Inventory instance = Inventory.getInstance();
        BookInventoryInfo[] books = {new BookInventoryInfo("book1",1, 100)};
        instance.load(books);
        assertEquals(instance.checkAvailabiltyAndGetPrice("book1"),100);
        instance.take("book1");
        assertEquals(instance.checkAvailabiltyAndGetPrice("book1"),-1);
    }

    @Test
    public void printInventoryToFile() {

    }
}