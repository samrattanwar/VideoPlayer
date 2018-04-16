package com.vp.player.video.videoplayer;

/**
 * Created by Saaem on 08-Mar-18.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CustomAdapter2 extends ArrayAdapter<DataModel2> implements View.OnClickListener{

    private ArrayList<DataModel2> dataSet;
    Context mContext;
    Bitmap mbitmap;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtDuration;
        ImageView thumb;
    }



    public CustomAdapter2(ArrayList<DataModel2> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel2 dataModel=(DataModel2)object;




        switch (v.getId())
        {

          /*  case R.id.item_info:

              //  Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                //        .setAction("No action", null).show();

                break;

*/
        }


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel2 dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            //viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            //viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);
            viewHolder.txtDuration = (TextView) convertView.findViewById(R.id.duration);
            //  if(viewHolder.thumb!=null) {
            viewHolder.thumb = (ImageView) convertView.findViewById(R.id.imageview);
            // }else {
            //Toast.makeText(getContext(),"Folders Found",Toast.LENGTH_SHORT).show();
            // }
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

       /* Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        */
        ShareFiles mainActivity = new ShareFiles();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(),new RoundedCorners(20));
        File f = new File(dataModel.getLocation());
        if(f.isDirectory()){
            Glide
                    .with(mContext)
                    .load(R.drawable.foldericon)
                    .into(viewHolder.thumb);

        }else if(mainActivity.isVideo(f)){
            Glide
                    .with(mContext)
                    .asBitmap()
                    .load(Uri.fromFile(new File(dataModel.getLocation())))
                    //.thumbnail(0.1f)
                    .apply(requestOptions)
                    .into(viewHolder.thumb);

        }else if(mainActivity.isMusic(f)){
            Glide
                    .with(mContext)
                    .load(R.drawable.music)
                    .into(viewHolder.thumb);


        }else if(mainActivity.isImage(f)){
            Glide
                    .with(mContext)
                    .asBitmap()
                    .load(Uri.fromFile(new File(dataModel.getLocation())))
                    //.thumbnail(0.1f)
                    .apply(requestOptions)
                    .into(viewHolder.thumb);

        }else if(mainActivity.isApp(f)){
            //Drawable icon = getPackageManager().getApplicationIcon("ur_package");
           try{
               Glide
                       .with(mContext)

                       .load(dataModel.getDrawable())
                       .into(viewHolder.thumb);
           }catch (Exception e){
               Glide
                       .with(mContext)
                       .load(R.drawable.androidicon)
                       .into(viewHolder.thumb);
           }
        }
        else{
            Glide
                    .with(mContext)
                    .load(R.drawable.unknown)
                    .into(viewHolder.thumb);
        }
        String timer = milliSecondsToTimer(dataModel.getDuration());

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
*/        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtDuration.setText(timer);
        viewHolder.txtType.setText(dataModel.getLocation());
        // viewHolder.txtVersion.setText(dataModel.getVersion_number());
//        viewHolder.info.setOnClickListener(this);
        //      viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    public static String milliSecondsToTimer(String time) {
        if( time!=null ) {
            if (time.matches("[0-9]+") && time.length() > 2) {
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
                return finalTimerString;}
        }else {
            String nostring = "";
            return nostring;
        }
        return "";
    }

}

