package com.example.twainz;

import android.util.Log;

public class train {
    private int platform;
    private String arrivalTime;
    private int delay;
    private String destination;
    private String type;
    private String id;
    private String date;

    public train(int platform_, String arrivalTime_, int delay_, String destination_, String type_, String id_, String date_){
        this.platform = platform_;
        this.arrivalTime = arrivalTime_;
        this.delay = delay_;
        this.destination = destination_;
        this.type = type_;
        this.id = id_;
        this.date = date_;

    }

    public train(){}

    public void printData(){
        Log.d("Debug", this.type + ": " + this.arrivalTime + " + " + String.valueOf(this.delay));
    }

    public String getArrivalTime(){
        return arrivalTime;
    }
    public void setArrivalTime(String arrivalTime_){ arrivalTime = arrivalTime_;}

    public String getDestination(){
        return destination;
    }
    public void setDestination(String destination_){ destination = destination_;}

    public int getDelay(){
        return delay;
    }
    public void setDelay(int delay_){ delay = delay_;}

    public String getType(){
        return type;
    }
    public void setType(String type_){ type = type_;}

    public String getId(){ return id; }
    public void setId(String id_){ id = id_;}

    public String getDate(){ return date; }
    public void setDate(String date_){ date = date_;}

}
