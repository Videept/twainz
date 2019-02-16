package com.example.twainz;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

public class stationInformationActivity extends Fragment {
    private TableLayout layout;
    int textSize = 20; //Variable to define text size for displaying the train data
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_station_information, container, false);

        //Initialise the layout global
        layout = rootView.findViewById(R.id.trainListLayout);
        layout.setStretchAllColumns(true);

        //Initialise the trainFetcher. Object should already contain the string of the specified station
        trainFetcher tf = new trainFetcher();

        //Display the station name
        TextView stationDisplay = rootView.findViewById(R.id.stationView);
        stationDisplay.setText(tf.getStationQuery());

        //Get the current time
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        //Display the current time
        TextView displayRefresh = rootView.findViewById(R.id.refreshView);
        displayRefresh.setText("Updated at " + time.format(calender.getTime()));


        //Declaring a tablerow to contain the textviews labelling the columns
        TableRow columnTitles = new TableRow(rootView.getContext());

        //Creating a textview to display the label for the arrival time column
        TextView arrivalColumn = new TextView(rootView.getContext());
        arrivalColumn.setText("Arrival Time         ");
        arrivalColumn.setTextSize(20);
        arrivalColumn.setWidth(220);

        //Creating a textview to display the label for the destination column
        TextView destinationColumn = new TextView(rootView.getContext());
        destinationColumn.setText("Destination");
        destinationColumn.setTextSize(20);

        //Adding the textviews to the row
        columnTitles.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        columnTitles.addView(arrivalColumn);
        columnTitles.addView(destinationColumn);

        //Adding the row to the layout
        TableLayout l = rootView.findViewById(R.id.linearLayout);
        l.addView(columnTitles);
        l.setStretchAllColumns(true);

        //Using the trainFetcher to retrieve the data for the station
        Vector<train> trains = tf.getTrains();

        //For each train add a new UI element
        for (train t : trains)
            addTrain(t);



        return rootView;

    }

    private void addTrain(train t){
        TableLayout subTable = new TableLayout(rootView.getContext());
        subTable.setStretchAllColumns(true);

        //Adding a border to the subTables
        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(4, 0xFF000000); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            subTable.setBackgroundDrawable(border);
        } else {
            subTable.setBackground(border);
        }

        //Declaring the upper row and changing the color
        TableRow row2 = new TableRow(rootView.getContext());
        int color = (128 & 0xff) << 24 | (0 & 0xff) << 16 | (128 & 0xff) << 8 | (128 & 0xff);
        row2.setBackgroundColor(color);

        //TextView for the arrival time
        TextView arrivalTime = new TextView(rootView.getContext());
        arrivalTime.setText(t.getArrivalTime());
        arrivalTime.setTextSize(textSize);
        arrivalTime.setWidth(40);


        //TextView for the delay duration
        TextView delay = new TextView(rootView.getContext());
        delay.setText("+ " + String.valueOf(t.getDelay()));
        delay.setTextSize(textSize);
        delay.setWidth(40);

        //Set the delay TextView font color based on the delay
        if (t.getDelay() > 0)
            delay.setTextColor(Color.RED);
        else if (t.getDelay() < 0){
            delay.setText("- " + String.valueOf(-1 * t.getDelay()));
            delay.setTextColor(Color.GREEN);
        }

        //TextView for the train destination
        TextView destination = new TextView(rootView.getContext());
        destination.setText("To " + t.getDestination());
        destination.setTextSize(textSize);
        destination.setWidth(200);

        //TextView for the type of train
        TextView type = new TextView(rootView.getContext());
        type.setText(t.getType());
        type.setTextSize(textSize);
        type.setWidth(200);

        //Creating a new row and adding the delay and train destination
        TableRow row1 = new TableRow(rootView.getContext());
        row1.addView(delay);
        row1.addView(destination);

        //Adding the arrival time and train type to the second row
        row2.addView(arrivalTime);
        row2.addView(type);

        //Add the rows to the subtable
        subTable.addView(row2);
        subTable.addView(row1);

        //Add the subtable to the parent layout
        layout.addView(subTable);

    }
}
