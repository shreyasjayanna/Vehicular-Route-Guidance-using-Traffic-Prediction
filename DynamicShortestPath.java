/*
    DynamicShortestPath.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statements
import java.util.*;

/**
 * Class DynamicShortestPath. This class finds shortest paths between a source and destination
 */
public class DynamicShortestPath {

    RoadGraph graph;
    Set<MyNode> visitedQueue;
    ArrayList<MyNode> unvisitedQueue;

    HashMap<String,Double> nodeDistanceMap;
    HashMap<String,String> nodePreviousMap;
    HashMap<MyEdge,MyEdge> edgePreviousMap;

    Connections connections;

    /**
     * Default Constructor
     */
    public DynamicShortestPath() {
        this.graph = new RoadGraph();
        this.visitedQueue = new HashSet<>();
        this.unvisitedQueue = new ArrayList<MyNode>();
        this.nodeDistanceMap = new HashMap<>();
        this.nodePreviousMap = new HashMap<>();
        this.connections = new Connections();
        this.edgePreviousMap = new HashMap<>();
    }

    /**
     * Constructor
     * @param graph         The graph of road network
     * @param connections   The connections in the graph
     */
    public DynamicShortestPath(RoadGraph graph, Connections connections) {
        this();
        this.graph = new RoadGraph(graph);
        this.connections = new Connections(connections);
    }

    /**
     * This method sets the roadgraph object
     * @param graph     The road graph
     */
    public  void setGraph(RoadGraph graph) {
        this.graph = graph;
    }

    /**
     * This method returns the road graph
     * @return  The road graph object
     */
    public RoadGraph getGraph() {
        return this.graph;
    }

    /**
     * This method finds shortest path between two nodes
     * @param from          The source node
     * @param target        The destination node
     * @param currentEdge   The current edge from which the vehicles will be rerouted
     * @return  The new path in a Stack
     */
    public Stack<MyEdge> findShortestPath(MyNode from, MyNode target, MyEdge currentEdge) {
        /*
            Distance refers to Travel Time
            Pick edge list from the from node,
            update the distance to every to node of each edge in a hashtable <node(string),double(distance)>
            Update the previous node for each node in a hashtable <node(string),prevNode(string)>
         */

        //System.out.println();
        //System.out.println("In DSP - findpaths, from " + from.getId() + " target " + target.getId() + " current edge " + currentEdge.getId());

        Stack<MyEdge> path = null;

        boolean targetReached = false;
        MyNode node = from;
        node.setDistance(0.0);
        this.nodeDistanceMap.put(node.getId(), 0.0);
        this.unvisitedQueue.add(node);
        MyEdge prevEdge = null;

        boolean firstEdgeConsidered = false;
        boolean firstEdgeFlag = true;

        while(unvisitedQueue.size() > 0) {
            if(!firstEdgeConsidered) {
                prevEdge = currentEdge;
                firstEdgeConsidered = true;
            }

            Collections.sort(unvisitedQueue, new Comparator<MyNode>() {
                @Override
                public int compare(MyNode o1, MyNode o2) {
                    if(o1.distance < o2.distance)
                        return -1;
                    return 1;
                }
            });

            // print unvisited queue contents
    /*
            System.out.print("Unvisited queue [");
            for(MyNode myNode : unvisitedQueue) {
                System.out.print(" " + myNode.getId());
            }
            System.out.println(" ]");
    */
            MyNode minNode = unvisitedQueue.remove(0);
            //System.out.println("min node from unvisited queue " + minNode.getId());
            visitedQueue.add(minNode);
            if(minNode == target) {
                targetReached = true;
              //  System.out.println("Target " + target.getId() + " reached");
                break;
            }

            this.calculateShortestPaths(minNode, prevEdge, firstEdgeFlag);
            firstEdgeFlag = false;
        }

        ArrayList<String> edges = new ArrayList<>();

        // If the target is reached, it means a path exists from source to destination node
        if(targetReached) {
            path = new Stack<>();
            MyNode prev = null;
            while(target != from) {
                prev = this.graph.nodes.get(this.nodePreviousMap.get(target.getId()));

                MyEdge connectingEdge = this.getConnectingEdge(prev, target, prevEdge);

                edges.add(connectingEdge.getId());

                path.push(connectingEdge);
                prevEdge = connectingEdge;

                target = prev;

                //System.out.println("Connecting edge : " + connectingEdge);

            }
            path.push(currentEdge);
            edges.add(currentEdge.getId());
            //Collections.reverse(edges);
        }
//        System.out.println((path == null ? "path is null" : "path size " + path.size()));

        return path;
    }

