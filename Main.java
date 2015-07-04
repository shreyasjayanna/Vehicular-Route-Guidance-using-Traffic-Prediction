/*
    Main.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statements
import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import de.tudresden.sumo.util.ConvertHelper;
import de.tudresden.sumo.util.Sumo;
import de.tudresden.ws.Traci;
import de.tudresden.ws.container.SumoStringList;
import de.tudresden.ws.log.Log_txt;
import it.polito.appeal.traci.SumoTraciConnection;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is the main class
 */
public class Main {

    static final String linux_path = "/media/shreyas/New Volume/MS/Capstone/maps/SF/new map/";
    static final String windows_path = "H:/MS/Capstone/maps/SF/new map/";

	static String sumo_bin = "/usr/bin/sumo-gui"; // H:/MS/Capstone/sumo-0.22.0/bin/sumo-gui.exe";
	static String config_file = linux_path + "config.sumo.cfg";
    static String map_file = linux_path + "SF.osm";
    static String net_file = linux_path + "SF.net.xml";

    Sumo sumo;
    Traci traci;
    Log_txt log_txt;
    ConvertHelper helper;

    RoadGraph roadGraph;
    VehicleStore vehicleStore;
    Connections connections;

    Routes routes;

    static String resultFile;
    PrintWriter wr;

    ArrayList<String> routesInUse;
    HashSet<String> edgesInUse;

    HashMap<String, ArrayList<String>> edgeRouteMap;

    HashMap<String,String> initialVehRouteMap;

    HashMap<String,HashMap<Integer,Integer>> edgeFootprintCounter;

    int numRoutes;

