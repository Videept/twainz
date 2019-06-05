package com.example.twainz;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TrainFetcher {
    private static Vector<String> stationList;
    static private Vector<train> trainList;
    static private Vector<station> stations;
    static private String stationQuery;
    private Context context;
    static int stationQueryCode;

    public TrainFetcher(Context c){
        this.context = c;

        this.getStationList();
    }

    String getStationQuery(){
        return stationQuery;
    }

    public Vector<train> getTrains(){
        return trainList;
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
        int curr_hour = time_date.getHours();
        int curr_min = time_date.getMinutes();
        for (int i = 0; i < stations.size();i++) {

            if (compareTimes(stations.get(i).arrival_time, curr_hour,curr_min)){
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

        if (stationList == null || stationList.isEmpty()) {
            stationList = new Vector<>();

            stations = readStationFromXML();

            for (station s : stations)
                stationList.add(s.stationName);
        }
        return stationList;
    }

    public Vector<train> retrieveTrainsAtStation(String station, int current_index){
        stationQuery = station;
        //Check if the station exists
        if (stationList.contains(station)) {
            trainList = new Vector<>();

            Thread networkThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Document doc = null;

                    try {
                        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                                new URL("http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML?StationCode=" +
                                        stations.get(current_index).stationCode)
                                        .openStream());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }

                    if (doc != null) {
                        NodeList trains = doc.getElementsByTagName("objStationData"); //Convert the objStation objects into a NodeList

                        //Loop through each element of the NodeList
                        for (int i = 0; i < trains.getLength(); i++) {
                            Node current = trains.item(i);

                            //If the current node is an element
                            if (current.getNodeType() == Node.ELEMENT_NODE) {
                                train t = new train();

                                //If the "Schdepart" is 00:00 try the Scharrival instead
                                t.arrivalTime = ((Element) current).getElementsByTagName("Schdepart").item(0).getTextContent().equals("00:00") ?
                                        ((Element) current).getElementsByTagName("Scharrival").item(0).getTextContent() :
                                        ((Element) current).getElementsByTagName("Schdepart").item(0).getTextContent();
                                t.dueTime = Byte.valueOf(((Element) current).getElementsByTagName("Duein").item(0).getTextContent());
                                t.delay = Integer.valueOf(((Element) current).getElementsByTagName("Late").item(0).getTextContent());
                                t.destination = ((Element) current).getElementsByTagName("Destination").item(0).getTextContent();
                                t.type = ((Element) current).getElementsByTagName("Traintype").item(0).getTextContent();
                                t.id = ((Element) current).getElementsByTagName("Traincode").item(0).getTextContent();
                                t.date = ((Element) current).getElementsByTagName("Traindate").item(0).getTextContent();

                                trainList.add(t);
                            }
                        }
                    }


                }
            });
            networkThread.start();

            try {
                networkThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //sort according to due time
            Collections.sort(trainList, new Comparator<train>() {

                @Override
                public int compare(train o1, train o2) {
                    String train1 = o1.getDueTime();
                    String train2 = o2.getDueTime();
                    //Split the string according to whitespace
                    String[] t1 = train1.split(" ",0);
                    String[] t2 = train2.split(" ",0);
                    if (train1.charAt(2) == 'h') {
                        //train1 in hour and train2 in mins then duetime of train1 is greater than train2
                        //train1 and train2 both in hour then check number of hours
                        if (train2.charAt(2) != 'h' || Integer.valueOf(t1[0]) > Integer.valueOf(t2[0])) {
                            return 1;
                        }
                        //train1 and train2 both in equal hours then check minutes
                        else if (Integer.valueOf(t1[0]) == Integer.valueOf(t2[0]) && Integer.valueOf(t1[2]) > Integer.valueOf(t2[2])) {
                            return 1;
                        }
                        else {
                            return -1;
                        }
                    }
                    else{
                        if (train2.charAt(2) == 'h' || Integer.valueOf(t1[0]) < Integer.valueOf(t2[0])) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                }
            });

            return trainList;

        }
        //If the station doesnt exist return a null vector
        Log.e("Error", "Station name doesnt exist. Couldnt get data");
        return null;
    }

    // Filters out trains at current station that don't stop at another specified station
    void filterByDestination(String origin, String destination){
        // Empty vector for storing trains to be filtered out (removed)
        Vector<train> deleteTrains = new Vector<>();
        // For each train at current station
        for (train tr : trainList) {
            // Empty vector for storing all stations on route
            Vector<LinerunStation> tempStations = new Vector<>();
            // Populate vector with stations
            getLineRun(tempStations, tr.id, tr.date, origin);
            // Ensure iteration direction is against train direction
            if(!tempStations.get(0).location.equals(tr.destination)){ Collections.reverse(tempStations); }
            // Check each station on route
            for (LinerunStation st : tempStations){
                // If destination is after (or at) origin station
                if(destination.equals(st.location)){ break; }
                // If destination is behind origin station
                if(origin.equals(st.location)){ deleteTrains.add(tr); break; }
            }
        }
        // Remove filtered trains
        trainList.removeAll(deleteTrains);
    }
    public Vector<station> readStationFromXML(){

        InputStream stationFile = context.getResources().openRawResource(context.getResources().getIdentifier("station_list",
                        "raw", context.getPackageName()));
        Vector<station> sList = new Vector<>();

        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stationFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        NodeList nodeList = document.getElementsByTagName("objStation");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                station s = new station();

                s.stationName = ((Element) node).getElementsByTagName("StationDesc").item(0).getTextContent();
                s.stationCode = ((Element) node).getElementsByTagName("StationCode").item(0).getTextContent();
                s.latitude = Double.valueOf(((Element) node).getElementsByTagName("StationLatitude").item(0).getTextContent());
                s.longitude = Double.valueOf(((Element) node).getElementsByTagName("StationLongitude").item(0).getTextContent());
                s.stationId = Integer.valueOf(((Element) node).getElementsByTagName("StationId").item(0).getTextContent());

                sList.add(s);
            }

        }
        return sList;
    }

    class station{
        protected String stationName;
        protected String stationCode;
        protected double latitude;
        protected double longitude;
        protected int stationId;

        public String getStationName(){
            return stationName;
        }
        public String getStationCode(){
            return stationCode;
        }
        public double getLatitude(){
            return latitude;
        }
        public double getLongitude(){
            return longitude;
        }
        public int getStationId(){
            return stationId;
        }
    }

    public class LinerunStation {
        public String location;
        public int[] arrival_time = new int[2];      //arrival_time[0] = hours, arrival_time[1] = mins
        //having this as an int instead of a string reduces the amount on string-int conversions
        public Integer delay;
        public boolean visited ;  //set this boolean to true if we have passed by the station

        public LinerunStation(){}

    }


    public class train implements Comparable<train>{
        protected String arrivalTime;
        protected int delay;
        protected String destination;
        protected String type;
        protected String id;
        protected String date;
        protected byte dueTime;

        public String getArrivalTime(){
            return arrivalTime;
        }
        public String getDestination(){
            return destination;
        }
        public int getDelay(){
            return delay;
        }
        public String getType(){
            return type;
        }
        public String getId(){ return id; }
        public String getDate(){ return date; }
        public String getDueTime(){
            int hours = dueTime/60;
            int minutes = dueTime%60;
            String s1 = Integer.toString(hours);
            String s2 = Integer.toString(minutes);
            String s3 = " hr ";
            String s5 = " mins ";
            String s4,s6;
            // if due time greater than 1 hour
            if(hours!=0) {
                s4 = s1.concat(s3);
                s6 = s4.concat(s2);
                String finalstring1 = s6.concat(s5);
                return finalstring1;
            }
            else{
                String finalstring2 = s2.concat(s5);
                return finalstring2;
            }
        }

        @Override
        public int compareTo(train o) {

            String total = "";
            String total_i = "";
            char[] charray = o.arrivalTime.toCharArray();
            if(charray[0]  == '0' && charray[1] == '0') {
                charray[0] = '2';
                charray[1] = '5';
                }
            total += charray[0] + charray[1] + charray[3] + charray[4];
            char[] charray_i = arrivalTime.toCharArray();
            if(charray_i[0]  == '0' && charray[1] == '0') {
                charray_i[0] = '2';
                charray_i[1] = '5';
            }
            total_i += charray_i[0] + charray_i[1] + charray_i[3] + charray_i[4];

            return total_i.compareTo(total);
        }
    }


}




