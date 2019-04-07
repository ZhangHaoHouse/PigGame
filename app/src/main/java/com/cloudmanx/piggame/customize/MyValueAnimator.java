package com.cloudmanx.piggame.customize;

import android.os.SystemClock;

import com.cloudmanx.piggame.utils.ThreadPool;

/**
 * @version 1.0
 * @Description:
 * @Author: zhh
 * @Date: 2019/4/7 21:50
 */
public class MyValueAnimator {
    private long duration;
    private Object[] mTargets;
    private OnAnimatorEndListener mOnAnimatorEndListener;
    private OnAnimatorMiddleListener mOnAnimatorMiddleListener;
    private OnAnimatorUpdateListener mOnAnimatorUpdateListener;
    private volatile boolean isAnimationStopped;
    private float fromX,fromY,xPart,yPart;
    private boolean isRunOnCurrentThread,isOnAnimatorMiddleListenerCalled;

    public MyValueAnimator( float fromX, float toX, float fromY, float toY,Object... targets) {
        mTargets = targets;
        this.fromX = fromX;
        this.fromY = fromY;
        this.xPart = toX - fromX;
        this.yPart = toY - fromY;
    }

    public static MyValueAnimator create(float fromX, float toX, float fromY, float toY,Object... targets){
        return new MyValueAnimator(fromX,toX,fromY,toY,targets);
    }

    public MyValueAnimator setRunOnCurrentThread(){
        this.isRunOnCurrentThread = true;
        return this;
    }

    public void start(){
        stop();
        if (duration>0){
            if (isRunOnCurrentThread){
                startAnimation();
            }else {
                ThreadPool.getInstance().excute(this::startAnimation);
            }
        }
    }

    private void startAnimation(){
        isAnimationStopped = false;
        final  long startTime = SystemClock.uptimeMillis();
        long currentPlayedDuration;
        while ((currentPlayedDuration = SystemClock.uptimeMillis() - startTime) < duration){
            if (isAnimationStopped){
                break;
            }
            float progress = (float) currentPlayedDuration/(float)duration;
            if (!isOnAnimatorMiddleListenerCalled && mOnAnimatorMiddleListener != null && progress > .5f){
                isOnAnimatorMiddleListenerCalled = true;
                mOnAnimatorMiddleListener.onAnimatorMiddle();
            }
            if (mOnAnimatorUpdateListener != null){
                mOnAnimatorUpdateListener.onUpdate(progress);
            }else {
                update(progress);
            }
        }
        if (mOnAnimatorEndListener != null){
            mOnAnimatorEndListener.onAnimationEnd();
        }
    }

    public void stop(){
        isAnimationStopped =true;
    }

    private void update(float progress){
        if (isAnimationStopped){
            return;
        }
        float x = fromX + xPart*progress;
        float y = fromY + yPart*progress;
        if (mTargets != null){
            for (Object tmp:mTargets){
                if (tmp instanceof  Pig){
                    Pig pig = (Pig) tmp;
                    pig.setX(x);
                    pig.setY(y);
                }else if (tmp instanceof MyDrawable){
                    MyDrawable myDrawable = (MyDrawable) tmp;
                    myDrawable.setX(x);
                    myDrawable.setY(y);
                }
            }
        }
    }

    public boolean isAnimationPlaying(){
        return !isAnimationStopped;
    }

    public MyValueAnimator setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public MyValueAnimator setOnAnimatorEndListener(OnAnimatorEndListener onAnimatorEndListener) {
        mOnAnimatorEndListener = onAnimatorEndListener;
        return this;
    }

    public MyValueAnimator setOnAnimatorMiddleListener(OnAnimatorMiddleListener onAnimatorMiddleListener) {
        mOnAnimatorMiddleListener = onAnimatorMiddleListener;
        return this;
    }

    public MyValueAnimator setOnAnimatorUpdateListener(OnAnimatorUpdateListener onAnimatorUpdateListener) {
        mOnAnimatorUpdateListener = onAnimatorUpdateListener;
        return this;
    }

    public interface OnAnimatorUpdateListener{
        void onUpdate(float progress);
    }
    public interface OnAnimatorEndListener{
        void onAnimationEnd();
    }
    public interface OnAnimatorMiddleListener{
        void onAnimatorMiddle();
    }
}
