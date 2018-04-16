package com.vp.player.video.videoplayer.Fragments;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vp.player.video.videoplayer.Adapter.AppsAdapter;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.ShareFiles1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {
    String refresh = null;
    String path = refresh;
    ArrayList<DataModel2> arrayList;
    private String uril;
    private RecyclerView rv_list;
    private AppsAdapter adapter;
    String filelocation = "";
//    ArrayList<String> listselect = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_recycler, container, false);
        rv_list = myView.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        doAppStuff();
        return myView;
    }

    public void doAppStuff() {
        if (refresh != null) {
            path = refresh;
        }
        arrayList = new ArrayList<>();
        loadAppFiles();


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                TextView c = view.findViewById(R.id.type);
//                TextView name = view.findViewById(R.id.name);
//                uril = c.getText().toString();
//                title = name.getText().toString();
//                File file = new File(uril);
//                if (isApp(file)) {
//                    filelocation = c.getText().toString();
//                    if (!file.isDirectory()) {
//                        if (listselect.contains(filelocation)) {
//                            listselect.remove(filelocation);
//                            name.setTextColor(Color.BLACK);
//                        } else {
//                            listselect.add(filelocation);
//                            name.setTextColor(Color.parseColor("#1CAAC9"));
//                        }
//                    }
//
//                } else if (file.isDirectory()) {
//                    refresh = uril;
//                    doAppStuff();
//                }
//            }
//        });
    }


    public void loadAppFiles() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> apps = getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
                for (ResolveInfo info : apps) {
                    File inFile = new File(info.activityInfo.applicationInfo.publicSourceDir);
                    String remove = "/" + inFile.getName();
                    String remove2 = "/data/app/";
                    String currentLocation = inFile.getPath();
                    String left = currentLocation.replace(remove, "");
                    String currentTitle = left.replace(remove2, "");
                    String currentDuration = "10";//etriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            Drawable icon = info.getContext().getPackageManager().getApplicationIcon(pkg);
                    Drawable icon = info.activityInfo.applicationInfo.loadIcon(getActivity().getPackageManager());
                    String appName = info.activityInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
                    if (!currentLocation.startsWith("/sys")) {
                        DataModel2 d = new DataModel2(appName, currentLocation, currentDuration);
                        d.setDrawable(icon);
                        arrayList.add(d);
                    }

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new AppsAdapter(arrayList, AppsFragment.this);
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        rv_list.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    public boolean isApp(File inFile) {
        if (inFile.toString().toLowerCase().endsWith(".apk")) {
            return true;
        } else {
            return false;
        }
    }


    public void itemClicked(DataModel2 data) {
        File file = new File(data.getLocation());
        if (isApp(file)) {
            filelocation = data.getLocation();

        } else if (file.isDirectory()) {
            refresh = uril;
            doAppStuff();
        }
    }
}
