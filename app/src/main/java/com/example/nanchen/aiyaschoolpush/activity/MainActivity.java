package com.example.nanchen.aiyaschoolpush.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.view.TitleView;

public class MainActivity extends ActivityBase {

    private TitleView mTitleBar;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopLoading();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
    }

    private void bindView() {
        mTitleBar = (TitleView) findViewById(R.id.main_title_view);
        mTitleBar.setLeftButtonAsFinish(MainActivity.this);
        mTitleBar.setTitle("主页");
    }

    public void btnClick(View view) {
        showLoading(this);
        handler.sendEmptyMessageDelayed(0x123,5000);
    }
}
