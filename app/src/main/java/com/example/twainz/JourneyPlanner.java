package com.example.twainz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;
import java.util.Vector;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class JourneyPlanner extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View journeyView = inflater.inflate(R.layout.journey_planner, container, false);

        // Layout view objects
        AutoCompleteTextView textDest = journeyView.findViewById(R.id.editText_dest);
        AutoCompleteTextView textOrig = journeyView.findViewById(R.id.editText_orig);
        ImageButton buttonDir = journeyView.findViewById(R.id.changeDirButton);

        // Animation for switch direction button
        Animation shake = AnimationUtils.loadAnimation(journeyView.getContext(), R.anim.shake);

        // Populate array with all stations and get position of origin station
        ArrayList<String> listOfStations;
        final trainFetcher tf = new trainFetcher(getContext());
        listOfStations = new ArrayList<>(tf.getStationList());
        int position = listOfStations.indexOf(textOrig.getText().toString());

        // Set up autocomplete with from list of train stations
        ArrayAdapter<String> adapter = new ArrayAdapter<>(journeyView.getContext(),android.R.layout.simple_dropdown_item_1line,listOfStations);
        textDest.setAdapter(adapter);
        textOrig.setAdapter(adapter);

        // Fragment stuff
        android.support.v4.app.FragmentManager childManager = getChildFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   // Begin the fragment change
        Fragment fragment = new stationInformationActivity();                                               // Initialise the new fragment

        Bundle fragmentData = new Bundle();                     // Bundle passes position of selected train to Line-run fragment
        fragmentData.putString(((stationInformationActivity) fragment).DATA_RECEIVE, textOrig.getText().toString());
        fragmentData.putInt(((stationInformationActivity) fragment).INDEX_RECEIVE, position);
        fragmentData.putString(((stationInformationActivity) fragment).IS_JOURNEY_PLANNER, "true");
        fragmentData.putString(((stationInformationActivity) fragment).DEST_STATION, textDest.getText().toString());
        fragment.setArguments(fragmentData);

        fragmentTransaction.replace(R.id.fragFrame, fragment);  // Replace frame layout with the new fragment
        fragmentTransaction.addToBackStack(null);               // Add the previous fragment to the stack so the back button works
        fragmentTransaction.commit();                           // Complete the fragment transaction

        // Button listener for switching direction
        buttonDir.setOnClickListener((View v) -> {
            textDest.clearFocus();
            textOrig.clearFocus();
            v.startAnimation(shake);
            fragmentData.putString(((stationInformationActivity) fragment).DATA_RECEIVE, textOrig.getText().toString());
            fragmentData.putInt(((stationInformationActivity) fragment).INDEX_RECEIVE, listOfStations.indexOf(textOrig.getText().toString()));
            fragmentData.putString(((stationInformationActivity) fragment).DEST_STATION, textDest.getText().toString());
            fragment.setArguments(fragmentData);
            ((stationInformationActivity) fragment).onRefresh();
        });

        buttonDir.setOnLongClickListener((View v) -> {
                CharSequence temp = textDest.getText();
                textDest.setText(textOrig.getText());
                textOrig.setText(temp);
                return false;
        });

        return journeyView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

}
