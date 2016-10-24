package com.example.nanchen.aiyaschoolpush.activity;

import android.os.Bundle;
import android.util.Log;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.config.AddConfig;
import com.example.nanchen.aiyaschoolpush.view.TitleView;

public class ReleaseActivity extends ActivityBase {

    private static final String TAG = "ReleaseActivity";
    private TitleView mTitleBar;
    private String mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);

        bindView();
    }

    private void bindView() {

        mFrom = getIntent().getStringExtra("name");
        mTitleBar = (TitleView) findViewById(R.id.release_title);
        mTitleBar.setLeftButtonAsFinish(this);
        Log.e(TAG,mFrom);
        if (mFrom.equals(AddConfig.NOTICE)){
            mTitleBar.setTitle("发布公告");
        }else if (mFrom.equals(AddConfig.HOMEWORK)){
            mTitleBar.setTitle("发布作业");
        }else {
            mTitleBar.setTitle("发布动态");
        }
    }
}
