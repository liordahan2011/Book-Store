package bgu.spl.mics.application.passiveObjects;

/**
 * Data structure represents order pair
 */
public class Pair {
    private String first;
    private Integer second;

    /**
     * constructor
     *
     * @param first string that is the first in the order pair
     * @param second Integer that is the second of the order pair
     */
    public Pair(String first, Integer second){
        this.first = first;
        this.second = second;
    }

    /**
     * retrieves the first string of the pair
     *
     <p>
     *
     * @return String which is the first of the pair
     */
    public String getFirst() {
        return first;
    }

    /**
     * retrieves the second string of the pair
     *
     <p>
     *
     * @return Integer which is the second of the pair
     */
    public Integer getSecond(){
        return second;
    }

}
