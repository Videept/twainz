package com.example.twainz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Vector;

public class stationList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView mListView = (ListView) findViewById(R.id.listView);

        trainFetcher tf = new trainFetcher();
        Vector<String> names = new Vector<String>();
        names = tf.getStationList();

        ArrayList<String> list = new ArrayList<String>(names);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.adapter_view, R.id.textView1,list);

        mListView.setAdapter(adapter);

    }
}