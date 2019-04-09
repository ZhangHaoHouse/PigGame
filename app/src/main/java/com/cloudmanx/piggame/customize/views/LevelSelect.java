package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.MyDrawable;
import com.cloudmanx.piggame.utils.BitmapUtil;

/**
 * @version 1.0
 * @Description:
 * @Author: zhh
 * @Date: 2019/4/9 22:08
 */
public class LevelSelect extends ViewGroup {

    private AnimationButton mItems[];
    private MyDrawable mItemDrawable,mItemDrawableDisable;
    private int mItemSize;
    private int mMaxCount;
    private OnLevelSelectedListener mOnLevelSelectedListener;

    public LevelSelect(Context context) {
        this(context,null);
    }

    public LevelSelect(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LevelSelect(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mItemSize = (int) getResources().getDimension(R.dimen.xhpx_128);
        mItemDrawable = new MyDrawable(0,BitmapUtil.getBitmapFromResource(getContext(),R.mipmap.ic_level_select_bg));
        mItemDrawableDisable = new MyDrawable(0,BitmapUtil.toGray(mItemDrawable.getBitmap()));
    }

    public void setValidItemCount(int count){
        if (count > mMaxCount){
            count = mMaxCount;
        }
        if (count<0){
            count = 0;
        }
        if (mItems != null){
            for (int i=0;i<count;i++){
                AnimationButton item = mItems[i];
                item.setText(String .valueOf(i+1));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    item.setBackground(mItemDrawable);
                }else {
                    item.setBackgroundDrawable(mItemDrawable);
                }
                item.setEnabled(true);
            }
        }
    }

    public void setMaxItemCount(int count){
        if (getChildCount()>0){
            removeAllViews();
        }
        mMaxCount = count;
        mItems = new AnimationButton[count];
        float textSize = getResources().getDimension(R.dimen.xhpx_48);
        OnClickListener onClickListener = v->{
            if (mOnLevelSelectedListener != null){
                mOnLevelSelectedListener.onSelected((int)v.getTag());
            }
        };
        for (int i = 0;i<count;i++){
            AnimationButton temp = new AnimationButton(getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                temp.setBackground(mItemDrawableDisable);
            }else {
                temp.setBackgroundDrawable(mItemDrawableDisable);
            }
            temp.setTag(i+1);
            temp.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
            temp.setTextColor(Color.WHITE);
            temp.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            temp.setGravity(Gravity.CENTER);
            temp.setOnClickListener(onClickListener);
            temp.setEnabled(false);
            mItems[i] = temp;
            addView(temp);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width =mItemSize * 5,height = 0;
        if (mItems != null){
            int heightCount = mItems.length /5;
            if (mItems.length % 5 > 0){
                heightCount++;
            }
            height = mItemSize * heightCount;
        }
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mItems != null){
            int maxWidth = mItemSize * 4;
            int currentWidth;
            int currentHeight = -mItemSize;
            for (int i = 0;i<mItems.length;i++){
                if (mItems[i] != null){
                    currentWidth = i * mItemSize;
                    if (currentWidth >= maxWidth){
                        currentWidth = i%5 * mItemSize;
                    }
                    if (i%5 == 0){
                        currentHeight += mItemSize;
                    }
                    int left,top,right,bottom;
                    left = currentWidth;
                    right = currentWidth + mItemSize;
                    top = currentHeight;
                    bottom = currentHeight + mItemSize;
                    mItems[i].layout(left,top,right,bottom);
                }
            }
        }
    }

    public void release(){
        if (mItems != null){
            for (AnimationButton tmp:mItems){
                if (tmp != null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        tmp.setBackground(null);
                    }else {
                        tmp.setBackgroundDrawable(null);
                    }
                    tmp.setOnClickListener(null);
                }
            }
            mItems = null;
        }
        mItemDrawable.release();
        mItemDrawableDisable.release();
        mOnLevelSelectedListener = null;
    }

    public void setOnLevelSelectedListener(OnLevelSelectedListener onLevelSelectedListener) {
        mOnLevelSelectedListener = onLevelSelectedListener;
    }

    public interface OnLevelSelectedListener{
        void onSelected(int level);
    }
}
