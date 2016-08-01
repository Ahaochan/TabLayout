package com.ahao.tablayoutsamples;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ahao.tablayout.entity.TabEntity;
import com.ahao.tablayout.listener.OnTabClickListener;
import com.ahao.tablayout.ui.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] mTitles = {"首页", "发现", "书架", "我的"};
    private int[] mIcons = {R.drawable.ic_home, R.drawable.ic_find, R.drawable.ic_library, R.drawable.ic_myself};
    List<Fragment> frags = new ArrayList<Fragment>();
    List<TabEntity> entitys = new ArrayList<TabEntity>();

    private TabLayout tabLayout;
    private ViewPager viewPager;
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
        tabLayout.setOnTabClickListener(new OnTabClickListener() {
            @Override
            public void OnTabClick(View view, int position) {
                viewPager.setCurrentItem(position);
            }
        });

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), frags));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabLayout.scrollToTab(position, positionOffset);
            }
        });
    }
}
