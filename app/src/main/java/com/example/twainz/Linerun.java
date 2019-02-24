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

        //testing variables:
        String  current_station_name = "placeholder"; // pass this from the stationinformationactivity onclick
        String[] test0 =new String[]{"station0","station1","station2", "station3", "stations4", "station5", "station6",
                "station7","station8","station9", "station10", "stations11", "station12", "station13",
                "station14","station15","station16", "station17","station18","station19", "station20"};
        String[] test1 = new String[]{"station0","station1","station2", "station3", "stations4", "station5", "station6"};

        Vector<String> stations_on_route = new Vector<String>(Arrays.asList(test0));
        //get this from wherever - some function to return stations on the route in order start -> end

        int current_station = 17; //stations_on_route.indexOf(current_station_name);  //current_station on the route

        drawLinerun(stations_on_route, current_station );

        return rootView;
    }

    private void drawLinerun(Vector<String> stations, int current_station){
        //takes in the number of stations needed to show on the linerun (size),
        //and the station names themselves.
        //adds n stations to the station_LL with a height of box_height_px
        //draws the progress bar on the left indicating which stations have been visited
        //TODO: add the delay times to the stations that havent been visited yet

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        height_dp = Math.round(displayMetrics.heightPixels / displayMetrics.density);
        width_dp = Math.round(displayMetrics.widthPixels / displayMetrics.density);
        int margins = 3;
        int station_count = stations.size();
        int width_LL_px = Math.round((width_dp - 45)*displayMetrics.density);                                               //45 dp is to account for the bar on the left
        int box_height_px = Math.round((height_dp - 208 - margins*station_count)*displayMetrics.density/(station_count));   //208 dp is for the other views in the parent that take up space

        LinearLayout stationLL = rootView.findViewById(R.id.station_LL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width_LL_px,box_height_px + margins );
        layoutParams.topMargin = margins;
        layoutParams.bottomMargin =margins;
        int c = getResources().getColor(R.color.colorPrimary);
        for(int i = 0; i < station_count; i++) {
            TextView tv = new TextView(this.getContext());
            tv.setLayoutParams(layoutParams);
            tv.setText(stations.get(i));
            tv.setTextSize(14);                 // TODO : make this size adaptive more stops?
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setPadding(5, 0, 0, 0);
            tv.setBackgroundColor(c);
            if(i == current_station)tv.setTextColor(getResources().getColor(R.color.soft_white));

            stationLL.addView(tv);

        }
        DrawProgressBar(current_station, box_height_px, margins);

    }

    private void DrawProgressBar(int current, int box_h_px, int margins){
        //draws the progress bar along the side
        ConstraintLayout layout = rootView.findViewById(R.id.bar_foreground);
        layout.setMaxHeight(box_h_px*current + 3*margins*current);
    }

}
