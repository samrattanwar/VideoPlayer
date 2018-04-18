package com.vp.player.video.videoplayer.Adapter;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.Fragments.FilesFragment;
import com.vp.player.video.videoplayer.Fragments.VideoShareFragment;
import com.vp.player.video.videoplayer.MyApp;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.ShareFiles;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Abhishek on 22-04-2017.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyViewHolder> {

    ArrayList<DataModel2> data = new ArrayList<>();
    private LayoutInflater inflater;
    private Fragment context;

    public FilesAdapter(Fragment context, ArrayList<DataModel2> data) {
        this.context = context;
        inflater = LayoutInflater.from(context.getContext());
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_files, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataModel2 current = data.get(position);


        if(current.isSelected()){
           holder.img_selection.setImageResource(R.drawable.checkmark_icon);
        }else {
            holder.img_selection.setImageResource(R.drawable.checkmark_unfilled);
        }


        holder.txt_time.setText(MyApp.milliSecondsToTimer(current.getDuration()));
        ShareFiles mainActivity = new ShareFiles();
        File f = new File(current.getLocation());
        if (f.isDirectory()) {
            holder.txt_title.setText(current.getName()+" ("+current.getCounter()+")");
            holder.img_selection.setVisibility(View.GONE);
            Glide
                    .with(context)
                    .load(R.drawable.foldericon)
                    .into(holder.imageview);

        } else if (mainActivity.isVideo(f)) {
            holder.txt_title.setText(current.getName());
            holder.img_selection.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .asBitmap()
                    .load(Uri.fromFile(new File(current.getLocation())))
                    //.thumbnail(0.1f)
//                    .apply(requestOptions)
                    .into(holder.imageview);

        } else if (mainActivity.isMusic(f)) {
            holder.img_selection.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(R.drawable.music)
                    .into(holder.imageview);


        } else if (mainActivity.isImage(f)) {
            holder.img_selection.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .asBitmap()
                    .load(Uri.fromFile(new File(current.getLocation())))
                    //.thumbnail(0.1f)
//                    .apply(requestOptions)
                    .into(holder.imageview);

        } else if (mainActivity.isApp(f)) {
            holder.img_selection.setVisibility(View.VISIBLE);
            //Drawable icon = getPackageManager().getApplicationIcon("ur_package");
            try {
                Glide
                        .with(context)
                        .load(R.drawable.androidicon)
                        .into(holder.imageview);
            } catch (Exception e) {
                Glide
                        .with(context)
                        .load(R.drawable.androidicon)
                        .into(holder.imageview);
            }
        } else {
            holder.img_selection.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(R.drawable.unknown)
                    .into(holder.imageview);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageview;
        TextView txt_title;
        TextView txt_time;
        ImageView img_selection;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageview);
            txt_title = itemView.findViewById(R.id.name);
            txt_time = itemView.findViewById(R.id.duration);
            img_selection = itemView.findViewById(R.id.img_selection);
            itemView.setOnClickListener(this);
            img_selection.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                ((FilesFragment) context).onItemClicked(data.get(getLayoutPosition()));
            } else if (v == img_selection) {
                if (data.get(getLayoutPosition()).isSelected()) {
                    data.get(getLayoutPosition()).setSelected(false);
                } else {
                    data.get(getLayoutPosition()).setSelected(true);
                }
                ((FilesFragment)context).addRemoveItem(data.get(getLayoutPosition()));
                notifyDataSetChanged();
            }
        }
    }


}
