package com.example.twainz;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.ObservableBoolean;
import java.util.ArrayList;



public class FavViewModel extends ViewModel{

    // bool for if if the stationlist needs to be reloaded
    public ObservableBoolean station_reload_needed = new ObservableBoolean();
    public ObservableBoolean favourites_reload_needed = new ObservableBoolean();



}
