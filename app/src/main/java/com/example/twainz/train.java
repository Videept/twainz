package com.example.twainz;

import android.util.Log;

public class train {
    private int platform;
    private String arrivalTime;
    private int delay;
    private String destination;
    private String type;

    public train(int platform_, String arrivalTime_, int delay_, String destination_, String type_){
        this.platform = platform_;
        this.arrivalTime = arrivalTime_;
        this.delay = delay_;
        this.destination = destination_;
        this.type = type_;
    }

    public void printData(){
        Log.d("Debug", this.type + ": " + this.arrivalTime + " + " + String.valueOf(this.delay));
    }

    public String getArrivalTime(){
        return arrivalTime;
    }

    public String getDestination(){
        return destination;
    }

    public int getDelay(){
        return delay;
    }

    public String getType(){
        return type;
    }

}
