package com.example.nanchen.aiyaschoolpush.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerAdapter;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerHolder;
import com.example.nanchen.aiyaschoolpush.model.HomeworkModel;
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

public class HomeworkFragment extends FragmentBase {
    private static final String TAG = "HomeworkFragment";
    private XRecyclerView mRecyclerView;
    private CommonRecyclerAdapter<HomeworkModel> mAdapter;
    private List<HomeworkModel> mHomeworkModelList;
    private View footerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.homework_recycler);

        mHomeworkModelList = new ArrayList<>();

        // 获取一些假数据
        getSomeData();


        mAdapter = new CommonRecyclerAdapter<HomeworkModel>(getActivity(), mHomeworkModelList, R.layout.layout_notice_item) {
            @Override
            public void convert(final CommonRecyclerHolder holder, final HomeworkModel item, final int position, boolean isScrolling) {
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
                mHomeworkModelList.clear();
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
        HomeworkModel model = new HomeworkModel();
        User user = new User();
        user.username = "4班班主任";
        model.user = user;
        model.time = 1475899596;
        model.content = "这是一条测试作业~";
        model.praiseCount = 30;
        model.commentCount = 51;
        mHomeworkModelList.add(model);


        HomeworkModel model3 = new HomeworkModel();
        User user3 = new User();
        user3.username = "4班语文老师";
        model3.user = user3;
        model3.time = 1475906894;
        model3.content = "今天的作业是：\n" +
                "1、自命题作文一篇800字\n" +
                "2、抄写唐诗三百首\n" +
                "3、写100个成语\n";
        model3.praiseCount = 22;
        model3.commentCount = 87;
        mHomeworkModelList.add(model3);

        HomeworkModel model2 = new HomeworkModel();
        User user2 = new User();
        user2.username = "4班数学老师";
        model2.user = user2;
        model2.time = 1475906894;
        model2.content = "今天的数学作业是：\n" +
                "1、错题\n" +
                "2、卷子两张\n" +
                "3、算术题\n"+
                "4、10以内的加减法\n" +
                "5、计算题100页\n";
        model2.praiseCount = 24;
        model2.commentCount = 25;
        mHomeworkModelList.add(model2);

//        mHomeworkModelList.add(model);
//        mHomeworkModelList.add(model2);
//        mHomeworkModelList.add(model3);
//
//        mHomeworkModelList.add(model);
//        mHomeworkModelList.add(model2);
//        mHomeworkModelList.add(model3);
//
//        mHomeworkModelList.add(model);
//        mHomeworkModelList.add(model2);
//        mHomeworkModelList.add(model3);
//
//        mHomeworkModelList.add(model);
//        mHomeworkModelList.add(model2);
//        mHomeworkModelList.add(model3);
//
//        mHomeworkModelList.add(model);
//        mHomeworkModelList.add(model2);
//        mHomeworkModelList.add(model3);
//
//        mHomeworkModelList.add(model);
//        mHomeworkModelList.add(model2);
//        mHomeworkModelList.add(model3);
//
//        mHomeworkModelList.add(model);
//        mHomeworkModelList.add(model2);
//        mHomeworkModelList.add(model3);
//
//        mHomeworkModelList.add(model);
//        mHomeworkModelList.add(model2);
//        mHomeworkModelList.add(model3);



    }

}
