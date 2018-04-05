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
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.tml.sharethem.receiver.ReceiverActivity;
import com.tml.sharethem.sender.SHAREthemActivity;
import com.tml.sharethem.sender.SHAREthemService;
import com.tml.sharethem.utils.HotspotControl;
import com.tml.sharethem.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.VideoInfo;

public class ShareFiles extends AppCompatActivity{

        private static final int MY_PERMISSION_REQUEST = 1;
        ArrayList<DataModel2> arrayList;
        ListView listView;
        private static CustomAdapter2 adapter;
        private String uril;
        TextView files;
        TextView images;
        TextView videos;
        TextView music;
        TextView apps;
        String category = "files";
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
        Intent intent = getIntent();
        String refresh = null;
        String path = refresh;
        private ArrayList<String> allDirectories = new ArrayList<String>();
        DataModel2 dataModel;

    private AdView mAdView;
    private String TAG = MainActivity.class.getSimpleName();
    InterstitialAd mInterstitialAd;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_share_files);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("File Share");
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);


            //MobileAds.initialize(this, getString(R.string.admob_app_id));


            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            videos = (TextView) findViewById(R.id.videos);
            images = (TextView) findViewById(R.id.images);
            music = (TextView) findViewById(R.id.music);
            files = (TextView) findViewById(R.id.files);
            apps = (TextView) findViewById(R.id.apps);
            
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                // if ("text/plain".equals(type)) {
                handleSendFiles(intent); // Handle text being sent
                //} else if (type.startsWith("image/")) {
                // }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                // if (type.startsWith("image/")) {
                handleSendMultipleFiles(intent); // Handle multiple images being sent
            }

            if(ContextCompat.checkSelfPermission(ShareFiles.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(ShareFiles.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(ShareFiles.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                } else {
                    ActivityCompat.requestPermissions(ShareFiles.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                }
            } else {
                if(refresh == null){
                    path =  dir.getAbsolutePath();
                }
                doStuff();

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
               // Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
                return;
            }

            mAdView = (AdView) findViewById(R.id.adView);
           // mAdView.setAdSize(AdSize.BANNER);
           // mAdView.setAdUnitId(getString(R.string.banner_home_footer));


            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdClosed() {
                   // Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                   // Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
                   // Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
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
                if(arrayselect2.length>0){
                    Intent shareit = new Intent(getApplicationContext(), SHAREthemActivity.class);
                    intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect2);
                    intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
                    intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                    startActivity(shareit);
        /*        if(!listselect.isEmpty()){
                arrayselect = new String[listselect.size()];
                arrayselect = listselect.toArray(arrayselect);

                if (arrayselect.length>0) {
                    Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                    intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect); // mandatory
                    intent.putExtra(SHAREthemService.EXTRA_PORT, 52287); //optional but preferred
                    intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Video Player"); //optional
                    startActivity(intent);

                */}}
        }

        //  }


        public void doStuff(){
            if(refresh!=null){
                path = refresh;
            }
            listView=(ListView)findViewById(R.id.list);
            arrayList= new ArrayList<>();
            allfiles();
            adapter= new CustomAdapter2(arrayList,getApplicationContext());
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
                           // Toast.makeText(getApplicationContext(), "Video Files Only", Toast.LENGTH_SHORT).show();
                        }
                     if(file.isDirectory()){
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




            listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
                @Override
                public void onSwipeLeft() {
                    if(category == "files"){
                        category = "apps";
                        files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        refresh = dir.getAbsolutePath();
                        files.setTextColor(Color.parseColor("#ffffff"));
                        apps.setTextColor(Color.parseColor("#1CAAC9"));
                        apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        doAppStuff();
                    }
                    else if(category == "apps"){
                        category = "images";
                        images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        refresh = dir.getAbsolutePath();
                        images.setTextColor(Color.parseColor("#1CAAC9"));
                        apps.setTextColor(Color.parseColor("#ffffff"));
                        apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        doImageStuff();
                    }
                    else if(category == "images"){
                        category = "videos";
                        refresh = dir.getAbsolutePath();
                        images.setTextColor(Color.parseColor("#ffffff"));
                        videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        videos.setTextColor(Color.parseColor("#1CAAC9"));
                        doVideoStuff();
                    }
                    else if(category == "videos"){
                        category = "music";
                        refresh = dir.getAbsolutePath();
                        videos.setTextColor(Color.parseColor("#ffffff"));
                        music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        music.setTextColor(Color.parseColor("#1CAAC9"));
                        doMusicStuff();
                    }
                    else if(category == "music"){
                        category = "files";
                        music.setTextColor(Color.parseColor("#ffffff"));
                        files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        refresh = dir.getAbsolutePath();
                        files.setTextColor(Color.parseColor("#1CAAC9"));
                        doStuff();
                    }
                }
                public void onSwipeRight() {
                    if(category == "files"){
                        category = "music";
                        music.setTextColor(Color.parseColor("#1CAAC9"));
                        music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        files.setTextColor(Color.parseColor("#ffffff"));
                        refresh = dir.getAbsolutePath();
                        doMusicStuff();
                    }
                    else if(category == "images"){
                        category = "apps";
                        apps.setTextColor(Color.parseColor("#1CAAC9"));
                        apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        images.setTextColor(Color.parseColor("#ffffff"));
                        refresh = dir.getAbsolutePath();
                        doAppStuff();
                    }
                    else if(category == "apps"){
                        category = "files";
                        files.setTextColor(Color.parseColor("#1CAAC9"));
                        apps.setTextColor(Color.parseColor("#ffffff"));
                        apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        refresh = dir.getAbsolutePath();
                        doStuff();
                    }
                    else if(category == "videos"){
                        category = "images";
                        images.setTextColor(Color.parseColor("#1CAAC9"));

                        images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        videos.setTextColor(Color.parseColor("#ffffff"));
                        refresh = dir.getAbsolutePath();
                        doImageStuff();
                    }
                    else if(category == "music"){
                        category = "videos";
                        music.setTextColor(Color.parseColor("#ffffff"));
                        videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                        videos.setTextColor(Color.parseColor("#1CAAC9"));
                        refresh = dir.getAbsolutePath();
                        doVideoStuff();
                    }
                }
            });

        }

    public void doMusicStuff(){
        if(refresh!=null){
            path = refresh;
        }
        listView=(ListView)findViewById(R.id.list);
        arrayList= new ArrayList<>();
        getMusic();
        adapter= new CustomAdapter2(arrayList,getApplicationContext());
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



        listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                if(category == "files"){
                    category = "apps";
                    files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    files.setTextColor(Color.parseColor("#ffffff"));
                    apps.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    doAppStuff();
                }
                else if(category == "apps"){
                    category = "images";
                    images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    refresh = dir.getAbsolutePath();
                    images.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setTextColor(Color.parseColor("#ffffff"));
                    apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    doImageStuff();
                }
                else if(category == "images"){
                    category = "videos";
                    refresh = dir.getAbsolutePath();
                    images.setTextColor(Color.parseColor("#ffffff"));
                    videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#1CAAC9"));
                    doVideoStuff();
                }
                else if(category == "videos"){
                    category = "music";
                    refresh = dir.getAbsolutePath();
                    videos.setTextColor(Color.parseColor("#ffffff"));
                    music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    music.setTextColor(Color.parseColor("#1CAAC9"));
                    doMusicStuff();
                }
                else if(category == "music"){
                    category = "files";
                    music.setTextColor(Color.parseColor("#ffffff"));
                    files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    files.setTextColor(Color.parseColor("#1CAAC9"));
                    doStuff();
                }
            }
            public void onSwipeRight() {
                if(category == "files"){
                    category = "music";
                    music.setTextColor(Color.parseColor("#1CAAC9"));
                    music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    files.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doMusicStuff();
                }
                else if(category == "images"){
                    category = "apps";
                    apps.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    images.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doAppStuff();
                }
                else if(category == "apps"){
                    category = "files";
                    files.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setTextColor(Color.parseColor("#ffffff"));
                    apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    refresh = dir.getAbsolutePath();
                    doStuff();
                }
                else if(category == "videos"){
                    category = "images";
                    images.setTextColor(Color.parseColor("#1CAAC9"));

                    images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doImageStuff();
                }
                else if(category == "music"){
                    category = "videos";
                    music.setTextColor(Color.parseColor("#ffffff"));
                    videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    doVideoStuff();
                }
            }
        });
    }
    public void doImageStuff(){
        if(refresh!=null){
            path = refresh;
        }
        listView=(ListView)findViewById(R.id.list);
        arrayList= new ArrayList<>();
        getImages();
        adapter= new CustomAdapter2(arrayList,getApplicationContext());
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



        listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                if(category == "files"){
                    category = "apps";
                    files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    files.setTextColor(Color.parseColor("#ffffff"));
                    apps.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    doAppStuff();
                }
                else if(category == "apps"){
                    category = "images";
                    images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    refresh = dir.getAbsolutePath();
                    images.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setTextColor(Color.parseColor("#ffffff"));
                    apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    doImageStuff();
                }
                else if(category == "images"){
                    category = "videos";
                    refresh = dir.getAbsolutePath();
                    images.setTextColor(Color.parseColor("#ffffff"));
                    videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#1CAAC9"));
                    doVideoStuff();
                }
                else if(category == "videos"){
                    category = "music";
                    refresh = dir.getAbsolutePath();
                    videos.setTextColor(Color.parseColor("#ffffff"));
                    music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    music.setTextColor(Color.parseColor("#1CAAC9"));
                    doMusicStuff();
                }
                else if(category == "music"){
                    category = "files";
                    music.setTextColor(Color.parseColor("#ffffff"));
                    files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    files.setTextColor(Color.parseColor("#1CAAC9"));
                    doStuff();
                }
            }
            public void onSwipeRight() {
                if(category == "files"){
                    category = "music";
                    music.setTextColor(Color.parseColor("#1CAAC9"));
                    music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    files.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doMusicStuff();
                }
                else if(category == "images"){
                    category = "apps";
                    apps.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    images.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doAppStuff();
                }
                else if(category == "apps"){
                    category = "files";
                    files.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setTextColor(Color.parseColor("#ffffff"));
                    apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    refresh = dir.getAbsolutePath();
                    doStuff();
                }
                else if(category == "videos"){
                    category = "images";
                    images.setTextColor(Color.parseColor("#1CAAC9"));

                    images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doImageStuff();
                }
                else if(category == "music"){
                    category = "videos";
                    music.setTextColor(Color.parseColor("#ffffff"));
                    videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    doVideoStuff();
                }
            }
        });
    }
    public void doVideoStuff(){
        if(refresh!=null){
            path = refresh;
        }
        listView=(ListView)findViewById(R.id.list);
        arrayList= new ArrayList<>();
        getVideos();
        adapter= new CustomAdapter2(arrayList,getApplicationContext());
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



        listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                if(category == "files"){
                    category = "apps";
                    files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    files.setTextColor(Color.parseColor("#ffffff"));
                    apps.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    doAppStuff();
                }
                else if(category == "apps"){
                    category = "images";
                    images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    refresh = dir.getAbsolutePath();
                    images.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setTextColor(Color.parseColor("#ffffff"));
                    apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    doImageStuff();
                }
                else if(category == "images"){
                    category = "videos";
                    refresh = dir.getAbsolutePath();
                    images.setTextColor(Color.parseColor("#ffffff"));
                    videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#1CAAC9"));
                    doVideoStuff();
                }
                else if(category == "videos"){
                    category = "music";
                    refresh = dir.getAbsolutePath();
                    videos.setTextColor(Color.parseColor("#ffffff"));
                    music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    music.setTextColor(Color.parseColor("#1CAAC9"));
                    doMusicStuff();
                }
                else if(category == "music"){
                    category = "files";
                    music.setTextColor(Color.parseColor("#ffffff"));
                    files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    files.setTextColor(Color.parseColor("#1CAAC9"));
                    doStuff();
                }
            }
            public void onSwipeRight() {
                if(category == "files"){
                    category = "music";
                    music.setTextColor(Color.parseColor("#1CAAC9"));
                    music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    files.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doMusicStuff();
                }
                else if(category == "images"){
                    category = "apps";
                    apps.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    images.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doAppStuff();
                }
                else if(category == "apps"){
                    category = "files";
                    files.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setTextColor(Color.parseColor("#ffffff"));
                    apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    refresh = dir.getAbsolutePath();
                    doStuff();
                }
                else if(category == "videos"){
                    category = "images";
                    images.setTextColor(Color.parseColor("#1CAAC9"));

                    images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doImageStuff();
                }
                else if(category == "music"){
                    category = "videos";
                    music.setTextColor(Color.parseColor("#ffffff"));
                    videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    doVideoStuff();
                }
            }
        });
    }

    public void doAppStuff(){
        if(refresh!=null){
            path = refresh;
        }
        listView=(ListView)findViewById(R.id.list);
        arrayList= new ArrayList<>();
        getApps();
        adapter= new CustomAdapter2(arrayList,getApplicationContext());
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



        listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                if(category == "files"){
                    category = "apps";
                    files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    files.setTextColor(Color.parseColor("#ffffff"));
                    apps.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    doAppStuff();
                }
                else if(category == "apps"){
                    category = "images";
                    images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    refresh = dir.getAbsolutePath();
                    images.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setTextColor(Color.parseColor("#ffffff"));
                    apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    doImageStuff();
                }
                else if(category == "images"){
                    category = "videos";
                    refresh = dir.getAbsolutePath();
                    images.setTextColor(Color.parseColor("#ffffff"));
                    videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#1CAAC9"));
                    doVideoStuff();
                }
                else if(category == "videos"){
                    category = "music";
                    refresh = dir.getAbsolutePath();
                    videos.setTextColor(Color.parseColor("#ffffff"));
                    music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    music.setTextColor(Color.parseColor("#1CAAC9"));
                    doMusicStuff();
                }
                else if(category == "music"){
                    category = "files";
                    music.setTextColor(Color.parseColor("#ffffff"));
                    files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    files.setTextColor(Color.parseColor("#1CAAC9"));
                    doStuff();
                }
            }
            public void onSwipeRight() {
                if(category == "files"){
                    category = "music";
                    music.setTextColor(Color.parseColor("#1CAAC9"));
                    music.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    files.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    files.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doMusicStuff();
                }
                else if(category == "images"){
                    category = "apps";
                    apps.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    images.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    images.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doAppStuff();
                }
                else if(category == "apps"){
                    category = "files";
                    files.setTextColor(Color.parseColor("#1CAAC9"));
                    apps.setTextColor(Color.parseColor("#ffffff"));
                    apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    files.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    refresh = dir.getAbsolutePath();
                    doStuff();
                }
                else if(category == "videos"){
                    category = "images";
                    images.setTextColor(Color.parseColor("#1CAAC9"));

                    images.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#ffffff"));
                    refresh = dir.getAbsolutePath();
                    doImageStuff();
                }
                else if(category == "music"){
                    category = "videos";
                    music.setTextColor(Color.parseColor("#ffffff"));
                    videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    music.setBackgroundColor(Color.parseColor("#1CAAC9"));
                    videos.setTextColor(Color.parseColor("#1CAAC9"));
                    refresh = dir.getAbsolutePath();
                    doVideoStuff();
                }
            }
        });
    }

  /*public static boolean isVideoFile(String paths) {
        String mimeType = URLConnection.guessContentTypeFromName(paths);
        File file = new File(paths);
        return mimeType != null && mimeType.startsWith("video");
    }*/

        /*
            public void addfiles(String direc){
                file = new File(direc);
                File list[] = file.listFiles();
                for (int i = 0; i < list.length; i++) {
                    File  mFile = new File(file, list[i].getName());
                    File dirList[] = mFile.listFiles();
                    if(dirList == null) continue;
                    for (int j = 0; j < dirList.length; j++) {
                        if(isVideo(dirList[j])){
                            String currentTitle  = list[i].getName();
                            String currentLocation = list[i].getPath();
                            String currentDuration = "dir";
                            arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
                            break;
                        }
                    }
                }
            }
        
            public void listAllDirectories(String patho) {
        
                File tempfile = new File(patho);
                File[] files = tempfile.listFiles();
        
                if (files != null) {
                    for (File checkFile : files) {
                        if (checkFile.isDirectory()) {
                            allDirectories.add(checkFile.getName());
                            listAllDirectories(checkFile.getAbsolutePath());
                        }
                    }
                }
            }
        */
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

    public void getApps(){
        loadAppFiles();
    }


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void loadAppFiles(){
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(mainIntent, 0);
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
        public void loadVideoFolders(){
            ContentResolver contentResolver = getContentResolver();
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

    public void loadMusicFolders(){
        ContentResolver contentResolver = getContentResolver();
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
        ContentResolver contentResolver = getContentResolver();
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

    public void allFolders(){
        ContentResolver contentResolver = getContentResolver();
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

    public boolean isApp(File inFile){
        if (inFile.toString().toLowerCase().endsWith(".apk")){
            return true;
        }
        else {
            return false;
        }
    }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode){
                case MY_PERMISSION_REQUEST: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(ShareFiles.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                            doStuff();
                        }
                    }else {
                        Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;
                }
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main_menu2, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.share: {
                    if (Utils.isShareServiceRunning(getApplicationContext())) {
                        startActivity(new Intent(getApplicationContext(), SHAREthemActivity.class));
                        return true;
                    }
                    if(!listselect.isEmpty()){
                        arrayselect = new String[listselect.size()];
                        arrayselect = listselect.toArray(arrayselect);

                        if (arrayselect.length>0) {
                            Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect);
                            intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
                            intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                            startActivity(intent);
        /*        if(!listselect.isEmpty()){
                arrayselect = new String[listselect.size()];
                arrayselect = listselect.toArray(arrayselect);

                if (arrayselect.length>0) {
                    Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                    intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect); // mandatory
                    intent.putExtra(SHAREthemService.EXTRA_PORT, 52287); //optional but preferred
                    intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Video Player"); //optional
                    startActivity(intent);

                */}} else
                        Toast.makeText(getApplicationContext(),"No Items Selected", Toast.LENGTH_SHORT).show();
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


        @Override
        public void onBackPressed() {
            if(path ==  dir.getAbsolutePath()){
                listselect.clear();
                finish();

            }
            else if(category == "files"){
                refresh = dir.getAbsolutePath();

                doStuff();
            }
            else if(category == "music"){
                refresh = dir.getAbsolutePath();

                doMusicStuff();
            }
            else if(category == "images"){
                refresh = dir.getAbsolutePath();

                doImageStuff();
            }
            else if(category == "videos"){
                refresh = dir.getAbsolutePath();

                doVideoStuff();
            }
            else if(category == "apps"){
                refresh = dir.getAbsolutePath();
                doAppStuff();
            }
        }

    public void videoloader(View view) {
        category = "videos";
        music.setTextColor(Color.parseColor("#ffffff"));
        videos.setBackgroundColor(Color.parseColor("#EEEEEE"));
        apps.setTextColor(Color.parseColor("#ffffff"));
        apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
        music.setBackgroundColor(Color.parseColor("#1CAAC9"));
        videos.setTextColor(Color.parseColor("#1CAAC9"));
        images.setBackgroundColor(Color.parseColor("#1CAAC9"));
        images.setTextColor(Color.parseColor("#ffffff"));
        refresh = dir.getAbsolutePath();
        files.setBackgroundColor(Color.parseColor("#1CAAC9"));
        files.setTextColor(Color.parseColor("#ffffff"));
        doVideoStuff();
    }

    public void imageloader(View view) {
        category = "images";
        images.setTextColor(Color.parseColor("#1CAAC9"));
        music.setBackgroundColor(Color.parseColor("#1CAAC9"));
        apps.setTextColor(Color.parseColor("#ffffff"));
        apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
        images.setBackgroundColor(Color.parseColor("#EEEEEE"));
        videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
        videos.setTextColor(Color.parseColor("#ffffff"));
        music.setTextColor(Color.parseColor("#ffffff"));
        files.setBackgroundColor(Color.parseColor("#1CAAC9"));
        files.setTextColor(Color.parseColor("#ffffff"));
        refresh = dir.getAbsolutePath();
        doImageStuff();
    }

    public void musicloader(View view) {
        category = "music";
        music.setTextColor(Color.parseColor("#1CAAC9"));
        music.setBackgroundColor(Color.parseColor("#EEEEEE"));
        files.setBackgroundColor(Color.parseColor("#1CAAC9"));
        apps.setTextColor(Color.parseColor("#ffffff"));
        apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
        files.setTextColor(Color.parseColor("#ffffff"));
        videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
        videos.setTextColor(Color.parseColor("#ffffff"));
        refresh = dir.getAbsolutePath();
        images.setBackgroundColor(Color.parseColor("#1CAAC9"));
        images.setTextColor(Color.parseColor("#ffffff"));
        doMusicStuff();
    }

    public void fileloader(View view) {
        category = "files";
        files.setTextColor(Color.parseColor("#1CAAC9"));
        music.setBackgroundColor(Color.parseColor("#1CAAC9"));
        images.setBackgroundColor(Color.parseColor("#1CAAC9"));
        apps.setTextColor(Color.parseColor("#ffffff"));
        apps.setBackgroundColor(Color.parseColor("#1CAAC9"));
        images.setTextColor(Color.parseColor("#ffffff"));
        music.setTextColor(Color.parseColor("#ffffff"));
        videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
        videos.setTextColor(Color.parseColor("#ffffff"));
        files.setBackgroundColor(Color.parseColor("#EEEEEE"));
        refresh = dir.getAbsolutePath();
        doStuff();
    }

    public void apploader(View view) {
        category = "apps";
        apps.setTextColor(Color.parseColor("#1CAAC9"));
        apps.setBackgroundColor(Color.parseColor("#EEEEEE"));
        files.setTextColor(Color.parseColor("#ffffff"));
        music.setBackgroundColor(Color.parseColor("#1CAAC9"));
        images.setBackgroundColor(Color.parseColor("#1CAAC9"));
        images.setTextColor(Color.parseColor("#ffffff"));
        music.setTextColor(Color.parseColor("#ffffff"));
        videos.setBackgroundColor(Color.parseColor("#1CAAC9"));
        videos.setTextColor(Color.parseColor("#ffffff"));
        files.setBackgroundColor(Color.parseColor("#1CAAC9"));
        refresh = dir.getAbsolutePath();
        doAppStuff();
    }
}
