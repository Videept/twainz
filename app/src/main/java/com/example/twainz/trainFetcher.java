package com.example.twainz;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class trainFetcher {
    private static Vector<String> stationList;
    private Vector<train> trainList;
    private boolean fetched;

    public trainFetcher(){
        Log.d("Debug", "Constructing trainFetcher");
        stationList = new Vector<String>(); //Initialise the vector to contain the station names
        trainList = new Vector<train>(); //Initialise the vector containing the train data for later use
        fetched = false;

        this.getStationList();

    }

    //Getter for the station list vector
    public Vector<String> getStationList(){

        if (stationList.isEmpty() && !fetched){
            fetched = true;
            Thread networkThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    //Initialise the document build to build the document from the XML
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = null;
                    Document doc;

                    try {
                        db = dbf.newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }

                    try {

                        //Retrieve the XML from the URL
                        doc = db.parse(new URL("http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML_WithStationType?StationType=D").openStream());
                        NodeList stations = doc.getElementsByTagName("objStation"); //Convert the objStation objects into a NodeList

                        //Loop through each element of the NodeList
                        for (int i = 0; i < stations.getLength(); i++){
                            Node current = stations.item(i);

                            //If the current node is an element
                            if (current.getNodeType() == Node.ELEMENT_NODE){

                                //Add the station name to the station name vector
                                stationList.add(((Element)current)
                                        .getElementsByTagName("StationDesc")
                                        .item(0)
                                        .getTextContent());
                            }
                        }
                        //Horrible error "handling"
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }
                    Log.d("Debug", "Thread ran");
                }

            });
            networkThread.start();
            try {
                networkThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return stationList;
    }

    public Vector<train> getTrains(final String station){

        //Check if the station exists
        if (stationList.contains(station)) {

            if (trainList.isEmpty()) {

                Thread networkThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = null;
                        Document doc;

                        try {
                            db = dbf.newDocumentBuilder();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        }


                        try {
                            doc = db.parse(new URL("http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByNameXML?StationDesc=" + station).openStream());
                            NodeList trains = doc.getElementsByTagName("objStationData"); //Convert the objStation objects into a NodeList

                            //Loop through each element of the NodeList
                            for (int i = 0; i < trains.getLength(); i++) {
                                Node current = trains.item(i);

                                //If the current node is an element
                                if (current.getNodeType() == Node.ELEMENT_NODE) {
                                    String arrival = ((Element) current)
                                            .getElementsByTagName("Scharrival")
                                            .item(0)
                                            .getTextContent();

                                    String delay = ((Element) current)
                                            .getElementsByTagName("Late")
                                            .item(0)
                                            .getTextContent();

                                    String destination = ((Element) current)
                                            .getElementsByTagName("Destination")
                                            .item(0)
                                            .getTextContent();

                                    String type = ((Element) current)
                                            .getElementsByTagName("Traintype")
                                            .item(0)
                                            .getTextContent();

                                    train t = new train(0, arrival, Integer.valueOf(delay),
                                            destination, type);

                                    trainList.add(t);
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        }
                    }
                });
                networkThread.start();

                try {
                    networkThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return trainList;

        }
        //If the station doesnt exist return a null vector
        Log.e("Error", "Station name doesnt exist. Couldnt get data");
        return null;
    }



}