    /**
     * This method calculates the shortest path between the source and a node in the graph
     * @param from          From node
     * @param prevEdge      Previous edge
     * @param firstEdge     Frist edge
     */
    public void calculateShortestPaths(MyNode from, MyEdge prevEdge, boolean firstEdge) {
        ArrayList<MyNode> neighbors = new ArrayList<>(from.getNeighborNodes());

        ArrayList<MyEdge> edges = new ArrayList<>(from.getEdgeList());

        for(MyNode to : neighbors) {
            double dist = getDistance(from) + distance(from,to);
            if(getDistance(to) > dist) {
                MyEdge possibleEdge = null;
                for(MyEdge edge : edges) {
                    if(edge.getFrom().getId().equals(from.getId()) && edge.getTo().getId().equals(to.getId())) {
                        possibleEdge = edge;
                        break;
                    }
                }

                boolean edgeFound = false;
                if(!firstEdge) {
                    MyNode fromPrevNode = this.graph.getNodes().get(this.nodePreviousMap.get(from.getId()));
                    if(fromPrevNode != null) {
                        List<MyEdge> fromPrevEdges = fromPrevNode.getEdgeList();
                        for (MyEdge anEdge : fromPrevEdges) {
                            if (anEdge.getFrom().getId().equals(fromPrevNode.getId()) &&
                                    anEdge.getTo().getId().equals(from.getId())) {
                                edgeFound = true;
                                prevEdge = anEdge;
                                break;
                            }
                        }
                    }

                    if(!edgeFound)
                        continue;
                }

                if(possibleEdge != null) {
                    if(this.connections.edgeConnections.containsKey(prevEdge.getId())) {
                        if (this.connections.getConnection(prevEdge.getId()).contains(possibleEdge.getId())) {
                            to.setDistance(dist);
                            this.nodeDistanceMap.put(to.getId(), dist);
                            this.nodePreviousMap.put(to.getId(), from.getId());
                            unvisitedQueue.add(to);
                            this.edgePreviousMap.put(possibleEdge, prevEdge);
                            //                    System.out.println("Edge in path " + possibleEdge.getId());
                        }
                    }
                }
            }
        }
    }

    /**
     * This method returns the distance between two nodes
     * @param from  From node
     * @param to    To node
     * @return      The distance between from and to node as a double value
     */
    private double distance(MyNode from, MyNode to) {
        ArrayList<MyEdge> edges = new ArrayList<>(from.getEdgeList());
        for(MyEdge edge : edges) {
            // If an edge exists between from and to node, return the distance
            if(edge.getFrom().getId().equals(from.getId()) && edge.getTo().getId().equals(to.getId())) {
                return edge.getTravelTime();
            }
        }
        // If no edge exists between from and to node, return infinity
        return Double.POSITIVE_INFINITY;
    }

    /**
     * This method returns the distance of a node from source
     * @param node  Node
     * @return      distance of node from source as a double value
     */
    public double getDistance(MyNode node) {
        if(this.nodeDistanceMap.containsKey(node.getId()))
            return this.nodeDistanceMap.get(node.getId());
        return Double.POSITIVE_INFINITY;
    }

    /**
     * This method retruns the connecting edge between two nodes
     * @param from          From node
     * @param to            To node
     * @param prevEdge      Previous edge
     * @return              Edge connecting from and to nodes, null if no such edge exists
     */
    public MyEdge getConnectingEdge(MyNode from, MyNode to, MyEdge prevEdge) {
        ArrayList<MyEdge> edges;
        if(from.getEdgeList().size() > 0) {
            edges = new ArrayList<>(from.getEdgeList());
            for (MyEdge edge : edges) {
                if (edge.getFrom().getId().equals(from.getId()) && edge.getTo().getId().equals(to.getId())) {
                        return edge;
                }
            }
        }
        return null;
    }
}
