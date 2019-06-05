package com.example.twainz;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import java.util.ArrayList;

public class FragmentJourneyPlanner extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View journeyView = inflater.inflate(R.layout.fragment_journey_planner, container, false);

        // Layout view objects
        AutoCompleteTextView textDest = journeyView.findViewById(R.id.editText_dest);
        AutoCompleteTextView textOrig = journeyView.findViewById(R.id.editText_orig);
        ImageButton buttonDir = journeyView.findViewById(R.id.changeDirButton);

        // Animation for switch direction button and enter
        Animation shake = AnimationUtils.loadAnimation(journeyView.getContext(), R.anim.shake);
        Animation full_rotate = AnimationUtils.loadAnimation(journeyView.getContext(), R.anim.full_rotate);

        // Populate array with all stations and get position of origin station
        ArrayList<String> listOfStations;
        final TrainFetcher tf = new TrainFetcher(getContext());
        listOfStations = new ArrayList<>(tf.getStationList());
        int position = listOfStations.indexOf(textOrig.getText().toString());

        // Set up autocomplete with from list of train stations
        ArrayAdapter<String> adapter = new ArrayAdapter<>(journeyView.getContext(),android.R.layout.simple_dropdown_item_1line,listOfStations);
        textDest.setAdapter(adapter);
        textOrig.setAdapter(adapter);

        // Fragment stuff
        android.support.v4.app.FragmentManager childManager = getChildFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = childManager.beginTransaction();   // Begin the fragment change
        Fragment fragment = new FragmentStationInformation();                                               // Initialise the new fragment

        Bundle fragmentData = new Bundle();                     // Bundle passes position of selected train to Line-run fragment
        fragmentData.putString(((FragmentStationInformation) fragment).DATA_RECEIVE, textOrig.getText().toString());
        fragmentData.putInt(((FragmentStationInformation) fragment).INDEX_RECEIVE, position);
        fragmentData.putString(((FragmentStationInformation) fragment).IS_JOURNEY_PLANNER, "true");
        fragmentData.putString(((FragmentStationInformation) fragment).DEST_STATION, textDest.getText().toString());
        fragment.setArguments(fragmentData);

        fragmentTransaction.replace(R.id.fragFrame, fragment);  // Replace frame layout with the new fragment
        fragmentTransaction.addToBackStack(null);               // Add the previous fragment to the stack so the back button works
        fragmentTransaction.commit();                           // Complete the fragment transaction

        // Button listener for entering stations
        buttonDir.setOnClickListener((View v) -> {
            // Clear cursor and focus from EditTexts
            textDest.clearFocus();
            textOrig.clearFocus();
            // Tell user if entered stations are invalid
            if(!listOfStations.contains(textDest.getText().toString())){ textDest.startAnimation(shake); }
            if(!listOfStations.contains(textOrig.getText().toString())){ textOrig.startAnimation(shake); }
            // Update trains if stations are valid
            if(listOfStations.contains(textDest.getText().toString()) && listOfStations.contains(textOrig.getText().toString())) {
                // Start "enter" animation
                v.startAnimation(shake);
                // Enter current stations into fragment and reload
                fragmentData.putString(((FragmentStationInformation) fragment).DATA_RECEIVE, textOrig.getText().toString());
                fragmentData.putInt(((FragmentStationInformation) fragment).INDEX_RECEIVE, listOfStations.indexOf(textOrig.getText().toString()));
                fragmentData.putString(((FragmentStationInformation) fragment).DEST_STATION, textDest.getText().toString());
                fragment.setArguments(fragmentData);
                ((FragmentStationInformation) fragment).onRefresh();
            }
        });

        // Button listener for switching direction
        buttonDir.setOnLongClickListener((View v) -> {
            // Clear cursor and focus from EditTexts
            textDest.clearFocus();
            textOrig.clearFocus();
            // Start "switch direction" animation
            v.startAnimation(full_rotate);
            // Swap stations in EditTexts
            CharSequence temp = textDest.getText();
            textDest.setText(textOrig.getText());
            textOrig.setText(temp);
            return true;
        });

        textOrig.setOnItemClickListener((parent, view, position1, id) -> {
            // Hide keyboard function
            hideSoftKeyboard(getActivity());
            // Clear cursor and focus from EditTexts
            textDest.clearFocus();
            textOrig.clearFocus();
        });
        textDest.setOnItemClickListener((parent, view, position1, id) -> {
            // Hide keyboard function
            hideSoftKeyboard(getActivity());
            // Clear cursor and focus from EditTexts
            textDest.clearFocus();
            textOrig.clearFocus();
        });

        return journeyView;
    }



    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}
