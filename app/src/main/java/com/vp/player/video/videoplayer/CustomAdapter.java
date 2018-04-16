package com.vp.player.video.videoplayer;

/**
 * Created by Saaem on 08-Mar-18.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {

//    private ArrayList<DataModel> dataSet;
    Context mContext;
    Bitmap mbitmap;
    float displayMatrix;
    int dpWidthInPxVideo;
    int dpWidthInPxFolder;
    int dpHeightInPxVideo;
    int dpHeightInPxFolder;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtDuration;
        ImageView thumb;

    }


    public CustomAdapter(ArrayList<DataModel> data, Context context) {

        super(context, R.layout.row_item, data);
        if(data.size()==0){
            Log.d("size",data.size()+" no data set");
        }else{
            Log.d("size",data.size()+"");
        }
//        this.dataSet = data;
        this.mContext = context;
        this.displayMatrix = MyApp.getDisplayMatrix(mContext);
        dpWidthInPxVideo = (int) (90 * displayMatrix);
        dpHeightInPxVideo = (int) (65 * displayMatrix);

        dpWidthInPxFolder = (int) (75 * displayMatrix);
        dpHeightInPxFolder = (int) (50 * displayMatrix);
    }


    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        DataModel dataModel = (DataModel) object;


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName =  convertView.findViewById(R.id.name);
            viewHolder.txtType =  convertView.findViewById(R.id.type);
            //viewHolder.txtVersion =  convertView.findViewById(R.id.version_number);
            //viewHolder.info =  convertView.findViewById(R.id.item_info);
            viewHolder.txtDuration =  convertView.findViewById(R.id.duration);
            //  if(viewHolder.thumb!=null) {
            viewHolder.thumb =  convertView.findViewById(R.id.imageview);
            // }else {
            //Toast.makeText(getContext(),"Folders Found",Toast.LENGTH_SHORT).show();
            // }
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

       /* Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        */
        MainActivity mainActivity = new MainActivity();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(20));

        File f = new File(dataModel.getLocation());
        String timer = "";


        if (f.isDirectory()) {
            viewHolder.txtDuration.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpWidthInPxFolder, dpHeightInPxFolder);
            viewHolder.thumb.setLayoutParams(layoutParams);
        } else if (mainActivity.isVideo(f)) {
            viewHolder.txtDuration.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpWidthInPxVideo, dpHeightInPxVideo);
            viewHolder.thumb.setLayoutParams(layoutParams);
            Glide
                    .with(mContext)
                    .asBitmap()
                    .load(Uri.fromFile(new File(dataModel.getLocation())))
                    //.thumbnail(0.1f)
                    .apply(requestOptions)
                    .into(viewHolder.thumb);

            timer = milliSecondsToTimer(dataModel.getDuration());

        }


        // if (!timer.equals("folder")) {

        // }
  /*      mbitmap = ThumbnailUtils.createVideoThumbnail(dataModel.getLocation(), MediaStore.Video.Thumbnails.MICRO_KIND);
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setColor(Color.parseColor("#EEEEEE"));
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 10, 10, mpaint);// Round Image Corner 100 100 100 100
        viewHolder.thumb.setImageBitmap(imageRounded);

        viewHolder.thumb.setImageBitmap(mbitmap);
*/
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtDuration.setText(timer);
        viewHolder.txtType.setText(dataModel.getLocation());
        // viewHolder.txtVersion.setText(dataModel.getVersion_number());
//        viewHolder.info.setOnClickListener(this);
        //      viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
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

