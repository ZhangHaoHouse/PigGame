package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cloudmanx.piggame.R;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/3/28 上午10:56
 */
public class LoadingView extends SurfaceView implements Runnable {

    private int mCenterX,mCenterY,mMaxRadius;
    private Paint mPaint;
    private SurfaceHolder mSurfaceHolder;

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

//    public void startLoad()

    @Override
    public void run() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w/2;
        mCenterY = h/2;
        mMaxRadius = (int)Math.sqrt(Math.pow(w,2)+Math.pow(h,2))/2;
    }

    public interface OnAnimationFinishListener{

    }
}
