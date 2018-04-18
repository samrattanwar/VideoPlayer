package com.vp.player.video.videoplayer.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tml.sharethem.sender.SHAREthemActivity;
import com.tml.sharethem.sender.SHAREthemService;
import com.vp.player.video.videoplayer.CustomAdapter;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.FormarPlayer;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.StickyService;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.VideoInfo;

public class FoldersFragment extends Fragment {
    private String[] arrayselect;
    private String[] arrayselect2;
    private static final int MY_PERMISSION_REQUEST = 1;
    ArrayList<DataModel> arrayList;
    ListView listView;
    private static CustomAdapter adapter;
    private String uril;
    private String title;
    ArrayList<String> listselect = new ArrayList<String>();
    String filelocation = "";
    VideoInfo videoInfo;
    File file;
    int r = 0;
    ArrayList<String> myList = new ArrayList<String>();
    SharedPreferences preferences;
    public File dir = Environment.getExternalStorageDirectory();
    //    Intent intent = getActivity().getIntent();
    public String refresh = null;
    public String path = refresh;
    private ArrayList<String> allDirectories = new ArrayList<String>();
    DataModel dataModel;
    boolean isBackPressed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home, container, false);

        Intent stickyService = new Intent(getActivity(), StickyService.class);
        getActivity().startService(stickyService);
        listView = myView.findViewById(R.id.list);
        preferences = getActivity().getSharedPreferences("video", Context.MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            if (refresh == null) {
                path = dir.getAbsolutePath();
            }

            doStuff();

        }


        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            // if ("text/plain".equals(type)) {
            handleSendFiles(intent); // Handle text being sent
            //} else if (type.startsWith("image/")) {
            // }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            // if (type.startsWith("image/")) {
            handleSendMultipleFiles(intent); // Handle multiple images being sent
        }

        myView.setFocusableInTouchMode(true);
        myView.requestFocus();
        myView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (path == dir.getAbsolutePath()) {
                        if (isBackPressed) {
                            getActivity().finish();
                        } else {
                            isBackPressed = true;
                        }
                    } else {
                        refresh = dir.getAbsolutePath();
//            toolbar.setTitle("Folders");
                        doStuff();
                    }
//            super.onBackPressed();
//                    isBackPressed = false;
                    return true;
                }
//                Toast.makeText(getActivity(), "other stuff pressed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return myView;
    }

    public void doStuff() {
        if (refresh != null) {
            path = refresh;
        }

        arrayList = new ArrayList<>();
        getMusic();

        if (arrayList.size() > 0) {
            adapter = new CustomAdapter(arrayList, getActivity().getApplicationContext());
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setAdapter(adapter);

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = view.findViewById(R.id.type);
                TextView name = view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if (isVideo(file)) {
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
//                                .setAspectRatio(0)
                            .setFullScreenAnimation(true)
                            .setPortraitWhenFullScreen(true)
                            .setShowTopBar(true)
                            .setRetryInterval(r)
                            .setPortraitWhenFullScreen(true);//portrait when full screen

                    GiraffePlayer.play(getActivity(), videoInfo);
                } else if (file.isDirectory()) {
                    isBackPressed = false;
                    refresh = uril;
                    doStuff();
//                    toolbar.setTitle("(" + Integer.toString(arrayList.size()) + ")" + title);
                    //   arrayList.clear();
                }

            }
        });


//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//                                           int pos, long id) {
//
//                TextView c = arg1.findViewById(R.id.type);
//                TextView name = arg1.findViewById(R.id.name);
//                filelocation = c.getText().toString();
//                File file = new File(filelocation);
//                if (!file.isDirectory()) {
//                    if (listselect.contains(filelocation)) {
//                        listselect.remove(filelocation);
//                        name.setTextColor(Color.BLACK);
//                    } else {
//                        listselect.add(filelocation);
//                        name.setTextColor(Color.parseColor("#3B86F3"));
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "Video Files Only", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        });

    }


    public void getMusic() {
       /*  myList = new ArrayList<String>();
           path = "Internal storage/Pictures";
           addfiles(path);
*/
        if (path == dir.getAbsolutePath()) {
            loadVideoFolders();
            myList.clear();
        } else {
            loadVideoFiles();
        }

    }

    public void loadVideoFolders() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = getActivity().getContentResolver();
                Uri songUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                Cursor songcursor = contentResolver.query(songUri, null, null, null, null, null);
                if (songcursor != null && songcursor.moveToFirst()) {
                    //int songTitle = songcursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    int songLocation = songcursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    //int songDuration = songcursor.getColumnIndex(MediaStore.Video.Media.DURATION);

                    do {
                        //String currentTitle = songcursor.getString(songTitle);
                        String currentLocation = songcursor.getString(songLocation);
                        //String currentDuration = songcursor.getString(songDuration);

                        File f1 = new File(currentLocation);
                        String remove = "/" + f1.getName();
                        String newpath = currentLocation.replace(remove, "");
                        File f2 = new File(newpath);
                        int counter = f2.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return isVideo(pathname);
                            }
                        }).length;
                        String foldertitle = f2.getName();
                        String folderdir = f2.getPath();
                        String foldertype = "";

                        //myList.add("string");

                        dataModel = new DataModel(foldertitle, folderdir, foldertype);
                        dataModel.setFileCounter(counter);
                        if (!myList.contains(folderdir)) {
                            arrayList.add(dataModel);
                            myList.add(folderdir);
                        }
                        //test = currentLocation;
                    } while (songcursor.moveToNext());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new CustomAdapter(arrayList, getActivity().getApplicationContext());
                            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            listView.setAdapter(adapter);

                        }
                    });
                }
            }
        }).start();
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

    public void loadVideoFiles() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File f = new File(path);
                File[] files = f.listFiles();
                for (File inFile : files) {
                    if (isVideo(inFile)) {
                        //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        String currentTitle = inFile.getName();
                        String currentLocation = inFile.getPath();
//                        path=currentLocation;
//                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(getActivity().getApplicationContext(), Uri.fromFile(inFile));
                        String currentDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        //long timeInMillisec = Long.parseLong(time );
                        retriever.release();
                        arrayList.add(new DataModel(currentTitle, currentLocation, currentDuration));
                    }
                }
                //Toast.makeText(this, Integer.toString(arrayList.size()), Toast.LENGTH_SHORT).show();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (arrayList.size() > 0) {
                            adapter = new CustomAdapter(arrayList, getActivity().getApplicationContext());
                            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            listView.setAdapter(adapter);
                            path = refresh;
                        }


                    }
                });
            }

        }).start();
    }

    private void handleSendFiles(Intent intent) {

        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String to = imageUri.getPath();
        if (imageUri != null) {
//            String share = imageUri.toString();
            Intent shareit = new Intent(getActivity().getApplicationContext(), SHAREthemActivity.class);
            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, new String[]{to});
            intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
            intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
            startActivity(shareit);
        }

    }

    public void handleSendMultipleFiles(Intent intent) {

        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

        if (imageUris != null) {
            arrayselect = new String[imageUris.size()];
            arrayselect = imageUris.toArray(arrayselect2);
            if (arrayselect2.length > 0) {
                Intent shareit = new Intent(getActivity().getApplicationContext(), SHAREthemActivity.class);
                intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect2);
                intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
                intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                startActivity(shareit);
            }
        }
    }
}
