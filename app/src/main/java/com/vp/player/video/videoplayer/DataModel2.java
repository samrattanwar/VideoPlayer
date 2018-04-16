package com.vp.player.video.videoplayer;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Saaem on 08-Mar-18.
 */
public class DataModel2 implements Parcelable {

    String name;
    String location;
    String version_number;
    String duration;
    Drawable drawable;
    boolean isSelected = false;
    int counter = 0;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public DataModel2(String name, String location, String duration) {
        this.name = name;
        this.location = location;
        this.duration = duration;
    }


    protected DataModel2(Parcel in) {
        name = in.readString();
        location = in.readString();
        version_number = in.readString();
        duration = in.readString();
    }

    public static final Creator<DataModel2> CREATOR = new Creator<DataModel2>() {
        @Override
        public DataModel2 createFromParcel(Parcel in) {
            return new DataModel2(in);
        }

        @Override
        public DataModel2[] newArray(int size) {
            return new DataModel2[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDuration() {
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(location);
        parcel.writeString(version_number);
        parcel.writeString(duration);
    }
}

