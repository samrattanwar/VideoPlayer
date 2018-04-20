package com.vp.player.video.videoplayer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.Fragments.VideosFragment;
import com.vp.player.video.videoplayer.MyApp;
import com.vp.player.video.videoplayer.R;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek on 22-04-2017.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {

    ArrayList<DataModel> data = new ArrayList<>();
    private LayoutInflater inflater;
    private Fragment context;
    private SparseBooleanArray mSelectedItemsIds;

    public VideosAdapter(Fragment context, ArrayList<DataModel> data) {
        this.context = context;
        inflater = LayoutInflater.from(context.getContext());
        this.data = data;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_video, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataModel current = data.get(position);
        try {
            holder.txt_title.setText(current.getName());
        } catch (Exception e) {
            holder.txt_title.setText("No Name");
        }
        try {
            holder.txt_time.setText(MyApp.milliSecondsToTimer(current.getDuration()));
            Glide.with(context)
                    .asBitmap()
                    .load(Uri.fromFile(new File(current.getLocation())))
                    //.thumbnail(0.1f)
                    //                .apply(requestOptions)
                    .into(holder.imageview);
        } catch (Exception e) {
        }
        if(mSelectedItemsIds.get(position)){

            holder.ic_selected.setVisibility(View.VISIBLE);
        }else {

            holder.ic_selected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        ImageView imageview, btn_more,ic_selected;
        TextView txt_title;
        TextView txt_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            ic_selected = itemView.findViewById(R.id.ic_selected);
            imageview = itemView.findViewById(R.id.imageview);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_time = itemView.findViewById(R.id.txt_time);
            btn_more = itemView.findViewById(R.id.btn_more);
            btn_more.setImageResource(R.drawable.ic_overflow);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            btn_more.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                ((VideosFragment) context).videoItemClicked(getLayoutPosition(), data.get(getLayoutPosition()));
            } else if (v == btn_more) {
                @SuppressLint("RestrictedApi") ContextThemeWrapper ctw = new ContextThemeWrapper(context.getActivity(), R.style.CustomPopupTheme);

                PopupMenu popup = new PopupMenu(context.getActivity(), btn_more);
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_overflow, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                         if (item.getItemId() == R.id.nav_share) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
                            intent.setType("video/*"); /* This example is sharing jpeg images. */

                            ArrayList<Uri> files = new ArrayList<Uri>();
                            List<String> filesToSend = new ArrayList<>();
                            filesToSend.add(data.get(getLayoutPosition()).getLocation());

                            for (String path : filesToSend /* List of the files you want to send */) {
                                File file = new File(path);
                                Uri uri = Uri.fromFile(file);
                                files.add(uri);
                            }

                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                            context.getActivity().startActivity(intent);
                        } else if(item.getItemId() == R.id.nav_rename){
                             ((VideosFragment)context).renameFile(getLayoutPosition(),data.get(getLayoutPosition()));
                         }else if(item.getItemId() == R.id.nav_delete){
                             ((VideosFragment)context).deleteFile(getLayoutPosition(),data.get(getLayoutPosition()));
                         }

                        return true;
                    }
                });

                popup.show();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        ((VideosFragment)context).mActionMode = null;
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


}
