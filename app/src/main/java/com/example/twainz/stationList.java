package com.example.twainz;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.stream.Collectors;

public class stationList extends Fragment  {
    private ListView mListView;
    private View rootView;
    private static ArrayList<String> list;  //had to make this static to avoid having to make it final
                                                //(it would otherwise have to be final inside the textwatcher)


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_list, container, false);

        mListView = rootView.findViewById(R.id.listView);
        EditText searchbar = rootView.findViewById(R.id.search_text);
        final trainFetcher tf = new trainFetcher(getContext());

        Favourites.favourites_alist =  Favourites.mDatabase.displayfavourites();


        list = new ArrayList<>(tf.getStationList());

        final StringAdapter adapter = new StringAdapter(rootView.getContext(), list);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String search = list.get(position);

                android.support.v4.app.FragmentManager childManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   //Begin the fragment change
                Fragment fragment = new stationInformationActivity();   //Initialise the new fragment

                Bundle fragmentData = new Bundle(); //This bundle is used to pass the position of the selected train to the linerun fragment
                fragmentData.putString(((stationInformationActivity) fragment).DATA_RECEIVE, search);
                if(!searchbar.getText().toString().equals("")) {
                    position = tf.getStationList().indexOf(list.get(position));
                }
                fragmentData.putInt(((stationInformationActivity) fragment).INDEX_RECIEVE, position);
                fragment.setArguments(fragmentData);

                fragmentTransaction.replace(R.id.listConstraintLayout, fragment);   //Replace listConstraintLayout with the new fragment
                fragmentTransaction.addToBackStack(null);   //Add the previous fragment to the stack so the back button works
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isAdded()) {
            ((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.app_name));
        }
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

    public StringAdapter(Context context, ArrayList<String> parent_stations) {
        super(context, 0, parent_stations);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String station = getItem(position);

        //if (convertView == null) {
        //removed null check to stop convert view from reusing layouts like in linerun.
        //this causes less smooth performance though
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view, parent, false);
        //}

        TextView t = convertView.findViewById(R.id.stationButton);
        t.setText(station);
        t.setTag(position); //Tag the button with it's position in the list

        CheckBox cb = convertView.findViewById(R.id.checkBox2);
        if(Favourites.favourites_alist.contains(station)) {
            cb.setChecked(true);
        }else{
            cb.setChecked(false);
        }
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check box
                //Log.d("d_tag", " intial state is " + String.valueOf(cb.isChecked()));
                if(!cb.isChecked()){
                    //remove from database
                    //update static favourites list for the next time its needed
                    // ( since it wont be null in the next time onCreate is called )
                    //remove item from listview and reload listview
                    Favourites.favourites_alist.remove(station);
                    //remove item from database
                    Favourites.mDatabase.deleteData(station);
                }else {
                    //add to datebase
                    //add item to database
                    Favourites.mDatabase.insertData(station);
                    ///update static favourites list for the next time onCreate is called
                    Favourites.favourites_alist.add(station);
                }


            }
        });
        return convertView;

    }

}