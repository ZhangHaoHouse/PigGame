package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.MyValueAnimator;
import com.cloudmanx.piggame.utils.ThreadPool;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/3/28 上午10:56
 */
public class LoadingView extends SurfaceView implements Runnable {

    private static final String TAG = "LoadingView";
    private int mCenterX,mCenterY,mMaxRadius,mCurrentRadius;
    private Paint mPaint;
    private SurfaceHolder mSurfaceHolder;
    private boolean isOpen,isLoading,isProcessing;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorLoadingViewBackground));
        mPaint.setAntiAlias(true);
        setVisibility(INVISIBLE);
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    public void startLoad(final OnAnimationFinishListener listener){
        if(listener == null)
            return;
        isProcessing =isLoading =isOpen = true;
        startAnimation(() -> post(() -> {
            listener.onAnimationFinish();
            finishLoad();
        }));
    }

    public void finishLoad(){
        isOpen = false;
        startAnimation(()->post(()->{
            setVisibility(GONE);
            isLoading = isProcessing = false;
        }));
    }

    private int count = 0;
    private void startAnimation(MyValueAnimator.OnAnimatorEndListener finishListener){
        count = 0;
        setVisibility(VISIBLE);
        ThreadPool.getInstance().excute(this);
//        ValueAnimator.setFrameDelay(5);
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1f);
//        valueAnimator.setDuration(350L);
//        Log.e(TAG, "startAnimation: "+ValueAnimator.getFrameDelay());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float currentValue = (float)animation.getAnimatedValue();
//                Log.e(TAG, "onAnimationUpdate: "+(++count)+"/"+currentValue);
//                if (isOpen){
//                    currentValue = 1 - currentValue;
//                }
//                mCurrentRadius = (int)(currentValue* mMaxRadius);
//                doDraw();
//            }
//        });
//        valueAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                finishListener.onAnimationFinish();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        valueAnimator.start();

        MyValueAnimator.create(0,0,0,0).setDuration(350L)
                .setOnAnimatorUpdateListener( progress -> {
//                    Log.e(TAG, "OnAnimatorUpdateListener: "+(++count)+"/"+progress);
                    if (!isOpen){
                        progress  =1F - progress;
                    }
                    mCurrentRadius = (int) (mMaxRadius * progress);
//                    doDraw();
                }).setOnAnimatorEndListener(finishListener).start();
    }

    @Override
    public void run() {
        while (isProcessing){
            doDraw();
        }
    }

    private void doDraw(){
        Log.e(TAG, "doDraw: count/"+(++count));
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas == null)
            return;
        canvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);
        Log.e(TAG, "doDraw: mCurrentRadius/"+mCurrentRadius);
        canvas.drawCircle(mCenterX,mCenterY,mCurrentRadius,mPaint);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w/2;
        mCenterY = h/2;
        mMaxRadius = (int)Math.sqrt(Math.pow(w,2)+Math.pow(h,2))/2;
    }

    public interface OnAnimationFinishListener{
        void onAnimationFinish();
    }
}
