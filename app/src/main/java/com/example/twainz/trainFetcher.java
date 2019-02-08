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
    private Vector<String> stationList;

    public trainFetcher(){
        Log.d("Debug", "Constructing trainFetcher");

        //Initialise the document build to build the document from the XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document doc;
        stationList = new Vector<String>(); //Initialise the vector to contain the station names

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
                    stationList.add(((Element)current).getElementsByTagName("StationDesc").item(0).getTextContent());
                }
            }
        //Horrible error "handling"
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


    }
    //Getter for the station list vector
    public Vector<String> getStationList(){
        return stationList;
    }


}


