package com.example.twainz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

public class stationInformationActivity extends AppCompatActivity {
    private TableLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_information);

        layout = findViewById(R.id.trainListLayout);
        //layout.setGravity(Gravity.CENTER);
        Intent cause = getIntent();

        String station = cause.getStringExtra("STATION");

        TextView stationDisplay = findViewById(R.id.stationView);
        stationDisplay.setText(station);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        TextView displayRefresh = findViewById(R.id.refreshView);
        displayRefresh.setText("Updated at " + mdformat.format(calender.getTime()));

        TableRow columnTitles = new TableRow(this);

        TextView arrivalColumn = new TextView(this);
        arrivalColumn.setText("Arrival Time");

        TextView delayColumn = new TextView(this);
        delayColumn.setText("Delay");

        TextView destinationColumn = new TextView(this);
        destinationColumn.setText("Destination");

        columnTitles.addView(arrivalColumn);
        columnTitles.addView(delayColumn);
        columnTitles.addView(destinationColumn);

        layout.addView(columnTitles);


        trainFetcher tf = new trainFetcher();
        Vector<train> trains = tf.getTrains(station);

        for (train t : trains)
            addTrain(t);

    }

    private void addTrain(train t){
        TableRow row = new TableRow(this);

        TextView arrivalTime = new TextView(this);
        arrivalTime.setText(t.getArrivalTime());

        TextView delay = new TextView(this);
        delay.setText(String.valueOf(t.getDelay()));

        TextView destination = new TextView(this);
        destination.setText(t.getDestination());


        row.addView(arrivalTime);
        row.addView(delay);
        row.addView(destination);


        layout.addView(row);
    }
}
