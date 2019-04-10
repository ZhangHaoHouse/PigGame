package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cloudmanx.piggame.R;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/10 上午10:51
 */
public class ClassicModeView extends FrameLayout {

    private String mLevelStringFormat;//关卡数的格式
    private ClassicMode mClassicMode;//经典模式实例
    private TextView mRefreshButton;//刷新按钮
    private TextView mLevelTextView;//显示关卡数的TextView
    private long mStartTime;//开始时间
    private int mCurrentLevel;//当前关卡
    private AlertDialog mGameResultDialog,mExitDialog,mHeartEmptyDialog;

    public ClassicModeView( @NonNull Context context) {
        this(context,null);
    }

    public ClassicModeView( @NonNull Context context,  @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ClassicModeView( @NonNull Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_classic_mode,this,true);
        mClassicMode = findViewById(R.id.item_group);
    }
}
