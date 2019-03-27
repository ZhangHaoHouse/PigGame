package com.cloudmanx.piggame.activities;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.views.HomeView;

public class MainActivity extends BaseActivity {

    private HomeView mHomeView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mHomeView = findViewById(R.id.home_view);
    }
}
