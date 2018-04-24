package com.vp.player.video.videoplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.vp.player.video.videoplayer.utils.ExceptionHandler;

public class SplashActivity extends AppCompatActivity {

    Animation animZoomIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_splash);
        ImageView image = findViewById(R.id.image);
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new LinearInterpolator());
        animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);

        image.startAnimation(animZoomIn);
//        image.startAnimation(rotate);
        // set animation listener


        String crash = getIntent()
                .getStringExtra(ExceptionHandler.CRASH_REPORT);

        if (crash == null) {
            startSplash();
        } else {
            showCrashDialog(crash);
        }

    }

    private void startSplash(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, FormarPlayer.class));
                    finish();
                }
            }, 1000);
        }

        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, FormarPlayer.class));
                        finish();
                    }
                }, 1000);
            }
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(new Intent(SplashActivity.this, FormarPlayer.class));
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(new Intent(SplashActivity.this, FormarPlayer.class));
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    public void showCrashDialog(final String report) {
        android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(this);
        b.setTitle("App Crashed");
        b.setMessage("Oops! The app crashed due to below reason:\n\n" + report);

        DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/html");
                    i.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{"myaiwsoft@gmail.com"});
                    i.putExtra(Intent.EXTRA_TEXT, report);
                    i.putExtra(Intent.EXTRA_SUBJECT, "App Crashed");
                    startActivity(Intent.createChooser(i, "Send Mail via:"));
//                    finish();
                } else {
                    startSplash();
                }
                dialog.dismiss();
            }
        };
        b.setCancelable(false);
        b.setPositiveButton("Send Report", ocl);
        b.setNegativeButton("Restart", ocl);
        b.create().show();
    }
}
