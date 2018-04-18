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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vp.player.video.videoplayer.Adapter.FilesAdapter;
import com.vp.player.video.videoplayer.Adapter.ImagesAdapter;
import com.vp.player.video.videoplayer.Adapter.VideosShareAdapter;
import com.vp.player.video.videoplayer.CustomAdapter2;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.ShareFiles1;
import com.vp.player.video.videoplayer.utils.SingleInstance;

import java.io.File;
import java.util.ArrayList;

public class ImagesFragment extends Fragment {

    private RecyclerView rv_list;
    String refresh = null;
    String path = refresh;
    File dir = Environment.getExternalStorageDirectory();
    ArrayList<DataModel2> arrayList = new ArrayList<>();
    private String uril;
    String filelocation = "";
    //    ArrayList<String> listselect = new ArrayList<String>();
    ArrayList<String> myList = new ArrayList<>();
    DataModel2 dataModel;
    private ImagesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_recycler, container, false);
        rv_list = myView.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        if (refresh == null) {
            path = dir.getAbsolutePath();
        }
        arrayList = SingleInstance.getInstance().getImagesList();
        if (arrayList.size() == 0)
            getAllDir(Environment.getExternalStorageDirectory());
        else {
            adapter = new ImagesAdapter(ImagesFragment.this, arrayList);
            rv_list.setAdapter(adapter);
        }
        return myView;
    }

    public void doImageStuff() {
        if (refresh != null) {
            path = refresh;
        }
        arrayList = new ArrayList<>();
        getImages();
        adapter = new ImagesAdapter(ImagesFragment.this, arrayList);
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        rv_list.setAdapter(adapter);


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                TextView c = view.findViewById(R.id.type);
//                TextView name = view.findViewById(R.id.name);
//                uril = c.getText().toString();
//                title = name.getText().toString();
//                File file = new File(uril);
//                if (isImage(file)) {
//                    filelocation = c.getText().toString();
//                    if (!file.isDirectory()) {
//                        if (listselect.contains(filelocation)) {
//                            listselect.remove(filelocation);
//                            name.setTextColor(Color.BLACK);
//                        } else {
//                            listselect.add(filelocation);
//                            name.setTextColor(Color.parseColor("#1CAAC9"));
//                        }
//                    }
//                } else if (file.isDirectory()) {
//                    refresh = uril;
//                    if (category == "files") {
//                        doStuff();
//                    } else if (category == "images") {
//                        doImageStuff();
//                    } else if (category == "videos") {
//                        doVideoStuff();
//                    } else if (category == "music") {
//                        doMusicStuff();
//                    } else if (category == "apps") {
//                        doAppStuff();
//                    }
//                }
//            }
//        });

    }

    public void getImages() {
        if (path == dir.getAbsolutePath()) {
            loadImagesFolders();
//            doImageStuff();
            myList.clear();
        } else {
            loadImageFiles();
        }
    }

    public void loadImageFiles() {
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (isImage(inFile)) {
                String currentTitle = inFile.getName();
                String currentLocation = inFile.getPath();
                String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
            }
        }
    }

    public void loadImagesFolders() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor = contentResolver.query(songUri, null, null, null, null, null);
        if (songcursor != null && songcursor.moveToFirst()) {
            int songLocation = songcursor.getColumnIndex(MediaStore.Images.Media.DATA);

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

    public boolean isImage(File inFile) {
        if (inFile.toString().toLowerCase().endsWith(".jpeg")
                || inFile.toString().toLowerCase().endsWith(".jpg")
                || inFile.toString().toLowerCase().endsWith(".png")
                || inFile.toString().toLowerCase().endsWith(".tiff")
                || inFile.toString().toLowerCase().endsWith(".bmp") && !inFile.toString().toLowerCase().contains("com.")) {
            return true;
        } else {
            return false;
        }
    }


    public void onItemClicked(DataModel2 data) {
        uril = data.getLocation();
        File file = new File(uril);
        if (isImage(file)) {
            filelocation = data.getLocation();

        } else if (file.isDirectory()) {
            refresh = uril;
            doImageStuff();
        }
    }


    public void addRemove(DataModel2 data) {
        uril = data.getLocation();
        File file = new File(uril);
        filelocation = uril;
        if (!file.isDirectory()) {
            if (((ShareFiles1) getActivity()).listselect.contains(filelocation)) {
                ((ShareFiles1) getActivity()).listselect.remove(filelocation);
//                    name.setTextColor(Color.BLACK);
            } else {
                ((ShareFiles1) getActivity()).listselect.add(filelocation);
//                    name.setTextColor(Color.parseColor("#1CAAC9"));
            }
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
                            if (isImage(listFile[i])) {
                                Log.e("Image file", listFile[i].getName());
                                String currentTitle = listFile[i].getName();
                                String currentLocation = listFile[i].getPath();
                                String currentDuration = "10";

                                DataModel2 d = new DataModel2(currentTitle, currentLocation, currentDuration);
//                                retriever.release();
                                try {
                                    d.isSelected();
                                    arrayList.add(d);
                                } catch (Exception e) {
                                }
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
                        adapter = new ImagesAdapter(ImagesFragment.this, arrayList);
                        rv_list.setAdapter(adapter);
                    }
                });
            }


        }).start();
    }
}
