package com.example.twainz;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import static android.graphics.Color.rgb;

public class FragmentFavourites extends FragmentRoot {

    private View rootView;
    public static Database mDatabase;
    //set true if the favourites list needs to be reloaded by another class
    // cant make this an array of ints since we need to reference the stations by station name
    public static FavViewModel model = null;
    public static ArrayList<String> favourites_list_f;
    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        mDatabase = new Database(rootView.getContext()); //object of Database class
        ListView mListView = rootView.findViewById(R.id.favourites_list);
        TrainFetcher tf = new TrainFetcher(rootView.getContext());

        favourites_list_f = mDatabase.displayfavourites();

        model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(FavViewModel.class);

        model.favourites_reload_needed.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                com.example.twainz.favouritesListAdapter adapter = new com.example.twainz.favouritesListAdapter(rootView.getContext(), favourites_list_f);
                mListView.setAdapter(adapter);

            }
        });

        com.example.twainz.favouritesListAdapter adapter = new com.example.twainz.favouritesListAdapter(rootView.getContext(), favourites_list_f);
        mListView.setAdapter(adapter);



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                android.support.v4.app.FragmentManager childManager = getChildFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   //Begin the fragment change
                Fragment fragment = new FragmentStationInformation();   //Initialise the new fragment
                fragment.setUserVisibleHint(true);

                Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
               // fragmentData.putString(((FragmentStationInformation) fragment).DATA_RECEIVE, model.getArrayList(rootView.getContext()).get(position));
                fragmentData.putString(((stationInformationActivity) fragment).DATA_RECEIVE, favourites_list_f.get(position));
                fragment.setArguments(fragmentData);
                //position = tf.getStationList().indexOf(model.getArrayList(rootView.getContext()).get(position)); //need to get new position since favourites are out of order
                position = tf.getStationList().indexOf(favourites_list_f.get(position)); //need to get new position since favourites are out of order
                fragmentData.putInt(((FragmentStationInformation) fragment).INDEX_RECEIVE, position);
                fragment.setArguments(fragmentData);

                fragmentTransaction.replace(R.id.favourites_listConstraint, fragment);   //Replace favourites_listconstraint with the new fragment
                fragmentTransaction.addToBackStack("Favourite:StationInformation");   //Add the previous fragment to the stack so the back button works
                fragmentTransaction.commit();   //Complete the fragment transaction
            }
        });



        return rootView;
    }



}

class favouritesListAdapter extends ArrayAdapter<String> {


    public favouritesListAdapter(Context context, ArrayList<String> parent_favourite_stations) {
        super(context, 0, parent_favourite_stations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String station = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_favourite_row, parent, false);
        }

        TextView t = convertView.findViewById(R.id.stationButton);
        t.setText(station);
        t.setTag(position);
        t.setBackgroundColor(rgb(244, 129, 145));
        CheckBox cb = convertView.findViewById(R.id.checkBox);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.setChecked(true);
                Favourites.mDatabase.deleteData(station);
                Favourites.favourites_list_f.remove(station);
                stationList.favourites_list_s = Favourites.favourites_list_f;
                //Favourites.model.favourites_reload_needed.set(!Favourites.model.favourites_reload_needed.get());
                favouritesListAdapter.super.notifyDataSetChanged();
                Favourites.model.station_reload_needed.set(!Favourites.model.station_reload_needed.get());    //reload the station list

            }
        });
        return convertView;

    }

}


