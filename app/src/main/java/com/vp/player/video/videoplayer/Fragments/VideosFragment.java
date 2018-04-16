package com.vp.player.video.videoplayer.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vp.player.video.videoplayer.Adapter.VideosAdapter;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.R;

import java.io.File;
import java.util.ArrayList;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.VideoInfo;

public class VideosFragment extends Fragment {

    ArrayList<DataModel> arrayList;
    String path;
    RecyclerView rv_videos;
    //    CustomAdapter adapter;
    private VideosAdapter adapter;
    private String uril;
    private String title;
    VideoInfo videoInfo;
    SharedPreferences preferences;
    int r = 0;
//    private TextView type, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_videos, container, false);
        arrayList = new ArrayList<>();
//        loadVideoFiles();
        rv_videos = myView.findViewById(R.id.rv_videos);
        rv_videos.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        int spacingInPixels = 10;
//        rv_videos.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        preferences = getActivity().getSharedPreferences("videolist", Context.MODE_PRIVATE);
        getAllDir(Environment.getExternalStorageDirectory());
//        type = myView.findViewById(R.id.type);
//        name = myView.findViewById(R.id.name);
        return myView;
    }

    public void videoItemClicked(int position, DataModel data) {
//        TextView c = view.findViewById(R.id.type);
//        TextView name = view.findViewById(R.id.name);
        uril = data.getLocation();
        title = data.getName();
        File file = new File(uril);
        SharedPreferences playlist = getActivity().getSharedPreferences("playlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editory = playlist.edit();
//        int currenttrack = listView.getPositionForView(view) + 1;
        editory.putInt("current", position);
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

//    public void setup(View view) {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                TextView c = view.findViewById(R.id.type);
//                TextView name = view.findViewById(R.id.name);
//                uril = c.getText().toString();
//                title = name.getText().toString();
//                File file = new File(uril);
//                SharedPreferences playlist = getActivity().getSharedPreferences("playlist", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editory = playlist.edit();
//                int currenttrack = listView.getPositionForView(view) + 1;
//                editory.putInt("current", currenttrack);
//                editory.apply();
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("lastplayed", uril);
//                editor.apply();
//
//                videoInfo = new VideoInfo(Uri.parse(uril))
//                        .setShowTopBar(true) //show mediacontroller top bar
//                        .setBgColor(Color.BLACK)
//                        .setTitle(title)
//                        .setAspectRatio(0)
//                        .setRetryInterval(r)
//                        .setPortraitWhenFullScreen(true);//portrait when full screen
//
//                GiraffePlayer.play(getActivity(), videoInfo);
//
//            }
//        });
//    }


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

    public void getAllDir(final File dir) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File listFile[] = dir.listFiles();
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        if (listFile[i].isDirectory()) {
                            getAllDir(listFile[i]);
                        } else {
                            if (isVideo(listFile[i])) {
                                Log.e("Video file", listFile[i].getName());
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

//            adapter = new CustomAdapter(arrayList, getActivity().getApplicationContext());
//            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//            listView.setAdapter(adapter);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new VideosAdapter(VideosFragment.this, arrayList);
                        rv_videos.setAdapter(adapter);
                    }
                });
            }


        }).start();
    }


}
