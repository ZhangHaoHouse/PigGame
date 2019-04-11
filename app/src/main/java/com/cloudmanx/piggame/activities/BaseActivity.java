package com.cloudmanx.piggame.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/3/27 下午4:15
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static Map<String,BaseActivity> mAliveActivities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mAliveActivities ==null){
            mAliveActivities = new HashMap<>();
        }
        mAliveActivities.put(this.getClass().getSimpleName(),this);

        setContentView(getLayoutId());
        configWindow();
        initView();
    }

    private void configWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
            View.SYSTEM_UI_FLAG_IMMERSIVE|
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAliveActivities !=null){
            mAliveActivities.remove(this.getClass().getSimpleName());
            if (mAliveActivities.isEmpty()){
                mAliveActivities = null;
            }
        }
    }
}
