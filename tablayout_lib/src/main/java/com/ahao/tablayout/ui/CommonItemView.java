package com.ahao.tablayout.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import com.ahao.tablayout.R;
import com.ahao.tablayout.entity.TabEntity;

/**
 * Created by Avalon on 2016/7/30.
 */
public class CommonItemView extends View {
    private static final String TAG = CommonItemView.class.getSimpleName();
    private static final String INSTANCE_STATE = "instance_state";
    private static final String STATE_ALPHA = "state_alpha";
    private Context mContext;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;

    private int mIndicatorColor = Color.BLUE;
    private float mAlpha = 0.0f;

    private Bitmap mIconBitmap;
    private Rect mIconRect;
    private PorterDuffXfermode mIconXfermode;


    private String mTitle = "";
    private int mTitleSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    private int mTitleColor = Color.BLACK;
    private Paint mTitlePaint;
    private Rect mTitleBounds = new Rect();
    private int mTitleGravity;

    private TabEntity entity;

    public CommonItemView(Context context, TabEntity entity) {
        super(context);
        this.entity = entity;
        this.mContext = context;
        initView(mContext, null);
    }

    public CommonItemView(Context context) {
        this(context, null, 0);
    }

    public CommonItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        Log.i(TAG, "onCreate");
        initView(mContext, attrs);
    }

    /** 获取xml属性 */
    private void obtainStyledAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonItemView);
        for(int i = 0; i < ta.getIndexCount(); i++){
            int attr = ta.getIndex(i);
            if (attr == R.styleable.CommonItemView_icon) {
                BitmapDrawable bd = (BitmapDrawable) ta.getDrawable(attr);
                if (bd != null) {
                    mIconBitmap = bd.getBitmap();
                }
            } else if(attr == R.styleable.CommonItemView_indicatorColor){
                mIndicatorColor = ta.getColor(attr, Color.BLUE);
            } else if(attr == R.styleable.CommonItemView_text) {
                mTitle = ta.getString(attr);
            } else if(attr == R.styleable.CommonItemView_textSize){
                mTitleSize = (int) ta.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            } else if(attr == R.styleable.CommonItemView_textColor) {
                mTitleColor = ta.getColor(attr, Color.BLACK);
            } else if(attr == R.styleable.CommonItemView_textGravity){
                mTitleGravity = ta.getInt(attr, Gravity.BOTTOM);
            }
        }
        ta.recycle();
    }

    /** 初始化View*/
    private void initView(Context context, AttributeSet attrs) {
        setPadding(5,5,5,10);
        if(entity!=null){
            mTitle = entity.getTitle();
            mIconBitmap = BitmapFactory.decodeResource(getResources(), entity.getIconResId());
        } else {
            obtainStyledAttributes(context, attrs);
        }

        mIconRect = new Rect();
        mIconXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mIndicatorColor);

        mTitlePaint = new Paint();
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setDither(true);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.getTextBounds(mTitle, 0, mTitle.length(), mTitleBounds);
    }

    /** 测量icon绘制区域*/
    private void measureIconRect() {
        int iconSize = 0;
        int left = 0;
        int top = 0;
        if(mTitleGravity == Gravity.TOP){
            iconSize = Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(),
                    getMeasuredHeight()-getPaddingTop()-getPaddingBottom()-mTitleBounds.height());
            left = getMeasuredWidth()/2-iconSize/2;
            top = getMeasuredHeight()-iconSize;
        } else if(mTitleGravity == Gravity.LEFT){
            iconSize = Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight()-mTitleBounds.width(),
                    getMeasuredHeight()-getPaddingTop()-getPaddingBottom());
            left = getMeasuredWidth()-iconSize;
            top = getMeasuredHeight()/2-iconSize/2;
        } else if(mTitleGravity == Gravity.RIGHT){
            iconSize = Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight()-mTitleBounds.width(),
                    getMeasuredHeight()-getPaddingTop()-getPaddingBottom());
            left = (getMeasuredWidth()-mTitleBounds.width())/2-iconSize/2;
            top = getMeasuredHeight()/2-iconSize/2;
        } else {
            iconSize = Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(),
                    getMeasuredHeight()-getPaddingTop()-getPaddingBottom()-mTitleBounds.height());
            left = getMeasuredWidth()/2-iconSize/2;
            top = (getMeasuredHeight()-mTitleBounds.height())/2-iconSize/2;
        }
        mIconRect = new Rect(left, top, left+iconSize, top+iconSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getWidthMeasureSpec(MeasureSpec.getMode(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
        int height = getHeightMeasureSpec(MeasureSpec.getMode(heightMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
//        Log.i(TAG, "onMeasure："+width+","+height);
        setMeasuredDimension(width, height);
        measureIconRect();
    }

    /** 测量View宽度*/
    private int getWidthMeasureSpec(int mode, int size) {
        int width;
        if(mode == MeasureSpec.EXACTLY){
            width = size;
        } else {
            if(mTitleGravity==Gravity.LEFT || mTitleGravity==Gravity.RIGHT){
                width = mTitleBounds.width()+mIconBitmap.getWidth()+getPaddingLeft()+getPaddingRight();
            } else {
                width = Math.max(mTitleBounds.width(), mIconBitmap.getWidth())+getPaddingLeft()+getPaddingRight();
            }
            if(mode == MeasureSpec.AT_MOST){
                width = Math.min(width, size);
            }
        }
//        Log.i(TAG, "getWidthMeasureSpec:"+width+","+mTitleBounds.width()+","+mIconBitmap.getWidth()+","+size);
        return width;
    }

    /** 测量View高度*/
    private int getHeightMeasureSpec(int mode, int size) {
        int height;
        if(mode == MeasureSpec.EXACTLY){
            height = size;
        } else {
            if(mTitleGravity==Gravity.LEFT || mTitleGravity==Gravity.RIGHT){
                height = Math.max(mIconBitmap.getHeight(), mTitleBounds.height())+getPaddingTop()+getPaddingBottom();
            } else {
                height = mTitleBounds.height()+mIconBitmap.getHeight()+getPaddingTop()+getPaddingBottom();
            }
            if(mode == MeasureSpec.AT_MOST){
                height = Math.min(height, size);
            }
        }
//        Log.i(TAG, "getHeightMeasureSpec:"+height+","+mTitleBounds.height()+","+mIconBitmap.getHeight()+","+size);
        return height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.i(TAG, "onDraw");
        int alpha = (int) Math.ceil(255*mAlpha);
        drawIcon(canvas, alpha);
        drawSourceText(canvas, alpha);
        drawIndicatorText(canvas, alpha);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /** 绘制图标*/
    private void drawIcon(Canvas canvas, int alpha) {
        Log.i(TAG,"drawIcon_Alpha:"+alpha+","+mIconXfermode.toString());
        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(mIconXfermode);
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }

    /** 绘制原始文字*/
    private void drawSourceText(Canvas canvas, int alpha) {
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setAlpha(255-alpha);
        drawTextByGravity(canvas);
    }

    /** 绘制变色文字*/
    private void drawIndicatorText(Canvas canvas, int alpha) {
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setColor(mIndicatorColor);
        mTitlePaint.setAlpha(alpha);
        drawTextByGravity(canvas);
    }

    /** 根据Gravity绘制文字*/
    private void drawTextByGravity(Canvas canvas) {
        if(mTitleGravity == Gravity.TOP){
            canvas.drawText(mTitle, mIconRect.left+mIconRect.width()/2-mTitleBounds.width()/2,
                    mTitleBounds.top+mTitleBounds.height(), mTitlePaint);
        } else if(mTitleGravity == Gravity.LEFT){
            canvas.drawText(mTitle, mIconRect.left-mTitleBounds.width(),
                    mIconRect.top+mIconRect.height()/2+mTitleBounds.height()/2, mTitlePaint);
        } else if(mTitleGravity == Gravity.RIGHT){
            canvas.drawText(mTitle, mIconRect.left+mIconRect.width(),
                    mIconRect.top+mIconRect.height()/2+mTitleBounds.height()/2, mTitlePaint);
        } else {
            canvas.drawText(mTitle, mIconRect.left+mIconRect.width()/2-mTitleBounds.width()/2,
                    mIconRect.bottom+mTitleBounds.height(), mTitlePaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE,super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void notifyDataSetChanged() {
        if(Looper.getMainLooper() == Looper.myLooper()){
            invalidate();
        } else {
            postInvalidate();
        }
    }


    /**
     * getter and setter of field
     */
    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        notifyDataSetChanged();
    }
    public int getIndicatorColor(){
        return mIndicatorColor;
    }

    public void setIndicatorAlpha(float alpha) {
        this.mAlpha = alpha;
        notifyDataSetChanged();
    }
    public float getIndicatorAlpha(){
        return mAlpha;
    }

    public void setEntity(TabEntity entity) {
        this.entity = entity;
        notifyDataSetChanged();
    }
    public TabEntity getEntity() {
        return entity;
    }

    public void setIconBitmap(@IdRes int iconResId){
        this.mIconBitmap = BitmapFactory.decodeResource(getResources(), iconResId);
        if(mIconRect != null) {
            notifyDataSetChanged();
        }
    }
    public void setIconBitmap(Bitmap iconBitmap){
        this.mIconBitmap = iconBitmap;
        if(mIconBitmap != null){
            notifyDataSetChanged();
        }
    }
    public Bitmap getIconBitmap(){
        return mIconBitmap;
    }

    public void setTitle(String title){
        this.mTitle = title;
        notifyDataSetChanged();
    }
    public String getTitle(){
        return mTitle;
    }

    public void setTitleColor(int titleColor) {
        this.mTitleColor = titleColor;
        notifyDataSetChanged();
    }
    public int getTitleColor(){
        return mTitleColor;
    }

    public void setTitleSize(int titleSize){
        this.mTitleSize = titleSize;
        notifyDataSetChanged();
    }
    public int getTitleSize(){
        return mTitleSize;
    }

    public void setTitleGravity(int gravity){
        this.mTitleGravity = gravity;
        notifyDataSetChanged();
    }
    public int getTitleGravity(){
        return mTitleGravity;
    }
}
