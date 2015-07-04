/*
    RoadGraph.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statements
import java.util.HashMap;

/**
 * Class RoadGraph. This class defines a road network as a graph
 */
public class RoadGraph {

    HashMap<String,MyNode> nodes;
    HashMap<String,MyEdge> edges;

    /**
     * Default Constructor
     */
    public RoadGraph() {
        this.nodes = new HashMap<String, MyNode>();
        this.edges = new HashMap<String, MyEdge>();
    }

    /**
     * Constructor
     * @param graph RoadGraph object
     */
    public RoadGraph(RoadGraph graph) {
        this();
        this.nodes.putAll(graph.getNodes());
        this.edges.putAll(graph.getEdges());
    }

    /**
     * This method sets the nodes hashmap
     * @param nodes     Nodes hashmap
     */
    public void setNodes(HashMap<String, MyNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * This method returns the nodes hashmap
     * @return  Nodes hashmap
     */
    public HashMap<String, MyNode> getNodes() {
        return this.nodes;
    }

    /**
     * This method returns the edges hashmap
     * @return  Edges hashmap
     */
    public HashMap<String, MyEdge> getEdges() {
        return this.edges;
    }

    /**
     * This method adds a node
     * @param node  Node object
     * @return  True if add successful
     */
    public boolean addNode(MyNode node) {
        if(this.nodes.put(node.getId(), node) != null)
            return true;
        return false;
    }

    /**
     * This method returns the number of nodes in the road graph
     * @return  Number of nodes
     */
    public int getNumNodes() {
        return this.nodes.size();
    }

    /**
     * This method removes a node from the road graph
     * @param id    ID of the node to be removed
     * @return      True if the remove was successful
     */
    public boolean removeNode(String id) {
        if(this.nodes.remove(id) != null)
            return true;
        return false;
    }

    /**
     * This method removes node from the road graph
     * @param node      Node to be removed
     * @return          True if remove was successful
     */
    public boolean removeNode(MyNode node) {
        if(this.nodes.containsValue(node)) {
            for(MyNode myNode : node.getNeighborNodes()) {
                myNode.getNeighborNodes().remove(node);
            }
            if(this.nodes.remove(node.getId()) != null)
                return true;
        }
        return false;
    }

    /**
     * This method sets the edges hashmap to the passed hashmap
      * @param edges    Edges hashmap
     */
    public void setEdges(HashMap<String, MyEdge> edges) {
        this.edges = edges;
    }

    /**
     * This method adds an edge
      * @param myEdge   Edge
     * @return          True if add was successful
     */
    public boolean addEdge(MyEdge myEdge) {
        if(this.edges.put(myEdge.getId(), myEdge) != null) {
            return true;
        }
        return false;
    }

    /**
     * This method returns the number of edges
     * @return  Number of edges
     */
    public int getNumEdges() {
        return this.edges.size();
    }

    /**
     * This method removes an edge from the road graph
     * @param edge  Edge to be removed from the graph
     */
    public void removeEdge(MyEdge edge) {
        String id = edge.getId();
        this.edges.remove(id);
    }
}
