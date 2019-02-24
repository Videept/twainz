package com.example.twainz;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Vector;

public class Linerun extends Fragment {
    private View rootView;
    private static int height_dp;
    private static int width_dp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_linerun, container, false);

        String  current_station_name = "placeholder"; // pass this from the stationinformationactivity onclick

        Vector<String> stations_on_route = new Vector<String>(Arrays.asList(new String[]{"station0",
                "station1","station2", "station3", "stations4", "station5", "station6"}));
        //get this from wherever - some function to return stations on the route in order start -> end

        int current_station = 4; //stations_on_route.indexOf(current_station_name);  //current_station on the route

        drawLine(stations_on_route, current_station );

        return rootView;
    }

    private void drawLine(Vector<String> stations, int current_station){
        //takes in the number of stations needed to show on the linerun (size),
        //and the station names themselves.
        //adds n stations to the station_LL with a height of box_height
        //TODO: add the delay times to these stations

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        height_dp = Math.round(displayMetrics.heightPixels / displayMetrics.density);
        width_dp = Math.round(displayMetrics.widthPixels / displayMetrics.density);

        int station_count = stations.size();
        int width_LL_px = Math.round((width_dp - 45)*displayMetrics.density);                       //45 dp is to account for the bar on the left
        int box_height_px = Math.round((height_dp - 210)*displayMetrics.density/(station_count));   //210 dp is for that massive TWAINZ banner from the parent view(???)
                                                                                                    //TODO : confirm this lines up nicely on other phones (it should though) and refine it
        LinearLayout stationLL = rootView.findViewById(R.id.station_LL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width_LL_px,box_height_px );
        layoutParams.topMargin = 3;
        layoutParams.bottomMargin = 3;
        int c = getResources().getColor(R.color.colorPrimary);
        for(String s : stations) {
            TextView tv = new TextView(this.getContext());
            tv.setLayoutParams(layoutParams);
            tv.setText(s);
            tv.setTextSize(14);                 // TODO : make this size adaptive for having >12 stops
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setPadding(5, 0, 0, 0);
            tv.setBackgroundColor(c);

            stationLL.addView(tv);

        }
        DrawProgressBar(current_station, box_height_px);

    }

    private void DrawProgressBar(int current, int box_h_px){
        //draws the progress bar along the side
        ConstraintLayout layout = rootView.findViewById(R.id.bar_foreground);
        layout.setMaxHeight(box_h_px*current);
    }

}
