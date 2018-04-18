package com.vp.player.video.videoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;

import java.io.File;

public class VideoPlayerActivity extends AppCompatActivity implements BetterVideoCallback {

    private BetterVideoPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        player = findViewById(R.id.player);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.fromFile(new File(getIntent().getStringExtra("path"))));
        player.setAutoPlay(true);
        player.enableControls();
        player.enableSwipeGestures();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    @Override
    public void onStarted(BetterVideoPlayer player) {

    }

    @Override
    public void onPaused(BetterVideoPlayer player) {

    }

    @Override
    public void onPreparing(BetterVideoPlayer player) {

    }

    @Override
    public void onPrepared(BetterVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(BetterVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(BetterVideoPlayer player) {

    }

    @Override
    public void onToggleControls(BetterVideoPlayer player, boolean isShowing) {

    }
}
