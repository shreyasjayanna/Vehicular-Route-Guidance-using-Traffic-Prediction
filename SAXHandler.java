/*
    SAXHandler.java
    Author: Shreyas Jayanna
    Version 1
    Date: 05/15/2015
 */

// Import statements
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.List;

/**
 *  Class SAXHandler. This class defines a handler to read XML file
 */
public class SAXHandler extends DefaultHandler {
    MyNode myNode;
    List<MyNode> myNodeList;

    static MyEdge myEdge;
    List<MyEdge> myEdgeList;

    RoadGraph roadGraph;
    Connections connections;

    String from;
    String to;

    String content;

    /**
     * Constructor
     * @param roadGraph     Road graph object
     * @param connections   Connections object
     */
    public SAXHandler(RoadGraph roadGraph, Connections connections) {
        myNode = null;
        myNodeList = new ArrayList<MyNode>();

        myEdge = null;
        myEdgeList = new ArrayList<MyEdge>();

        this.roadGraph = roadGraph;
        this.connections = connections;

        this.content = null;
    }

    /**
     * This method reads the start element in the XML tag
     * @param uri           URI
     * @param localName     Local name
     * @param qName         Name of the element
     * @param attributes    attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if(qName.equals("node")) {
            myNode = new MyNode(attributes.getValue("id"));
        } else if(qName.equals("edge")) {

            if(attributes.getValue("from") != null) {
                myEdge = new MyEdge(attributes.getValue("id"),
                        this.roadGraph.getNodes().get(attributes.getValue("from")),
                        this.roadGraph.getNodes().get(attributes.getValue("to")));
            } else {
                myEdge = null;
            }
        }
        if(qName.equals("lane")) {
            if(myEdge != null) {
                double len = Double.parseDouble(attributes.getValue("length"));
                myEdge.setLength(len);

                double speed = Double.parseDouble(attributes.getValue("speed"));
                myEdge.setSpeedLimit(speed);

                double time = len/speed;
                myEdge.setTime(time);
            }
        }

        if(qName.equals("connection")) {
            this.from = new String(attributes.getValue("from"));
            this.to = new String(attributes.getValue("to"));

            //System.out.println("Connection from " + this.from + " to " + this.to);
        }
    }

    /**
     * This method reads the end element of the XML tag
     * @param uri           URI
     * @param localName     Local name
     * @param qName         Name of the element
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals("node")) {
            this.myNodeList.add(myNode);
        } else if(qName.equals("edge")) {
            if(myEdge != null)
                this.myEdgeList.add(myEdge);
        } else if(qName.equals("connection")) {
            this.connections.addConnection(this.from, this.to);
        }
    }

    /**
     * This method extracts the characters and sets it into a variable.
     * @param ch        Character array
     * @param start     start position
     * @param length    Length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content = String.copyValueOf(ch, start, length).trim();
    }

    /**
     * This method returns the node list
     * @return  List of nodes
     */
    public List<MyNode> getMyNodeList() {
        return this.myNodeList;
    }

    /**
     * This method returns the edge list
     * @return  List of edges
     */
    public List<MyEdge> getMyEdgeList() {
        return this.myEdgeList;
    }

    /**
     * This method returns the connections
     * @return  map of connections
     */
    public Connections getConnections() {
        return this.connections;
    }
}
