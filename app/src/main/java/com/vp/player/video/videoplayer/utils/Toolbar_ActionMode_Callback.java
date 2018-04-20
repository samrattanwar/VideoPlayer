package com.vp.player.video.videoplayer.utils;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vp.player.video.videoplayer.Adapter.VideosAdapter;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.FormarPlayer;
import com.vp.player.video.videoplayer.Fragments.VideosFragment;
import com.vp.player.video.videoplayer.R;

import java.util.ArrayList;

/**
 * Created by SONU on 22/03/16.
 */
public class Toolbar_ActionMode_Callback implements ActionMode.Callback {

    private Context context;
    private VideosAdapter recyclerView_adapter;
    private ArrayList<DataModel> message_models;
    private boolean isListViewFragment;


    public Toolbar_ActionMode_Callback(Context context, VideosAdapter recyclerView_adapter, ArrayList<DataModel> message_models, boolean isListViewFragment) {
        this.context = context;
        this.recyclerView_adapter = recyclerView_adapter;

        this.message_models = message_models;
        this.isListViewFragment = isListViewFragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.multiselect_menu, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
//        if (Build.VERSION.SDK_INT < 11) {
//            MenuItemCompat.setShowAsAction(menu.findItem(R.id.count), MenuItemCompat.SHOW_AS_ACTION_NEVER);
//            MenuItemCompat.setShowAsAction(menu.findItem(R.id.delete_btn), MenuItemCompat.SHOW_AS_ACTION_NEVER);
//            MenuItemCompat.setShowAsAction(menu.findItem(R.id.share_btn), MenuItemCompat.SHOW_AS_ACTION_NEVER);
//        } else {
        menu.findItem(R.id.count).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.delete_btn).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.share_btn).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_btn:

                //Check if current action mode is from ListView Fragment or RecyclerView Fragment

                //If current fragment is recycler view fragment
                Fragment recyclerFragment = ((FormarPlayer) context).videosFragment;//Get recycler view fragment
                if (recyclerFragment != null)
                    //If recycler fragment not null
                    ((VideosFragment) recyclerFragment).deleteRows();//delete selected rows

                break;

            case R.id.share_btn:
                Fragment f = ((FormarPlayer) context).videosFragment;//Get recycler view fragment
                if (f != null)
                    //If recycler fragment not null
                    ((VideosFragment) f).shareFiles();//delete selected rows
                break;


        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {

        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode

        recyclerView_adapter.removeSelection();  // remove selection
        Fragment recyclerFragment = ((FormarPlayer)context).videosFragment;//Get recycler fragment
        if (recyclerFragment != null)
            ((VideosFragment) recyclerFragment).setNullToActionMode();//Set action mode null
    }

}
