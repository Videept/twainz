package com.example.twainz;

public class TwitterPost {

    private String date;
    private String hour;
    private String content;
    private String imageUrl;

    TwitterPost(String hour, String date, String content){
        this.hour = hour;
        this.date = date;
        this.content = content;
        this.imageUrl = null;
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

    String getImageUrl() { return imageUrl; }

    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
