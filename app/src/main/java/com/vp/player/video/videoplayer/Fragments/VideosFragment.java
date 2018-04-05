package com.vp.player.video.videoplayer.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.vp.player.video.videoplayer.CustomAdapter;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.VideoInfo;

public class VideosFragment extends Fragment {

    ArrayList<DataModel> arrayList;
    String path;
    ListView listView;
    CustomAdapter adapter;
    private String uril;
    private String title;
    VideoInfo videoInfo;
    SharedPreferences preferences;
    int r=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home, container, false);
        arrayList = new ArrayList<>();
//        loadVideoFiles();
        listView = myView.findViewById(R.id.list);
        preferences = getActivity().getSharedPreferences("videolist",Context.MODE_PRIVATE);
        getAllDir(Environment.getExternalStorageDirectory());
        setup(myView);
        return myView;
    }

    public void setup(View view){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = view.findViewById(R.id.type);
                TextView name = view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                SharedPreferences playlist = getActivity().getSharedPreferences("playlist", Context.MODE_PRIVATE);
                SharedPreferences.Editor editory = playlist.edit();
                int currenttrack = listView.getPositionForView(view) + 1;
                editory.putInt("current", currenttrack);
                editory.apply();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("lastplayed", uril);
                editor.apply();

                videoInfo = new VideoInfo(Uri.parse(uril))
                        .setShowTopBar(true) //show mediacontroller top bar
                        .setBgColor(Color.BLACK)
                        .setTitle(title)
                        .setAspectRatio(0)
                        .setRetryInterval(r)
                        .setPortraitWhenFullScreen(true);//portrait when full screen

                GiraffePlayer.play(getActivity(), videoInfo);

            }
        });
    }


    public boolean isVideo(File inFile) {


        if (inFile.toString().toLowerCase().endsWith(".mp4")
                || inFile.toString().toLowerCase().endsWith(".avi")
                || inFile.toString().toLowerCase().endsWith(".mpeg4")
                || inFile.toString().toLowerCase().endsWith(".mpg")
                //  ||inFile.toString().toLowerCase().endsWith(".gif")
                || inFile.toString().toLowerCase().endsWith(".mpeg")
                || inFile.toString().toLowerCase().endsWith(".flv")
                || inFile.toString().toLowerCase().endsWith(".mkv")
                || inFile.toString().toLowerCase().endsWith(".mov")) {
            return true;
        } else {
            return false;
        }
    }

    public void getAllDir(File dir) {

        File listFile[] = dir.listFiles();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getAllDir(listFile[i]);
                } else {
                    if (isVideo(listFile[i])){
                        Log.e("Video file",listFile[i].getName());
                        String currentTitle = listFile[i].getName();
                        String currentLocation = listFile[i].getPath();
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(getActivity().getApplicationContext(), Uri.fromFile(listFile[i]));
                        String currentDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                        retriever.release();
                        arrayList.add(new DataModel(currentTitle, currentLocation, currentDuration));
                    }
                }
            }
            adapter = new CustomAdapter(arrayList, getActivity().getApplicationContext());
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setAdapter(adapter);
        }
    }



}