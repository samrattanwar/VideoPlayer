package com.vp.player.video.videoplayer.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vp.player.video.videoplayer.Adapter.MusicAdapter;
import com.vp.player.video.videoplayer.CustomAdapter2;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.ShareFiles1;

import java.io.File;
import java.util.ArrayList;

public class MusicFragment extends Fragment {

    private RecyclerView rv_list;
    String refresh = null;
    String path = refresh;
    File dir = Environment.getExternalStorageDirectory();
    ArrayList<DataModel2> arrayList;
    private String uril;
    String filelocation = "";
//    ArrayList<String> listselect = new ArrayList<String>();
    ArrayList<String> myList = new ArrayList<>();
    DataModel2 dataModel;
    private MusicAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_recycler, container, false);
        rv_list = myView.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (refresh == null) {
            path = dir.getAbsolutePath();
        }
        doMusicStuff();
        return myView;
    }

    public void doMusicStuff() {
        if (refresh != null) {
            path = refresh;
        }
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new MusicAdapter(MusicFragment.this, arrayList);
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        rv_list.setAdapter(adapter);


    }

    public void getMusic() {
        if (path == dir.getAbsolutePath()) {
            loadMusicFolders();
            myList.clear();
        } else {
            loadMusicFiles();
        }
    }

    public void loadMusicFiles() {
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (isMusic(inFile)) {
                String currentTitle = inFile.getName();
                String currentLocation = inFile.getPath();
                String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
            }
        }
    }

    public void loadMusicFolders() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor = contentResolver.query(songUri, null, null, null, null, null);
        if (songcursor != null && songcursor.moveToFirst()) {
            int songLocation = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA);

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

    public boolean isMusic(File inFile) {
        if (inFile.toString().toLowerCase().endsWith(".3gp")
                || inFile.toString().toLowerCase().endsWith(".ogg")
                || inFile.toString().toLowerCase().endsWith(".opus")
                || inFile.toString().toLowerCase().endsWith(".m4a")
                || inFile.toString().toLowerCase().endsWith(".mp3")
                || inFile.toString().toLowerCase().endsWith(".wav")
                || inFile.toString().toLowerCase().endsWith(".wma")
                || inFile.toString().toLowerCase().endsWith(".raw")) {
            return true;
        } else {
            return false;
        }
    }


    public void onItemClicked(DataModel2 data) {
        uril = data.getLocation();
        File file = new File(uril);
        if (isMusic(file)) {
            filelocation = data.getLocation();

        } else if (file.isDirectory()) {
            refresh = uril;
            doMusicStuff();
        }
    }

    public void addRemove(DataModel2 data) {
        uril = data.getLocation();
        File file = new File(uril);
        if (!file.isDirectory()) {
            if (((ShareFiles1)getActivity()).listselect.contains(filelocation)) {
                ((ShareFiles1)getActivity()).listselect.remove(filelocation);
            } else {
                ((ShareFiles1)getActivity()).listselect.add(filelocation);
            }
        }
    }
}
