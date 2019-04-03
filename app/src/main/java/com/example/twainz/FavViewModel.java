package com.example.twainz;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import java.util.ArrayList;



public class FavViewModel extends ViewModel{

    private MutableLiveData<ArrayList<String>> favourites;
    private ArrayList<String> fav_alist;

    // bool for if if the stationlist needs to be reloaded
    public final ObservableBoolean reload_needed = new ObservableBoolean();


    public LiveData<ArrayList<String>> getFavourites() {
        if (favourites == null) {
            //not allowed to reference context in viewmodel
            // - viewmodel is independant of context of classes
            ///Database database = new Database(context);
            initFavourites();
        }
        return favourites;
    }

    public void setFavourites(ArrayList<String> alist){
        if(favourites == null){
            initFavourites();
        }
        favourites.setValue(alist);
    }

    public ArrayList<String> getArrayList(){
        return fav_alist;
    }

    private void initFavourites(){
        fav_alist =  new ArrayList<>();
        favourites = new MutableLiveData<>();
        favourites.setValue(fav_alist);
    }

}
