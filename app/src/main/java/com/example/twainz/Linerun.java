package com.example.twainz;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Vector;

import static android.graphics.Color.rgb;

public class Linerun extends Fragment {
    private View rootView;

    final static String DATA_RECEIVE = "data_receive";

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_linerun, container, false);
        trainFetcher tf = new trainFetcher(getContext());

        Bundle args = getArguments();
        trainFetcher.train t = tf.getTrains().get(Integer.valueOf(args.getString(DATA_RECEIVE)));
        ((MainActivity)getActivity()).setActionBarTitle("Line run test string");

        Vector<LinerunStation> stations = new Vector<>();
        TextView display = rootView.findViewById(R.id.textView);
        String display_text = t.getType() + " to " + t.getDestination();
        display.setText(display_text);
        int currentStation = tf.getLineRun(stations, t.getId(), t.getDate(), tf.getStationQuery());
        drawLinerun(stations, currentStation);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isAdded()) {
            ((MainActivity)getActivity()).setActionBarTitle("Line run test string");
            Log.d("D", "Line run called");
        }
    }


    private void drawLinerun(Vector<LinerunStation> stations, final int current_station) {
        ArrayList<LinerunStation> alist_stations = new ArrayList<>(stations);

        LinerunAdapter linerunAdapter = new LinerunAdapter(rootView.getContext(), alist_stations);

        final ListView listView = rootView.findViewById(R.id.linerun_listview);
        listView.setAdapter(linerunAdapter);

        //open listview to current position on linerun
        listView.post(new Runnable() {
            @Override
            public void run() {
                int pos = (current_station >1) ?current_station -1 : current_station;
                listView.setSelection(pos);
            }
        });


    }

}


class LinerunAdapter extends ArrayAdapter<LinerunStation> {

    public LinerunAdapter(Context context, ArrayList<LinerunStation> stations) {
        super(context, 0, stations);

    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LinerunStation s = getItem(position);

        /*TODO: fix this
        because right now you either surround this line (80) with a check to see if the convert view is null
        and this causes the linerun bar to be recycled and randomly disappear while scrolling,
        OR you have it as it currently is and you suffer a slight lag while scrolling, but none of the
        bars on the lineview disappear

         */

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.linerun_row, parent, false);

        TextView tv = convertView.findViewById(R.id.station_name);
        tv.setText(s.location);
        tv = convertView.findViewById(R.id.arrival_time);
        String mins = (s.arrival_time[1] < 10) ? "0" + String.valueOf(s.arrival_time[1]) : String.valueOf(s.arrival_time[1]);
        String atime = String.valueOf(s.arrival_time[0]) + ":" + mins;
        tv.setText(atime);

        tv = convertView.findViewById(R.id.delay);
        String del_mins = String.valueOf(Math.abs(s.delay));
        if(s.delay < 0){
            tv.setText(del_mins +" mins late");
            tv.setTextColor(rgb(242, 9, 71));
        }else if (s.delay > 0){
            tv.setText(del_mins +" mins early");
            tv.setTextColor(rgb(47, 191, 47));
        }else {
            tv.setText("- on time");
        }

        if(s.visited){
            convertView.findViewById(R.id.view_bar).setBackgroundColor(rgb(229, 220, 137));
        }else{
            convertView.findViewById(R.id.view_bar).setBackgroundColor(rgb(209, 213, 214));
        }

        return convertView;
    }


}
