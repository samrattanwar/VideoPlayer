package com.vp.player.video.videoplayer.Adapter;

/**
 * Created by Saaem on 08-Mar-18.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.Fragments.AppsFragment;
import com.vp.player.video.videoplayer.Fragments.VideosFragment;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.ShareFiles;

import java.io.File;
import java.util.ArrayList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> {

    private ArrayList<DataModel2> dataSet;
    Fragment mContext;
    private LayoutInflater inflater;

    public AppsAdapter(ArrayList<DataModel2> data, Fragment context) {
        inflater = LayoutInflater.from(context.getActivity());
        this.dataSet = data;
        this.mContext = context;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_apps, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataModel2 current = dataSet.get(position);

        if (current.isSelected()) {
            holder.rl_main.setBackgroundColor(Color.parseColor("#330C71E5"));
        } else {
            holder.rl_main.setBackgroundColor(Color.WHITE);
        }

        holder.txtName.setText(current.getName());
        Glide.with(mContext)
                .load(current.getDrawable())
                .into(holder.thumb);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtName;
        TextView txtType;
        ImageView thumb;
        RelativeLayout rl_main;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.name);
            txtType = itemView.findViewById(R.id.type);
            thumb = itemView.findViewById(R.id.imageview);
            rl_main = itemView.findViewById(R.id.rl_main);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                if (dataSet.get(getLayoutPosition()).isSelected()) {
                    dataSet.get(getLayoutPosition()).setSelected(false);
                } else {
                    dataSet.get(getLayoutPosition()).setSelected(true);
                }
                notifyDataSetChanged();
                ((AppsFragment) mContext).itemClicked(dataSet.get(getLayoutPosition()));
            }
        }
    }
}

