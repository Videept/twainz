package com.example.twainz;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;


public class FavViewModel extends ViewModel{

    // bool for if if the stationlist needs to be reloaded
    public ObservableBoolean station_reload_needed = new ObservableBoolean();
    public ObservableBoolean favourites_reload_needed = new ObservableBoolean();



}
