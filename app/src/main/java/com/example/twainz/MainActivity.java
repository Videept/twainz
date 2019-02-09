package com.example.twainz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //This function handles UI events
    public void onClick(View v) {

        //Modify this switch statement to test new features
        switch (v.getId()) {

            case R.id.button:
                Log.d("Debug", "Button 1 pressed");
                trainFetcher tf = new trainFetcher();
                if (!tf.getStationList().isEmpty()) {
                    for (String station : tf.getStationList()) {
                        Log.d("Debug", station); //Print the station names for testing purposes
                    }
                }
                else
                    Log.d("Debug", "Vector empty");

/*                //Network operations cannot occur on the main thread
                Thread networkThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        trainFetcher tf = new trainFetcher();

                        //Ensure the vector is not empty before creating an iterator
                        if (!tf.getStationList().isEmpty()) {
                            for (String station : tf.getStationList()) {
                                Log.d("Debug", station); //Print the station names for testing purposes
                            }
                        }
                        Vector<train> trains = tf.getTrains("Dublin Pearse");

                        if (!trains.isEmpty()) {
                            for (train t : trains)
                                t.printData();
                        }
                    }
                });
                networkThread.start();
                */

                Intent informationActivity = new Intent(this, stationInformationActivity.class);
                informationActivity.putExtra("STATION", "Dublin Pearse");
                startActivity(informationActivity);



                break;

            case R.id.button2:
                //Add code here
                Log.d("Debug", "Button 2 pressed");
                break;

            case R.id.button3:
                //Add code here
                Log.d("Debug", "Button 3 pressed");
                break;

            case R.id.button4:
                //Add code here
                Log.d("Debug", "Button 4 pressed");
                break;

            default:
                throw new RuntimeException("Unknown ID");
        }
    }

}
