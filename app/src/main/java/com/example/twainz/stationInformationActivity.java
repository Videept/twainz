package com.example.twainz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
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
        setTitle("Station View");

        layout = findViewById(R.id.trainListLayout);
        layout.setStretchAllColumns(true);
        //layout.setGravity(Gravity.CENTER);
        //layout.setScrollContainer(true);
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
        arrivalColumn.setText("Arrival Time         ");
        arrivalColumn.setTextSize(20);
        arrivalColumn.setWidth(220);

        TextView delayColumn = new TextView(this);
        delayColumn.setText("Delay");
        delayColumn.setTextSize(20);
        delayColumn.setGravity(1);
        //delayColumn.setPadding(0,0,40,0);
        //delayColumn.setWidth(40);

        TextView destinationColumn = new TextView(this);
        destinationColumn.setText("Destination");
        destinationColumn.setTextSize(20);
        //destinationColumn.setGravity(1);
        //destinationColumn.setWidth(40);

        columnTitles.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        columnTitles.addView(arrivalColumn);
        columnTitles.addView(destinationColumn);

        TableLayout l = findViewById(R.id.linearLayout);

        l.addView(columnTitles);
        l.setStretchAllColumns(true);

        trainFetcher tf = new trainFetcher();
        Vector<train> trains = tf.getTrains(station);
        for (train t : trains)
            addTrain(t);

    }

    private void addTrain(train t){
        TableLayout subTable = new TableLayout(this);
        //subTable.setMeasureWithLargestChildEnabled(true);
        //subTable.setColumnStretchable(2, true);
        /*GridLayout subTable = new GridLayout(this);
        subTable.setRowCount(2);
        subTable.setColumnCount(1);


        GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
        GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);

        GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                rowSpan, colspan);
*/

        subTable.setStretchAllColumns(true);
        //subTable.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
        p.setMargins(0, 50, 0, 50);
        //subTable.setLayoutParams(p);


        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(4, 0xFF000000); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            subTable.setBackgroundDrawable(border);
        } else {
            subTable.setBackground(border);
        }

        //subTable.setPadding(0,50,0,50);

        TableRow row1 = new TableRow(this);
        int color = (128 & 0xff) << 24 | (0 & 0xff) << 16 | (128 & 0xff) << 8 | (128 & 0xff);
        row1.setBackgroundColor(color);
        int textSize = 20;
        //row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


        TextView arrivalTime = new TextView(this);
        arrivalTime.setText(t.getArrivalTime());
        arrivalTime.setTextSize(textSize);
        arrivalTime.setWidth(40);
        //arrivalTime.setLayoutParams(new Layout);
        //arrivalTime.setGravity(1);



        TextView delay = new TextView(this);
        delay.setText("+ " + String.valueOf(t.getDelay()));
        if (t.getDelay() > 0)
            delay.setTextColor(Color.RED);
        else if (t.getDelay() < 0){
            delay.setText("- " + String.valueOf(-1 * t.getDelay()));
            delay.setTextColor(Color.GREEN);
        }


        delay.setTextSize(textSize);
        delay.setWidth(40);
        //delay.setGravity(1);



        TextView destination = new TextView(this);
        destination.setText("To " + t.getDestination());
        destination.setTextSize(textSize);
        destination.setWidth(200);
        //destination.setGravity(1);

        TextView type = new TextView(this);
        type.setText(t.getType());
        type.setTextSize(textSize);
        //type.setGravity(1);
        type.setWidth(200);


        row1.addView(arrivalTime);
        //row.addView(delay);
        row1.addView(type);

        //row1.setGravity(1);
        subTable.addView(row1);
        TableRow r = new TableRow(this);
        r.addView(delay);
        r.addView(destination);
        //r.setGravity(1);
        subTable.addView(r);


        layout.addView(subTable);

    }
}
