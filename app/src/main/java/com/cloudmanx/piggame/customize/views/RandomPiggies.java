package com.cloudmanx.piggame.customize.views;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.MyLayoutParams;
import com.cloudmanx.piggame.models.WayData;

import java.util.Random;
import java.util.concurrent.Future;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/8 下午3:46
 */
public class RandomPiggies extends ViewGroup {

    private Random mRandom;
    private Future mTask;
    private volatile boolean isNeed;
    private OnTouchListener mOnTouchListener;
    private int mDropWidth,mDropHeight,//小猪接受触摸事件的长宽
            mRunWidth;//小猪实际宽度

    public RandomPiggies(Context context) {
        this(context,null);
    }

    public RandomPiggies(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RandomPiggies(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(){
        mRandom = new Random();
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.anim_drop_left);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
        mDropWidth = drawable.getIntrinsicWidth();
        mDropHeight = drawable.getIntrinsicHeight();

        mOnTouchListener = (v,event) ->{
            int x = (int) (event.getX() + v.getLeft() + v.getTranslationX());
            int y = (int) event.getY() + v.getTop();
            MyLayoutParams lp = (MyLayoutParams)v.getLayoutParams();
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    ((ViewPropertyAnimator)v.getTag()).cancel();
                    ((ImageView)v).setImageResource(lp.isLeft ? R.drawable.anim_drop_right:R.drawable.anim_drop_left);
                    AnimationDrawable drawable1 = (AnimationDrawable) ((ImageView)v).getDrawable();
                    drawable1.start();
                    MyLayoutParams layoutParams = new MyLayoutParams((int)(mDropWidth*lp.scale) ,(int)(mDropHeight * lp.scale));
                    layoutParams.x = (int)(v.getX() - (mDropWidth - mRunWidth));
                    layoutParams.y = (int) v.getY();
                    lp.isDrag = true;
                    layoutParams.scale = lp.scale;
                    layoutParams.isLeft = lp.isLeft;
                    v.setLayoutParams(layoutParams);
                    v.setTag(R.id.position_data,new WayData(x,y));
                    requestLayout();
                    break;
                case MotionEvent.ACTION_MOVE:
                    lp.isDrag = true;
                    int lastX = 0, lastY = 0;
                    WayData data = (WayData) v.getTag(R.id.position_data);
                    if (data != null){
                        lastX = data.x;
                        lastY = data.y;
                    }
                    lp.x = v.getLeft() + (x - lastX);
                    lp.y = v.getTop() + (y - lastY);
                    requestLayout();
                    v.setTag(R.id.position_data,new WayData(x,y));
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    //手指松开后,恢复播放动画
                    //根据 时间 * 速度 = 路程
                    //先计算出小猪的速度(因为每个小猪的速度都是随机的)
                    //然后根据速度和剩下的距离计算出需要的时长
                    lp.isDrag = true;
                    lastX = 0;
                    lastY = 0;
                    data = (WayData) v.getTag(R.id.position_data);
                    if (data != null){
                        lastX = data.x;
                        lastY = data.y;
                    }
                    lp.x = v.getLeft() + (x - lastX);
                    lp.y = v.getTop() + (y - lastY);
                    requestLayout();
                    ((ImageView) v).setImageResource(lp.isLeft ? R.drawable.anim_run_right2 : R.drawable.anim_run_left2);
                    drawable1 = (AnimationDrawable) ((ImageView) v).getDrawable();
                    drawable1.start();
                    v.setTranslationX(lp.isLeft ? v.getWidth() + v.getTranslationX() + lp.x : -(getWidth() + v.getWidth()) + v.getWidth() + v.getTranslationX() + lp.x);
                    ViewPropertyAnimator animator =v.animate();
                    float oldSpeed = (float) (getWidth() + v.getWidth()) / ((long) v.getTag(R.id.current_duration));
//                    float distance = Math.abs(lp.isLeft ? (getWidth() + v.getWidth() : -(getWidth() + v.getWidth())))
                    break;
            }
            return true;
        };
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private static class AnimatorListener implements Animator.AnimatorListener{

        private ViewGroup mViewGroup;
        private View mView;
        private boolean isHasDragAction;

        public AnimatorListener(ViewGroup viewGroup, View view) {
            mViewGroup = viewGroup;
            mView = view;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!isHasDragAction){
                mViewGroup.removeView(mView);
                mViewGroup = null;
                mView = null;
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            isHasDragAction = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
