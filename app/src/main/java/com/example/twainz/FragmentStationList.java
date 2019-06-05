package com.example.twainz;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.Observable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;


public class FragmentStationList extends FragmentRoot {
    private ListView mListView;
    private View rootView;
    private static ArrayList<String> list;  //had to make this static to avoid having to make it final
                                                //(it would otherwise have to be final inside the textwatcher)

    public static ArrayList<String> favourites_list_s;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_station_list, container, false);

        mListView = rootView.findViewById(R.id.listView);
        EditText searchbar = rootView.findViewById(R.id.search_text);
        final TrainFetcher tf = new TrainFetcher(getContext());

        favourites_list_s = FragmentFavourites.mDatabase.displayfavourites();

        list = new ArrayList<>(tf.getStationList());
       StringAdapter adapter = new StringAdapter(rootView.getContext(), list);


        FragmentFavourites.model.station_reload_needed.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                StringAdapter adapter = new StringAdapter(rootView.getContext(), list);
                mListView.setAdapter(adapter);

            }
        });


        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String search = list.get(position);

                Fragment fragment = new FragmentStationInformation();   //Initialise the new fragment

                Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
                fragmentData.putString(((FragmentStationInformation) fragment).DATA_RECEIVE, search);
                if(!searchbar.getText().toString().equals("")) {
                    position = tf.getStationList().indexOf(list.get(position));
                }
                fragmentData.putInt(((FragmentStationInformation)fragment).INDEX_RECEIVE, position);
                fragmentData.putString(((FragmentStationInformation) fragment).IS_JOURNEY_PLANNER, "false");

                fragment.setArguments(fragmentData);

                launchFragment(fragment, search, R.id.listConstraintLayout, fragmentData);
            }
        });


        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                list = new ArrayList<>(tf.getStationList());
                adapter.clear();
                adapter.addAll(list);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                list = SearchSequence(s, list);
                adapter.clear();
                adapter.addAll(list);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s){
            }
        });

        return rootView;
    }


    private ArrayList<String> SearchSequence(CharSequence s, ArrayList<String> list){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return list.stream()
            .filter(future_results->future_results.contains(s))
            .collect(Collectors.toCollection(ArrayList<String>::new));
        }
        ArrayList<String> results = new ArrayList<>();
        for (String str : list){
            if(str.contains(s) || str.contains(s.toString().toUpperCase())) {
                results.add(str);
            }
        }
        return results;
    }




}
//Custom adapter class to ensure new buttons are uniquely tagged so UI callbacks can be processed correctly
class StringAdapter extends ArrayAdapter<String> {

    public StringAdapter(Context context, ArrayList<String> stations) {
        super(context, 0, stations);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String station = getItem(position);

        //if (convertView == null) {
        //removed null check to stop convert view from reusing layouts like in linerun.
        //this causes less smooth performance though
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_station_list_row, parent, false);
        //}

        TextView t = convertView.findViewById(R.id.stationButton);
        t.setText(station);
        t.setTag(position); //Tag the button with it's position in the list

        final CheckBox cb = convertView.findViewById(R.id.checkBox2);

        cb.setChecked(FragmentStationList.favourites_list_s.contains(station));

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cb.isChecked()){
                    //remove from database
                    FragmentFavourites.mDatabase.deleteData(station);
                    FragmentStationList.favourites_list_s.remove(station);

                }else {
                    //add to datebase
                    FragmentFavourites.mDatabase.insertData(station);
                    FragmentStationList.favourites_list_s.add(station);
                }//set favourites
                FragmentFavourites.favourites_list_f = FragmentStationList.favourites_list_s;
                FragmentFavourites.model.favourites_reload_needed.set(!FragmentFavourites.model.favourites_reload_needed.get());
            }
        });
        return convertView;

    }

}