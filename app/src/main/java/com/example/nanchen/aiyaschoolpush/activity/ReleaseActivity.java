package com.example.nanchen.aiyaschoolpush.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.api.AppService;
import com.example.nanchen.aiyaschoolpush.config.AddConfig;
import com.example.nanchen.aiyaschoolpush.model.info.InfoType;
import com.example.nanchen.aiyaschoolpush.net.okgo.JsonCallback;
import com.example.nanchen.aiyaschoolpush.net.okgo.LslResponse;
import com.example.nanchen.aiyaschoolpush.utils.ScreenUtil;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.example.nanchen.aiyaschoolpush.view.TitleView;
import com.example.nanchen.aiyaschoolpush.view.WavyLineView;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 发布信息专用的Activity
 */
public class ReleaseActivity extends ActivityBase {

    private static final String TAG = "ReleaseActivity";
    private TitleView mTitleBar;
    private String mFrom;
    private WavyLineView mWavyLine;
    private EditText mEditText;
    private int infoType;

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
            infoType = InfoType.NOTICE;
        }else if (mFrom.equals(AddConfig.HOMEWORK)){
            mTitleBar.setTitle("发布作业");
            infoType = InfoType.HOMEWORK;
        }else {
            mTitleBar.setTitle("发布动态");
            infoType = InfoType.COMMUNITY;
        }
        mTitleBar.changeRightButtonTextColor(getResources().getColor(R.color.white3));
        mTitleBar.setRightButtonText(getResources().getString(R.string.send_back_right));
        mTitleBar.setRightButtonTextSize(25);
        mTitleBar.setFixRightButtonPadingTop();
        mTitleBar.setRightButtonOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInfo();
            }
        });


        // 波浪线设置
        mWavyLine = (WavyLineView) findViewById(R.id.release_wavyLine);
        int initStrokeWidth = 1;
        int initAmplitude = 5;
        float initPeriod = (float)(2 * Math.PI / 60);
        mWavyLine.setPeriod(initPeriod);
        mWavyLine.setAmplitude(initAmplitude);
        mWavyLine.setStrokeWidth(ScreenUtil.dp2px(initStrokeWidth));


        mEditText = (EditText) findViewById(R.id.release_edit);

    }

    private void sendInfo() {
        String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            UIUtil.showToast("发布内容不能为空！");
            return;
        }
        showLoading(this);
        int classId = AppService.getInstance().getCurrentUser().classid;
        String username = AppService.getInstance().getCurrentUser().username;
        AppService.getInstance().addMainInfoAysnc(classId, username, infoType, content, new JsonCallback<LslResponse<Object>>() {
            @Override
            public void onSuccess(LslResponse<Object> objectLslResponse, Call call, Response response) {
                if (objectLslResponse.code == LslResponse.RESPONSE_OK){
                    UIUtil.showToast("发布信息成功！");
                    stopLoading();
                    ReleaseActivity.this.finish();
                }else {
                    UIUtil.showToast("发布信息失败，请稍后再试！");
                    stopLoading();
                }
            }
        });
    }
}
