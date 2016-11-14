package com.example.nanchen.aiyaschoolpush.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.activity.LookDetailActivity;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerAdapter;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerHolder;
import com.example.nanchen.aiyaschoolpush.model.NoticeModel;
import com.example.nanchen.aiyaschoolpush.model.User;
import com.example.nanchen.aiyaschoolpush.utils.TimeUtils;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.XRecyclerView.LoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.fragment
 * @date 2016/10/08  08:57
 */

public class NoticeFragment extends FragmentBase {
    private static final String TAG = "NoticeFragment";
    private XRecyclerView mRecyclerView;
    private CommonRecyclerAdapter<NoticeModel> mAdapter;
    private List<NoticeModel> mNoticeModelList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.notice_recycler);

        mNoticeModelList = new ArrayList<>();

        // 获取一些假数据
        getSomeData();

        loadData();


        mAdapter = new CommonRecyclerAdapter<NoticeModel>(getActivity(), mNoticeModelList, R.layout.layout_notice_item) {
            @Override
            public void convert(final CommonRecyclerHolder holder, final NoticeModel item, final int position, boolean isScrolling) {
                if (item.user.icon == null){
                    holder.setImageResource(R.id.notice_item_avatar,R.drawable.default_avatar);
                }else {
                    holder.setImageByUrl(R.id.notice_item_avatar,item.user.icon);
                }
                holder.setText(R.id.notice_item_name,item.user.username);
                holder.setText(R.id.notice_item_time, TimeUtils.longToDateTime(item.time));
                holder.setText(R.id.notice_item_content,item.content);
                holder.setText(R.id.notice_item_like,"赞 "+item.praiseCount);
                holder.setText(R.id.notice_item_comment,"评论 "+item.commentCount);

                holder.setOnRecyclerItemClickListener(R.id.notice_item_like, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int praiseCount = item.praiseCount;
                        if (item.isIPraised){
//                            praiseCount--;
                            holder.setTextColor(R.id.notice_item_like,getResources().getColor(R.color.gray));
                            item.isIPraised = false;
                        }else {
                            praiseCount++;
                            holder.setTextColor(R.id.notice_item_like,getResources().getColor(R.color.red));
                            item.isIPraised = true;
                        }
                        holder.setText(R.id.notice_item_like,"赞 "+praiseCount);
                    }
                });

                holder.setOnRecyclerItemClickListener(R.id.notice_item_comment, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),"你点击了评论，将进入详情页面！",Toast.LENGTH_SHORT).show();

                        LookDetailActivity.start(getActivity(),mNoticeModelList.get(position));
                    }
                });
            }
        };


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));


        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);


        mRecyclerView.setLoadingListener(new LoadingListener() {
            @Override
            public void onRefresh() {
                mNoticeModelList.clear();
                getSomeData();
                mHandler.sendEmptyMessageDelayed(0x124,2000);
            }

            @Override
            public void onLoadMore() {
//                getSomeData();
//                mHandler.sendEmptyMessageDelayed(0x123,4000);
            }
        });

        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_not_more,(ViewGroup)getActivity().findViewById(android.R.id.content),false);
        mRecyclerView.addFootView(footerView);

        // 设置下拉图片为自己的图片
        mRecyclerView.setArrowImageView(R.mipmap.refresh_icon);
    }

    private void loadData() {

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x123:
                    mRecyclerView.loadMoreComplete();
                    UIUtil.showToast("加载成功！");
                    break;
                case 0x124:
                    mRecyclerView.refreshComplete();
                    UIUtil.showToast("刷新成功！");
                    break;
            }
        }
    };

    private void getSomeData() {

        NoticeModel model = new NoticeModel();
        User user = new User();
        user.username = "测试者账号";
        model.user = user;
        model.time = 1475899596;
        model.content = "这是一条测试信息~";
        model.praiseCount = 30;
        model.commentCount = 51;
        mNoticeModelList.add(model);


        NoticeModel model3 = new NoticeModel();
        User user3 = new User();
        user3.username = "测试者账号2";
        model3.user = user3;
        model3.time = 1475906894;
        model3.content = "你好，欢迎使用爱吖校推！你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！" +
                "你好，欢迎使用爱吖校推！";
        model3.praiseCount = 22;
        model3.commentCount = 87;
        mNoticeModelList.add(model3);

        NoticeModel model2 = new NoticeModel();
        User user2 = new User();
        user2.username = "测试者账号2";
        model2.user = user2;
        model2.time = 1475906894;
        model2.content = "这是一条公告信息测试~";
        model2.praiseCount = 24;
        model2.commentCount = 25;
        mNoticeModelList.add(model2);

        Log.e(TAG,model.toString());

        mNoticeModelList.add(model);
        mNoticeModelList.add(model2);
        mNoticeModelList.add(model3);

        mNoticeModelList.add(model);
        mNoticeModelList.add(model2);
        mNoticeModelList.add(model3);

        mNoticeModelList.add(model);
        mNoticeModelList.add(model2);
        mNoticeModelList.add(model3);

    }

}
