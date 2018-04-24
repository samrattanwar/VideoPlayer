package com.vp.player.video.videoplayer;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.tml.sharethem.sender.SHAREthemActivity;
import com.tml.sharethem.sender.SHAREthemService;
import com.vp.player.video.videoplayer.Adapter.ViewPagerAdapter;
import com.vp.player.video.videoplayer.Fragments.FoldersFragment;
import com.vp.player.video.videoplayer.Fragments.MoreFragment;
import com.vp.player.video.videoplayer.Fragments.VideosFragment;
import com.vp.player.video.videoplayer.utils.ExceptionHandler;

import java.util.ArrayList;

public class FormarPlayer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private String[] arrayselect;
    private String[] arrayselect2;
    private AdView mAdView;
    private static final int MY_PERMISSION_REQUEST = 1;
    InterstitialAd mInterstitialAd;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_formar_player);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupUi();


        if (ContextCompat.checkSelfPermission(FormarPlayer.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FormarPlayer.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(FormarPlayer.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(FormarPlayer.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {

        }


        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);

//        mAdView.setAdUnitId(getString(R.string.banner_home_footer));


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
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
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("4F32629C31AC053EC5A69E481EF6CE75")
                .build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showInterstitial();
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd = new InterstitialAd(FormarPlayer.this);

                // set the ad unit ID
                mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

                mInterstitialAd.loadAd(adRequest);
                //Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public
    FoldersFragment foldersFragment = new FoldersFragment();
    public VideosFragment videosFragment = new VideosFragment();
    private MoreFragment moreFragment = new MoreFragment();

    private void setupUi() {
        viewPager = findViewById(R.id.viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), foldersFragment, videosFragment, moreFragment);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.formar_player, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    public Fragment getFragment(int pos) {
        return videosFragment;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();

            // Handle the camera action
        } else if (id == R.id.nav_more_features) {

        } else if (id == R.id.nav_share) {
            String shareBody = "https://play.google.com/store/apps/details?" + "id=com.vp.player.video.videoplayer";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "FormePlayer (Open it in Google Play Store to Download the Application)");

            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share with"));
        } else if (id == R.id.nav_rate_us) {
            Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = playlist.edit();
        editor.clear();
        editor.commit();
    }


    @Override
    public void onDestroy() {
        SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = playlist.edit();
        editor.clear();
        editor.commit();
        super.onDestroy();
    }

    ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\\\


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(FormarPlayer.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        foldersFragment.doStuff();
                    }
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

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

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            finish();
//        }
//
//    }
}
