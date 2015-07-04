/*
    KPath.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statement
import java.util.ArrayList;

/**
 * Class KPath. This class finds k shortest paths between two nodes. It implements Comparable interface
 */
public class KPath implements Comparable<KPath> {

    double traveltime;
    ArrayList<MyEdge> edges;
    String originalRouteId;

    int numEdges;

    /**
     * Default ocnstructor
     */
    public KPath() {
        this.edges = new ArrayList<>();
        this.numEdges = 0;
    }

    /**
     * Constructor
     * @param edges The list containing edges
     */
    public KPath(ArrayList<MyEdge> edges) {
        this();
        this.edges.addAll(edges);
        this.numEdges = this.edges.size();
        //System.out.println("Size of this new path " + this.numEdges);

        this.traveltime = 0.0;
        this.calculateTravelTime();
    }

    /**
     * This method calculates the travel time of all edges
     */
    private void calculateTravelTime() {
        for(MyEdge edge : edges) {
            this.traveltime += edge.getTravelTime();
        }
    }

    /**
     * This method returns the route ID
     * @return  Route ID as a String
     */
    public String getRouteId() {
        return this.originalRouteId;
    }

    /**
     * This method returns the number of edges in the path
     * @return
     */
    public int getSize() {
        return this.numEdges;
    }

    /**
     * This method implements CompareTo method from Comparable interface
     * @param o     The other object
     * @return      -1 if this edge's travel time is less, else 0
     */
    @Override
    public int compareTo(KPath o) {
        if(this.traveltime < o.traveltime)
            return -1;
        return 0;
    }

    /**
     * This method returns the ith node from the path
     * @param i     The index of the node
     * @return      The node
     */
    public MyNode getNode(int i) {
        MyEdge spurEdge = this.edges.get(i);
        MyNode spurNode = spurEdge.getTo();
        return spurNode;
    }

    /**
     * This method returns the first i edges from the path
     * @param i     The index i
     * @return      The edges list as an ArrayList
     */
    public ArrayList<MyEdge> getEdges(int i) {
        ArrayList<MyEdge> edges = new ArrayList<>();
        for(int j = 0; j <= i; ++j) {
            edges.add(this.edges.get(j));
        }
        return edges;
    }

    /**
     * This method returns the edge at ith position
     * @param i     The position i
     * @return      The edge at ith position
     */
    public MyEdge getEdge(int i) {
        MyEdge edge = this.edges.get(i);
        return edge;
    }

    /**
     * This method returns all the edges
     * @return      All edges as an ArrayList
     */
    public ArrayList<MyEdge> getAllEdges() {
        return this.edges;
    }
}
