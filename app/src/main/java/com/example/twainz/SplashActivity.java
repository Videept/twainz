package com.example.twainz;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            try {
                setContentView(R.layout.splashfile);
            }catch (Exception e){
                e.printStackTrace();
                setContentView(R.layout.splashfile_basic);
            }
                //added this basic splash file just because my phone (Zuk Z2 model)
                // was crashing trying to inflate the image view in that splashfile.xml
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
    }
}