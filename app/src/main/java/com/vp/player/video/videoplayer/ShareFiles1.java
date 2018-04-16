package com.vp.player.video.videoplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.tml.sharethem.receiver.ReceiverActivity;
import com.tml.sharethem.sender.SHAREthemActivity;
import com.tml.sharethem.sender.SHAREthemService;
import com.tml.sharethem.utils.HotspotControl;
import com.tml.sharethem.utils.Utils;
import com.vp.player.video.videoplayer.Adapter.ViewPagerAdapter;
import com.vp.player.video.videoplayer.Fragments.AppsFragment;
import com.vp.player.video.videoplayer.Fragments.FilesFragment;
import com.vp.player.video.videoplayer.Fragments.ImagesFragment;
import com.vp.player.video.videoplayer.Fragments.MusicFragment;
import com.vp.player.video.videoplayer.Fragments.VideoShareFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer2.VideoInfo;

public class ShareFiles1 extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    ArrayList<DataModel2> arrayList;
    ListView listView;
    private static CustomAdapter2 adapter;
    private String uril;
    String category = "files";
    private String title;
    private String[] arrayselect;
    private String[] arrayselect2;
    public ArrayList<String> listselect = new ArrayList<String>();
    String filelocation = "";
    VideoInfo videoInfo;
    File file;
    int r = 0;
    ArrayList<String> myList = new ArrayList<String>();
    SharedPreferences preferences;
    File dir = Environment.getExternalStorageDirectory();
    Intent intent = getIntent();
    String refresh = null;
    String path = refresh;
    private ArrayList<String> allDirectories = new ArrayList<String>();
    DataModel2 dataModel;
    TabLayout tabLayout;
    private AdView mAdView;
    private String TAG = MainActivity.class.getSimpleName();
    InterstitialAd mInterstitialAd;
    private ViewPager viewPager;
    private TextView txt_receive;
    private TextView txt_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_files1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("File Share");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
        txt_send = findViewById(R.id.txt_send);
        txt_receive = findViewById(R.id.txt_receive);

        txt_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HotspotControl hotspotControl = HotspotControl.getInstance(getApplicationContext());
                if (null != hotspotControl && hotspotControl.isEnabled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShareFiles1.this);
                    builder.setMessage("Sender(Hotspot) mode is active. Please disable it to proceed with Receiver mode");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
                startActivity(new Intent(getApplicationContext(), ReceiverActivity.class));
            }
        });

        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isShareServiceRunning(getApplicationContext())) {
                    startActivity(new Intent(getApplicationContext(), SHAREthemActivity.class));
                }
                if (!listselect.isEmpty()) {
                    arrayselect = new String[listselect.size()];
                    arrayselect = listselect.toArray(arrayselect);

                    if (arrayselect.length > 0) {
                        Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                        intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect);
                        intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
                        intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                        startActivity(intent);
                    }
                } else
                    Toast.makeText(getApplicationContext(), "No Items Selected", Toast.LENGTH_SHORT).show();
            }
        });

        txt_receive = findViewById(R.id.txt_receive);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.addTab(tabLayout.newTab().setText("File"));