    /**
     * Constructor
     */
    public Main() {
        this.roadGraph = new RoadGraph();
        this.vehicleStore = new VehicleStore();
        this.routes = new Routes();
        this.connections = new Connections();
        this.initialVehRouteMap = new HashMap<>();

        // Initialize SUMO and TraCI
        this.sumo = new Sumo();
        this.sumo.conn = new SumoTraciConnection(sumo_bin,config_file);
        this.sumo.conn.addOption("step-length","0.0");
        this.sumo.conn.addOption("time-to-teleport","-1");

        this.traci = new Traci();
        this.log_txt = new Log_txt();
        this.helper = new ConvertHelper(this.log_txt);

        this.routesInUse = new ArrayList<>();

        // Initialize result file
        DateFormat dateFormat = new SimpleDateFormat("MMddyyyy_HH-mm-ss");
        Date date = new Date();
        resultFile = "Simulation_" + dateFormat.format(date) + ".txt";

        try {
            wr = new PrintWriter(resultFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method initializes TraCI
     */
    public void initTraci() {
        this.traci.init(this.sumo, this.log_txt, this.helper);
    }

    /**
     * builRoadMap method
     * This method reads the map osm file and net file and builds the road map into
     * the RoadGraph object.
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void buildRoadMap() throws ParserConfigurationException, SAXException {
        SAXParserFactory parserFactory = new SAXParserFactoryImpl().newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        SAXHandler handler = new SAXHandler(this.roadGraph, this.connections);

        // Read nodes from map file and add them to the hashmap
        try {
            System.out.println("Reading " + Main.map_file);
            parser.parse(new File(Main.map_file), handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(handler.getMyNodeList() != null) {
            for (MyNode node : handler.getMyNodeList()) {
                // Add the node to the graph
                this.roadGraph.addNode(node);
            }
        }

        // Read edges and connections from the net file and add them to the
        // hashmap and connections object
        try {
            System.out.println("Reading " + Main.net_file);
            parser.parse(new File(Main.net_file), handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(handler.getMyEdgeList() != null) {
            for (MyEdge edge : handler.getMyEdgeList()) {
                // Add the edge to the graph
                this.roadGraph.addEdge(edge);
                // Add neighbor nodes for each node that the edge connects
                this.roadGraph.getNodes().get(edge.getFrom().getId()).addNeighbor(edge.getTo());
                this.roadGraph.getNodes().get(edge.getTo().getId()).addNeighbor(edge.getFrom());
                // Add edge to edge list of from node
                this.roadGraph.getNodes().get(edge.getFrom().getId()).addEdge(edge);
            }
        }
        // Add the connections
        Connections connects = handler.getConnections();
        if(connects.edgeConnections.size() > 0) {
            this.connections = new Connections(connects);
        }

        System.out.println("Road map built!");

    }


    /**
     * This method creates vehicles for the simulation
     * @param numVehicles   Number of vehicles to be created for simulation
     * @param vehTypes      Number of vehicle types
     * @param numRoutes     Number of routes available in the route file
     */
    public void createVehicles(int numVehicles, int vehTypes, int numRoutes) {

        System.out.println("Creating vehicles... Started");

        // Generate a random seed to generate random route numbers for vehicles
        Random rSeed = new Random();
        Random random = new Random(rSeed.nextLong());
        Random vehTypeRandom = new Random(rSeed.nextLong());

        for(int i = 1; i <= numVehicles; ++i) {

            String vehId = "" + i;

            int routeRan = random.nextInt(numRoutes) + 1;
            while(routeRan > numRoutes)
                routeRan = random.nextInt(numRoutes) + 1;

            String routeId = "" + routeRan;

            int vehRan = vehTypeRandom.nextInt(vehTypes) + 1;
            while(vehRan > vehTypes)
                vehRan =  vehTypeRandom.nextInt(vehTypes) + 1;

            String typeId = "type" + vehRan;

            this.routes.addRouteDensity(routeId);
            int depart = this.routes.getRouteDensity(routeId) * 5;

            wr.println("v id: " + vehId + ", route id: " + routeId + ", veh type id: " + typeId + ", depart: " + depart);

            traci.Vehicle_add(vehId, typeId, routeId, depart, 0.00, 0.10, (byte) 0);
            traci.Vehicle_setMinGap(vehId, 2.0);

            this.vehicleStore.addVehicle(vehId);
            this.vehicleStore.setVehicleRoute(vehId, routeId);
            this.vehicleStore.setVehicleDepart(vehId, depart);
            this.initialVehRouteMap.put(vehId,routeId);

        }

        wr.println("**********************************************************************");
        wr.flush();

        // Update routes in use list
        for(int i = 1; i < 51; ++i) {
            this.routesInUse.add("" + i);
        }

        System.out.println("Creating vehicles... Done");
    }

    /**
     * This method checks for congestion in the road network
     * @param allArrivedVeh List of all vehicles that have arrived at destination
     * @return              Returns an Arraylist of congested edges
     */
    public ArrayList<String> checkForCongestion(ArrayList<String> allArrivedVeh) {

        // Hashmap to store footprint counter of edges
        this.edgeFootprintCounter = new HashMap<>();

        ArrayList<String> vehicles = new ArrayList<>(this.vehicleStore.getVehicles());
        vehicles.removeAll(allArrivedVeh);
        this.routesInUse.clear();

        System.out.println("Num of vehicles: " + vehicles.size());

        // Get route ID of all currently active routes
        HashSet<String> routesInUseSet = new HashSet<>();
        for(String vehicle : vehicles) {
            String routeId = traci.Vehicle_getRouteID(vehicle);
            routesInUseSet.add(routeId);
        }
        this.routesInUse = new ArrayList<>(routesInUseSet);

        System.out.println("Num of routes in use: " + this.routesInUse.size());

        this.edgeRouteMap = new HashMap<>();

        // Get all edges currently in use from all currently active routes
        HashSet<String> edgesInUseSet = new HashSet<>();
        for(String route : this.routesInUse) {
            ArrayList<String> edges = new ArrayList<>(traci.Route_getEdges(route));
            edgesInUseSet.addAll(edges);
            this.edgeRouteMap.put(route,edges);
        }
        this.edgesInUse = new HashSet<>(edgesInUseSet);

        System.out.println("Num of edges in use: " + this.edgesInUse.size());

        // Prepare a list of congested edges
        HashSet<String> congestedEdges = new HashSet<>();
        for(String edge : this.edgesInUse) {
            int numVehOnEdge = traci.Edge_getLastStepHaltingNumber(edge);
            if(this.roadGraph.edges.get(edge).isCongested(numVehOnEdge))
                congestedEdges.add(edge);
            // Update travel time of the edge
            double time = traci.Edge_getTraveltime(edge);
            this.roadGraph.edges.get(edge).setTravelTime(time);
        }

        if(congestedEdges.size() > 0)
            return new ArrayList<>(congestedEdges);

        return null;
    }

    /**
     * This method runs the simulation with re-routes when congestion detected
     * @param numVehicles       Number of vehicles
     * @param rerouteApproach   The reroute approach
     */
    public void runRerouteSimulation(int numVehicles, String rerouteApproach) {

        this.numRoutes = 50;

        //start TraCI
        sumo.start(sumo_bin, config_file);

        //int numVehicles = 2500;
        // Create vehicles for simulation           // Passed args
        this.createVehicles(numVehicles, 10, 50);   // Num of vehicles
                                                    // Num of types of vehicles
                                                    // Num of routes

        System.out.println("Simulation... Starting");
        // Perform initial timestep
        sumo.do_timestep();

        int vehicleCount = 0;
        int count_timestep = 0;


        int prevVehicleCount = -1;

        List<String> arrivedVeh;
        ArrayList<String> allArrivedVeh = new ArrayList<String>();
        ArrayList<String> vehicles = new ArrayList<>(this.vehicleStore.getVehicles());

        boolean printSimProgressMsg = false;
        while(vehicleCount < numVehicles) {
            if(!printSimProgressMsg) {
                System.out.println("Simulation... In progress.. View Sumo window..");
                printSimProgressMsg = true;
            }
            try {
                arrivedVeh = traci.Simulation_getArrivedIDList();
                allArrivedVeh.addAll(arrivedVeh);

                vehicles.removeAll(allArrivedVeh);

                vehicleCount += arrivedVeh.size();

                ++count_timestep;

                if(arrivedVeh.size() > 0) {
                    for (String vehicle : arrivedVeh) {
                        //String route = this.vehicleStore.getVehicleRoute(vehicle);
                        String route = this.initialVehRouteMap.get(vehicle);
                        this.routes.addRouteTime(route,
                                (count_timestep - this.vehicleStore.getVehicleDepart(vehicle)));
                    }
                }

                // Every 10 minutes, check for congestion
                if(count_timestep % 600 == 0) {
                    System.out.println("Number of vehicles currently active is " + vehicles.size());
                    if(vehicles.size() > 10) {
                        System.out.println("Checking for congestion at timestep " + count_timestep + "...");
                        ArrayList<String> congestedEdgeList = this.checkForCongestion(allArrivedVeh);
                        if (congestedEdgeList != null) {
                            System.out.println("Num of congested edges: " + congestedEdgeList.size());
                            //System.out.println("Congestion detected on edge(s) " + congestedEdgeList);


                            // If congestion found, invoke appropriate rerouting strategy
                            if (rerouteApproach.equals("dsp"))
                                rerouteDSP(congestedEdgeList);
                            else if (rerouteApproach.equals("ebksp"))
                                rerouteEbksp(congestedEdgeList);

                        } else {
                            System.out.println("No Congestion detected at this point of time..");
                        }
                    }
                }

                sumo.do_timestep();

            } catch (NullPointerException e) {
                e.printStackTrace();
                wr.println();
                wr.println("####################################################");
                wr.println("!!! EXCEPTION OCCURED !!!");
                wr.println("####################################################");
                e.printStackTrace(wr);
                wr.flush();
                // wr.close();
                break;
            }
        }

        System.out.println("Num vehicles arrived: " + vehicleCount);
        System.out.println("Simulation... Completed");

        System.out.println("Num timesteps: " + count_timestep);


        // Calculate Average travel time for each route
        System.out.println("Calculating average travel time for each route... Starting");
        wr.println("Route \t Avg Travel Time");

        HashMap<String,Integer> routeDensity = this.routes.getRouteDensity();
        HashMap<String,Double> routeTime = this.routes.getRouteTime();
        ArrayList<String> routesList = new ArrayList<>(routeTime.keySet());

        Collections.sort(routesList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i = Integer.parseInt(o1);
                int j = Integer.parseInt(o2);

                if (i < j)
                    return -1;
                return 1;
            }
        });

        //System.out.println("Route list " + routesList);
        //for(String route : routesList) {
        for(int i = 1; i <= 50; ++i) {
            double avgTravelTime = (routeTime.get("" + i) / routeDensity.get("" + i)) / 60;
            wr.println(i + "\t" + avgTravelTime);
        }

        System.out.println("Calculating average travel time for each route... Completed");

        wr.flush();
        wr.close();

        System.out.println("Please check the Simulation data file for the results!");

        //stop TraCI
        sumo.stop_instance();
    }

    /**
     * This method reroutes a vehicle based on dynamic routing with prediction approach when traffic
     * congestion is predicted at an intersection.
     * @param congestedEgdeList     The list containing congested edges IDs
     */
    private void rerouteEbksp(ArrayList<String> congestedEgdeList) {
        Set<String> routes = this.edgeRouteMap.keySet();

        //vehicle,source;destination
        HashMap<String,HashSet<String>> vehicleSourceDestinationMap = new HashMap<>();
        HashMap<String,HashSet<String>> sourceDestNewRouteMap = new HashMap<>();

        /*
            For each congested edge, get the reroute edge and get the vehicles from reroute edge.
            For each (source,destination) pair, store the vehicle list.
            For each (source,destination) pair, create hashset object to store the new path
         */
        for(String congestedEdge : congestedEgdeList) {
            for(String route : routes) {
                if(this.edgeRouteMap.get(route).contains(congestedEdge)) {
                    ArrayList<String> routeEdges = this.edgeRouteMap.get(route);
                    int index = -1;
                    for(String edge : routeEdges) {
                        ++index;
                        if(edge.equals(congestedEdge))
                            break;
                    }

                    if(index < 1)
                        continue;

                    String rerouteEdge = routeEdges.get(index-1);

                    // Get the last step occupancy of an edge and if the occupancy is greater than 0,
                    // get vehicles on the edge
                    if(traci.Edge_getLastStepOccupancy(rerouteEdge) > 0.00) {
                        HashSet<String> vehicleIDs = new HashSet<>(traci.Edge_getLastStepVehicleIDs(rerouteEdge));
                        for(String vehicle : vehicleIDs) {
                            ArrayList<String> vehOldEdges = this.edgeRouteMap.get(this.vehicleStore.getVehicleRoute(vehicle));
                            String destination = vehOldEdges.get(vehOldEdges.size()-1);     // Destination edge

                            if(!vehicleSourceDestinationMap.containsKey(rerouteEdge+";"+destination))
                                vehicleSourceDestinationMap.put(rerouteEdge+";"+destination,new HashSet<String>());
                            vehicleSourceDestinationMap.get(rerouteEdge+";"+destination).add(vehicle);

                            if(!sourceDestNewRouteMap.containsKey(rerouteEdge+";"+destination))
                                sourceDestNewRouteMap.put(rerouteEdge+";"+destination,new HashSet<String>());
                        }
                    }

                }
            }
        }

        HashSet<String> sourceDestKeys = new HashSet<>(sourceDestNewRouteMap.keySet());

        Random random = new Random(System.currentTimeMillis());

        for(String sourceDest : sourceDestKeys) {
            String[] sourceDestPair = sourceDest.split(";");

            // Source and destination edges (not nodes)
            String source = sourceDestPair[0];
            String destination = sourceDestPair[1];

            MyNode from = this.roadGraph.edges.get(source).getTo();
            MyNode target = this.roadGraph.edges.get(destination).getTo();

            int _k = 3;
            KShortestPath kpaths = new KShortestPath(this.roadGraph,this.connections,_k);
            ArrayList<KPath> paths = kpaths.findPaths(from, target, this.roadGraph.edges.get(source));

            if(paths == null)
                continue;

            if(paths.size() > 0 && paths != null) {

                if(vehicleSourceDestinationMap.containsKey(sourceDest)) {

                    HashSet<String> vehicles = vehicleSourceDestinationMap.get(sourceDest);
                    System.out.println("Number of vehicles for reroute " + vehicles.size());

                    // For each vehicle, get k paths and assign the least popular path
                    for(String vehicle : vehicles) {

                        int k = random.nextInt(paths.size());

                        ArrayList<MyEdge> edges = paths.get(k).getAllEdges();

                        ArrayList<String> newRouteEdges = new ArrayList<>();
                        for (MyEdge edge : edges) {
                            newRouteEdges.add(edge.getId());
                        }

                        String newRoute = "" + ++this.numRoutes;
                        SumoStringList edgeList = new SumoStringList();
                        edgeList.addAll(newRouteEdges);

                        this.traci.Route_add(newRoute, edgeList);

                        this.traci.Vehicle_setRouteID(vehicle, newRoute);
                        this.vehicleStore.setVehicleRoute(vehicle, newRoute);
                        System.out.println("vehicle " + vehicle + "'s new route " + newRoute);

                        this.edgeRouteMap.put(newRoute, new ArrayList<>(edgeList));
                    }
                }
            }

        }
    }

    /**
     * This method reroutes a vehicle based on the dynamic routing approach.
     * @param congestedEgdeList     The list containing congested edges IDs
     */
    private void rerouteDSP(ArrayList<String> congestedEgdeList) {
        HashMap<String,String> oldRouteNewRouteMap = new HashMap<>();
        Set<String> routes = this.edgeRouteMap.keySet();
        HashSet<String> edgesConsidered = new HashSet<>();

        for(String congestedEdge : congestedEgdeList) {
            if(edgesConsidered.contains(congestedEdge)) {
                continue;
            }
            edgesConsidered.add(congestedEdge);
            for(String route : routes) {
                if(this.edgeRouteMap.get(route).contains(congestedEdge)) {
                    String congestedRoute = route;
                    ArrayList<String> routeEdges = this.edgeRouteMap.get(congestedRoute);
                    int index = 0;
                    for(int i = 0; i < routeEdges.size(); ++i) {
                        if(routeEdges.get(i).equals(congestedEdge)) {
                            index = i;
                            break;
                        }
                    }

                    if(index < 2)
                        continue;

                    String rerouteEdge = routeEdges.get(index-2);

                    ArrayList<String> vehiclesForReroute;
                    try {
                        System.out.println("Getting vehicles for reroute on edge " + rerouteEdge);
                            // Get vehicles to be rerouted
                            vehiclesForReroute = new ArrayList<>(traci.Edge_getLastStepVehicleIDs(rerouteEdge));
                            Stack<String> prevReroutedRoute = new Stack<>();
                            for (String vehicle : vehiclesForReroute) {

                                // If vehicle route hasn't been changed in this method call, change it
                                if (vehicleStore.getVehicleRoute(vehicle).equals(route)) {
                                    String vehRoute = route;

                                    ArrayList<String> vehEdges = this.edgeRouteMap.get(vehRoute);
                                    MyNode from = this.roadGraph.edges.get(rerouteEdge).getTo();
                                    MyNode target = this.roadGraph.edges.get(vehEdges.get(vehEdges.size() - 1)).getTo();

                                    // Find a new path for the vehicle and reroute it along that path
                                    DynamicShortestPath dsp = new DynamicShortestPath(this.roadGraph, this.connections);
                                    Stack<MyEdge> newEdges = dsp.findShortestPath(from, target, this.roadGraph.edges.get(rerouteEdge));
                                    if (newEdges != null) {
                                        // System.out.println("New edges " + newEdges);
                                        List<String> newRouteEdges = new ArrayList<>();
                                        while (newEdges.size() > 0) {
                                            String edgeId = newEdges.pop().getId();
                                            //System.out.println(edgeId);
                                            newRouteEdges.add(edgeId);
                                        }
                                        // System.out.println("Number of edges in new route: " + newRouteEdges.size());
                                        String newRoute = "" + ++this.numRoutes;
                                        SumoStringList edgeList = new SumoStringList();
                                        edgeList.addAll(newRouteEdges);

                                        this.traci.Route_add(newRoute, edgeList);

                                        this.traci.Vehicle_setRouteID(vehicle, newRoute);
                                        this.vehicleStore.setVehicleRoute(vehRoute, newRoute);
                                        System.out.println("old route " + vehRoute + " replaced with new route " + newRoute
                                                + " for vehicle " + vehicle);
                                        oldRouteNewRouteMap.put(route, newRoute);
                                        prevReroutedRoute.push("" + vehRoute);
                                    }

                                }

                            }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method runs the simulation using static routes. Vehicles are not rerouted.
     * @param numVehicles     Number of vehicles
     */
    public void runNoRerouteSimulation(int numVehicles) {

        //start TraCI
        sumo.start(sumo_bin, config_file);

        //int numVehicles = 2500;
        // Create vehicles for simulation           // Passed args
        this.createVehicles(numVehicles, 10, 50);    // Num of vehicles
                                                    // Num of types of vehicles
                                                    // Num of routes

        System.out.println("Simulation... Starting");
        // Perform initial timestep
        sumo.do_timestep();

        int vehicleCount = 0;
        int count_timestep = 0;
        //int vehicleNum;

        int prevVehicleCount = -1;

        List<String> arrivedVeh; // = new ArrayList<String>();
        List<String> allArrivedVeh = new ArrayList<String>();
        ArrayList<String> vehicles = new ArrayList<>(this.vehicleStore.getVehicles());

        boolean printSimProgressMsg = false;
        while(vehicleCount < numVehicles) {
            if(!printSimProgressMsg) {
                System.out.println("Simulation... In progress.. View Sumo window..");
                printSimProgressMsg = true;
            }
            try {
                System.out.println("Getting arrived ID list");
                arrivedVeh = traci.Simulation_getArrivedIDList();
              //  allArrivedVeh.addAll(arrivedVeh);

                vehicleCount += arrivedVeh.size();
                System.out.println("Vehicle count " + vehicleCount);

                ++count_timestep;

                if(arrivedVeh.size() > 0) {
                    for (String vehicle : arrivedVeh) {
                        String route = this.vehicleStore.getVehicleRoute(vehicle);
                        this.routes.addRouteTime(route, count_timestep);
                    }
                }

                if(vehicleCount > prevVehicleCount) {
                    System.out.println("Number of vehicles reached: " + vehicleCount);
                    prevVehicleCount = vehicleCount;
                }

                sumo.do_timestep();

            } catch (NullPointerException e) {
                e.printStackTrace();
                wr.println();
                wr.println("####################################################");
                wr.println("!!! EXCEPTION OCCURED !!!");
                wr.println("####################################################");
                e.printStackTrace(wr);
                wr.flush();
               // wr.close();
                break;
            }
        }

        System.out.println("Num vehicles arrived: " + vehicleCount);
        System.out.println("Simulation... Completed");

        System.out.println("Num timesteps: " + count_timestep);


        // Calculate Average travel time for each route
        System.out.println("Calculating average travel time for each route... Starting");
        wr.println("Route \t Avg Travel Time");

        HashMap<String,Integer> routeDensity = this.routes.getRouteDensity();
        HashMap<String,Double> routeTime = this.routes.getRouteTime();
        ArrayList<String> routesList = new ArrayList<>(routeTime.keySet());

        Collections.sort(routesList);

        for(String route : routesList) {
            double avgTravelTime = (routeTime.get(route) / routeDensity.get(route)) / 60;
            wr.println(route + "\t" + avgTravelTime);
        }

        System.out.println("Calculating average travel time for each route... Completed");

        wr.flush();
        wr.close();

        System.out.println("Please check the Simulation data file for the results!");

        //stop TraCI
        sumo.stop_instance();
    }

    /**
     * The maain method
     * @param args  Command line arguments
     * @throws ParserConfigurationException
     * @throws SAXException
     */
	public static void main(String[] args) throws ParserConfigurationException, SAXException {

        Main mainObj = new Main();

        // Create road map
        mainObj.buildRoadMap();

       // Initialize TraCI
        mainObj.initTraci();

        int numOfVehicles = 1000;

        // Run simulation
        mainObj.runNoRerouteSimulation(numOfVehicles);

        //mainObj.runRerouteSimulation(numOfVehicles,"dsp");

        //mainObj.runRerouteSimulation(numOfVehicles,"ebksp");

    }

}
