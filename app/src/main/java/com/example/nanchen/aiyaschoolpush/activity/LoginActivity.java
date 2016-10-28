package com.example.nanchen.aiyaschoolpush.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.nanchen.aiyaschoolpush.App;
import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.db.DemoDBManager;
import com.example.nanchen.aiyaschoolpush.helper.DemoHelper;
import com.example.nanchen.aiyaschoolpush.utils.IntentUtil;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.example.nanchen.aiyaschoolpush.view.IcomoonTextView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class LoginActivity extends ActivityBase implements OnClickListener {

    private EditText mEditUserName;
    private EditText mEditPwd;
    private Button mBtnLogin;
    private LinearLayout mLinearRegister;
    private IcomoonTextView mTextFindPwd;
    private boolean autoLogin = false;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DemoHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            IntentUtil.newIntent(this, MainActivity.class);
            return;
        }
        setContentView(R.layout.activity_login);

        bindView();
        setListener();
    }

    private void setListener() {
        mBtnLogin.setOnClickListener(this);
        mLinearRegister.setOnClickListener(this);
        mTextFindPwd.setOnClickListener(this);
    }

    private void bindView() {
        mEditUserName = (EditText) findViewById(R.id.login_edt_username);
        mEditPwd = (EditText) findViewById(R.id.login_edt_pwd);
        mBtnLogin = (Button) findViewById(R.id.login_btn_login);
        mLinearRegister = (LinearLayout) findViewById(R.id.linear_layout_btn_register);

        mTextFindPwd = (IcomoonTextView) findViewById(R.id.login_find_pwd);

        //  if user changed, clear the password
        mEditUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mEditPwd.setText(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (DemoHelper.getInstance().getCurrentUsernName() != null) {
            mEditUserName.setText(DemoHelper.getInstance().getCurrentUsernName());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login:
                login();
                break;
            case R.id.linear_layout_btn_register:
                IntentUtil.newIntent(this, RegisterActivity.class);
                break;
            case R.id.login_find_pwd:
//                UIUtil.showToast(this,"你点击了找回密码！");

                String phone = mEditUserName.getText().toString().trim();
                Intent intent = new Intent(this, ResetPwdActivity.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            UIUtil.showToast("网络连接不可用，请检查！");
            return;
        }
        String currentUsername = mEditUserName.getText().toString().trim();
        String currentPassword = mEditPwd.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            UIUtil.showToast("用户名不能为空!");
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            UIUtil.showToast("密码不能为空！");
            return;
        }
        showLoading(this);
        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        // go login 环信
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");

                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                        App.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIUtil.showToast("登录成功！");
                        stopLoading();
                    }
                });

                IntentUtil.newIntent(LoginActivity.this,MainActivity.class);
                finish();
            }

            @Override
            public void onError(final int code, final String message) {

                Log.d(TAG, "login: onError: " + code);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopLoading();
                        UIUtil.showToast("登陆失败:"+message+",code:"+code);
                    }
                });
            }

            @Override
            public void onProgress(final int code, final String message) {
                Log.d(TAG, "login: onProgress");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin){
            return;
        }
    }
}
