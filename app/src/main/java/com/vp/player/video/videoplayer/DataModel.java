package com.vp.player.video.videoplayer;

/**
 * Created by Saaem on 08-Mar-18.
 */
public class DataModel {

    String name;
    String location;
    String version_number;
    String duration;



    public DataModel(String name, String location, String duration) {
        this.name=name;
        this.location=location;
        this.duration = duration;
    }


    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDuration() {
        return duration;
    }
}

