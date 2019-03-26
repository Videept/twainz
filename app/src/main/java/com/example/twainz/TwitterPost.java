package com.example.twainz;

public class TwitterPost {

    private String date;
    private String hour;
    private String content;

    TwitterPost(String hour, String date, String content){
        this.hour = hour;
        this.date = date;
        this.content = content;
    }

    String getDate(){
        return date;
    }

    String getHour(){
        return  hour;
    }

    String getContent(){
        return content;
    }
}
