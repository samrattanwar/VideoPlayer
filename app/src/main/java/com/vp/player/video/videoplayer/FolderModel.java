package com.vp.player.video.videoplayer;

/**
 * Created by samrat on 4/4/18.
 */

public class FolderModel {
    String foldername;
    Boolean isfolder;

    public FolderModel(String foldername, Boolean isfolder) {
        this.foldername = foldername;
        this.isfolder = isfolder;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public Boolean getIsfolder() {
        return isfolder;
    }

    public void setIsfolder(Boolean isfolder) {
        this.isfolder = isfolder;
    }
}
