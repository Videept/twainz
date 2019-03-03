package com.example.twainz;

import android.annotation.TargetApi;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
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

    public int getLineRun(final Vector<LinerunStation> stations, final String trainID, final String date, final String currentStation){
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
                                //create a new LinerunStation to add to the station vector
                                LinerunStation new_station = new LinerunStation();

                                new_station.location =
                                        ((Element)current)
                                        .getElementsByTagName("LocationFullName")
                                        .item(0)
                                        .getTextContent();
                                if( i == 0){
                                    new_station.delay = 0;
                                    String raw =
                                            ((Element) current)
                                            .getElementsByTagName("ExpectedDeparture")
                                            .item(0)
                                            .getTextContent();
                                    new_station.arrival_time[0] = Integer.valueOf(raw.substring(0, 2));
                                    new_station.arrival_time[1] = Integer.valueOf(raw.substring(3, 5));
                                }else {
                                    String raw_scheduledArrival =
                                            ((Element) current)   // in the form 01:34:67
                                                    .getElementsByTagName("ScheduledArrival")
                                                    .item(0)
                                                    .getTextContent();
                                    int h_sch = Integer.valueOf(raw_scheduledArrival.substring(0, 2));
                                    int m_sch = Integer.valueOf(raw_scheduledArrival.substring(3, 5));
                                    String raw_expectedArrival =
                                            ((Element) current)    // in the form 01:34:67
                                                    .getElementsByTagName("ExpectedArrival")
                                                    .item(0)
                                                    .getTextContent();
                                    int h_exp = Integer.valueOf(raw_expectedArrival.substring(0, 2));
                                    int m_exp = Integer.valueOf(raw_expectedArrival.substring(3, 5));

                                    new_station.delay = 60 *h_sch + m_sch - 60 * h_exp - m_exp;

                                    new_station.arrival_time[0] = h_exp;
                                    new_station.arrival_time[1] = m_exp;
                                }
                                //assume we haven't visited station yet
                                new_station.visited = false;
                                stations.add(new_station);
                            }
                        }
                        //Horrible error "handling" lol
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
        Date time_date = new Date();
        int curr_hour = time_date.getHours(); //time_date.toInstant().atZone(ZoneId.systemDefault()).getHour();
        int curr_min = time_date.getMinutes(); //time_date.toInstant().atZone(ZoneId.systemDefault()).getMinute();
        for (int i = 0; i < stations.size();i++) {

            if (compareTimes(stations.get(i).arrival_time, curr_hour,curr_min)){
                Log.d("d_tag", currentStation +"  "+String.valueOf(stations.get(i).location));
                return location;
            }
            location++;
            stations.get(i).visited = true;
        }
        return location;

    }

    private boolean compareTimes(int[] arrival_time, int curr_hour, int curr_min){

        if(curr_hour - arrival_time[0] < 23){
            //standard case return true if the station has not yet been visited
            return (curr_hour *60 + curr_min < 60*arrival_time[0] + arrival_time[1]);
        }
        return (curr_hour *60 + curr_min < 60*(24+arrival_time[0]) + arrival_time[1]);


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




