package tcking.github.com.giraffeplayer2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.widget.Toast;

import com.github.tcking.giraffeplayer2.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.tml.sharethem.sender.SHAREthemActivity;
import com.tml.sharethem.sender.SHAREthemService;
import com.tml.sharethem.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tcking on 2017
 */

public class PlayerActivity extends BasePlayerActivity {

    protected ArrayList<String> arrayList = new ArrayList<String>();

    String test;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giraffe_player_activity);
        //finish();
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        VideoInfo videoInfo = intent.getParcelableExtra("__video_info__");
        if (videoInfo == null) {
            finish();
            return;
        }
        PlayerManager.getInstance().releaseByFingerprint(videoInfo.getFingerprint());
        VideoView videoView = (VideoView) findViewById(R.id.video_view);
        videoView.videoInfo(videoInfo);
        loadfolderfiles(videoView);
         test = Integer.toString(arrayList.size());


        PlayerManager.getInstance().getPlayer(videoView).start();
        GiraffePlayer player = videoView.getPlayer();
        player.start();
    }



    public void loadad(){
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId("9362697259187932/4726345513");

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

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadad();
    }

    public void sharefiles(Context context){
        Toast.makeText(context, "Unable to share this video, Try again", Toast.LENGTH_SHORT).show();
  //      final Intent intent = new Intent("my.package.action.MY_ACTION");
  //      intent.addCategory("com.github.tcking.giraffeplayer2");
//        getApplication().startActivity(intent);
    }public void loadfolderfiles(VideoView videoView){

        if (videoView.getVideoInfo().getTitle() != null) {
            String titler = videoView.getVideoInfo().getTitle().toString();
            String totrim = videoView.getVideoInfo().getUri().toString();
            String toremove = "/" + titler;
            String trimmed = totrim.replace(toremove, "");
            File f = new File(trimmed);
            File[] files = f.listFiles();
            int i = 1;
            SharedPreferences playlist = getApplicationContext().getSharedPreferences("playlist", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = playlist.edit();
            for (File inFile : files) {
                //Toast.makeText(this, "its being called again", Toast.LENGTH_SHORT).show();
  /*          if (inFile.isDirectory()) {
                String currentTitle  = inFile.getName();
                String currentLocation = inFile.getPath();
                String currentDuration = "dir";
                arrayList.add(new DataModel(currentTitle, currentLocation, currentDuration));
            }*/
                if (isVideo(inFile)&&!arrayList.contains(inFile)) {
                    //MediaMetadataRetriever retriever = new MediaMetadataRetr
                    String currentLocation = inFile.getPath();
                    //Toast.makeText(this, "video"+Integer.toString(i), Toast.LENGTH_SHORT).show();
//                retriever.setDataSource(getApplicationContext(), Uri.fromFile(inFile));

                    arrayList.add(currentLocation);
                    editor.putString("video"+Integer.toString(i), currentLocation);
                    i++;
                }
            }
            editor.putInt("size", arrayList.size());
            editor.putInt("initial", 1);
            editor.commit();
        }
    }

    public boolean isVideo(File inFile){
        if (inFile.toString().toLowerCase().endsWith(".mp4")
                ||inFile.toString().toLowerCase().endsWith(".avi")
                ||inFile.toString().toLowerCase().endsWith(".mpeg4")
                ||inFile.toString().toLowerCase().endsWith(".mpg")
                //  ||inFile.toString().toLowerCase().endsWith(".gif")
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

    @Override
    public void onBackPressed() {
        loadad();
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ExitActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
       // Toast.makeText(this, "6546", Toast.LENGTH_SHORT).show();

    }
    public String getsize (){
        return test;
    }

    public ArrayList<String> getArrayList(){
        return arrayList;
    }
}
