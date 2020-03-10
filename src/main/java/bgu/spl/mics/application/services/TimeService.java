package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	int speed;
	int duration;
	AtomicInteger globalTick;
	private Timer time;

	/**
	 * constructor
	 <p>
	 * the global tick starts from 1
	 * initializing {@link Timer} timer of java
	 * <p>
	 * @param speed speed of the time ticks
	 * @param duration the duration that the book store exists
	 */
	public TimeService(int speed, int duration) {
		super("Timer");
		this.speed = speed;
		this.duration = duration;
		this.globalTick = new AtomicInteger(1);
		time = new Timer();
	}

	/**
	 * sending broadcast of the time ticks to the interested microservices
	 <p>
	 * if the global tick equals duration- stops the ticks and terminates gracefully
	 */
	protected void initialize() {
		sendBroadcast(new TickBroadcast(globalTick.get(),duration));
		time.schedule(new TimerTask() {
			public void run() {
				globalTick.incrementAndGet();
				sendBroadcast(new TickBroadcast(globalTick.get(), duration));
				if (globalTick.get() == duration){
					time.cancel();
			}
			}},0,speed);
		terminate();
	}

}
