package com.example.twainz;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FragmentStationList extends FragmentRoot {
    private ListView mListView;
    private View rootView;
    private static ArrayList<String> list;  //had to make this static to avoid having to make it final
                                                //(it would otherwise have to be final inside the textwatcher

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_list, container, false);
        //setUserVisibleHint(false);

        mListView = rootView.findViewById(R.id.listView);
        EditText searchbar = rootView.findViewById(R.id.search_text);
        final TrainFetcher tf = new TrainFetcher(getContext());

        setTitle("Stations");

        list = new ArrayList<>(tf.getStationList());

        final StringAdapter adapter = new StringAdapter(rootView.getContext(), list);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String search = list.get(position);

                android.support.v4.app.FragmentManager childManager = getChildFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   //Begin the fragment change
                Fragment fragment = new FragmentStationInformation();   //Initialise the new fragment
                fragment.setUserVisibleHint(true);


                Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
                fragmentData.putString(((FragmentStationInformation) fragment).DATA_RECEIVE, search);
                if(!searchbar.getText().toString().equals("")) {
                    position = tf.getStationList().indexOf(list.get(position));
                }
                fragmentData.putInt(((FragmentStationInformation) fragment).INDEX_RECIEVE, position);
                fragment.setArguments(fragmentData);

                fragmentTransaction.replace(R.id.listConstraintLayout, fragment, "StationList:StationInformation" + String.valueOf(position));   //Replace listConstraintLayout with the new fragment
                fragmentTransaction.addToBackStack("StationList:StationInformation" + String.valueOf(position));   //Add the previous fragment to the stack so the back button works
                fragmentTransaction.commit();   //Complete the fragment transaction
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

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view, parent, false);
        }

        TextView t = convertView.findViewById(R.id.stationButton);
        t.setText(station);
        t.setTag(position); //Tag the button with it's position in the list

        return convertView;

    }

}