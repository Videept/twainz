package com.example.twainz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TwitterAdapter extends ArrayAdapter<TwitterPost> {
    private List<TwitterPost> items;
    private Context context;
    private LayoutInflater mInflater;

    TwitterAdapter(Context context, List<TwitterPost> posts){
        super(context, 0, posts);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.items = posts;
    }

    @Override
    public TwitterPost getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        if (convertView == null ) {
            convertView = inflater.inflate(R.layout.twitter_post, null );
        }

        TwitterPost twitterPost = getItem(position);

        ((TextView) convertView.findViewById(R.id.date)).setText(twitterPost.getDate());
        ((TextView) convertView.findViewById(R.id.hour)).setText(twitterPost.getHour());
        ((TextView) convertView.findViewById(R.id.twitterContent)).setText(twitterPost.getContent());
        return convertView;
    }
}
