package com.cloudmanx.piggame.activities;

import android.util.Log;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.views.HomeView;
import com.cloudmanx.piggame.customize.views.LoadingView;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

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
}
