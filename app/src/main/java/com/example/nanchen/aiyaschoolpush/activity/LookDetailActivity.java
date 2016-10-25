package com.example.nanchen.aiyaschoolpush.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.adapter.CommonAdapter;
import com.example.nanchen.aiyaschoolpush.adapter.ViewHolder;
import com.example.nanchen.aiyaschoolpush.model.CommentModel;
import com.example.nanchen.aiyaschoolpush.model.NoticeModel;
import com.example.nanchen.aiyaschoolpush.model.User;
import com.example.nanchen.aiyaschoolpush.utils.ScreenUtil;
import com.example.nanchen.aiyaschoolpush.utils.SoftInputMethodUtil;
import com.example.nanchen.aiyaschoolpush.utils.TimeUtils;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.example.nanchen.aiyaschoolpush.view.IcomoonTextView;
import com.example.nanchen.aiyaschoolpush.view.RoundImageView;
import com.example.nanchen.aiyaschoolpush.view.TitleView;
import com.example.nanchen.aiyaschoolpush.view.WavyLineView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LookDetailActivity extends ActivityBase {

    private static final String TAG = "LookDetailActivity";
    private TitleView mTitleBar;
    private EditText mEditText;
    private IcomoonTextView mTextSend;
    private TextView mTextView;
    private boolean isReply = false;

    private static final String EXTRA_KEY = "v";
    private ListView mListView;
    private NoticeModel mNoticeModel;

    private CommonAdapter<CommentModel> mAdapter;
    private List<CommentModel> mCommentModels;


    public static void start(Context context, NoticeModel noticeModel) {
        Intent intent = new Intent(context, LookDetailActivity.class);
        intent.putExtra(EXTRA_KEY, noticeModel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_detail);

        if (getIntent() == null) {
            return;
        }
        mNoticeModel = (NoticeModel) getIntent().getSerializableExtra(EXTRA_KEY);
        initList();
        bindView();

    }

    private void initList() {
        mCommentModels = new ArrayList<>();

        CommentModel model = new CommentModel();
        User user = new User();
        user.userName = "南尘";
        model.sender = user;
        model.content = "你好，这是一条评论测试！";
        model.createTs = 1477297421;
        mCommentModels.add(model);


        CommentModel model1 = new CommentModel();
        User user1 = new User();
        user1.userName = "南尘2";
        model1.sender = user1;
        model1.content = "你好，这是第二条评论测试！";
        model1.createTs = 1475899596;
        mCommentModels.add(model1);


        CommentModel model2 = new CommentModel();
        User user2 = new User();
        user2.userName = "南尘3";
        model2.sender = user2;
        model2.content = "欢迎大家一起来测试爱吖校推校推~~~多有不足，谢谢指教！多有不足，谢谢指教！多有不足，谢谢指教！多有不足，谢谢指教！";
        model2.createTs = 1475899596;
        mCommentModels.add(model2);

        mCommentModels.add(model);
        mCommentModels.add(model2);
        mCommentModels.add(model1);
        mCommentModels.add(model1);
    }


    private void showKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mEditText, 0);
    }

    private void bindView() {

        mTitleBar = (TitleView) findViewById(R.id.detail_titleBar);
        mTitleBar.setLeftButtonAsFinish(this);
        mTitleBar.setTitle("通知详情");

        mEditText = (EditText) findViewById(R.id.detail_edit);
        mTextSend = (IcomoonTextView) findViewById(R.id.detail_send);
        mTextView = (TextView) findViewById(R.id.detail_send_text);

        mTextSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommentCheck();
            }
        });

        mEditText.addTextChangedListener(new MyTextWatcher());

        mListView = (ListView) findViewById(R.id.detail_lv);

        mAdapter = new CommonAdapter<CommentModel>(this, mCommentModels, R.layout.layout_detail_comment) {
            @Override
            public void convert(ViewHolder holder, CommentModel item) {
                if (item.sender.icon == null) {
                    holder.setImageDrawable(R.id.comment_image, getResources().getDrawable(R.drawable.default_avatar));
                } else {
                    holder.setImageByUrl(R.id.comment_image, item.sender.icon);
                }
                holder.setText(R.id.comment_name, item.sender.userName);
                holder.setText(R.id.comment_time, TimeUtils.longToDateTime(item.createTs));
                holder.setText(R.id.comment_text, item.content);
            }
        };


        View topView = getHeaderView();
        mListView.addHeaderView(topView,null,false);

        View lineView = getLineView();
        mListView.addHeaderView(lineView,null,false);


        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                UIUtil.showToast(position-2+"");

                // 这里点击的时候把发送按钮设置为不可点击和灰色，这里算一个小bug
                mTextSend.setTextColor(getResources().getColor(R.color.gray19));
                mTextSend.setClickable(false);

                String reply = "回复 " + mCommentModels.get(position-2).sender.userName + ":";
                mEditText.setText(reply);
                mEditText.requestFocus();
                mEditText.setSelection(reply.length()); // 将光标移至文字末尾
                showKeyboard(); // 弹出软键盘
            }
        });
    }

    /**
     * 获取赞的人数
     */
    private void getSupportView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_comment_support, null);
        TextView textView = (TextView) view.findViewById(R.id.comment_support_text);
        TextView tv_line = (TextView) view.findViewById(R.id.comment_line2);

    }


    /**
     * 获取波浪线View
     */
    private View getLineView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_wavy_line, null);
        WavyLineView mWavyLine = (WavyLineView) view.findViewById(R.id.wavyLine);
        int initStrokeWidth = 1;
        int initAmplitude = 5;
        float initPeriod = (float) (2 * Math.PI / 60);
        mWavyLine.setPeriod(initPeriod);
        mWavyLine.setAmplitude(initAmplitude);
        mWavyLine.setStrokeWidth(ScreenUtil.dp2px(initStrokeWidth));
        return view;
    }

    /**
     * 获取信息发布者的View
     */
    private View getHeaderView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_detail_header, null);
        RoundImageView avatar = (RoundImageView) view.findViewById(R.id.notice_item_avatar);
        TextView tv_name = (TextView) view.findViewById(R.id.notice_item_name);
        TextView tv_time = (TextView) view.findViewById(R.id.notice_item_time);
        TextView tv_comment = (TextView) view.findViewById(R.id.notice_item_comment);
        TextView tv_like = (TextView) view.findViewById(R.id.notice_item_like);
        TextView tv_content = (TextView) view.findViewById(R.id.notice_item_content);

        if (mNoticeModel.user.icon != null) {
            Picasso.with(this).load(mNoticeModel.user.icon).into(avatar);
        } else {
            Picasso.with(this).load(R.drawable.default_avatar).into(avatar);
        }
        tv_name.setText(mNoticeModel.user.userName);
        tv_time.setText(TimeUtils.longToDateTime(mNoticeModel.time));
        tv_content.setText(mNoticeModel.content);
        tv_like.setText(String.format(Locale.CHINA, "赞 %d", mNoticeModel.praiseCount));
        tv_comment.setText(String.format(Locale.CHINA, "评论 %d", mNoticeModel.commentCount));

        return view;
    }

    private void sendCommentCheck() {
        String comment = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            UIUtil.showToast("评论内容不能为空");
            return;
        }
        UIUtil.showToast("正在尝试发送....");
        SoftInputMethodUtil.HideSoftInput(mEditText.getWindowToken());
        cleanEdit();
    }

    private void cleanEdit() {
        mEditText.setText("");
        mEditText.setHint("写评论...");

    }

    private class MyTextWatcher implements TextWatcher {



        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEditText.length() > 0) {
                mTextSend.setTextColor(getResources().getColor(R.color.gray_blue));
                mTextSend.setClickable(true);
            } else {
                mTextSend.setTextColor(getResources().getColor(R.color.gray19));
                mTextSend.setClickable(false);
            }
        }
    }
}