//        tabLayout.addTab(tabLayout.newTab().setText("App"));
//        tabLayout.addTab(tabLayout.newTab().setText("Image"));
//        tabLayout.addTab(tabLayout.newTab().setText("Video"));
//        tabLayout.addTab(tabLayout.newTab().setText("Music"));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                if (tabLayout.getSelectedTabPosition() == 0) {
//                    category = "files";
//                    doStuff();
//                } else if (tabLayout.getSelectedTabPosition() == 1) {
//                    category = "apps";
//                    doAppStuff();
//                } else if (tabLayout.getSelectedTabPosition() == 2) {
//                    category = "images";
//                    doImageStuff();
//                } else if (tabLayout.getSelectedTabPosition() == 3) {
//                    category = "videos";
//                    doVideoStuff();
//                } else if (tabLayout.getSelectedTabPosition() == 4) {
//                    category = "music";
//                    doMusicStuff();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //MobileAds.initialize(this, getString(R.string.admob_app_id));


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            handleSendFiles(intent); // Handle text being sent
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            handleSendMultipleFiles(intent); // Handle multiple images being sent
        }

        if (ContextCompat.checkSelfPermission(ShareFiles1.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ShareFiles1.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(ShareFiles1.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(ShareFiles1.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
//            if (refresh == null) {
//                path = dir.getAbsolutePath();
//            }
//            doStuff();

        }
        ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\\\
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("4F32629C31AC053EC5A69E481EF6CE75")
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        if (TextUtils.isEmpty(getString(R.string.banner_home_footer))) {
            return;
        }

        mAdView = findViewById(R.id.adView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {

        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\\\


    private void handleSendFiles(Intent intent) {

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String share = imageUri.toString();
            Intent shareit = new Intent(getApplicationContext(), SHAREthemActivity.class);
            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, new String[]{share});
            intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
            intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
            startActivity(shareit);
        }

    }

    private void handleSendMultipleFiles(Intent intent) {

        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

        if (imageUris != null) {
            arrayselect = new String[imageUris.size()];
            arrayselect = imageUris.toArray(arrayselect2);
            if (arrayselect2.length > 0) {
                Intent shareit = new Intent(getApplicationContext(), SHAREthemActivity.class);
                intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect2);
                intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
                intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                startActivity(shareit);
            }
        }
    }

    public void doStuff() {
        if (refresh != null) {
            path = refresh;
        }
        listView = findViewById(R.id.list);
        arrayList = new ArrayList<>();
        allfiles();
        adapter = new CustomAdapter2(arrayList, getApplicationContext());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = view.findViewById(R.id.type);
                TextView name = view.findViewById(R.id.name);
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
                        name.setTextColor(Color.parseColor("#1CAAC9"));
                    }
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

    public void doMusicStuff() {
        if (refresh != null) {
            path = refresh;
        }
        listView = findViewById(R.id.list);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new CustomAdapter2(arrayList, getApplicationContext());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = view.findViewById(R.id.type);
                TextView name = view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if (isMusic(file)) {
                    filelocation = c.getText().toString();
                    if (!file.isDirectory()) {
                        if (listselect.contains(filelocation)) {
                            listselect.remove(filelocation);
                            name.setTextColor(Color.BLACK);
                        } else {
                            listselect.add(filelocation);
                            name.setTextColor(Color.parseColor("#1CAAC9"));
                        }
                    }
                } else if (file.isDirectory()) {
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

    public void doImageStuff() {
        if (refresh != null) {
            path = refresh;
        }
        listView = findViewById(R.id.list);
        arrayList = new ArrayList<>();
        getImages();
        adapter = new CustomAdapter2(arrayList, getApplicationContext());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = view.findViewById(R.id.type);
                TextView name = view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if (isImage(file)) {
                    filelocation = c.getText().toString();
                    if (!file.isDirectory()) {
                        if (listselect.contains(filelocation)) {
                            listselect.remove(filelocation);
                            name.setTextColor(Color.BLACK);
                        } else {
                            listselect.add(filelocation);
                            name.setTextColor(Color.parseColor("#1CAAC9"));
                        }
                    }
                } else if (file.isDirectory()) {
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

    public void doVideoStuff() {
        if (refresh != null) {
            path = refresh;
        }
        listView = findViewById(R.id.list);
        arrayList = new ArrayList<>();
        getVideos();
        adapter = new CustomAdapter2(arrayList, getApplicationContext());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = view.findViewById(R.id.type);
                TextView name = view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if (isVideo(file)) {
                    filelocation = c.getText().toString();
                    if (!file.isDirectory()) {
                        if (listselect.contains(filelocation)) {
                            listselect.remove(filelocation);
                            name.setTextColor(Color.BLACK);
                        } else {
                            listselect.add(filelocation);
                            name.setTextColor(Color.parseColor("#1CAAC9"));
                        }
                    }

                } else if (file.isDirectory()) {
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

    public void doAppStuff() {
        if (refresh != null) {
            path = refresh;
        }
        listView = findViewById(R.id.list);
        arrayList = new ArrayList<>();
        getApps();
        adapter = new CustomAdapter2(arrayList, getApplicationContext());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView c = view.findViewById(R.id.type);
                TextView name = view.findViewById(R.id.name);
                uril = c.getText().toString();
                title = name.getText().toString();
                File file = new File(uril);
                if (isApp(file)) {
                    filelocation = c.getText().toString();
                    if (!file.isDirectory()) {
                        if (listselect.contains(filelocation)) {
                            listselect.remove(filelocation);
                            name.setTextColor(Color.BLACK);
                        } else {
                            listselect.add(filelocation);
                            name.setTextColor(Color.parseColor("#1CAAC9"));
                        }
                    }

                } else if (file.isDirectory()) {
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

    public void getVideos() {
        if (path == dir.getAbsolutePath()) {
            loadVideoFolders();
            myList.clear();
        } else if (category == "videos") {
            loadVideoFiles();
        }
    }

    public void getImages() {
        if (path == dir.getAbsolutePath()) {
            loadImagesFolders();
            myList.clear();
        } else if (category == "images") {
            loadImageFiles();
        }
    }


    public void getMusic() {
        if (path == dir.getAbsolutePath()) {
            loadMusicFolders();
            myList.clear();
        } else if (category == "music") {
            loadMusicFiles();
        }
    }

    public void allfiles() {
        loadAllFiles();
    }

    public void getApps() {
        loadAppFiles();
    }

    public void loadAppFiles() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : apps) {
            File inFile = new File(info.activityInfo.applicationInfo.publicSourceDir);
            String remove = "/" + inFile.getName();
            String remove2 = "/data/app/";
            String currentLocation = inFile.getPath();
            String left = currentLocation.replace(remove, "");
            String currentTitle = left.replace(remove2, "");
            String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            Drawable icon = info.getContext().getPackageManager().getApplicationIcon(pkg);
            Drawable icon = info.activityInfo.applicationInfo.loadIcon(this.getPackageManager());
            String appName = info.activityInfo.applicationInfo.loadLabel(this.getPackageManager()).toString();
            if (!currentLocation.startsWith("/sys")) {
                DataModel2 d = new DataModel2(appName, currentLocation, currentDuration);
                d.setDrawable(icon);
                arrayList.add(d);
            }

        }
    }

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

    public void loadAllFiles() {
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {

            String currentTitle = inFile.getName();
            String currentLocation = inFile.getPath();
            String currentDuration = "dir";
            arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
        }
    }

    public void loadVideoFolders() {
        ContentResolver contentResolver = getContentResolver();
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

    public void loadMusicFolders() {
        ContentResolver contentResolver = getContentResolver();
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

    public void loadImagesFolders() {
        ContentResolver contentResolver = getContentResolver();
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

    public void allFolders() {
        ContentResolver contentResolver = getContentResolver();
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

    public boolean isImage(File inFile) {
        if (inFile.toString().toLowerCase().endsWith(".jpeg")
                || inFile.toString().toLowerCase().endsWith(".jpg")
                || inFile.toString().toLowerCase().endsWith(".png")
                || inFile.toString().toLowerCase().endsWith(".tiff")
                || inFile.toString().toLowerCase().endsWith(".bmp")) {
            return true;
        } else {
            return false;
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

    public boolean isApp(File inFile) {
        if (inFile.toString().toLowerCase().endsWith(".apk")) {
            return true;
        } else {
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ShareFiles1.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                        doStuff();
                    }
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu2, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.share: {
                if (Utils.isShareServiceRunning(getApplicationContext())) {
                    startActivity(new Intent(getApplicationContext(), SHAREthemActivity.class));
                    return true;
                }
                if (!listselect.isEmpty()) {
                    arrayselect = new String[listselect.size()];
                    arrayselect = listselect.toArray(arrayselect);

                    if (arrayselect.length > 0) {
                        Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                        intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect);
                        intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
                        intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                        startActivity(intent);
                    }
                } else
                    Toast.makeText(getApplicationContext(), "No Items Selected", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.receive: {

                HotspotControl hotspotControl = HotspotControl.getInstance(getApplicationContext());
                if (null != hotspotControl && hotspotControl.isEnabled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Sender(Hotspot) mode is active. Please disable it to proceed with Receiver mode");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                    return true;
                }
                startActivity(new Intent(getApplicationContext(), ReceiverActivity.class));
                break;
            }

        }
        return false;
    }


//    @Override
//    public void onBackPressed() {
//        if (path == dir.getAbsolutePath()) {
//            listselect.clear();
//            finish();
//
//        } else if (category == "files") {
//            refresh = dir.getAbsolutePath();
//
//            doStuff();
//        } else if (category == "music") {
//            refresh = dir.getAbsolutePath();
//
//            doMusicStuff();
//        } else if (category == "images") {
//            refresh = dir.getAbsolutePath();
//
//            doImageStuff();
//        } else if (category == "videos") {
//            refresh = dir.getAbsolutePath();
//
//            doVideoStuff();
//        } else if (category == "apps") {
//            refresh = dir.getAbsolutePath();
//            doAppStuff();
//        }
//    }

    public void videoloader(View view) {
        category = "videos";
        refresh = dir.getAbsolutePath();
        doVideoStuff();
    }

    //
    public void imageloader(View view) {
        category = "images";
        refresh = dir.getAbsolutePath();
        doImageStuff();
    }

    public void musicloader(View view) {
        category = "music";
        refresh = dir.getAbsolutePath();
        doMusicStuff();
    }

    public void fileloader(View view) {
        category = "files";
        refresh = dir.getAbsolutePath();
        doStuff();
    }

    public void apploader(View view) {
        category = "apps";
        refresh = dir.getAbsolutePath();
        doAppStuff();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FilesFragment(), "File");
        adapter.addFrag(new AppsFragment(), "Apps");
        adapter.addFrag(new ImagesFragment(), "Images");
        adapter.addFrag(new VideoShareFragment(), "Videos");
        adapter.addFrag(new MusicFragment(), "Music");
        viewPager.setAdapter(adapter);
    }
}
