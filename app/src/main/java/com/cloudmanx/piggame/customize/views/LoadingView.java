package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/3/28 上午10:56
 */
public class LoadingView extends SurfaceView implements Runnable {
    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void run() {

    }
}
