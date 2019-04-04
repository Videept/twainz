package com.example.twainz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TwitterAdapter extends ArrayAdapter<FragmentTwitter.TwitterPost> {
    private List<FragmentTwitter.TwitterPost> items;
    private Context context;
    private LayoutInflater mInflater;

    TwitterAdapter(Context context, List<FragmentTwitter.TwitterPost> posts){
        super(context, 0, posts);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.items = posts;
    }

    @Override
    public FragmentTwitter.TwitterPost getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );

        FragmentTwitter.TwitterPost twitterPost = getItem(position);

        if (twitterPost.getImageUrl()==null) {
            convertView = inflater.inflate(R.layout.adapter_twitter_post, null );
        }else{
            convertView = inflater.inflate(R.layout.adapter_twitter_post_image, null );
            Picasso.get().load(twitterPost.getImageUrl()).into((ImageView) convertView.findViewById(R.id.twitterImage));
        }

        /*if (convertView != null && twitterPost.getImageUrl()==null) {
            convertView = inflater.inflate(R.layout.adapter_twitter_post, null );
        }else if(convertView != null){
            convertView = inflater.inflate(R.layout.adapter_twitter_post_image, null );
            Picasso.get().load(twitterPost.getImageUrl()).into((ImageView) convertView.findViewById(R.id.twitterImage));
        }*/

        ((TextView) convertView.findViewById(R.id.date)).setText(twitterPost.getDate());
        ((TextView) convertView.findViewById(R.id.hour)).setText(twitterPost.getHour());
        ((TextView) convertView.findViewById(R.id.twitterContent)).setText(twitterPost.getContent());
        return convertView;
    }
}
