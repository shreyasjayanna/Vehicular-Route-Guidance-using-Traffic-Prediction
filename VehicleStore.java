/*
    VehicleStore.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statements
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class VehicleStore. This class stores information about vehicles.
 */
public class VehicleStore {

    List<String> vehicles;
    HashMap<String,String> vehicleRoutes;
    HashMap<String,Double> vehicleTimes;
    HashMap<String,Double> vehicleSpeeds;
    HashMap<String, Integer> vehicleNumSpeeds;
    HashMap<String,Integer> vehicleDepart;

    /**
     * Default constructor
     */
    public VehicleStore() {
        this.vehicles = new ArrayList<String>();
        this.vehicleRoutes = new HashMap<String, String>();
        this.vehicleTimes = new HashMap<String, Double>();
        this.vehicleSpeeds = new HashMap<String,Double>();
        this.vehicleNumSpeeds = new HashMap<String, Integer>();
        this.vehicleDepart = new HashMap<>();
    }

    /**
     * This method returns the depart time of a vehicle
     * @param id    Vehicle ID
     * @return      Depart time of the vehicle
     */
    public int getVehicleDepart(String id) {
        if(this.vehicleDepart.containsKey(id))
            return this.vehicleDepart.get(id);
        return 0;
    }

    /**
     * This method sets the depart time of a vehicle
     * @param id        Vehicle ID
     * @param depart    Depart time of the vehicle
     */
    public void setVehicleDepart(String id, int depart) {
        this.vehicleDepart.put(id,depart);
    }

    /**
     * This method returns the map of vehicle depart times
     * @return  Hashmap of vehicle depart times
     */
    public HashMap<String,Integer> getAllVehiclesDepart() {
        return this.vehicleDepart;
    }

    /**
     * This method returns the map of vehicles and routes
     * @return  Hashmap of vehicles and the routes
     */
    public HashMap<String, String> getVehicleRoutes() {
        return vehicleRoutes;
    }

    /**
     * This method sets the vehicle routes hashmap to the passed hashmap
     * @param vehicleRoutes     Vehicles and routes hashmap
     */
    public void setVehicleRoutes(HashMap<String, String> vehicleRoutes) {
        this.vehicleRoutes = vehicleRoutes;
    }

    /**
     * This method sets the speed of a vehicle
     * @param id        Vehicle ID
     * @param speed     Speed of the vehicle
     */
    public void setVehicleSpeed(String id, double speed) {
        if(this.vehicleSpeeds.containsKey(id))
            speed += this.vehicleSpeeds.get(id);
        this.vehicleSpeeds.put(id, speed);
    }

    /**
     * This method returns the speed of a vehicle
     * @param id    Vehicle ID
     * @return      Speed of the vehicle
     */
    public double getVehicleSpeed(String id) {
        if(this.vehicleSpeeds.containsKey(id))
            return this.vehicleSpeeds.get(id);
        return 0.00;
    }

    /**
     * This method sets the speed of the vehicle by increasing it by 1
     * @param id    Vehicle ID
     */
    public void setVehicleNumSpeeds(String id) {
        if(this.vehicleNumSpeeds.containsKey(id))
            this.vehicleNumSpeeds.put(id,this.vehicleNumSpeeds.get(id)+1);
        else
            this.vehicleNumSpeeds.put(id,1);
    }

    /**
     * This method returns the vehicle number speed hashmap
     * @param id    Vehicle ID
     * @return
     */
    public int getVehicleNumSpeed(String id) {
        return this.vehicleNumSpeeds.get(id);
    }

    /**
     * This method returns the list of vehicles
     * @return      List of vehicles
     */
    public List<String> getVehicles() {
        return vehicles;
    }

    /**
     * This method sets the vehicles hashmap
     * @param vehicles  Vehicles hashmap
     */
    public void setVehicles(List<String> vehicles) {
        this.vehicles = vehicles;
    }

    /**
     * This method returns the number of vehicles
     * @return  Number of vehicles
     */
    public int getNumVehicles() {
        return this.vehicles.size();
    }

    /**
     * This method adds a vehicle to the store
     * @param id    ID of the vehicle
     * @return      True if adding a vehicle was successful
     */
    public boolean addVehicle(String id) {
        if(this.vehicles.add(id)) {
            return true;
        }
        return false;
    }

    /**
     * This method removes a vehicle
     * @param id    Vehicle ID
     * @return      True if removal was successful
     */
    public boolean removeVehicle(String id) {
        if(this.vehicles.remove(id)) {
            return true;
        }
        return false;
    }

    /**
     * This method clears the vehicle store
     */
    public void clearVehicles() {
        this.vehicles.clear();
    }

    /**
     * This method sets the route of a vehicle
     * @param vehId     vehicle ID
     * @param RouId     Route ID
     * @return          true if successful
     */
    public boolean setVehicleRoute(String vehId, String RouId) {
        if(this.vehicles.contains(vehId)) {
            if(vehicleRoutes.put(vehId,RouId) != null)
                return true;
        }
        return false;
    }

    /**
     * This method returns the route of a vehicle
     * @param id    vehicle ID
     * @return      route of the vehicle
     */
    public String getVehicleRoute(String id) {
        if(this.vehicles.contains(id))
            if(this.vehicleRoutes.containsKey(id))
                return this.vehicleRoutes.get(id);
        return null;
    }

    /**
     * This method sets the vehicle time
     * @param id        Vehicle ID
     * @param time      Time of the vehicle
     * @return          True if successful
     */
    public boolean setVehicleTime(String id, double time) {
        if(this.vehicles.contains(id))
            if(this.vehicleTimes.put(id,time) != null)
                return true;
        return false;
    }

    /**
     * This method returns the time of the vehicle
     * @param id    Vehicle ID
     * @return      Time of the vehicle
     */
    public double getVehicleTime(String id) {
        if(this.vehicles.contains(id))
            if(this.vehicleTimes.containsKey(id))
                return this.vehicleTimes.get(id);
        return 0.00;
    }
}
