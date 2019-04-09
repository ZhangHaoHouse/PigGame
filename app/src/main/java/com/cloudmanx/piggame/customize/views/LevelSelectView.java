package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cloudmanx.piggame.R;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/9 下午5:44
 */
public class LevelSelectView extends LinearLayout {
    public LevelSelectView(Context context) {
        this(context,null);
    }

    public LevelSelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LevelSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_level_select,this,true);
    }
}
