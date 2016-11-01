package com.example.nanchen.aiyaschoolpush.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.activity.ReleaseCommunityActivity;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerAdapter;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerHolder;
import com.example.nanchen.aiyaschoolpush.model.Topic;
import com.example.nanchen.aiyaschoolpush.model.User;
import com.example.nanchen.aiyaschoolpush.utils.CircularAnimUtil;
import com.example.nanchen.aiyaschoolpush.utils.TimeUtils;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.example.nanchen.aiyaschoolpush.view.TitleView;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.XRecyclerView.LoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.fragment
 * @date 2016/10/26  10:23
 */

public class CommunityFragment extends FragmentBase {

    private static final String TAG = "CommunityFragment";
    private TitleView mTitleBar;
    private XRecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private CommonRecyclerAdapter<Topic> mAdapter;
    private List<Topic> mTopics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_community,null);
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_test,null);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        mTitleBar = (TitleView) view.findViewById(R.id.community_titleBar);
        mTitleBar.setTitle("社区");

        mRecyclerView = (XRecyclerView) view.findViewById(R.id.community_recycler);
        mFab = (FloatingActionButton) view.findViewById(R.id.community_fab);

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReleaseCommunityActivity.class);
                CircularAnimUtil.startActivity(getActivity(), intent, mFab,
                        R.color.main_bg_color1);
            }
        });

        mTopics = new ArrayList<>();

        // 获取一些假数据
        getSomeData();


        mAdapter = new CommonRecyclerAdapter<Topic>(getActivity(), mTopics, R.layout.layout_community_item) {
            @Override
            public void convert(final CommonRecyclerHolder holder, final Topic item, final int position, boolean isScrolling) {
                if (item.author.icon == null){
                    holder.setImageResource(R.id.community_item_avatar,R.drawable.default_avatar);
                }else {
                    holder.setImageByUrl(R.id.community_item_avatar,item.author.icon);
                }
                holder.setText(R.id.community_item_name,item.author.userName);
                holder.setText(R.id.community_item_time, TimeUtils.longToDateTime(item.createTs));
                holder.setText(R.id.community_item_content,item.content);
                holder.setText(R.id.community_item_like,"赞 "+item.praiseCount);
                holder.setText(R.id.community_item_comment,"评论 "+item.commentCount);

                holder.setOnRecyclerItemClickListener(R.id.community_item_like, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int praiseCount = item.praiseCount;
                        if (item.isIPraised){
//                            praiseCount--;
                            holder.setTextColor(R.id.community_item_like,getResources().getColor(R.color.gray));
                            item.isIPraised = false;
                        }else {
                            praiseCount++;
                            holder.setTextColor(R.id.community_item_like,getResources().getColor(R.color.red));
                            item.isIPraised = true;
                        }
                        holder.setText(R.id.community_item_like,"赞 "+praiseCount);
                    }
                });

                holder.setOnRecyclerItemClickListener(R.id.community_item_comment, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),"你点击了评论，将进入详情页面！",Toast.LENGTH_SHORT).show();
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
                mTopics.clear();
                getSomeData();
                mHandler.sendEmptyMessageDelayed(0x124,2000);
            }

            @Override
            public void onLoadMore() {
                getSomeData();
                mHandler.sendEmptyMessageDelayed(0x123,4000);
            }
        });

//        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_not_more,(ViewGroup)getActivity().findViewById(android.R.id.content),false);
//        mRecyclerView.addFootView(footerView);

        // 设置下拉图片为自己的图片
        mRecyclerView.setArrowImageView(R.mipmap.refresh_icon);

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
        Topic model = new Topic();
        User author = new User();
        author.userName = "4班班主任";
        model.author = author;
        model.createTs = 1475899596;
        model.content = "这是一条测试作业~";
        model.praiseCount = 30;
        model.commentCount = 51;
        mTopics.add(model);


        Topic model3 = new Topic();
        User author3 = new User();
        author3.userName = "4班语文老师";
        model3.author = author3;
        model3.createTs = 1475906894;
        model3.content = "今天的作业是：\n" +
                "1、自命题作文一篇800字\n" +
                "2、抄写唐诗三百首\n" +
                "3、写100个成语\n";
        model3.praiseCount = 22;
        model3.commentCount = 87;
        mTopics.add(model3);

        Topic model2 = new Topic();
        User author2 = new User();
        author2.userName = "4班数学老师";
        model2.author = author2;
        model2.createTs = 1475906894;
        model2.content = "今天的数学作业是：\n" +
                "1、错题\n" +
                "2、卷子两张\n" +
                "3、算术题\n"+
                "4、10以内的加减法\n" +
                "5、计算题100页\n";
        model2.praiseCount = 24;
        model2.commentCount = 25;
        mTopics.add(model2);

//        mTopics.add(model);
//        mTopics.add(model2);
//        mTopics.add(model3);
//
//        mTopics.add(model);
//        mTopics.add(model2);
//        mTopics.add(model3);
//
//        mTopics.add(model);
//        mTopics.add(model2);
//        mTopics.add(model3);
//
//        mTopics.add(model);
//        mTopics.add(model2);
//        mTopics.add(model3);
//
//        mTopics.add(model);
//        mTopics.add(model2);
//        mTopics.add(model3);
//
//        mTopics.add(model);
//        mTopics.add(model2);
//        mTopics.add(model3);
//
//        mTopics.add(model);
//        mTopics.add(model2);
//        mTopics.add(model3);
//
//        mTopics.add(model);
//        mTopics.add(model2);
//        mTopics.add(model3);



    }
}
