package com.cloudmanx.piggame.activities;

import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.views.HomeView;
import com.cloudmanx.piggame.customize.views.LevelSelectView;
import com.cloudmanx.piggame.customize.views.LoadingView;
import com.cloudmanx.piggame.utils.ThreadPool;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    public static final int HOME = 0, //主页
            LEVEL_SELECT = 1,//关卡选择
            CLASSIC = 2, //经典模式
            PIGSTY = 3;//修猪圈模式
    public int mCurrentStatus = HOME;

    private FrameLayout mRootView;
    private HomeView mHomeView;
    private LoadingView mLoadingView;
    LevelSelectView mLevelSelectView;

    private AlertDialog mExitDialog;
    private MediaPlayer mPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mRootView = findViewById(R.id.root_view);
        mHomeView = findViewById(R.id.home_view);
        mLoadingView = findViewById(R.id.loading_view);
        if (mHomeView == null){
            Log.e(TAG, "initView: "+"mHomeView is null");
        }
        mHomeView.setOnButtonClickListener(new HomeView.OnButtonClickListener() {
            @Override
            public void onPigstyModeButtonClicked() {
                showFixPigstyModeLevelSelectView();
            }

            @Override
            public void onClassicModeButtonClicked() {

            }

            @Override
            public boolean onSoundButtonClicked() {
                boolean isMusicStopped = false;
                if (mPlayer != null){
                    if (mPlayer.isPlaying()){
                        mPlayer.pause();
                        isMusicStopped = true;
                    }else {
                        mPlayer.start();
                    }
                }
                return isMusicStopped;
            }

            @Override
            public void onExitButtonClicked() {

            }
        });

        mPlayer = MediaPlayer.create(this,R.raw.background_music);
        mPlayer.setOnPreparedListener(MediaPlayer::start);
        mPlayer.setLooping(true);
        mPlayer.setOnErrorListener((mp,what,extra)->{
            mPlayer = null;
            return false;
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

    private void showFixPigstyModeLevelSelectView(){
        if (!mLoadingView.isLoading){
            mLoadingView.startLoad(()->{
                mCurrentStatus = LEVEL_SELECT;
                mHomeView.setVisibility(View.GONE);
                mHomeView.stopShow();

                mLevelSelectView = new LevelSelectView(this);

                mRootView.addView(mLevelSelectView,0);
            });
        }
    }
}
