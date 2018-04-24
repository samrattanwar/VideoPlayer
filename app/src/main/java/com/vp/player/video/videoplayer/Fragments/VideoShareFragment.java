package com.vp.player.video.videoplayer.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vp.player.video.videoplayer.Adapter.ImagesAdapter;
import com.vp.player.video.videoplayer.Adapter.VideosAdapter;
import com.vp.player.video.videoplayer.Adapter.VideosShareAdapter;
import com.vp.player.video.videoplayer.CustomAdapter2;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.ShareFiles1;
import com.vp.player.video.videoplayer.utils.SingleInstance;

import java.io.File;
import java.util.ArrayList;

public class VideoShareFragment extends Fragment {

    String refresh = null;
    String path = refresh;
    File dir = Environment.getExternalStorageDirectory();
    private RecyclerView rv_list;
    ArrayList<DataModel2> arrayList;
    ArrayList<String> myList = new ArrayList<String>();
    DataModel2 dataModel;
    private String uril;
    private String title;
    String filelocation = "";
//    ArrayList<String> listselect = new ArrayList<String>();
    private VideosShareAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_recycler, container, false);
        rv_list = myView.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        if (refresh == null) {
            path = dir.getAbsolutePath();
        }
        arrayList = SingleInstance.getInstance().getVideoList();
        if (arrayList.size() == 0)
            getAllDir(Environment.getExternalStorageDirectory());
        else {
            adapter = new VideosShareAdapter(VideoShareFragment.this, arrayList);
            rv_list.setAdapter(adapter);
        }

        return myView;
    }

//    public void doVideoStuff() {
//        if (refresh != null) {
//            path = refresh;
//        }
//        arrayList = new ArrayList<>();
//        getVideos();
//        adapter = new VideosShareAdapter(VideoShareFragment.this, arrayList);
////        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        rv_list.setAdapter(adapter);
//
////        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                TextView c = view.findViewById(R.id.type);
////                TextView name = view.findViewById(R.id.name);
////                uril = c.getText().toString();
////                title = name.getText().toString();
////                File file = new File(uril);
////                if (isVideo(file)) {
////                    filelocation = c.getText().toString();
////                    if (!file.isDirectory()) {
////                        if (listselect.contains(filelocation)) {
////                            listselect.remove(filelocation);
////                            name.setTextColor(Color.BLACK);
////                        } else {
////                            listselect.add(filelocation);
////                            name.setTextColor(Color.parseColor("#1CAAC9"));
////                        }
////                    }
////
////                } else if (file.isDirectory()) {
////                    doVideoStuff();
////                }
////            }
////        });
//
//    }

//    public void getVideos() {
//        if (path == dir.getAbsolutePath()) {
//            loadVideoFolders();
//            doVideoStuff();
//            myList.clear();
//        } else {
//            loadVideoFiles();
//        }
//    }

    public void loadVideoFiles() {
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (isVideo(inFile)) {
                String currentTitle = inFile.getName();
                String currentLocation = inFile.getPath();
                String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
            }
        }
    }

    public void loadVideoFolders() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor = contentResolver.query(songUri, null, null, null, null, null);
        if (songcursor != null && songcursor.moveToFirst()) {
            int songLocation = songcursor.getColumnIndex(MediaStore.Video.Media.DATA);
            do {
                String currentLocation = songcursor.getString(songLocation);

                File f1 = new File(currentLocation);
                String remove = "/" + f1.getName();
                String newpath = currentLocation.replace(remove, "");
                File f2 = new File(newpath);

                String foldertitle = f2.getName();
                String folderdir = f2.getPath();
                String foldertype = "folder";

                dataModel = new DataModel2(foldertitle, folderdir, foldertype);
                if (!myList.contains(folderdir)) {
                    arrayList.add(dataModel);
                    myList.add(folderdir);
                }
            } while (songcursor.moveToNext());
        }
    }

    public boolean isVideo(File inFile) {
        if (inFile.toString().toLowerCase().endsWith(".mp4")
                || inFile.toString().toLowerCase().endsWith(".avi")
                || inFile.toString().toLowerCase().endsWith(".mpeg4")
                || inFile.toString().toLowerCase().endsWith(".mpg")
                || inFile.toString().toLowerCase().endsWith(".gif")
                || inFile.toString().toLowerCase().endsWith(".mpeg")
                || inFile.toString().toLowerCase().endsWith(".flv")
                || inFile.toString().toLowerCase().endsWith(".mkv")
                || inFile.toString().toLowerCase().endsWith(".mov")) {
            return true;
        } else {
            return false;
        }
    }

    public void videoItemClicked(int layoutPosition, DataModel2 data) {
        uril = data.getLocation();
        title = data.getName();
        File file = new File(uril);
        if (isVideo(file)) {
            filelocation = data.getLocation();


        } else if (file.isDirectory()) {
            refresh = uril;
//            doVideoStuff();
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
                                try{
                                    String currentTitle = listFile[i].getName();
                                    String currentLocation = listFile[i].getPath();
                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    retriever.setDataSource(getActivity().getApplicationContext(), Uri.fromFile(listFile[i]));
                                    String currentDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                                    retriever.release();
                                    arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
                                }catch (Exception e){}
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
                        adapter = new VideosShareAdapter(VideoShareFragment.this, arrayList);
                        rv_list.setAdapter(adapter);
                    }
                });
            }


        }).start();
    }

    public void addRemove(DataModel2 data) {
        uril = data.getLocation();
        File file = new File(uril);
        filelocation = uril;
        if (!file.isDirectory()) {
            if (((ShareFiles1)getActivity()).listselect.contains(filelocation)) {
                ((ShareFiles1)getActivity()).listselect.remove(filelocation);
//                    name.setTextColor(Color.BLACK);
            } else {
                ((ShareFiles1)getActivity()).listselect.add(filelocation);
//                    name.setTextColor(Color.parseColor("#1CAAC9"));
            }
        }
    }
}
