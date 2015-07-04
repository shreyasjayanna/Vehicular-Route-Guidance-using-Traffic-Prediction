/*
    KShortestPath.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statements
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Class KShortestPath. This class finds k-shortest paths between a source and a destination.
 */
public class KShortestPath {

    RoadGraph graph;
    RoadGraph tempGraph;

    Connections connections;

    DynamicShortestPath dsp;

    PriorityQueue<KPath> queue;

    int K;

    /**
     * Constructor
     * @param graph         Road graph
     * @param connections   Connections
     * @param k             k
     */
    public KShortestPath(RoadGraph graph, Connections connections, int k) {
        this.graph = new RoadGraph(graph);
        this.tempGraph = new RoadGraph(graph);
        this.connections = new Connections(connections);
        this.dsp = new DynamicShortestPath(graph,connections);
        this.queue = new PriorityQueue<>();

        this.K = k;
    }

    /**
     * This method finds k paths bewteen from and target nodes.
     * @param from          From node
     * @param target        Target node
     * @param currentEdge   Current Edge
     * @return              Arraylist of paths
     */
    public ArrayList<KPath> findPaths(MyNode from, MyNode target, MyEdge currentEdge) {
        ArrayList<KPath> paths = null;
        int index = 0;

        // Get the first path using Dijkstra's algorithm
        Stack<MyEdge> newEdges = this.dsp.findShortestPath(from,target,currentEdge);

        if(newEdges != null) {
            //System.out.println("Found first new path using Dijkstra's - path size is " + newEdges.size());
            // Add the shortest path from Dijkstra's into the arraylist
            ArrayList<MyEdge> altPath = new ArrayList<>();
            while(newEdges.size() > 0) {
                altPath.add(newEdges.pop());
            }

            KPath kPath = new KPath(altPath);
            paths = new ArrayList<KPath>();
            paths.add(index,kPath);

            // First shortest path is added to the list.
            // Need to find k-1 more shortest paths and add them to the list in the
            // increasing order of shortest paths
            for(int k = 1; k < K; ++k) {
                // Spur node ranges from the first node to the node before the last node in the previous k-shortest path
                for(int i = 0; i < paths.get(k-1).getSize() - 1; ++i) {
                    // Get spur node
                    MyNode spurNode = paths.get(k-1).getNode(i);
                    // Get root path
                    ArrayList<MyEdge> rootPath = new ArrayList<>(paths.get(k-1).getEdges(i));

                    for(KPath kpath : paths) {
                        ArrayList<MyEdge> edges = kpath.getEdges(i);
                        // Remove the edges from the graph which share the same path as root path
                        if(rootPath.equals(edges)) {
                            MyEdge removeEdge = kpath.getEdge(i+1);
                            this.tempGraph.removeEdge(removeEdge);
                        }
                    }

                    // Remove root path node from the graph except the spur node
                    for(MyNode node : this.getRootPathNodes(rootPath)) {
                        if(node != spurNode)
                            this.tempGraph.removeNode(node);
                    }

                    // Create new instance of Dijsktra's with the modified graph
                    this.dsp = new DynamicShortestPath(this.tempGraph,this.connections);
                    // Calculate the spur path
                    Stack<MyEdge> spurPathStack = dsp.findShortestPath(spurNode, target, currentEdge);

                    if(spurPathStack != null) {

                        // Create the total path
                        // Total path = root path + spur path
                        ArrayList<MyEdge> totalPath = new ArrayList<>(rootPath);

                        spurPathStack.pop();
                        while (spurPathStack.size() > 0) {
                            totalPath.add(spurPathStack.pop());
                        }

                        // Create a new k path object with the new k-shortest path
                        KPath newPath = new KPath(totalPath);

                        // Add the new path to the priority queue
                        this.queue.add(newPath);
                        // Restore the temporary graph as this will be used for next iterations
                        this.tempGraph = new RoadGraph(this.graph);
                    }
                }

                // If there are no k-shortest paths (all paths are exhausted), break
                if(this.queue.size() < 1) {
                    break;
                }
                // Add the new path to the list which will be returned
                paths.add(k,queue.poll());
            }
        }
        // Return the k-shortest paths
        return paths;
    }

    /**
     * This method returns the root path nodes
     * @param rootPath  Root path
     * @return          Nodes from root path
     */
    public ArrayList<MyNode> getRootPathNodes(ArrayList<MyEdge> rootPath) {
        ArrayList<MyNode> nodes = new ArrayList<>();
        for(MyEdge edge : rootPath) {
            nodes.add(edge.getTo());
        }
        return nodes;
    }
}
