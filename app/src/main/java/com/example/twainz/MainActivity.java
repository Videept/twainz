package com.example.twainz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Vector;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //This function handles UI events
    public void onClick(View v) {

        //Modify this switch statement to test new features
        switch (v.getId()) {

            case R.id.button:
                Intent informationActivity = new Intent(this, stationList.class);
                startActivity(informationActivity);

                break;

            case R.id.button2:
                //Add code here
                Intent twitter = new Intent(this, Twitter.class);
                startActivity(twitter);
                /*Twitter t = new Twitter();
                t.printTweets();
                Log.d("Debug", "Button 2 pressed");*/
                break;

            case R.id.button3:
                //Add code here
                Log.d("Debug", "Button 3 pressed");
                break;

            case R.id.button4:
                //Add code here
                Log.d("Debug", "Button 4 pressed");
                break;

            default:
                throw new RuntimeException("Unknown ID");
        }
    }

}
