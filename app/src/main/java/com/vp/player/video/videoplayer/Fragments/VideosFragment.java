package com.vp.player.video.videoplayer.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vp.player.video.videoplayer.Adapter.VideosAdapter;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.FormarPlayer;
import com.vp.player.video.videoplayer.MyApp;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.VideoPlayerActivity;
import com.vp.player.video.videoplayer.utils.RecyclerClick_Listener;
import com.vp.player.video.videoplayer.utils.RecyclerTouchListener;
import com.vp.player.video.videoplayer.utils.Toolbar_ActionMode_Callback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.VideoInfo;

public class VideosFragment extends Fragment {

    ArrayList<DataModel> arrayList;
    String path;
    RecyclerView rv_videos;
    //    CustomAdapter adapter;
    private VideosAdapter adapter;
    private String uril;
    private String title;
    VideoInfo videoInfo;
    SharedPreferences preferences;
    int r = 0;
    public ActionMode mActionMode;
//    private TextView type, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_videos, container, false);
        arrayList = new ArrayList<>();
//        loadVideoFiles();
        rv_videos = myView.findViewById(R.id.rv_videos);
        rv_videos.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        int spacingInPixels = 10;
//        rv_videos.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        preferences = getActivity().getSharedPreferences("videolist", Context.MODE_PRIVATE);
        getAllDir(Environment.getExternalStorageDirectory());
//        type = myView.findViewById(R.id.type);
//        name = myView.findViewById(R.id.name);
        implementRecyclerViewClickListeners();
        return myView;
    }

    public void videoItemClicked(int position, DataModel data) {
//        TextView c = view.findViewById(R.id.type);
//        TextView name = view.findViewById(R.id.name);
        uril = data.getLocation();
        title = data.getName();
        File file = new File(uril);
        SharedPreferences playlist = getActivity().getSharedPreferences("playlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editory = playlist.edit();
//        int currenttrack = listView.getPositionForView(view) + 1;
        editory.putInt("current", position);
        editory.apply();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastplayed", uril);
        editor.apply();

        startActivity(new Intent(getContext(), VideoPlayerActivity.class).putExtra("path", uril));
//        videoInfo = new VideoInfo(Uri.parse(uril))
//                .setShowTopBar(true) //show mediacontroller top bar
//                .setBgColor(Color.BLACK)
//                .setTitle(title)
//                .setAspectRatio(0)
//                .setRetryInterval(r)
//                .setPortraitWhenFullScreen(true);//portrait when full screen
//
//        GiraffePlayer.play(getActivity(), videoInfo);

    }

//    public void setup(View view) {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                TextView c = view.findViewById(R.id.type);
//                TextView name = view.findViewById(R.id.name);
//                uril = c.getText().toString();
//                title = name.getText().toString();
//                File file = new File(uril);
//                SharedPreferences playlist = getActivity().getSharedPreferences("playlist", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editory = playlist.edit();
//                int currenttrack = listView.getPositionForView(view) + 1;
//                editory.putInt("current", currenttrack);
//                editory.apply();
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("lastplayed", uril);
//                editor.apply();
//
//                videoInfo = new VideoInfo(Uri.parse(uril))
//                        .setShowTopBar(true) //show mediacontroller top bar
//                        .setBgColor(Color.BLACK)
//                        .setTitle(title)
//                        .setAspectRatio(0)
//                        .setRetryInterval(r)
//                        .setPortraitWhenFullScreen(true);//portrait when full screen
//
//                GiraffePlayer.play(getActivity(), videoInfo);
//
//            }
//        });
//    }


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
                                String currentTitle = listFile[i].getName();
                                String currentLocation = listFile[i].getPath();
                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                retriever.setDataSource(getActivity().getApplicationContext(), Uri.fromFile(listFile[i]));
                                String currentDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                                retriever.release();
                                arrayList.add(new DataModel(currentTitle, currentLocation, currentDuration));
                            }
                        }
                    }

