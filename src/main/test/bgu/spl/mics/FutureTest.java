package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future<Integer> future;

    @Before
    public  void setUp(){
        future = new Future();
    }

    @Test
    public void get() {
        assertFalse(future.isDone());
        assertNull(future.get());
        future.resolve(1);
        assertTrue(future.isDone());
        assertEquals(future.get(),(Integer)1);
    }

    @Test
    public void resolve() {
        assertFalse(future.isDone());
        future.resolve(1);
        assertTrue(future.isDone());
        assertTrue(future.get()==1);
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        future.resolve(1);
        assertTrue(future.isDone());
    }

    // does get returns the correct value while future is resolved meanwhile waiting or not resolved when time ends
    @Test
    public void get1() {
        assertFalse(future.isDone());
        Integer A = future.get(1000, TimeUnit.MILLISECONDS);
        if(future.isDone())
            assertEquals(future.get(), A);
    }

    // does get returns the correct value before future resolved
    @Test
    public void get2(){
        assertFalse(future.isDone());
        future.resolve(1);
        assertTrue(future.isDone());
        Integer A = future.get(10, TimeUnit.MILLISECONDS);
        assertEquals(A, (Integer)1);
    }
}