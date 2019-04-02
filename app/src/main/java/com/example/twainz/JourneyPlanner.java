package com.example.twainz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import java.util.Vector;
import android.widget.TextView;

public class JourneyPlanner extends Fragment {
    static private Vector<String> Message;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View journeyView = inflater.inflate(R.layout.journey_planner, container, false);

        TextView textDest = journeyView.findViewById(R.id.textView_dest);
        TextView textOrig = journeyView.findViewById(R.id.textView_orig);
        ImageButton buttonDir = journeyView.findViewById(R.id.changeDirButton);

        Animation shake = AnimationUtils.loadAnimation(journeyView.getContext(), R.anim.shake);
        buttonDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(shake);
                CharSequence temp = textDest.getText();
                textDest.setText(textOrig.getText());
                textOrig.setText(temp);
            }
        });

        return journeyView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

}
