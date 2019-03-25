package com.example.twainz;

public class LinerunStation {
    public String location;
    public int[] arrival_time = new int[2];      //arrival_time[0] = hours, arrival_time[1] = mins
        //having this as an int instead of a string reduces the amount on string-int conversions
    public Integer delay;
    public boolean visited ;  //set this boolean to true if we have passed by the station

    public LinerunStation(){}

}
