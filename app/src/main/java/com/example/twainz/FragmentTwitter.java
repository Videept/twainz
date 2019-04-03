package com.example.twainz;

import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentTwitter extends FragmentRoot {
    private List<Status> status;
    private List<TwitterPost> posts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_twitter, container, false);

        status = new ArrayList<Status>();
        posts = new ArrayList<>();

        ListView listView = rootView.findViewById(R.id.twitterListLayout);
        TwitterFeedFetcher twitterFeed = new TwitterFeedFetcher();
        status = twitterFeed.getTwitterFeed();

        if (!status.isEmpty()) {
            for (Status s : status) {
                String newTweet = s.getCreatedAt().toString();
                String[] DateParts = newTweet.split(" ");
                String[] TimeParts = DateParts[3].split(":");
                TwitterPost twitterPost = new TwitterPost(TimeParts[0] + ":" + TimeParts[1], DateParts[0] + " " + DateParts[2], s.getText());
                for(MediaEntity m : s.getMediaEntities()){
                    if(m.getType().equals("photo")){
                        twitterPost.setImageUrl(m.getMediaURL());
                    }
                }
                posts.add(twitterPost);
            }
        }

        TwitterAdapter twitterAdapter = new TwitterAdapter(getContext(),posts);
        listView.setAdapter(twitterAdapter);
        return rootView;
    }


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

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        String getImageUrl(){ return imageUrl; }

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

}
