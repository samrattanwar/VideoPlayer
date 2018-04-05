package com.vp.player.video.videoplayer;

/**
 * Created by samrat on 4/4/18.
 */

public class VideoModel {

    String videoname,thumbnailpath;

    public VideoModel(String videoname, String thumbnailpath) {
        this.videoname = videoname;
        this.thumbnailpath = thumbnailpath;
    }

    public String getVideoname() {
        return videoname;
    }

    public String getThumbnailpath() {
        return thumbnailpath;
    }
}
