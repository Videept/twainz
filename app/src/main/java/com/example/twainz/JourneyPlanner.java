package com.example.twainz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Vector;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class JourneyPlanner extends Fragment {
    static private Vector<String> Message;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View journeyView = inflater.inflate(R.layout.journey_planner, container, false);

        TextView textDest = journeyView.findViewById(R.id.textView_dest);
        TextView textOrig = journeyView.findViewById(R.id.textView_orig);
        ImageButton buttonDir = journeyView.findViewById(R.id.changeDirButton);

        Animation shake = AnimationUtils.loadAnimation(journeyView.getContext(), R.anim.shake);
        buttonDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(shake);
                CharSequence temp = textDest.getText();
                textDest.setText(textOrig.getText());
                textOrig.setText(temp);

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

        return journeyView;
    }

            android.support.v4.app.FragmentManager childManager = getFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   //Begin the fragment change
            Fragment fragment = new Linerun();   //Initialise the new fragment

            Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
                fragmentData.putString(((Linerun) fragment).DATA_RECEIVE, String.valueOf(position));
                fragment.setArguments(fragmentData);

                fragmentTransaction.replace(R.id.constraintLayout2
                        , fragment);   //Replace listConstraintLayout with the new fragment
                fragmentTransaction.addToBackStack(null);   //Add the previous fragment to the stack so the back button works
                fragmentTransaction.commit();   //Complete the fragment transactio

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

}
