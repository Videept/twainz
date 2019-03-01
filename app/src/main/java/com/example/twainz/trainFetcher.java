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
    static private Vector<train> trainList;
    private boolean fetched;
    static private String stationQuery;

    public trainFetcher(){
        stationList = new Vector<String>(); //Initialise the vector to contain the station names
        trainList = new Vector<train>(); //Initialise the vector containing the train data for later use

        this.getStationList();

    }
    void setStationQuery(String stationQuery_){
        stationQuery = new String(stationQuery_);
    }
    String getStationQuery(){
        return stationQuery;
    }

    public int getLineRun(final Vector<String> stations, final String trainID, final String date, final String currentStation){
        int location = 0;

        if (stations.isEmpty()){
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
                        String parse_date = date.replace(' ', '+');
                        String urlrequest = "http://api.irishrail.ie/realtime/realtime.asmx/getTrainMovementsXML?TrainId=" +
                                trainID.substring(0, trainID.length() -1) + "&" + "TrainDate=" + parse_date;
                        //Retrieve the XML from the URL
                        doc = db.parse(new URL(urlrequest).openStream());
                        NodeList s = doc.getElementsByTagName("objTrainMovements"); //Convert the objStation objects into a NodeList

                        //Loop through each element of the NodeList
                        for (int i = 0; i < s.getLength(); i++){
                            Node current = s.item(i);

                            //If the current node is an element
                            if (current.getNodeType() == Node.ELEMENT_NODE){

                                //Add the station name to the station name vector
                                stations.add(((Element)current)
                                        .getElementsByTagName("LocationFullName")
                                        .item(0)
                                        .getTextContent());

                            }
                        }
                        //Horrible error "handling"
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("d_tag", "caught error" + e.toString());
                    } catch (SAXException e) {
                        e.printStackTrace();
                        Log.d("d_tag", "caught error" + e.toString());

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
        for (String s : stations) {
            location++;
            if (s.equals(currentStation))
                return location;

        }
        return location;

    }

    //Getter for the station list vector
    public Vector<String> getStationList(){

        if (stationList.isEmpty()){

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

    public Vector<train> getTrains(){

        //Check if the station exists
        if (stationList.contains(stationQuery)) {

            if (!trainList.isEmpty())
                trainList.clear();

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
                            doc = db.parse(new URL("http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByNameXML?StationDesc=" + stationQuery + "&NumMins=90").openStream());
                            NodeList trains = doc.getElementsByTagName("objStationData"); //Convert the objStation objects into a NodeList

                            //Loop through each element of the NodeList
                            for (int i = 0; i < trains.getLength(); i++) {
                                Node current = trains.item(i);

                                //If the current node is an element
                                if (current.getNodeType() == Node.ELEMENT_NODE) {
                                    train t = new train();

                                    String buffer = ((Element) current)
                                            .getElementsByTagName("Schdepart")
                                            .item(0)
                                            .getTextContent();

                                    if (buffer.equals("00:00")){
                                        buffer = ((Element) current)
                                                .getElementsByTagName("Scharrival")
                                                .item(0)
                                                .getTextContent();
                                    }

                                    t.setArrivalTime(buffer);

                                    buffer = ((Element) current)
                                            .getElementsByTagName("Late")
                                            .item(0)
                                            .getTextContent();

                                    t.setDelay(Integer.valueOf(buffer));

                                    buffer = ((Element) current)
                                            .getElementsByTagName("Destination")
                                            .item(0)
                                            .getTextContent();

                                    t.setDestination(buffer);

                                    buffer = ((Element) current)
                                            .getElementsByTagName("Traintype")
                                            .item(0)
                                            .getTextContent();

                                    t.setType(buffer);

                                    buffer = ((Element) current)
                                            .getElementsByTagName("Traincode")
                                            .item(0)
                                            .getTextContent();
                                    t.setId(buffer);

                                    buffer = ((Element) current)
                                            .getElementsByTagName("Traindate")
                                            .item(0)
                                            .getTextContent();

                                    t.setDate(buffer);


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

            return trainList;

        }
        //If the station doesnt exist return a null vector
        Log.e("Error", "Station name doesnt exist. Couldnt get data");
        return null;
    }



}




