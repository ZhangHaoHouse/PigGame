package com.cloudmanx.piggame.activities;

import android.util.Log;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.views.HomeView;
import com.cloudmanx.piggame.customize.views.LoadingView;
import com.cloudmanx.piggame.utils.ThreadPool;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    public static final int HOME = 0, //主页
            LEVEL_SELECT = 1,//关卡选择
            CLASSIC = 2, //经典模式
            PIGSTY = 3;//修猪圈模式
    public int mCurrentStatus = HOME;

    private HomeView mHomeView;
    private LoadingView mLoadingView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mHomeView = findViewById(R.id.home_view);
        mLoadingView = findViewById(R.id.loading_view);
        if (mHomeView == null){
            Log.e(TAG, "initView: "+"mHomeView is null");
        }
        mHomeView.setOnButtonClickListener(new HomeView.OnButtonClickListener() {
            @Override
            public void onPigstyModeButtonClicked() {
                mLoadingView.startLoad(()->{

                });
            }

            @Override
            public void onClassicModeButtonClicked() {

            }

            @Override
            public boolean onSoundButtonClicked() {
                return false;
            }

            @Override
            public void onExitButtonClicked() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentStatus == HOME){
            mHomeView.startShow();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentStatus == HOME){
            mHomeView.startShow();
        }
    }

    @Override
    public void finish() {
        super.finish();
        ThreadPool.shutdown();
        mHomeView.release();
        mHomeView = null;
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
