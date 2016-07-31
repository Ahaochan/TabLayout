package com.ahao.tablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Avalon on 2016/7/29.
 */
public class TabLayout extends HorizontalScrollView {
    private static final String TAG = TabLayout.class.getSimpleName();
    private static final int DEFAULT_VISIBLE_ITEM_COUNT = 4;

    private Context mContext;

    private LinearLayout mTabsContainer;
    private ViewPager mViewPager;
    private List<TabEntity> mTabEntities;
    private int mVisibleCount = DEFAULT_VISIBLE_ITEM_COUNT;
    private int mTabCount;


    private int mIndicatorColor = Color.BLUE;

    private int mTitleSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    private int mTitleColor = Color.BLACK;
    private int mTitleGravity = Gravity.BOTTOM;


    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    /** 从XML中初始化*/
    private void obtainStyledAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabLayout);
        int n = ta.getIndexCount();
        for(int i = 0; i < n; i++){
            int attr = ta.getIndex(i);
            if(attr == R.styleable.TabLayout_indicatorColor) {
                mIndicatorColor = ta.getColor(attr, Color.BLUE);
            } else if (attr == R.styleable.TabLayout_textSize) {
                mTitleSize = (int) ta.getDimension(attr,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,getResources().getDisplayMetrics()));
            } else if(attr == R.styleable.TabLayout_textGravity){
                mTitleGravity = ta.getInt(attr, Gravity.BOTTOM);
            } else if(attr == R.styleable.TabLayout_visibleCount){
                mVisibleCount = ta.getInt(attr, DEFAULT_VISIBLE_ITEM_COUNT);
                if(mVisibleCount<0){
                    mVisibleCount = DEFAULT_VISIBLE_ITEM_COUNT;
                }
            }
        }
        ta.recycle();
    }

    /** 初始化View*/
    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        obtainStyledAttributes(mContext, attrs);

        mTabsContainer = new LinearLayout(this.mContext);
        addView(mTabsContainer);

        mTabEntities = new ArrayList<TabEntity>();
    }

    /** 将TabItemView加入LinearLayout */
    private void addTab(final int position, final TabItemView tabView) {
        tabView.setIndicatorColor(mIndicatorColor);

        tabView.setIconBitmap(mTabEntities.get(position).getIconResId());

        tabView.setTitle(mTabEntities.get(position).getTitle());
        tabView.setTitleColor(mTitleColor);
        tabView.setTitleSize(mTitleSize);
        tabView.setTitleGravity(mTitleGravity);

        if(position == 0){
            tabView.setIndicatorAlpha(1.0f);
        }

        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetOtherTabs();
                tabView.setIndicatorAlpha(1.0f);
                mViewPager.setCurrentItem(position);
            }
        });

        /** 每一个Tab的布局参数 */
//        Log.i(TAG, "lp:"+tabView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                getScreenWidth()/mVisibleCount, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTabsContainer.addView(tabView, position, lp);
    }

    /** 重置所有TabItemView*/
    private void resetOtherTabs() {
        for(int i = 0; i < mTabsContainer.getChildCount(); i++){
            TabItemView child = (TabItemView) mTabsContainer.getChildAt(i);
            child.setIndicatorAlpha(0.0f);
        }
    }

    /** 设置ViewPager*/
    public void setViewPager(ViewPager viewPager) {
        setViewPager(viewPager, 0);
    }

    /** 设置ViewPager*/
    public void setViewPager(ViewPager viewPager, int position) {
        if(viewPager == null){
            throw new IllegalStateException("viewPager can not be NULL or EMPTY !");
        }
        mViewPager = viewPager;
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled:"+position+","+positionOffset+","+positionOffsetPixels);
                if(positionOffset>0) {
                    final int i = position;
                    TabItemView leftView = (TabItemView) mTabsContainer.getChildAt(i);
                    TabItemView rightView = (TabItemView) mTabsContainer.getChildAt(i + 1);
                    leftView.setIndicatorAlpha(1 - positionOffset);
                    rightView.setIndicatorAlpha(positionOffset);
                }
            }
        });
    }

    /** 添加数据 */
    public void setTabData(List<TabEntity> entitys) {
        if (entitys == null || entitys.size() <= 0) {
            throw new IllegalStateException("entitys can not be NULL or EMPTY !");
        }
        mTabEntities = entitys;
        notifyDataSetChanged();
    }

    /** 更新数据 */
    private void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        this.mTabCount = mTabEntities.size();

        TabItemView tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = new TabItemView(mContext, mTabEntities.get(i));
            tabView.setTag(i);
            addTab(i, tabView);
        }
    }

    private int getScreenWidth(){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
