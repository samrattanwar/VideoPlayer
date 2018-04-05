package com.vp.player.video.videoplayer.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vp.player.video.videoplayer.Fragments.FoldersFragment;
import com.vp.player.video.videoplayer.Fragments.MoreFragment;
import com.vp.player.video.videoplayer.Fragments.VideosFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    FoldersFragment foldersFragment;
    VideosFragment videosFragment;
    MoreFragment moreFragment;

    public ViewPagerAdapter(FragmentManager fm, FoldersFragment foldersFragment, VideosFragment videosFragment, MoreFragment moreFragment) {
        super(fm);
        this.foldersFragment = foldersFragment;
        this.videosFragment = videosFragment;
        this.moreFragment = moreFragment;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            return foldersFragment;
        } else if (position == 1) {
            return videosFragment;
        } else if (position == 2) {
            return moreFragment;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Folders";
        } else if (position == 1) {
            title = "Videos";
        } else if (position == 2) {
            title = "More";
        }
        return title;
    }
}