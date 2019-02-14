package com.example.twainz;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TableLayout;
import android.widget.TextView;

public class Twitter extends AppCompatActivity {
    private TableLayout twitterlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_display);

        //Extract the station name from the intent which started the activity
        Intent cause = getIntent();
        String twitter = cause.getStringExtra("TWITTER");





    }
        // Get the Intent that started this activity and extract the string





    public void printTweets(){

        Thread network = new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigurationBuilder cb= new ConfigurationBuilder();

                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("iypFrvmpfpAXgS1QfIxXIqF2j")
                        .setOAuthConsumerSecret("GPBBczBLLI4fSFifLZWUEWbzwJ5GBOjXRZMSWwQemtZcQuyfIX")
                        .setOAuthAccessToken("110455096-8et3Ywd5qAzeaWSlDxbKGXSxM8hlPaG8u3I8rWoO")
                        .setOAuthAccessTokenSecret("wjDaPE8dOfn5PhvezKEfnBBHjPt3F8HEMCZuwuyfhFR9V ");

                TwitterFactory tf=new TwitterFactory(cb.build());
                twitter4j.Twitter twitter= tf.getInstance();

                List<Status> status = null;
                try {
                    status = twitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                for(Status st: status)
                {
                    System.out.println(st.getUser().getName()+"------"+st.getText());
                }
                /*public void gotomain(View view){
                    Intent newActivity = new Intent(this, MainActivity.class)
                }*/
                TextView textView = (TextView) findViewById(R.id.twitterListLayout);
                textView.setMovementMethod(new ScrollingMovementMethod());
            }
        });
        network.start();

    }
}
