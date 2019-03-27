package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.cloudmanx.piggame.R;

/**
 * @version 1.0
 * @Description:主页
 * @Author: zhanghao
 * @Date: 2019/3/27 下午4:07
 */
public class HomeView extends RelativeLayout {

    public boolean isMute;//静音

    private  OnButtonClickListener mOnButtonClickListener;
    public HomeView(Context context) {
        this(context,null);
    }

    public HomeView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public HomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        LayoutInflater.from(getContext()).inflate(R.layout.view_home,this,true);
    }


    public interface OnButtonClickListener{
        void onPigstyModeButtonClicked();
        void inClassicModeButtonClicked();
        boolean onSoundButtonClicked();
        void onExitButtonClicked();
    }
}
