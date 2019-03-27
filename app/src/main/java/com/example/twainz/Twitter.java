package com.example.twainz;

import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
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

public class Twitter extends Fragment {
    private List<Status> status;
    private List<TwitterPost> posts;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.twitter_display, container, false);
        status = new ArrayList<Status>();
        posts = new ArrayList<>();

        //Extract the station name from the intent which started the activity
        printTweets();

        ListView listView = rootView.findViewById(R.id.twitterListLayout);

        String combinedTweets = "";

        for (Status s : status){
            String newTweet = s.getCreatedAt().toString();
            String[] DateParts = newTweet.split(" ");
            String[] TimeParts = DateParts[3].split(":");
            //combinedTweets = combinedTweets + TimeParts[0] + ":" + TimeParts[1] + " " + DateParts[0] + " " + DateParts[2]+ "\n\n" +s.getText()+ "\n\n"+"------------------------------------------------------------------------------" + "\n\n";
            TwitterPost twitterPost = new TwitterPost(TimeParts[0] + ":" + TimeParts[1],DateParts[0] + " " + DateParts[2],s.getText());
            for(MediaEntity m : s.getMediaEntities()){
                if(m.getType().equals("photo")){
                    twitterPost.setImageUrl(m.getMediaURL());
                }
            }
            posts.add(twitterPost);
        }

        TwitterAdapter twitterAdapter = new TwitterAdapter(getContext(),posts);
        listView.setAdapter(twitterAdapter);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isAdded()) {
            //((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.app_name));
        }
    }

    public void printTweets(){

        Thread network = new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigurationBuilder cb= new ConfigurationBuilder();

                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("iypFrvmpfpAXgS1QfIxXIqF2j")
                        .setOAuthConsumerSecret("GPBBczBLLI4fSFifLZWUEWbzwJ5GBOjXRZMSWwQemtZcQuyfIX")
                        .setOAuthAccessToken("110455096-8et3Ywd5qAzeaWSlDxbKGXSxM8hlPaG8u3I8rWoO")
                        .setOAuthAccessTokenSecret("wjDaPE8dOfn5PhvezKEfnBBHjPt3F8HEMCZuwuyfhFR9V");

                TwitterFactory tf=new TwitterFactory(cb.build());
                twitter4j.Twitter twitter= tf.getInstance();


                try {
                    status = twitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        });
        network.start();

        try {
            network.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
