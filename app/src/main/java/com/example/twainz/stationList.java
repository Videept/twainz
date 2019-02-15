package com.example.twainz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Collections;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Vector;

public class stationList extends AppCompatActivity {
    private ListView mListView;
    private Vector<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = (ListView) findViewById(R.id.listView);

        trainFetcher tf = new trainFetcher();
        names = new Vector<String>();
        names = tf.getStationList();
        
        Collections.sort(names);

        ArrayList<String> list = new ArrayList<String>(names);

        StringAdapter adapter = new StringAdapter(this, list);

        mListView.setAdapter(adapter);

    }
    public void onClick(View v){
        //Use the tag to retrieve the button which caused the callback and start a new activity to display the data
        Intent informationActivity = new Intent(this, stationInformationActivity.class);
        informationActivity.putExtra("STATION", names.get(Integer.valueOf(v.getTag().toString())));
        startActivity(informationActivity);
    }
}
//Custom adapter class to ensure new buttons are uniquely tagged so UI callbacks can be processed correctly
class StringAdapter extends ArrayAdapter<String> {

    public StringAdapter(Context context, ArrayList<String> stations) {
        super(context, 0, stations);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String station = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view, parent, false);
        }

        TextView t = convertView.findViewById(R.id.textView1);
        t.setText(station);
        t.setTag(position); //Tag the button with it's position in the list

        return convertView;

    }

}
