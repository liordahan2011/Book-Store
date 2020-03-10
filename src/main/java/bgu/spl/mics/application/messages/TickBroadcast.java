package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * Broadcast (Message object) used as clock- representing the time of the store.
 */
public class TickBroadcast implements Broadcast {
    private int tick;
    private int duration;

    /**
     * Constructor
     *
     * @param tick represents the current tick
     * @param duration represents the duration of time in which the book store is active
     */
    public TickBroadcast(int tick, int duration){
        this.tick =tick;
        this.duration = duration;
    }

    /**
     * Retrieves the current tick
     <p>
     * @return int current tick
     */
    public int getTick(){
        return tick;
    }

    /**
     * @return int duration of the book store
     */
    public int getDuration(){return duration;}
}

