package com.cloudmanx.piggame.customize;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cloudmanx.piggame.utils.ThreadPool;

import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * @version 1.0
 * @Description:
 * @Author: zhh
 * @Date: 2019/4/7 22:18
 */
public class MyDrawable extends Drawable implements Cloneable {

    private final int mDelay;
    private final byte[] mLock;
    private Semaphore mSemaphore;
    private Bitmap[] mBitmaps;
    private Paint mPaint;
    private int mCurrentIndex;
    private float x,y;
    private Future mTask;
    private volatile boolean isPaused;



    public MyDrawable(int delay, Bitmap... bitmaps) {
        mSemaphore = new Semaphore(1);
        mDelay = delay;
        mBitmaps = bitmaps;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mLock = new byte[0];
    }

    public void start(){
        stop();
        mTask = ThreadPool.getInstance().excute(()->{
            while (true){
                synchronized (mLock){
                    while (isPaused){
                        try {
                            mLock.wait();
                        }catch (InterruptedException e){
                            return;
                        }
                    }
                }
                try {
                    Thread.sleep(mDelay);
                }catch (InterruptedException e){
                    return;
                }
                try {
                    mSemaphore.acquire();
                }catch (InterruptedException e){
                    return;
                }
                mCurrentIndex++;
                if (mCurrentIndex == mBitmaps.length){
                    mCurrentIndex = 0;
                }
                mSemaphore.release();
            }
        });
    }

    void pause(){
        isPaused = true;
    }

    void resume(){
        isPaused = false;
        synchronized (mLock){
            mLock.notifyAll();
        }
    }

    public void stop(){
        if (mTask != null){
            mTask.cancel(true);
            mTask = null;
            mCurrentIndex = 0;
        }
    }

    @Override
    public void draw( @NonNull Canvas canvas) {
        try {
            mSemaphore.acquire();;
        }catch (InterruptedException e){
            return;
        }
        canvas.drawBitmap(mBitmaps[mCurrentIndex],x,y,mPaint);
        mSemaphore.release();
    }

    public void release(){
        stop();
        if (mBitmaps != null){
            for (Bitmap bitmap : mBitmaps){
                if (bitmap != null && !bitmap.isRecycled()){
                    bitmap.recycle();
                }
            }
        }
        mBitmaps = null;
        mPaint = null;
        mTask = null;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Bitmap getBitmap(){
        Bitmap result = null;
        if (mBitmaps != null && mBitmaps.length > 0){
            result  = mBitmaps[0];
        }
        return result;
    }

    @Override
    public int getIntrinsicWidth() {
        if (mBitmaps == null || mBitmaps.length == 0){
            return  0 ;
        }
        return mBitmaps[0].getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        if (mBitmaps == null || mBitmaps.length == 0) {
            return 0;
        }
        return mBitmaps[0].getHeight();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter( @Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MyDrawable clone() {
        return new MyDrawable(0,mBitmaps[0]);
    }
}
