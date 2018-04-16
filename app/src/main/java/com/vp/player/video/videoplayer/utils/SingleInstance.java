package com.vp.player.video.videoplayer.utils;

import com.vp.player.video.videoplayer.DataModel2;

import java.util.ArrayList;

public class SingleInstance {
    private static final SingleInstance ourInstance = new SingleInstance();

    public static SingleInstance getInstance() {
        return ourInstance;
    }

    private SingleInstance() {
    }

    ArrayList<DataModel2> imagesList = new ArrayList<>();
    ArrayList<DataModel2> videoList = new ArrayList<>();

    public ArrayList<DataModel2> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<DataModel2> videoList) {
        this.videoList = videoList;
    }

    public ArrayList<DataModel2> getImagesList() {
        return imagesList;
    }

    public void setImagesList(ArrayList<DataModel2> imagesList) {
        this.imagesList = imagesList;
    }
}
