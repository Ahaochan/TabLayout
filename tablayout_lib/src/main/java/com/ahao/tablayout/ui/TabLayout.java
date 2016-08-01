package com.ahao.tablayout.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.ahao.tablayout.R;
import com.ahao.tablayout.entity.TabEntity;
import com.ahao.tablayout.listener.OnTabClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Avalon on 2016/7/29.
 */
public class TabLayout extends HorizontalScrollView{
    private static final String TAG = TabLayout.class.getSimpleName();
    private static final int DEFAULT_VISIBLE_ITEM_COUNT = 4;

    private Context mContext;

    /** 内嵌一个LinearLayout的Tab容器*/
    private LinearLayout mTabsContainer;
    private List<TabEntity> mTabEntities;
    /** 开放接口,取消对Viewpager等的依赖*/
    private OnTabClickListener listener;
    /** 默认显示4个Item,多出的Item用于滑动显示*/
    private int mVisibleCount = DEFAULT_VISIBLE_ITEM_COUNT;
    private int mTabCount;

    /** 默认居中滚动*/
    private float mScrollPivotX = 0.5f;

    /** 指示器的颜色*/
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

    /** 初始化View*/
    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        obtainStyledAttributes(mContext, attrs);

        mTabsContainer = new LinearLayout(this.mContext);
        addView(mTabsContainer);
        setHorizontalScrollBarEnabled(false);

        mTabEntities = new ArrayList<TabEntity>();
    }

    /** 从XML中初始化*/
    private void obtainStyledAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabLayout);
        int n = ta.getIndexCount();
        for(int i = 0; i < n; i++){
            int attr = ta.getIndex(i);
            if(attr == R.styleable.TabLayout_indicatorColor) {
                mIndicatorColor = ta.getColor(attr, Color.BLUE);
            } else if (attr == R.styleable.TabLayout_scrollPivotX){
                mScrollPivotX = ta.getFloat(attr, 0.5f);
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

    /** 将TabItemView加入LinearLayout */
    private void addTab(final int position, final CommonItemView tabView) {
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
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                resetOtherTabs();
                ((CommonItemView)view).setIndicatorAlpha(1.0f);
                if(listener != null) {
                    listener.OnTabClick(view, position);
                }
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
            CommonItemView child = (CommonItemView) mTabsContainer.getChildAt(i);
            child.setIndicatorAlpha(0.0f);
        }
    }

    /** 滚动Tab的方法*/
    public void scrollToTab(int position, float positionOffset){
        int count = mTabsContainer.getChildCount();
        if(count==0 || position<0 || position>=count){
            return;
        }

        if(positionOffset>0) {
            CommonItemView left = (CommonItemView) mTabsContainer.getChildAt(position);
            CommonItemView right = (CommonItemView) mTabsContainer.getChildAt(position + 1);

            left.setIndicatorAlpha(1 - positionOffset);//变色
            right.setIndicatorAlpha(positionOffset);//变色

            int leftCenter = left.getLeft()+left.getWidth()/2;
            int rightCenter = right.getLeft()+right.getWidth()/2;

            float scrollTo = leftCenter-getWidth()*mScrollPivotX;//left距离滚动中心位置
            float nextScrollTo = rightCenter-getWidth()*mScrollPivotX;//right距离滚动中心位置
            scrollTo((int) (scrollTo+(nextScrollTo-scrollTo)*positionOffset), 0);
        }


    }

    public void setOnTabClickListener(OnTabClickListener listener){
        this.listener = listener;
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

        CommonItemView tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = new CommonItemView(mContext, mTabEntities.get(i));
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
