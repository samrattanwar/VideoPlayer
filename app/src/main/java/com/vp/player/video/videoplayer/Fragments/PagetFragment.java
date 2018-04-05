package com.vp.player.video.videoplayer.Fragments;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.vp.player.video.videoplayer.CustomAdapter2;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer2.VideoInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class PagetFragment extends Fragment {

    ArrayList<DataModel2> arrayList;
    ListView listView;
    String refresh = null;
    private String uril;
    String category = "files";
    ViewPager viewPager;

    private String title;
    private String[] arrayselect;
    private String[] arrayselect2;
    ArrayList<String> listselect = new ArrayList<String>();
    String filelocation = "";
    VideoInfo videoInfo;
    File file;
    int r = 0;
    ArrayList<String> myList = new ArrayList<String>();
    SharedPreferences preferences;
    File dir = Environment.getExternalStorageDirectory();
    String path = refresh;
    private ArrayList<String> allDirectories = new ArrayList<String>();
    DataModel2 dataModel;
    CustomAdapter2 adapter;



    public static PagetFragment newInstance(String category) {
        Bundle args = new Bundle();
        PagetFragment fragment = new PagetFragment();
        args.putString("category",category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_paget, container, false);
        listView = v.findViewById(R.id.list);
        arrayList = getArguments().getParcelableArrayList("list");
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        CustomAdapter2 adapter2 = new CustomAdapter2(arrayList,getActivity());
        listView.setAdapter(adapter2);
        return v;
    }




    public void allfiles(){
       /*  myList = new ArrayList<String>();
           path = "Internal storage/Pictures";
           addfiles(path);

        if(path == dir.getAbsolutePath() ){
            allFolders();
            myList.clear();
        }
        else {*/
        loadAllFiles();
        // }


    }
    public void loadAllFiles(){
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {

            String currentTitle  = inFile.getName();
            String currentLocation = inFile.getPath();
            String currentDuration = "dir";
            arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));

           /*}else {

              //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
              String currentTitle = inFile.getName();
              String currentLocation = inFile.getPath();
//                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));
              String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
              arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
          }*/
        }
    }

    public void doStuff() {
        if (refresh != null) {
            path = refresh;
        }
        arrayList = new ArrayList<>();
        allfiles();
        adapter = new CustomAdapter2(arrayList, getActivity());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = (TextView) view.findViewById(R.id.type);
                TextView name = (TextView) view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);

                filelocation = c.getText().toString();
                if (!file.isDirectory()) {
                    if (listselect.contains(filelocation)) {
                        listselect.remove(filelocation);
                        name.setTextColor(Color.BLACK);
                    } else {
                        listselect.add(filelocation);
                        //arrayselect = new String[]{c.getText().toString()};
                        name.setTextColor(Color.parseColor("#1CAAC9"));
                    }
                } else {
                    // Toast.makeText(getApplicationContext(), "Video Files Only", Toast.LENGTH_SHORT).show();
                }
                if (file.isDirectory()) {
                    refresh = uril;
                    if (category == "files") {
                        doStuff();
                    } else if (category == "images") {
                        doImageStuff();
                    } else if (category == "videos") {
                        doVideoStuff();
                    } else if (category == "music") {
                        doMusicStuff();
                    } else if (category == "apps") {
                        doAppStuff();
                    }
                }

            }
        });
    }
    public void doMusicStuff(){
        if(refresh!=null){
            path = refresh;
        }
        arrayList= new ArrayList<>();
        getMusic();
        adapter= new CustomAdapter2(arrayList,getActivity());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = (TextView) view.findViewById(R.id.type);
                TextView name = (TextView) view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if(isMusic(file)) {
                    filelocation = c.getText().toString();
                    if(!file.isDirectory()){
                        if(listselect.contains(filelocation)){
                            listselect.remove(filelocation);
                            name.setTextColor(Color.BLACK);
                        }
                        else {
                            listselect.add(filelocation);
                            //arrayselect = new String[]{c.getText().toString()};
                            name.setTextColor(Color.parseColor("#1CAAC9"));
                        }}
                    else {
                        //  Toast.makeText(getApplicationContext(), "//", Toast.LENGTH_SHORT).show();
                    }
                } else if(file.isDirectory()){
                    refresh = uril;
                    if(category=="files"){
                        doStuff();
                    }
                    else if(category=="images"){
                        doImageStuff();
                    }
                    else if(category=="videos"){
                        doVideoStuff();
                    }
                    else if(category=="music"){
                        doMusicStuff();
                    }else if(category=="apps"){
                        doAppStuff();
                    }
                }

            }
        });

    }
    public void doImageStuff(){
        if(refresh!=null){
            path = refresh;
        }
        arrayList= new ArrayList<>();
        getImages();
        adapter= new CustomAdapter2(arrayList,getActivity());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = (TextView) view.findViewById(R.id.type);
                TextView name = (TextView) view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if(isImage(file)) {
                    filelocation = c.getText().toString();
                    if(!file.isDirectory()){
                        if(listselect.contains(filelocation)){
                            listselect.remove(filelocation);
                            name.setTextColor(Color.BLACK);
                        }
                        else {
                            listselect.add(filelocation);
                            //arrayselect = new String[]{c.getText().toString()};
                            name.setTextColor(Color.parseColor("#1CAAC9"));
                        }}
                    else {
                        //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                } else if(file.isDirectory()){
                    refresh = uril;
                    if(category=="files"){
                        doStuff();
                    }
                    else if(category=="images"){
                        doImageStuff();
                    }
                    else if(category=="videos"){
                        doVideoStuff();
                    }
                    else if(category=="music"){
                        doMusicStuff();
                    }else if(category=="apps"){
                        doAppStuff();
                    }
                }

            }
        });

    }
    public void doVideoStuff(){
        if(refresh!=null){
            path = refresh;
        }
        arrayList= new ArrayList<>();
        getVideos();
        adapter= new CustomAdapter2(arrayList,getActivity());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = (TextView) view.findViewById(R.id.type);
                TextView name = (TextView) view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if(isVideo(file)) {
                    filelocation = c.getText().toString();
                    if(!file.isDirectory()){
                        if(listselect.contains(filelocation)){
                            listselect.remove(filelocation);
                            name.setTextColor(Color.BLACK);
                        }
                        else {
                            listselect.add(filelocation);
                            //arrayselect = new String[]{c.getText().toString()};
                            name.setTextColor(Color.parseColor("#1CAAC9"));
                        }}

                } else if(file.isDirectory()){
                    refresh = uril;
                    if(category=="files"){
                        doStuff();
                    }
                    else if(category=="images"){
                        doImageStuff();
                    }
                    else if(category=="videos"){
                        doVideoStuff();
                    }
                    else if(category=="music"){
                        doMusicStuff();
                    }else if(category=="apps"){
                        doAppStuff();
                    }
                }

            }
        });


    }

    public void doAppStuff(){
        if(refresh!=null){
            path = refresh;
        }
        arrayList= new ArrayList<>();
        getApps();
        adapter= new CustomAdapter2(arrayList,getActivity());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = (TextView) view.findViewById(R.id.type);
                TextView name = (TextView) view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if(isApp(file)) {
                    filelocation = c.getText().toString();
                    if(!file.isDirectory()){
                        if(listselect.contains(filelocation)){
                            listselect.remove(filelocation);
                            name.setTextColor(Color.BLACK);
                        }
                        else {
                            listselect.add(filelocation);
                            //arrayselect = new String[]{c.getText().toString()};
                            name.setTextColor(Color.parseColor("#1CAAC9"));
                        }}

                } else if(file.isDirectory()){
                    refresh = uril;
                    if(category=="files"){
                        doStuff();
                    }
                    else if(category=="images"){
                        doImageStuff();
                    }
                    else if(category=="videos"){
                        doVideoStuff();
                    }
                    else if(category=="music"){
                        doMusicStuff();
                    }
                    else if(category=="apps"){
                        doAppStuff();
                    }
                }

            }
        });
    }

    public void getMusic(){
       /*  myList = new ArrayList<String>();
           path = "Internal storage/Pictures";
           addfiles(path);
*/
        if(path == dir.getAbsolutePath() ){
            loadMusicFolders();
            myList.clear();
        }
        else if(category == "music"){
            loadMusicFiles();
        }


    }
    public boolean isMusic(File inFile){
        if (inFile.toString().toLowerCase().endsWith(".3gp")
                ||inFile.toString().toLowerCase().endsWith(".ogg")
                ||inFile.toString().toLowerCase().endsWith(".opus")
                ||inFile.toString().toLowerCase().endsWith(".m4a")
                ||inFile.toString().toLowerCase().endsWith(".mp3")
                ||inFile.toString().toLowerCase().endsWith(".wav")
                ||inFile.toString().toLowerCase().endsWith(".wma")
                ||inFile.toString().toLowerCase().endsWith(".raw")) {
            return true;
        }
        else {
            return false;
        }
    }

    public void loadMusicFiles(){
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
  /*          if (inFile.isDirectory()) {
                String currentTitle  = inFile.getName();
                String currentLocation = inFile.getPath();
                String currentDuration = "dir";
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));

            }*/
            if(isMusic(inFile)) {
                //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                String currentTitle = inFile.getName();
                String currentLocation = inFile.getPath();
//                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));
                String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
            }
        }
    }

    public void loadMusicFolders(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor = contentResolver.query(songUri, null, null, null, null, null);
        if(songcursor != null && songcursor.moveToFirst()){
            //int songTitle = songcursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            int songLocation = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            //int songDuration = songcursor.getColumnIndex(MediaStore.Video.Media.DURATION);

            do{
                //String currentTitle = songcursor.getString(songTitle);
                String currentLocation = songcursor.getString(songLocation);
                //String currentDuration = songcursor.getString(songDuration);

                File f1 = new File(currentLocation);
                String remove = "/" + f1.getName();
                String newpath = currentLocation.replace(remove,"");
                File f2 = new File(newpath);

                String foldertitle = f2.getName();
                String folderdir = f2.getPath();
                String foldertype = "folder";

                //myList.add("string");

                dataModel = new DataModel2(foldertitle, folderdir, foldertype);
                if(!myList.contains(folderdir)) {
                    arrayList.add(dataModel);
                    myList.add(folderdir);
                }
                //test = currentLocation;
            }while (songcursor.moveToNext());
        }
    }

    public void loadImagesFolders(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor = contentResolver.query(songUri, null, null, null, null, null);
        if(songcursor != null && songcursor.moveToFirst()){
            //int songTitle = songcursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            int songLocation = songcursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //int songDuration = songcursor.getColumnIndex(MediaStore.Video.Media.DURATION);

            do{
                //String currentTitle = songcursor.getString(songTitle);
                String currentLocation = songcursor.getString(songLocation);
                //String currentDuration = songcursor.getString(songDuration);

                File f1 = new File(currentLocation);
                String remove = "/" + f1.getName();
                String newpath = currentLocation.replace(remove,"");
                File f2 = new File(newpath);

                String foldertitle = f2.getName();
                String folderdir = f2.getPath();
                String foldertype = "folder";

                //myList.add("string");

                dataModel = new DataModel2(foldertitle, folderdir, foldertype);
                if(!myList.contains(folderdir)) {
                    arrayList.add(dataModel);
                    myList.add(folderdir);
                }
                //test = currentLocation;
            }while (songcursor.moveToNext());
        }
    }
    public void getApps(){
        loadAppFiles();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void loadAppFiles(){
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : apps) {
            File inFile = new File(info.activityInfo.applicationInfo.publicSourceDir);
            String remove = "/"+inFile.getName();
            String remove2 = "/data/app/";
            String currentLocation = inFile.getPath();
            String left = currentLocation.replace(remove,"");
            String currentTitle = left.replace(remove2,"");
//                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));
            String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if(!currentLocation.startsWith("/sys"))
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));

        }
    }

    public void loadVideoFiles(){
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
  /*          if (inFile.isDirectory()) {
                String currentTitle  = inFile.getName();
                String currentLocation = inFile.getPath();
                String currentDuration = "dir";
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));

            }*/
            if(isVideo(inFile)) {
                //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                String currentTitle = inFile.getName();
                String currentLocation = inFile.getPath();
//                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));
                String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
            }
        }
    }

    public boolean isVideo(File inFile){
        if (inFile.toString().toLowerCase().endsWith(".mp4")
                ||inFile.toString().toLowerCase().endsWith(".avi")
                ||inFile.toString().toLowerCase().endsWith(".mpeg4")
                ||inFile.toString().toLowerCase().endsWith(".mpg")
                ||inFile.toString().toLowerCase().endsWith(".gif")
                ||inFile.toString().toLowerCase().endsWith(".mpeg")
                ||inFile.toString().toLowerCase().endsWith(".flv")
                ||inFile.toString().toLowerCase().endsWith(".mkv")
                ||inFile.toString().toLowerCase().endsWith(".mov")) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isImage(File inFile){
        if (inFile.toString().toLowerCase().endsWith(".jpeg")
                ||inFile.toString().toLowerCase().endsWith(".jpg")
                ||inFile.toString().toLowerCase().endsWith(".png")
                ||inFile.toString().toLowerCase().endsWith(".tiff")
                ||inFile.toString().toLowerCase().endsWith(".bmp")) {
            return true;
        }
        else {
            return false;
        }
    }


    public boolean isApp(File inFile){
        if (inFile.toString().toLowerCase().endsWith(".apk")){
            return true;
        }
        else {
            return false;
        }
    }

    public void getVideos(){
       /*  myList = new ArrayList<String>();
           path = "Internal storage/Pictures";
           addfiles(path);
*/
        if(path == dir.getAbsolutePath() ){
            loadVideoFolders();
            myList.clear();
        }
        else if(category == "videos"){
            loadVideoFiles();
        }


    }
    public void loadVideoFolders(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor songcursor = contentResolver.query(songUri, null, null, null, null, null);
        if(songcursor != null && songcursor.moveToFirst()){
            //int songTitle = songcursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            int songLocation = songcursor.getColumnIndex(MediaStore.Video.Media.DATA);
            //int songDuration = songcursor.getColumnIndex(MediaStore.Video.Media.DURATION);

            do{
                //String currentTitle = songcursor.getString(songTitle);
                String currentLocation = songcursor.getString(songLocation);
                //String currentDuration = songcursor.getString(songDuration);

                File f1 = new File(currentLocation);
                String remove = "/" + f1.getName();
                String newpath = currentLocation.replace(remove,"");
                File f2 = new File(newpath);

                String foldertitle = f2.getName();
                String folderdir = f2.getPath();
                String foldertype = "folder";

                //myList.add("string");

                dataModel = new DataModel2(foldertitle, folderdir, foldertype);
                if(!myList.contains(folderdir)) {
                    arrayList.add(dataModel);
                    myList.add(folderdir);
                }
                //test = currentLocation;
            }while (songcursor.moveToNext());
        }
    }
    public void getImages(){
       /*  myList = new ArrayList<String>();
           path = "Internal storage/Pictures";
           addfiles(path);
*/
        if(path == dir.getAbsolutePath() ){
            loadImagesFolders();
            myList.clear();
        }
        else if(category == "images"){
            loadImageFiles();
        }


    }
    public void loadImageFiles(){
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
  /*          if (inFile.isDirectory()) {
                String currentTitle  = inFile.getName();
                String currentLocation = inFile.getPath();
                String currentDuration = "dir";
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));

            }*/
            if(isImage(inFile)) {
                //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                String currentTitle = inFile.getName();
                String currentLocation = inFile.getPath();
//                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));
                String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
            }
        }
    }


}
