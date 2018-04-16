package com.vp.player.video.videoplayer.Fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.vp.player.video.videoplayer.utils.SingleInstance;

import java.util.ArrayList;

public class MoreFragment extends Fragment {
    Button sharefile,youtube,more;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_more, container, false);
        sharefile = myView.findViewById(R.id.sharefile);
        youtube = myView.findViewById(R.id.youtube);
        sharefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleInstance.getInstance().setImagesList(new ArrayList<DataModel2>());
                SingleInstance.getInstance().setVideoList(new ArrayList<DataModel2>());
                startActivity(new Intent(getActivity(), ShareFiles1.class));
            }
        });

//        if (((FormarPlayer)getActivity()).foldersFragment.path == ((FormarPlayer)getActivity()).foldersFragment.dir.getAbsolutePath()) {
////            finish();
//        } else {
//            ((FormarPlayer)getActivity()).foldersFragment.refresh = ((FormarPlayer)getActivity()).foldersFragment.dir.getAbsolutePath();
////            toolbar.setTitle("Folders");
//            ((FormarPlayer)getActivity()).foldersFragment.doStuff();
//        }

        return myView;
    }

}
