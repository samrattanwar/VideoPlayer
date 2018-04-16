package com.vp.player.video.videoplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.tml.sharethem.utils.Utils;
import com.tml.sharethem.utils.HotspotControl;
import com.tml.sharethem.receiver.ReceiverActivity;
import com.tml.sharethem.sender.SHAREthemActivity;
import com.tml.sharethem.sender.SHAREthemService;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.Option;
import tcking.github.com.giraffeplayer2.PlayerManager;
import tcking.github.com.giraffeplayer2.VideoInfo;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {


    private static final int MY_PERMISSION_REQUEST = 1;
    ArrayList<DataModel> arrayList;
    ListView listView;
    private static CustomAdapter adapter;
    private String uril;
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
    DataModel dataModel;
    Toolbar toolbar;

    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private String TAG = MainActivity.class.getSimpleName();
    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Folders");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent stickyService = new Intent(this, StickyService.class);
        startService(stickyService);


        Intent intent = getIntent();
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

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            if (refresh == null) {
                path = dir.getAbsolutePath();
            }

            doStuff();

        }
///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\\\

        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
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
            //Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }

        mAdView = (AdView) findViewById(R.id.adView);

//        mAdView.setAdUnitId(getString(R.string.banner_home_footer));


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                //Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
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
    protected void onRestart() {
        super.onRestart();
        SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = playlist.edit();
        editor.clear();
        editor.commit();
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("4F32629C31AC053EC5A69E481EF6CE75")
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
    }

    @Override
    public void onPause() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.pause(this);

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();


        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.resume(this);
        }

        if (mAdView != null) {
            mAdView.resume();
        }

    }

    @Override
    public void onDestroy() {
        SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = playlist.edit();
        editor.clear();
        editor.commit();
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.destroy(this);
        }
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\\\
    private void handleSendFiles(Intent intent) {

        //Uri uri = ShareCompat.IntentReader.from(this).getStream();
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String to = imageUri.getPath();
      /* try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);


            Toast.makeText(this, to, Toast.LENGTH_LONG).show();
       } catch (FileNotFoundException e) {
            e.printStackTrace();*/
        //Toast.makeText(this, to, Toast.LENGTH_SHORT).show();
        //   }
        if (imageUri != null) {
            String share = imageUri.toString();
            Intent shareit = new Intent(getApplicationContext(), SHAREthemActivity.class);
            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, new String[]{to});
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
        /*        if(!listselect.isEmpty()){
                arrayselect = new String[listselect.size()];
                arrayselect = listselect.toArray(arrayselect);

                if (arrayselect.length>0) {
                    Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                    intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, arrayselect); // mandatory
                    intent.putExtra(SHAREthemService.EXTRA_PORT, 52287); //optional but preferred
                    intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Video Player"); //optional
                    startActivity(intent);

                */
            }
        }
    }

    //  }


    public void doStuff() {
        if (refresh != null) {
            path = refresh;
        }
        listView = (ListView) findViewById(R.id.list);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new CustomAdapter(arrayList, getApplicationContext());
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
                if (isVideo(file)) {
                    SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist", Context.MODE_PRIVATE);
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

                    GiraffePlayer.play(getApplicationContext(), videoInfo);
                } else if (file.isDirectory()) {
                    refresh = uril;
                    doStuff();
                    toolbar.setTitle("(" + Integer.toString(arrayList.size()) + ")" + title);
                    //   arrayList.clear();
                }

            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                TextView c = (TextView) arg1.findViewById(R.id.type);
                TextView name = (TextView) arg1.findViewById(R.id.name);
                filelocation = c.getText().toString();
                File file = new File(filelocation);
                if (!file.isDirectory()) {
                    if (listselect.contains(filelocation)) {
                        listselect.remove(filelocation);
                        name.setTextColor(Color.BLACK);
                    } else {
                        listselect.add(filelocation);
                        //arrayselect = new String[]{c.getText().toString()};
                        name.setTextColor(Color.parseColor("#3B86F3"));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Video Files Only", Toast.LENGTH_SHORT).show();
                }
                return true;
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
                        arrayList.add(new DataModel(currentTitle, currentLocation, currentDuration));
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void loadVideoFiles() {
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
  /*          if (inFile.isDirectory()) {
                String currentTitle  = inFile.getName();
                String currentLocation = inFile.getPath();
                String currentDuration = "dir";
                arrayList.add(new DataModel(currentTitle, currentLocation, currentDuration));

            }*/
            if (isVideo(inFile)) {
                //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                String currentTitle = inFile.getName();
                String currentLocation = inFile.getPath();
//                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));
                String currentDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                //long timeInMillisec = Long.parseLong(time );

                retriever.release();
                arrayList.add(new DataModel(currentTitle, currentLocation, currentDuration));
            }

        }
        //Toast.makeText(this, Integer.toString(arrayList.size()), Toast.LENGTH_SHORT).show();
    }

    public void loadVideoFolders() {
        ContentResolver contentResolver = getContentResolver();
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

                String foldertitle = f2.getName();
                String folderdir = f2.getPath();
                String foldertype = "";

                //myList.add("string");

                dataModel = new DataModel(foldertitle, folderdir, foldertype);
                if (!myList.contains(folderdir)) {
                    arrayList.add(dataModel);
                    myList.add(folderdir);
                }
                //test = currentLocation;
            } while (songcursor.moveToNext());
        }
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        doStuff();
                    }
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
                if (!listselect.isEmpty()) {
                    arrayselect = new String[listselect.size()];
                    arrayselect = listselect.toArray(arrayselect);

                    if (arrayselect.length > 1) {
                        Toast.makeText(this, "Please select only 1 item", Toast.LENGTH_SHORT).show();
                    } else if (arrayselect.length > 0) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("video/*");
                        //Toast.makeText(this, arrayselect[0], Toast.LENGTH_SHORT).show();
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(arrayselect[0]));
                        startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                        return true;

             /*    Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
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

                */
                    }
                } else
                    Toast.makeText(getApplicationContext(), "No Items Selected", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.lastplayed: {
                //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String pathy = preferences.getString("Name", uril);
                if (pathy != null) {
                    videoInfo = new VideoInfo(Uri.parse(pathy))
                            .setShowTopBar(true) //show mediacontroller top bar
                            .setBgColor(Color.BLACK)
                            .setTitle(title)
                            .setAspectRatio(0)
                            .setPortraitWhenFullScreen(true);//portrait when full screen
                    GiraffePlayer.play(getApplicationContext(), videoInfo);
                } else
                    Toast.makeText(getApplicationContext(), "No last played video", Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.mybutton: {
                Intent intent = new Intent(getApplicationContext(), ShareFiles.class);
                startActivity(intent);
                break;
            }
            case R.id.youtube: {

                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MainActivity.this);

                mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

                    @Override
                    public void onRewarded(RewardItem rewardItem) {
                        // Toast.makeText(MainActivity.this, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                        // rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardedVideoAdLeftApplication() {
                        // Toast.makeText(MainActivity.this, "onRewardedVideoAdLeftApplication",
                        // Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardedVideoAdClosed() {
                        //Toast.makeText(MainActivity.this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(int errorCode) {
                        // Toast.makeText(MainActivity.this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
                    }//

                    @Override
                    public void onRewardedVideoAdLoaded() {
                        // Toast.makeText(MainActivity.this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardedVideoAdOpened() {
                        // Toast.makeText(MainActivity.this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardedVideoStarted() {
                        // Toast.makeText(MainActivity.this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
                    }
                });

                loadRewardedVideoAd();
                break;
            }
        }
        return false;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.rewarded_video),
                new AdRequest.Builder().build());
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    private void showRewardedVideo() {
        // make sure the ad is loaded completely before showing it
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getsharedvideo();
        SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = playlist.edit();
        editor.clear();
        editor.commit();
    }


    public void getsharedvideo() {

        String[] shared = new String[1];
        SharedPreferences sharepref = getApplicationContext().getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String pathsy = sharepref.getString("path", "");
        //Toast.makeText(this, pathsy, Toast.LENGTH_SHORT).show();
        shared[0] = pathsy;
        if (pathsy != "") {
            if (Utils.isShareServiceRunning(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), SHAREthemActivity.class));
            }
            SharedPreferences.Editor editit = sharepref.edit();
            editit.remove("path");
            editit.apply();
            Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, shared);
            intent.putExtra(SHAREthemService.EXTRA_PORT, 52287);
            intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        if (path == dir.getAbsolutePath()) {
            finish();
        } else {
            refresh = dir.getAbsolutePath();
            toolbar.setTitle("Folders");
            doStuff();
        }
    }
}
