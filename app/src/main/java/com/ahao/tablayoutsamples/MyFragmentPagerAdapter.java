package com.ahao.tablayoutsamples;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Avalon on 2016/7/30.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> frags;
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> frags) {
        super(fm);
        this.frags = frags;
    }

    @Override
    public int getCount() {
        return frags.size();
    }

    @Override
    public Fragment getItem(int position) {
        return frags.get(position);
    }
}
