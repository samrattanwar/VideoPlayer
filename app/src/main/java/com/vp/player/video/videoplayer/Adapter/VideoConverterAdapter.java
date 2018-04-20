package com.vp.player.video.videoplayer.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vp.player.video.videoplayer.DataModel2;
import com.vp.player.video.videoplayer.MyApp;
import com.vp.player.video.videoplayer.R;
import com.vp.player.video.videoplayer.VideoConverterActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Abhishek on 22-04-2017.
 */

public class VideoConverterAdapter extends RecyclerView.Adapter<VideoConverterAdapter.MyViewHolder> {

    ArrayList<DataModel2> data = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public VideoConverterAdapter(Context context, ArrayList<DataModel2> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_video_converter, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataModel2 current = data.get(position);


        holder.txt_title.setText(current.getName());
        holder.txt_time.setText(MyApp.milliSecondsToTimer(current.getDuration()));
        File f = new File(current.getLocation());
        if (f.isDirectory()) {
            Glide
                    .with(context)
                    .load(R.drawable.foldericon)
                    .into(holder.imageview);

        } else {
            Glide.with(context)
                    .asBitmap()
                    .load(Uri.fromFile(new File(current.getLocation())))
                    //.thumbnail(0.1f)
//                    .apply(requestOptions)
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
        TextView txt_convert;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageview);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_time = itemView.findViewById(R.id.txt_time);
            txt_convert = itemView.findViewById(R.id.txt_convert);
            txt_convert.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (v == txt_convert) {
                ((VideoConverterActivity)context).convertVideo(data.get(getLayoutPosition()));
            }
        }
    }


}
