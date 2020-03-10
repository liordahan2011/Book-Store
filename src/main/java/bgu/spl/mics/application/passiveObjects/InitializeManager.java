package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Passive data-object that manages the initialization of the book store.
 * this is a singelton that counts how many services has initialized so that the time service will be the last.
 *
 */
public class InitializeManager {
    private AtomicInteger counter;

    /**
     * creates this singelton in a thread safe manner
     */
    private static class InitializeManagerHolder {
        private static InitializeManager instance = new InitializeManager();
    }

    /**
     * constructor
     */
    private InitializeManager(){counter = new AtomicInteger(0);}

    /**
     * Retrieves the single instance of this class
     *
     <p>
     *
     * @return reference to initialize manager
     */
    public static InitializeManager getInstance() {
        return InitializeManagerHolder.instance;
    }

    /**
     * increase the counter in 1
     * done when each microservise initializes
     */
    public void increase(){counter.incrementAndGet();}

    /**
     * Retrieves the counter of microservices that initialized already
     *
     <p>
     *
     * @return counter
     */
    public int getCounter(){return counter.get();}

}
