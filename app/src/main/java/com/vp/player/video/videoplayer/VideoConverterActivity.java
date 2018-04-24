package com.vp.player.video.videoplayer;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.vp.player.video.videoplayer.Adapter.VideoConverterAdapter;
import com.vp.player.video.videoplayer.Adapter.VideosShareAdapter;
import com.vp.player.video.videoplayer.utils.ExceptionHandler;
import com.vp.player.video.videoplayer.utils.SingleInstance;

import java.io.File;
import java.util.ArrayList;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class VideoConverterActivity extends AppCompatActivity {

    String refresh = null;
    String path = refresh;
    File dir = Environment.getExternalStorageDirectory();
    private RecyclerView rv_list;
    ArrayList<DataModel2> arrayList;
    ArrayList<String> myList = new ArrayList<String>();
    DataModel2 dataModel;
    private String uril;
    private String title;
    String filelocation = "";
    private AdView mAdView;
    private VideoConverterAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_recycler);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Video to audio converter");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new GridLayoutManager(this, 1));
        if (refresh == null) {
            path = dir.getAbsolutePath();
        }
        arrayList = SingleInstance.getInstance().getVideoList();
        if (arrayList.size() == 0)
            getAllDir(Environment.getExternalStorageDirectory());
        else {
            adapter = new VideoConverterAdapter(this, arrayList);
            rv_list.setAdapter(adapter);
        }

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);

//        mAdView.setAdUnitId(getString(R.string.banner_home_footer));

//        mAdView.setAdSize(AdSize.BANNER);
//        AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("4F32629C31AC053EC5A69E481EF6CE75")
                .build();
        mAdView.loadAd(adRequest);
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


    public void getAllDir(final File dir) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File listFile[] = dir.listFiles();
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        if (listFile[i].isDirectory()) {
                            getAllDir(listFile[i]);
                        } else {
                            if (isVideo(listFile[i])) {
                                Log.e("Video file", listFile[i].getName());
                                try {
                                    String currentTitle = listFile[i].getName();
                                    String currentLocation = listFile[i].getPath();
                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    retriever.setDataSource(VideoConverterActivity.this.getApplicationContext(), Uri.fromFile(listFile[i]));
                                    String currentDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                                    retriever.release();
                                    arrayList.add(new DataModel2(currentTitle, currentLocation, currentDuration));
                                } catch (RuntimeException e) {
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new VideoConverterAdapter(VideoConverterActivity.this, arrayList);
                        rv_list.setAdapter(adapter);
                    }
                });
            }


        }).start();
    }


    public void convertVideo(DataModel2 dataModel) {

        final File flacFile = new File(dataModel.getLocation());
        final IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(final File convertedFile) {
                // So fast? Love it!
                MyApp.spinnerStop();
//                MyApp.showMassage(getActivity(), "Converted to mp3");
                AlertDialog.Builder b = new AlertDialog.Builder(VideoConverterActivity.this);
                b.setTitle("Audio converted").setMessage("Do you want to play this audio?").
                        setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        try {
                            Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
                            File file = new File(convertedFile.getAbsolutePath());
                            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                            String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            myIntent.setDataAndType(Uri.fromFile(file), mimetype);
                            startActivity(myIntent);
                        } catch (Exception e) {
                            String data = e.getMessage();
                            MyApp.showMassage(VideoConverterActivity.this, data);
                        }
                    }
                }).create().show();
            }

            @Override
            public void onFailure(Exception error) {
                MyApp.spinnerStop();
                MyApp.popMessage("Error!", "Error occurred during conversion",
                        VideoConverterActivity.this);
                // Oops! Something went wrong
            }
        };

        String[] items = {"MP3", "AAC", "FLAC", "M4A", "WAV", "WMA"};
        new AlertDialog.Builder(VideoConverterActivity.this).
                setTitle("Select your audio format")
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        switch (selectedPosition) {
                            case 0:
                                MyApp.spinnerStart(VideoConverterActivity.this, "Converting video to MP3");
                                AndroidAudioConverter.with(VideoConverterActivity.this)
                                        // Your current audio file
                                        .setFile(flacFile)
                                        // Your desired audio format
                                        .setFormat(AudioFormat.MP3)
                                        // An callback to know when conversion is finished
                                        .setCallback(callback)
                                        // Start conversion
                                        .convert();
                                break;
                            case 1:
                                MyApp.spinnerStart(VideoConverterActivity.this, "Converting video to AAC");
                                AndroidAudioConverter.with(VideoConverterActivity.this)
                                        // Your current audio file
                                        .setFile(flacFile)
                                        // Your desired audio format
                                        .setFormat(AudioFormat.AAC)
                                        // An callback to know when conversion is finished
                                        .setCallback(callback)
                                        // Start conversion
                                        .convert();
                                break;
                            case 2:
                                MyApp.spinnerStart(VideoConverterActivity.this, "Converting video to FLAC");
                                AndroidAudioConverter.with(VideoConverterActivity.this)
                                        // Your current audio file
                                        .setFile(flacFile)
                                        // Your desired audio format
                                        .setFormat(AudioFormat.FLAC)
                                        // An callback to know when conversion is finished
                                        .setCallback(callback)
                                        // Start conversion
                                        .convert();
                                break;
                            case 3:
                                MyApp.spinnerStart(VideoConverterActivity.this, "Converting video to M4A");
                                AndroidAudioConverter.with(VideoConverterActivity.this)
                                        // Your current audio file
                                        .setFile(flacFile)
                                        // Your desired audio format
                                        .setFormat(AudioFormat.MP3)
                                        // An callback to know when conversion is finished
                                        .setCallback(callback)
                                        // Start conversion
                                        .convert();
                                break;
                            case 4:
                                MyApp.spinnerStart(VideoConverterActivity.this, "Converting video to WAV");
                                AndroidAudioConverter.with(VideoConverterActivity.this)
                                        // Your current audio file
                                        .setFile(flacFile)
                                        // Your desired audio format
                                        .setFormat(AudioFormat.M4A)
                                        // An callback to know when conversion is finished
                                        .setCallback(callback)
                                        // Start conversion
                                        .convert();
                                break;
                            case 5:
                                MyApp.spinnerStart(VideoConverterActivity.this, "Converting video to WMA");
                                AndroidAudioConverter.with(VideoConverterActivity.this)
                                        // Your current audio file
                                        .setFile(flacFile)
                                        // Your desired audio format
                                        .setFormat(AudioFormat.WMA)
                                        // An callback to know when conversion is finished
                                        .setCallback(callback)
                                        // Start conversion
                                        .convert();
                                break;
                        }
                    }
                })
                .

                        show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
