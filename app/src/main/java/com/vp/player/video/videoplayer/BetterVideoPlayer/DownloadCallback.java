package com.vp.player.video.videoplayer.BetterVideoPlayer;

import java.io.File;

public interface DownloadCallback {
    public void onDownload(File file);
    public void onFail(Exception e);
}
