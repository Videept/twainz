package com.example.twainz;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class Favourites extends Fragment {

    private View rootView;
    public static Database mDatabase;
    public static ArrayList<String> favourites_alist;
    //set true if the favourites list needs to be reloaded by another class
    // cant make this an array of ints since we need to reference the stations by station name


    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.favourites_list, container, false);
        mDatabase = new Database(rootView.getContext()); //object of Database class
        ListView mListView = rootView.findViewById(R.id.favourites_list);
        trainFetcher tf = new trainFetcher(rootView.getContext());
        //TODO: the list of all stations is just initialised for testing purposes
        //String test = tf.getStationList().get(0);

        //mDatabase.deleteData(test);
        //mDatabase.insertData(test);
        favourites_alist = mDatabase.displayfavourites();

        ///we dont need to change the adapter here since we need to use tha custom adapter from the favouritesListAdapter class as below
        //ArrayAdapter arrayAdapter=new ArrayAdapter(rootView.getContext(),android.R.layout.simple_list_item_1,array_list);
        //mListView.setAdapter(arrayAdapter);
        //ArrayList<String> list = /* new ArrayList<>(tf.getStationList());*/mDatabase.getFavouritesList();

        final com.example.twainz.favouritesListAdapter adapter = new com.example.twainz.favouritesListAdapter(rootView.getContext(), favourites_alist);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                android.support.v4.app.FragmentManager childManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   //Begin the fragment change
                Fragment fragment = new stationInformationActivity();   //Initialise the new fragment

                Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
                fragmentData.putString(((stationInformationActivity) fragment).DATA_RECEIVE, favourites_alist.get(position));
                fragment.setArguments(fragmentData);
                position = tf.getStationList().indexOf(favourites_alist.get(position)); //need to get new position since favourites are out of order
                fragmentData.putInt(((stationInformationActivity) fragment).INDEX_RECIEVE, position);
                fragment.setArguments(fragmentData);

                fragmentTransaction.replace(R.id.favourites_listConstraint, fragment);   //Replace favourites_listconstraint with the new fragment
                fragmentTransaction.addToBackStack(null);   //Add the previous fragment to the stack so the back button works
                fragmentTransaction.commit();   //Complete the fragment transaction
            }
        });



        return rootView;
    }

    // 
    // public void insertData(String )
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isAdded()) {
            ((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.fav_appbar));
        }
    }

}

class favouritesListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> fav_stations;

    public favouritesListAdapter(Context context, ArrayList<String> parent_favourite_stations) {
        super(context, 0, parent_favourite_stations);
        fav_stations = parent_favourite_stations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String station = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favourites_view, parent, false);
        }

        TextView t = convertView.findViewById(R.id.stationButton);
        t.setText(station);
        t.setTag(position);
        t.setBackgroundColor(rgb(244, 129, 145));
        CheckBox cb = convertView.findViewById(R.id.checkBox);
        cb.setChecked(true);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//uncheck box
                cb.setChecked(false);
                //update static favourites list for the next time its needed
                // ( since it wont be null in the next time onCreate is called )

                //remove item from listview and reload listview
                Favourites.favourites_alist.remove(station);
                favouritesListAdapter.super.clear();
                favouritesListAdapter.super.addAll(Favourites.favourites_alist);
                favouritesListAdapter.super.notifyDataSetChanged();
                //remove item from database
                Favourites.mDatabase.deleteData(station);

            }
        });
        return convertView;

    }

}


