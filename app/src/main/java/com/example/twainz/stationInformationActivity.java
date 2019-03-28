package com.example.twainz;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
    private trainAdapter adapter;
    ArrayList<trainFetcher.train> trainList;


    final static String DATA_RECEIVE = "data_receive";
    final static String INDEX_RECEIVE = "index_receive";
    final static String IS_JOURNEY_PLANNER = "is_journey_planner";
    final static String DEST_STATION = "dest_station";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.station_view_fragment, container, false);

        //Initialise the trainFetcher. Object should already contain the string of the specified station
        tf = new trainFetcher(getContext());

        //Using the trainFetcher to retrieve the data for the station
        Bundle args = getArguments();
        tf.retrieveTrainsAtStation(args.getString(DATA_RECEIVE),args.getInt(INDEX_RECEIVE) );

        ((MainActivity)getActivity()).setActionBarTitle(args.getString(DATA_RECEIVE));

        //Get the current time
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        //Display the current time
        TextView displayRefresh = rootView.findViewById(R.id.refreshView);
        displayRefresh.setText("Updated at " + time.format(calender.getTime()));

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

        lv.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                android.support.v4.app.FragmentManager childManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   //Begin the fragment change
                Fragment fragment = new Linerun();   //Initialise the new fragment

                Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
                fragmentData.putString(((Linerun) fragment).DATA_RECEIVE, String.valueOf(position));
                fragment.setArguments(fragmentData);

                fragmentTransaction.replace(R.id.constraintLayout2
                        , fragment);   //Replace listConstraintLayout with the new fragment
                fragmentTransaction.addToBackStack(null);   //Add the previous fragment to the stack so the back button works
                fragmentTransaction.commit();   //Complete the fragment transaction
        });

        return rootView;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isAdded()) {
            Bundle args = getArguments();
            ((MainActivity)getActivity()).setActionBarTitle(args.getString(DATA_RECEIVE));
        }
    }

    @Override
    public void onResume(){
        Bundle args = getArguments();
        ((MainActivity)getActivity()).setActionBarTitle(args.getString(DATA_RECEIVE));
        super.onResume();
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

class trainAdapter extends ArrayAdapter<trainFetcher.train> {

    public trainAdapter(Context context, ArrayList<trainFetcher.train> trains) {
        super(context, 0, trains);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        trainFetcher.train t = getItem(position);

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
        tempView.setText(String.valueOf(t.getDueTime()));
        tempView.setTag(position); //Tag the button with it's position in the list

        return convertView;

    }

}