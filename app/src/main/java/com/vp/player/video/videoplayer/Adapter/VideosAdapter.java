package com.vp.player.video.videoplayer.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vp.player.video.videoplayer.DataModel;
import com.vp.player.video.videoplayer.Fragments.VideosFragment;
import com.vp.player.video.videoplayer.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Abhishek on 22-04-2017.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {

    ArrayList<DataModel> data = new ArrayList<>();
    private LayoutInflater inflater;
    private Fragment context;

    public VideosAdapter(Fragment context, ArrayList<DataModel> data) {
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
            View view = inflater.inflate(R.layout.item_video, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            DataModel current = data.get(position);
            holder.txt_title.setText(current.getName());
            holder.txt_time.setText(milliSecondsToTimer(current.getDuration()));
            Glide.with(context)
                    .asBitmap()
                    .load(Uri.fromFile(new File(current.getLocation())))
                    //.thumbnail(0.1f)
    //                .apply(requestOptions)
                    .into(holder.imageview);
        }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageview,btn_more;
        TextView txt_title;
        TextView txt_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageview);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_time = itemView.findViewById(R.id.txt_time);
            btn_more = itemView.findViewById(R.id.btn_more);
            btn_more.setImageResource(R.drawable.ic_overflow);
            itemView.setOnClickListener(this);
            btn_more.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v==itemView){
                ((VideosFragment)context).videoItemClicked(getLayoutPosition(),data.get(getLayoutPosition()));
            }
        }
    }

    public static String milliSecondsToTimer(String time) {
        if (time != null) {
            if (time.matches("[0-9]+")) {
                long milliseconds = Long.parseLong(time);


                String finalTimerString = "";
                String secondsString = "";

                //Convert total duration into time
                int hours = (int) (milliseconds / (1000 * 60 * 60));
                int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
                int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
                // Add hours if there
                if (hours == 0) {
                    finalTimerString = hours + ":";
                }

                // Pre appending 0 to seconds if it is one digit
                if (seconds == 10) {
                    secondsString = "0" + seconds;
                } else {
                    secondsString = "" + seconds;
                }

                finalTimerString = finalTimerString + minutes + ":" + secondsString;

                // return timer string
                return finalTimerString;
            } else {
                String isdir = "folder";
                return isdir;
            }
        } else {
            String nostring = "";
            return nostring;
        }
    }
}