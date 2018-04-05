package com.vp.player.video.videoplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Saaem on 19-Mar-18.
 */

public class StickyService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = playlist.edit();
        editor.clear();
        editor.commit();
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = playlist.edit();
        editor.clear();
        editor.commit();
        stopSelf();
    }
}