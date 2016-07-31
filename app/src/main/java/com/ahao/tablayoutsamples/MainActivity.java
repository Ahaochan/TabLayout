package com.ahao.tablayoutsamples;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ahao.tablayout.TabEntity;
import com.ahao.tablayout.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager viewPager;

    private String[] mTitles = {"首页", "发现", "书架", "我的"};
    private int[] mIcons = {R.drawable.ic_home, R.drawable.ic_find, R.drawable.ic_library, R.drawable.ic_myself};
    List<Fragment> frags = new ArrayList<Fragment>();;
    List<TabEntity> entitys = new ArrayList<TabEntity>();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        for (int i = 0; i < mTitles.length; i++) {
            frags.add(MyFragment.newInstance(mTitles[i]));
            entitys.add(new TabEntity(mIcons[i], mTitles[i]));
        }

        tabLayout.setTabData(entitys);
        tabLayout.setViewPager(viewPager);

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), frags));
    }
}
