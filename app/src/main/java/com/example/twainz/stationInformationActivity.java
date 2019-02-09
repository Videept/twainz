package com.example.twainz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Vector;

public class stationInformationActivity extends AppCompatActivity {
    private TableLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_information);

        layout = findViewById(R.id.trainListLayout);
        Intent cause = getIntent();

        String station = cause.getStringExtra("STATION");

        trainFetcher tf = new trainFetcher();
        Vector<train> trains = tf.getTrains(station);

        for (train t : trains)
            addTrain(t);


    }

    private void addTrain(train t){
        TextView tv = new TextView(this);
        tv.setText(t.getArrivalTime());

        layout.addView(tv);
    }
}