//            adapter = new CustomAdapter(arrayList, getActivity().getApplicationContext());
//            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//            listView.setAdapter(adapter);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new VideosAdapter(VideosFragment.this, arrayList);
                        rv_videos.setAdapter(adapter);
                    }
                });
            }


        }).start();
    }


    public void convertMp3(DataModel dataModel) {

        final File flacFile = new File(dataModel.getLocation());
        final IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(final File convertedFile) {
                // So fast? Love it!
                MyApp.spinnerStop();
//                MyApp.showMassage(getActivity(), "Converted to mp3");
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
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
//                        Intent intent = new Intent();
//                        intent.setAction(android.content.Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.parse(convertedFile.getPath()), "audio/*");
//                        startActivity(intent);

                        try {
                            Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
                            File file = new File(convertedFile.getAbsolutePath());
                            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                            String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            myIntent.setDataAndType(Uri.fromFile(file), mimetype);
                            startActivity(myIntent);
                        } catch (Exception e) {
                            String data = e.getMessage();
                            MyApp.showMassage(getContext(), data);
                        }
                    }
                }).create().show();
            }

            @Override
            public void onFailure(Exception error) {
                MyApp.spinnerStop();
                MyApp.popMessage("Error!", "Error occurred during conversion", getActivity());
                // Oops! Something went wrong
            }
        };

        String[] items = {"MP3", "AAC", "FLAC", "M4A", "WAV", "WMA"};
        new AlertDialog.Builder(getActivity())
                .setTitle("Select your audio format")
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        switch (selectedPosition) {
                            case 0:
                                MyApp.spinnerStart(getActivity(), "Converting video to MP3");
                                AndroidAudioConverter.with(getActivity())
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
                                MyApp.spinnerStart(getActivity(), "Converting video to AAC");
                                AndroidAudioConverter.with(getActivity())
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
                                MyApp.spinnerStart(getActivity(), "Converting video to FLAC");
                                AndroidAudioConverter.with(getActivity())
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
                                MyApp.spinnerStart(getActivity(), "Converting video to M4A");
                                AndroidAudioConverter.with(getActivity())
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
                                MyApp.spinnerStart(getActivity(), "Converting video to WAV");
                                AndroidAudioConverter.with(getActivity())
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
                                MyApp.spinnerStart(getActivity(), "Converting video to WMA");
                                AndroidAudioConverter.with(getActivity())
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
                .show();

    }

    //Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        rv_videos.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_videos, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null)
                    onListItemSelect(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    private void onListItemSelect(int position) {
        adapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = adapter.getSelectedCount() > 0;//Check if any items are already selected or not


        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity())
                    .startSupportActionMode(new Toolbar_ActionMode_Callback(getActivity(), adapter, arrayList, false));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(adapter
                    .getSelectedCount()) + " selected");


    }

    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    //Delete selected rows
    public void deleteRows() {
        SparseBooleanArray selected = adapter
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                File file = new File(arrayList.get(selected.keyAt(i)).getLocation());
                boolean deleted = file.delete();
                //If current id is selected remove the item via key
                arrayList.remove(selected.keyAt(i));
                adapter.notifyDataSetChanged();//notify adapter

            }
        }
        Toast.makeText(getActivity(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use

    }

    public void shareFiles() {
        SparseBooleanArray selected = adapter
                .getSelectedIds();//Get selected ids
        ArrayList<Uri> files = new ArrayList<Uri>();
        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                /* This example is sharing jpeg images. */
                List<String> filesToSend = new ArrayList<>();
                filesToSend.add(arrayList.get(selected.keyAt(i)).getLocation());

                for (String path : filesToSend /* List of the files you want to send */) {
                    File file = new File(path);
                    Uri uri = Uri.fromFile(file);
                    files.add(uri);
                }

            }
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("video/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        getActivity().startActivity(intent);
//        Toast.makeText(getActivity(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use

    }

    public void renameFile(final int position, final DataModel dataModel) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Rename");
        alertDialog.setMessage("Enter new file name");

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
//        alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();
                        if (password.isEmpty()) {
                            MyApp.showMassage(getActivity(), "Enter new file name");
                        } else {
//        String name = new File(dataModel.getLocation()).getName();

                            File file = new File(new File(dataModel.getLocation()).getParentFile().getAbsoluteFile(), dataModel.getName());

                            /* File (or directory) with new name */
                            File file2 = new File(new File(dataModel.getLocation()).getParentFile().getAbsoluteFile(), password+dataModel.getLocation().substring(dataModel.getLocation().lastIndexOf(".")));

                            if (file2.exists()) {
                                try {
                                    throw new java.io.IOException("File already exists!");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (file.renameTo(file2)) {
                                    arrayList.get(position).setName(password);
                                    adapter.notifyDataSetChanged();
                                }else{
                                    MyApp.showMassage(getActivity(),"Cannot change the name");
                                }

                            }
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void deleteFile(int layoutPosition, DataModel dataModel) {
        String path = dataModel.getLocation();
        if (new File(path).delete()) {
            MyApp.showMassage(getActivity(), "Deleted");
            arrayList.remove(layoutPosition);
            adapter.notifyDataSetChanged();
        }
    }
}
