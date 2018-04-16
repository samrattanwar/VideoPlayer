package com.vp.player.video.videoplayer.Fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vp.player.video.videoplayer.Adapter.FilesAdapter;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.ShareFiles1;

import java.io.File;
import java.util.ArrayList;

public class FilesFragment extends Fragment {

    private RecyclerView rv_list;
    String refresh = null;
    String path = refresh;
    File dir = Environment.getExternalStorageDirectory();
    ArrayList<DataModel2> arrayList;
    private FilesAdapter adapter;
    private String uril;
    String filelocation = "";
    //    ArrayList<String> listselect = new ArrayList<String>();
    int isBackPressed = 0;
//    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_recycler, container, false);
        rv_list = myView.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (refresh == null) {
            path = dir.getAbsolutePath();
        }
        doStuff();

        myView.setFocusableInTouchMode(true);
        myView.requestFocus();
        myView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (path == dir.getAbsolutePath()) {
                        if (isBackPressed == 0) {
                            getActivity().finish();
                        } else {
                            --isBackPressed;
                        }
                    } else {
                        refresh = dir.getAbsolutePath();
//            toolbar.setTitle("Folders");
                        doStuff();
                    }
//            super.onBackPressed();
//                    isBackPressed = false;
                    return true;
                }
//                Toast.makeText(getActivity(), "other stuff pressed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return myView;
    }

    public void doStuff() {
        if (refresh != null) {
            path = refresh;
        }
        arrayList = new ArrayList<>();
        allfiles();


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                TextView c = view.findViewById(R.id.type);
//                TextView name = view.findViewById(R.id.name);
//                uril = c.getText().toString();
//                title = name.getText().toString();
//                File file = new File(uril);
//
//                filelocation = c.getText().toString();
//                if (!file.isDirectory()) {
//                    if (listselect.contains(filelocation)) {
//                        listselect.remove(filelocation);
//                        name.setTextColor(Color.BLACK);
//                    } else {
//                        listselect.add(filelocation);
//                        name.setTextColor(Color.parseColor("#1CAAC9"));
//                    }
//                }
//                if (file.isDirectory()) {
//                    doStuff();
//                }
//            }
//        });
    }

    public void allfiles() {
        loadAllFiles();
    }

    public void loadAllFiles() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                File f = new File(path);
                File[] files = f.listFiles();
                for (File inFile : files) {

                    String currentTitle = inFile.getName();
                    String currentLocation = inFile.getPath();
                    String currentDuration = "dir";
                    DataModel2 data = new DataModel2(currentTitle, currentLocation, currentDuration);
                    try{
                        data.setCounter(inFile.listFiles().length);
                    }catch (Exception e){}
                    arrayList.add(data);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new FilesAdapter(FilesFragment.this, arrayList);
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        rv_list.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    public void onItemClicked(DataModel2 data) {
        uril = data.getLocation();
        File file = new File(uril);

        filelocation = data.getLocation();

        if (file.isDirectory()) {
            refresh = uril;
            ++isBackPressed;
            doStuff();

        }
    }

    public void addRemoveItem(DataModel2 data) {
        uril = data.getLocation();
        File file = new File(uril);
        if (!file.isDirectory()) {
            if (((ShareFiles1) getActivity()).listselect.contains(filelocation)) {
                ((ShareFiles1) getActivity()).listselect.remove(filelocation);
//                name.setTextColor(Color.BLACK);
            } else {
                ((ShareFiles1) getActivity()).listselect.add(filelocation);
//                name.setTextColor(Color.parseColor("#1CAAC9"));
            }
        }
    }
}
