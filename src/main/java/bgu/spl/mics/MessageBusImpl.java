package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> messages;
	ConcurrentHashMap<Class<? extends Message>, LinkedBlockingQueue<MicroService>> subscribes;
	ConcurrentHashMap<Class<? extends MicroService>,LinkedBlockingQueue<MicroService>> roundRobin;
	ConcurrentHashMap<Event,Future> futures;
	Object lockSubEv;
	Object lockSubBro;
	Object lockSendEv;
	Object lockRegister;

	/**
	 * creates singelton of the message bus holder in a thread safe manner
	 */
	private static class MessageBusHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	/**
	 * Constructor
	 *
	 * messages- describes the messages each microsrevice takes care of
	 * subsribes- describes the microservices that did subscribe to some broadcast
	 * round robin- describes the order of the microservices loop (how is next to take care of a message)
	 */
	private MessageBusImpl(){
		messages = new ConcurrentHashMap<>();
		subscribes = new ConcurrentHashMap<>();
		roundRobin = new ConcurrentHashMap<>();
		futures = new ConcurrentHashMap<>();
		lockSubBro = new Object();
		lockSubEv = new Object();
		lockSendEv = new Object();
		lockRegister = new Object();
	}

	/**
	 * retrieves the single instance of this class
	 * @return {@link MessageBusImpl} single instance of this class
	 */
	public static MessageBusImpl getInstance(){
		return MessageBusHolder.instance;
	}

	/**
	 *atdd microservice 'm' to the messages blocking queue of the given class type
	 * if the queue does not exist- create it
	 * 
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 * @param <T>
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (lockSubEv) {
			if (subscribes.get(type.getClasses()) == null) {
				subscribes.put(type, new LinkedBlockingQueue<>());
			}
		}
			try {
				subscribes.get(type).put(m);
			} catch (InterruptedException e) {
			}
	}

	/**
	 * add the microservice 'm' to the subscribes blocking queue of the given class type
	 * if the queue does not exist yet- create it
	 *
	 * @param type 	The type of class to subscribe to.
	 * @param m    	The subscribing micro-service.
	 */
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (lockSubBro) {
			if (subscribes.get(type) == null) {
				subscribes.put(type, new LinkedBlockingQueue<>());
			}
		}
			try {
				subscribes.get(type).put(m);
			} catch (InterruptedException e) {
			}
	}

	/**
	 * resolves the future of event e with the given result
	 *
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 */
	public <T> void complete(Event<T> e, T result) {
		futures.get(e).resolve(result);
	}

	/**
	 *sends the broadcast b to all of the microservices that were insterested
	 *
 	 * @param b 	The broadcast we need to send
	 */
	public void sendBroadcast(Broadcast b) {
		if (subscribes.get(b.getClass()) != null) {
			for (MicroService m : subscribes.get(b.getClass())) {
				try {
					messages.get(m).put(b);
				} catch (InterruptedException e) { }
			}
		}
	}


	/**
	 * look for the type of microservice that can take care of due to subscribes
	 * check who is the next microservice needs to take care of event due to round robin and sent it to him
	 * add the future represents the solution of this event to futures map
	 *
	 * @param e         	The event we need to send
	 * @param <T>
	 * @return <T>Future    that represents the solution of the event
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		MicroService runner;
		synchronized (lockSendEv) {
			if (subscribes.get(e.getClass()) == null || subscribes.get(e.getClass()).isEmpty() && subscribes.get(e.getClass()).peek() == null)
				return null;
			Class serv = subscribes.get(e.getClass()).peek().getClass();
			runner = roundRobin.get(serv).poll();
			try {
				roundRobin.get(serv).put(runner);
			} catch (InterruptedException i) { }
		}
		futures.put(e, new Future<T>());
		try {
			messages.get(runner).put(e);
		} catch (InterruptedException e1) { }
		return futures.get(e);
	}

	/**
	 * add the microservice m to the messages map, and to the round robin
	 * if the round robin does not exist, create it
	 *
	 * @param m the micro-service needs to be registered.
	 */
	public void register(MicroService m) {
		messages.put(m, new LinkedBlockingQueue<>());
		synchronized (lockRegister) {
			if (roundRobin.get(m.getClass()) == null)
				roundRobin.put(m.getClass(), new LinkedBlockingQueue<>());
		}
			roundRobin.get(m.getClass()).add(m);
	}

	/**
	 * remove the microsevice 'm' from the subscribes map, messages map and from round robin
	 *
	 * @param m the micro-service to unregister.
	 */
	public void unregister(MicroService m) {
		synchronized (lockSendEv) {
			removeFromSubscribe(m);
			removeFromMessages(m);
			removeFromRoundRobin(m);
		}
	}

	/**
	 * remove 'm' from the messages map, if it still has messages in its queue- complete them (the bookstore terminated)
	 *
	 * @param m microservice to remove from
	 */
	private void removeFromMessages (MicroService m){
		LinkedBlockingQueue deleteMessages = messages.get(m);
			while (!deleteMessages.isEmpty()) {
				if (deleteMessages.peek() instanceof Event) {
					complete((Event) deleteMessages.poll(), null);
				} else
					deleteMessages.poll();
			}
			messages.remove(m);
	}

	/**
	 * remove the microservice from the round robin
	 *
	 * @param m microservice
	 */
	private void removeFromRoundRobin(MicroService m) {
		LinkedBlockingQueue curr = roundRobin.get(m.getClass());
		LinkedBlockingQueue copy = new LinkedBlockingQueue<>();
		synchronized (curr) {
			while (!curr.isEmpty()) {
				if (curr.peek() == m) {
					curr.poll();
				} else {
					try {
						copy.put(curr.poll());
					} catch (InterruptedException e) {
					}
				}
			}
			roundRobin.put(m.getClass(), copy);
		}
	}

	/**
	 * remove this microservice from all the queues it appears at in subscribes map
	 *
	 * @param m microservice
	 */
	private void removeFromSubscribe(MicroService m) {
		for (LinkedBlockingQueue<MicroService> queue : subscribes.values())
			queue.remove(m);
	}

	/**
	 *
	 * if there is a message that 'm' can take care of - take it. otherwise, wait.
	 *
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return Message to take care of
	 */
	public Message awaitMessage(MicroService m) {
		Message tmp = null;
		try {
			tmp = messages.get(m).take();
		} catch (InterruptedException e) { }
		return tmp;
	}
	

}