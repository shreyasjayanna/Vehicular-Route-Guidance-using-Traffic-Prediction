/*
    Connections.java
    Author: Shreyas Jayanna
    Version 1
    Date: 5/15/2015
 */

// Import statements
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class connections. This class defines connections between intersections
 */
public class Connections {

    // Hashmap to store connections
    HashMap<String,HashSet<String>> edgeConnections;

    /**
     * Default constructor
     */
    public Connections() {
        this.edgeConnections = new HashMap<>();
    }

    /**
     * Constructor
     * @param connects  Connections object
     */
    public Connections(Connections connects) {
        this();
        this.edgeConnections.putAll(connects.getAllConnections());
    }

    /**
     * This method adds connection between two nodes
     * @param from  From node ID
     * @param to    To node ID
     */
    public void addConnection(String from, String to) {
        HashSet<String> toSet = null;
        if(this.edgeConnections.containsKey(from)) {
            toSet = this.edgeConnections.get(from);
        } else {
            toSet = new HashSet<>();
        }
        toSet.add(to);
        this.edgeConnections.put(from,toSet);
    }

    /**
     * This method returns the connections of a node
     * @param from  The from node
     * @return      Connections of from node as a HashSet
     */
    public HashSet<String> getConnection(String from) {
        HashSet<String> connections = null;

        if(this.edgeConnections.containsKey(from))
            connections = this.edgeConnections.get(from);

        return connections;
    }

    /**
     * This method returns all the connections in the graph
     * @return  connections in graph
     */
    public HashMap<String,HashSet<String>> getAllConnections() {
        return this.edgeConnections;
    }
}
