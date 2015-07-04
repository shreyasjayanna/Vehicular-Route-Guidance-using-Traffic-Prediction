/*
    MyNode.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statements
import java.util.ArrayList;
import java.util.List;

/**
 * Class MyNode. This class defines a node in the road network graph.
 */
public class MyNode {
    String id;
    List<MyEdge> edgeList = null;
    List<MyNode> neighborNodes = null;

    double distance;

    MyNode prev;

    /**
     * Constructor
     * @param id    ID of the node
     */
    public MyNode(String id) {
        this.id = id;
        edgeList = new ArrayList<MyEdge>();
        neighborNodes = new ArrayList<MyNode>();
        this.distance = Double.POSITIVE_INFINITY;
        prev = null;
    }

    /**
     * This method adds an edge to the edgelist
     * @param edge  Edge
     */
    public void addEdgeList(MyEdge edge) {
        this.edgeList.add(edge);
    }

    /**
     * This method adds a neighbor to a node
     * @param node  Node
     * @return      True if successful
     */
    public boolean addNeighbor(MyNode node) {
        if(this.neighborNodes.add(node))
            return true;
        return false;
    }

    /**
     * This method returns the ID of the node
     * @return  Node ID
     */
    public String getId() {
        return id;
    }

    /**
     * This method returns the edge list
     * @return  Edge list
     */
    public List<MyEdge> getEdgeList() {
        return edgeList;
    }

    /**
     * This method returns the neighbor nodes of a node.
     * @return  Neighbor nodes list
     */
    public  List<MyNode> getNeighborNodes() {
        return this.neighborNodes;
    }

    /**
     * This method sets the Id of the node
     * @param id    Node ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method adds an edge to the edgelist
     * @param edge  Edge
     * @return      True if successful
     */
    public boolean addEdge(MyEdge edge) {
        if(this.edgeList.add(edge))
            return true;
        return false;
    }

    /**
     * This method sets the distance of the node from the source.
     * @param distance  Distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

}
