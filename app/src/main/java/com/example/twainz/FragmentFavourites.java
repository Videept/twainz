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

                Fragment fragment = new FragmentStationInformation();   //Initialise the new fragment
                String train_name = favourites_list_f.get(position);

                Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
                fragmentData.putString(((FragmentStationInformation) fragment).DATA_RECEIVE, train_name);

                position = tf.getStationList().indexOf(train_name); //need to get new position since favourites are out of order
                fragmentData.putInt(((FragmentStationInformation) fragment).INDEX_RECEIVE, position);
                fragmentData.putString(((FragmentStationInformation) fragment).IS_JOURNEY_PLANNER, "false");

                fragment.setArguments(fragmentData);

                launchFragment(fragment, train_name, R.id.favourites_listConstraint, fragmentData);


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
        CheckBox cb = convertView.findViewById(R.id.checkBox);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.setChecked(true);
                FragmentFavourites.mDatabase.deleteData(station);
                FragmentFavourites.favourites_list_f.remove(station);
                FragmentStationList.favourites_list_s = FragmentFavourites.favourites_list_f;
                //Favourites.model.favourites_reload_needed.set(!Favourites.model.favourites_reload_needed.get());
                favouritesListAdapter.super.notifyDataSetChanged();
                FragmentFavourites.model.station_reload_needed.set(!FragmentFavourites.model.station_reload_needed.get());    //reload the station list

            }
        });
        return convertView;

    }

}


