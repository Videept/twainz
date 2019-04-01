package com.example.twainz;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentTwitter extends FragmentRoot {
    private List<Status> status;
    private List<TwitterPost> posts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.twitter_display, container, false);
        setTitle("Twitter");
        setUserVisibleHint(false);

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
                //combinedTweets = combinedTweets + TimeParts[0] + ":" + TimeParts[1] + " " + DateParts[0] + " " + DateParts[2]+ "\n\n" +s.getText()+ "\n\n"+"------------------------------------------------------------------------------" + "\n\n";
                TwitterPost twitterPost = new TwitterPost(TimeParts[0] + ":" + TimeParts[1], DateParts[0] + " " + DateParts[2], s.getText());
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

}
