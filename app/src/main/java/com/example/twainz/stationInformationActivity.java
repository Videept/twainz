package com.example.twainz;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class stationInformationActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View rootView;
    private trainFetcher tf;
    private Vector<train> currentTrains;
    private trainAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.station_view_fragment, container, false);

        //Initialise the trainFetcher. Object should already contain the string of the specified station
        tf = new trainFetcher();

        //Display the station name
        TextView stationDisplay = rootView.findViewById(R.id.stationView);
        stationDisplay.setText(tf.getStationQuery());

        //Get the current time
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        //Display the current time
        TextView displayRefresh = rootView.findViewById(R.id.refreshView);
        displayRefresh.setText("Updated at " + time.format(calender.getTime()));


        //Using the trainFetcher to retrieve the data for the station
        currentTrains = tf.getTrains();

        ArrayList<train> list = new ArrayList<train>(currentTrains);

        adapter = new trainAdapter(rootView.getContext(), list);

        ListView lv =  rootView.findViewById(R.id.trainListView);

        lv.setAdapter(adapter);

        SwipeRefreshLayout refresh = rootView.findViewById(R.id.swipeRefresh);
        refresh.setOnRefreshListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trainFetcher tf = new trainFetcher();
                train t = tf.getTrains().get(Integer.valueOf(position));


                android.support.v4.app.FragmentManager childManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   //Begin the fragment change
                Fragment fragment = new Linerun();   //Initialise the new fragment


                Bundle test = new Bundle();
                test.putString(((Linerun) fragment).DATA_RECEIVE, String.valueOf(position));
                fragment.setArguments(test);

                fragmentTransaction.replace(R.id.listConstraintLayout, fragment);   //Replace listConstraintLayout with the new fragment
                fragmentTransaction.addToBackStack(null);   //Add the previous fragment to the stack so the back button works
                fragmentTransaction.commit();   //Complete the fragment transaction
            }
        });


        return rootView;

    }

    @Override
    public void onRefresh() {
        currentTrains = tf.getTrains();
        adapter.notifyDataSetChanged();

        //Get the current time
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        //Display the current time
        TextView displayRefresh = rootView.findViewById(R.id.refreshView);
        displayRefresh.setText("Updated at " + time.format(calender.getTime()));

        SwipeRefreshLayout refresh = rootView.findViewById(R.id.swipeRefresh);
        refresh.setRefreshing(false);
    }
}

class trainAdapter extends ArrayAdapter<train> {

    public trainAdapter(Context context, ArrayList<train> trains) {
        super(context, 0, trains);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        train t = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.station_information_row, parent, false);
        }

        TextView tempView = convertView.findViewById(R.id.trainTypeView);
        tempView.setText(t.getType());
        tempView.setTag(position); //Tag the button with it's position in the list

        tempView = convertView.findViewById(R.id.trainDestinationView);
        tempView.setText(t.getDestination());
        tempView.setTag(position); //Tag the button with it's position in the list

        tempView = convertView.findViewById(R.id.arrivalTimeView);
        tempView.setText(t.getArrivalTime());
        tempView.setTag(position); //Tag the button with it's position in the list




        return convertView;

    }

}