/*
    Routes.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statement
import java.util.HashMap;

/**
 * Class Routes. This class stores traffic density and time taken to travel the route for every route.
 */
public class Routes {

	HashMap<String,Integer> routeDensity;       // <route id, num of vehicles on the route>
	HashMap<String,Double> routeTime;          // Time taken to travel the entire route

    /**
     * Default constructor
     */
	public Routes() {
		this.routeDensity = new HashMap<>();
		this.routeTime = new HashMap<>();
	}

    /**
     * This method returns the route density hashmap
     * @return
     */
	public HashMap<String, Integer> getRouteDensity() {
		return routeDensity;
	}

    /**
     * This method sets the route density hashmap to the passed hashmap
     * @param routeDensity  route density hashmap
     */
	public void setRouteDensity(HashMap<String, Integer> routeDensity) {
		this.routeDensity = routeDensity;
	}

    /**
     * This method returns the time taken to travel on the route.
     * @return  Time taken to travel the route
     */
	public HashMap<String, Double> getRouteTime() {
		return routeTime;
	}

    /**
     * This method increases route density by 1
     * @param id    Route ID
     */
	public void addRouteDensity(String id) {
		if(this.routeDensity.containsKey(id)) {
			this.routeDensity.put(id, (this.routeDensity.get(id) + 1));
		} else {
			this.routeDensity.put(id, 1);
		}
	}

    /**
     * This method returns the route density of a particular route.
     * @param id    Route ID
     * @return      Traffic density on the route
     */
	public int getRouteDensity(String id) {
		if(this.routeDensity.containsKey(id))
			return this.routeDensity.get(id);
		return 0;
	}

    /**
     * This method adds the travel time on the route.
     * @param id        Route ID
     * @param time      Time taken to travel the route
     */
	public void addRouteTime(String id, double time) {
		if(this.routeTime.containsKey(id))
			this.routeTime.put(id, this.routeTime.get(id) + time);
		else
			this.routeTime.put(id,time);
	}

    /**
     * This method returns the time taken to travel the route.
     * @param id    Route ID
     * @return      Time taken to travel the route
     */
	public double getRouteTime(String id) {
		if(this.routeTime.containsKey(id))
			return this.routeTime.get(id);
		return 0.00;
	}

}
