package com.vp.player.video.videoplayer.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tml.sharethem.sender.SHAREthemActivity;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.FormarPlayer;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.ShareFiles;
import com.vp.player.video.videoplayer.ShareFiles1;
import com.vp.player.video.videoplayer.VideoConverterActivity;
import com.vp.player.video.videoplayer.utils.SingleInstance;

import java.util.ArrayList;

public class MoreFragment extends Fragment {
    Button sharefile, youtube, btn_more_features;

    private boolean isFirstShown = false;
    private boolean isSecondShown = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_more, container, false);
        sharefile = myView.findViewById(R.id.sharefile);
        youtube = myView.findViewById(R.id.youtube);
        btn_more_features = myView.findViewById(R.id.btn_more_features);
        sharefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFirstShown)
                    ((FormarPlayer) getActivity()).showInterstitial();
                SingleInstance.getInstance().setImagesList(new ArrayList<DataModel2>());
                SingleInstance.getInstance().setVideoList(new ArrayList<DataModel2>());
                startActivity(new Intent(getActivity(), ShareFiles1.class));
                isFirstShown = true;
            }
        });
        btn_more_features.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSecondShown)
                    ((FormarPlayer) getActivity()).showInterstitial();
                startActivity(new Intent(getActivity(), VideoConverterActivity.class));
                isSecondShown = true;
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstShown = false;
                isSecondShown = false;
            }
        }, 1000 * 60 * 10);
        return myView;
    }

}
