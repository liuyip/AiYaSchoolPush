package com.example.nanchen.aiyaschoolpush.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.utils.IntentUtil;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.example.nanchen.aiyaschoolpush.view.IcomoonTextView;

public class LoginActivity extends ActivityBase implements OnClickListener{

    private EditText mEditUserName;
    private EditText mEditPwd;
    private Button mBtnLogin;
    private LinearLayout mLinearRegist;
    private IcomoonTextView mTextFindPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindView();
        setListener();
    }

    private void setListener() {
        mBtnLogin.setOnClickListener(this);
        mLinearRegist.setOnClickListener(this);
        mTextFindPwd.setOnClickListener(this);
    }

    private void bindView() {
        mEditUserName = (EditText) findViewById(R.id.login_edt_username);
        mEditPwd = (EditText) findViewById(R.id.login_edt_pwd);
        mBtnLogin = (Button) findViewById(R.id.login_btn_login);
        mLinearRegist = (LinearLayout) findViewById(R.id.linear_layout_btn_register);

        mTextFindPwd = (IcomoonTextView) findViewById(R.id.login_find_pwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn_login:
                UIUtil.showToast(this,"你点击了登陆！");
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.linear_layout_btn_register:
                IntentUtil.newIntent(this,RegisterActivity.class);
                break;
            case R.id.login_find_pwd:
                UIUtil.showToast(this,"你点击了找回密码！");
                break;
        }
    }

}
