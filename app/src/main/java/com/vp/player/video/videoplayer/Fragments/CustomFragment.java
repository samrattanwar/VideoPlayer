package com.vp.player.video.videoplayer.Fragments;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by android3 on 05-Oct-16.
 */
public class CustomFragment extends Fragment implements View.OnClickListener {

    public View setTouchNClick(View v) {

        v.setOnClickListener(this);
//        v.setOnTouchListener(CustomActivity.TOUCH);
        return v;
    }

    public View setClick(View v) {

        v.setOnClickListener(this);
        return v;
    }


    @Override
    public void onClick(View v) {

    }


}