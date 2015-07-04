/*
    MyEdge.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

/**
 * Class MyEdge. This class defines an edge in the road graph
 */
public class MyEdge implements Comparable<MyEdge> {

    String id;
    MyNode from;
    MyNode to;

    double length;
    double speedLimit;

    double currentSpeed;

    double time;

    int maxNumVehicles;
    int currentNumVehicles;

    double travelTime;

    /**
     * Constructor
     * @param id            ID of the edge
     * @param from          From node
     * @param to            To Node
     * @param length        Length of the edge
     * @param speedLimit    Speed limit on the road segment the edge represents
     * @param time          Time required to traverse the edge
     */
    public MyEdge(String id, MyNode from, MyNode to, double length, double speedLimit, double time) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.length = length;
        this.speedLimit = speedLimit;
        this.time = time;
        this.maxNumVehicles = (int) (this.length / (5+2.5));
        this.currentNumVehicles =  0;
        this.currentSpeed = this.speedLimit;
        this.travelTime = this.length/this.speedLimit;
    }

    /**
     * Constuctore
     * @param id        ID of the edge
     * @param from      From node
     * @param to        To node
     */
    public MyEdge(String id, MyNode from, MyNode to) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.length = 0.0;
        this.speedLimit = 0.0;
        this.time = 0.0;
        this.maxNumVehicles = (int) (this.length / (5+2.5));
        this.currentNumVehicles =  0;
        this.currentSpeed = speedLimit;
        this.travelTime = 0.0; //this.length/this.speedLimit;
    }

    /**
     * This method sets the ID of the edge
     * @param id    ID of the edge
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method sets the From node of an edge
     * @param from      From node
     */
    public void setFrom(MyNode from) {
        this.from = from;
    }

    /**
     * This method sets the To node of an edge
     * @param to        To node
     */
    public void setTo(MyNode to) {
        this.to = to;
    }

    /**
     * This method returns the ID of an edge
     * @return  ID of the edge as a String
     */
    public String getId() {
        return id;
    }

    /**
     * This method returns the from node of the edge
     * @return  From node
     */
    public MyNode getFrom() {
        return this.from;
    }

    /**
     * This method returns the to node of the edge
     * @return  To node
     */
    public MyNode getTo() {
        return this.to;
    }

    /**
     * This method returns the length of the edge
     * @return  Length of the edge as a double
     */
    public double getLength() {
        return length;
    }

    /**
     * This method sets the length of the edge
     * @param length
     */
    public void setLength(double length) {
        this.length = length;
        this.setMaxNumVehicles();
    }

    /**
     * This method returns the speed limit on the edge
     * @return  Speed limit as a double
     */
    public double getSpeedLimit() {
        return speedLimit;
    }

    /**
     * This method sets the speed limit on the edge
     * @param speedLimit    Speed limit
     */
    public void setSpeedLimit(double speedLimit) {
        this.speedLimit = speedLimit;
        this.setCurrentSpeed(speedLimit);
        this.setTravelTime(this.length/this.speedLimit);
    }

    /**
     * This method returns the time required to traverse the edge
     * @return  Time to traverse the edge
     */
    public double getTime() {
        return time;
    }

    /**
     * This method sets the time required to traverse the edge
     * @param time
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * This method checks if this edge is congested. Returns true if congested.
     * @param currentNumVehicles    Current number of vehicles on the edge
     * @return                      True if the edge is congested, false otherwise
     */
    public boolean isCongested(int currentNumVehicles) {
        this.currentNumVehicles = currentNumVehicles;
        if(this.currentNumVehicles > 0)// && this.travelTime == Double.POSITIVE_INFINITY)
            this.setTravelTime(this.length / this.speedLimit);
        if(this.maxNumVehicles == 0)
            this.maxNumVehicles = (int) (this.length / (5+2.5));
        if(this.maxNumVehicles == 0)
            this.maxNumVehicles = 1;

        if(this.currentNumVehicles / this.maxNumVehicles >= 0.75)
            return true;
        return false;
    }

    /**
     * This method sets the current average speed on the edge
     * @param speed     Current average speed
     */
    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
    }

    /**
     * This method returns the current average speed on the edge
     * @return      Current average speed
     */
    public double getCurrentSpeed() {
        return this.currentSpeed;
    }

    /**
     * This method sets the travel time on the edge
     * @param time  Travel time on the edge
     */
    public void setTravelTime(double time) {
        //System.out.println("Set travel time of " + this.getId() + " from " + this.travelTime +  " to " + time);
        this.travelTime = time;
    }

    /**
     * This method returns the travel time on the edge
     * @return      Travel time on the edge
     */
    public double getTravelTime() {
        return this.travelTime;
    }

    /**
     * This method sets the maximum number of vehicles that the edge can have on it.
     */
    public void setMaxNumVehicles() {
        this.maxNumVehicles = (int) (this.length / (5+2.5));
    }

    /**
     * Overridden method from Comparable interface
     * @param o     The other edge object
     * @return      1 if the current edge's travel time is less than the other edge's travel time
     */
    @Override
    public int compareTo(MyEdge o) {
        if(this.travelTime < o.getTravelTime())
            return 1;
        else
            return -1;
    }
}
