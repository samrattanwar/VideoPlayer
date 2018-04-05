package com.vp.player.video.videoplayer;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

/**
 * Created by Saaem on 18-Mar-18.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }
}
