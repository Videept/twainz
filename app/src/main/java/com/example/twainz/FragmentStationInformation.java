package com.example.twainz;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentStationInformation extends FragmentRoot implements SwipeRefreshLayout.OnRefreshListener{
    private View rootView;
    private TrainFetcher tf;
    private trainAdapter adapter;
    ArrayList<TrainFetcher.train> trainList;


    final static String DATA_RECEIVE = "data_receive";
    final static String INDEX_RECEIVE = "index_receive";
    final static String IS_JOURNEY_PLANNER = "is_journey_planner";
    final static String DEST_STATION = "dest_station";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_station_information, container, false);

        //Initialise the TrainFetcher. Object should already contain the string of the specified station
        tf = new TrainFetcher(getContext());

        //Using the TrainFetcher to retrieve the data for the station
        Bundle args = getArguments();
        tf.retrieveTrainsAtStation(args.getString(DATA_RECEIVE),args.getInt(INDEX_RECEIVE) );

        //Get the current time
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        //Display the current time
        TextView displayRefresh = rootView.findViewById(R.id.refreshView);
        displayRefresh.setText("Updated at " + time.format(calender.getTime()));

        trainList = new ArrayList<TrainFetcher.train>(tf.getTrains());
        // If fragment is open in Journey Planner
        if(args.getString(IS_JOURNEY_PLANNER).equals("true")) {
            // Call train filter function
            tf.filterByDestination(args.getString(DATA_RECEIVE), args.getString(DEST_STATION));
        }

        trainList = new ArrayList<>(tf.getTrains());

        adapter = new trainAdapter(rootView.getContext(), trainList);

        ListView lv =  rootView.findViewById(R.id.trainListView);

        lv.setAdapter(adapter);

        SwipeRefreshLayout refresh = rootView.findViewById(R.id.swipeRefresh);
        refresh.setOnRefreshListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new FragmentLinerun();   //Initialise the new fragment

                Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
                fragmentData.putString(((FragmentLinerun) fragment).DATA_RECEIVE, String.valueOf(position));

                launchFragment(fragment, "Line run", R.id.constraintLayout2, fragmentData);

            }
        });


        return rootView;

    }

    @Override
    public void onRefresh() {
        Bundle args = getArguments();
        tf.retrieveTrainsAtStation(args.getString(DATA_RECEIVE),args.getInt(INDEX_RECEIVE) );

        // If fragment is open in Journey Planner
        if(args.getString(IS_JOURNEY_PLANNER).equals("true")) {
            // Call train filter function
            tf.filterByDestination(args.getString(DATA_RECEIVE), args.getString(DEST_STATION));
        }

        //Clear the array list and update the data set.
        trainList.clear();
        trainList.addAll(tf.getTrains());
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

class trainAdapter extends ArrayAdapter<TrainFetcher.train> {

    public trainAdapter(Context context, ArrayList<TrainFetcher.train> trains) {
        super(context, 0, trains);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrainFetcher.train t = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_station_information_row, parent, false);
        }

        TextView tempView = convertView.findViewById(R.id.trainTypeView);
        tempView.setText(t.getType());
        tempView.setTag(position); //Tag the button with it's position in the list

        tempView = convertView.findViewById(R.id.trainDestinationView);
        tempView.setText(t.getDestination());
        tempView.setTag(position); //Tag the button with it's position in the list

        tempView = convertView.findViewById(R.id.arrivalTimeView);
        tempView.setText(String.valueOf(t.getDueTime()));
        tempView.setTag(position); //Tag the button with it's position in the list

        return convertView;

    }

}