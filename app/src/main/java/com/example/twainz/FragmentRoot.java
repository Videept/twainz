package com.example.twainz;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


public class FragmentRoot extends Fragment implements BackPressListener {

    private static Map<String, String> fragmentTitles = new HashMap<String, String>();   //Map the fragment tags to their titles

    @Override
    public boolean onBackPressed() {
        return new backPressHandler(this).onBackPressed();
    }

    //This function is called in onCreate in all fragments to set the title and store it in this class
    public void setTitle(String title) {
        if (title != null && !title.isEmpty()) {
            Log.d("D", "Setting " + title + ": " + String.valueOf(this.getId()) + " " + this.getTag());


            if (!fragmentTitles.containsKey(this.getTag()))
                fragmentTitles.put(this.getTag(), title);
            
        }
    }

    //Called when popping from the backstack
    public void updateTitle(){
        if (((MainActivity) getActivity()) != null) {
            ActionBar actionBar = ((MainActivity) getActivity()).getsupportactionbar();
            Log.d("D", "Setting title to  " + String.valueOf(getTag()) + ": " + fragmentTitles.get(this.getTag()));
            if (actionBar != null)
                actionBar.setTitle(fragmentTitles.get(this.getTag()));


        }
    }


    //This ensures that the title will be updated when swiping between fragments
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d("D", "VISIBLE " + String.valueOf(getTag()) + ": " + fragmentTitles.get(this.getTag()));
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isAdded() && getView() != null)
            updateTitle();


    }
    /*@Override
    public void onResume(){
        Log.d("D", "RESUMED " + String.valueOf(getTag()) + ": " + fragmentTitles.get(this.getTag()));
        updateTitle();
        super.onResume();
    }*/


}
