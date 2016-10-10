package com.example.nanchen.aiyaschoolpush.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerAdapter;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerHolder;
import com.example.nanchen.aiyaschoolpush.model.NoticeModel;
import com.example.nanchen.aiyaschoolpush.model.User;
import com.example.nanchen.aiyaschoolpush.utils.TimeUtils;

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
    private RecyclerView mRecyclerView;
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.notice_recycler);

        mNoticeModelList = new ArrayList<>();

        // 获取一些假数据
        getSomeData();


        mAdapter = new CommonRecyclerAdapter<NoticeModel>(getActivity(), mNoticeModelList, R.layout.layout_notice_item) {
            @Override
            public void convert(final CommonRecyclerHolder holder, final NoticeModel item, int position, boolean isScrolling) {
                if (item.user.icon == null){
                    holder.setImageResource(R.id.notice_item_avatar,R.drawable.default_avatar);
                }else {
                    holder.setImageByUrl(R.id.notice_item_avatar,item.user.icon);
                }
                holder.setText(R.id.notice_item_name,item.user.userName);
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
                            holder.setTextColor(R.id.notice_item_like,R.color.gray);
                            item.isIPraised = false;
                        }else {
                            praiseCount++;
                            holder.setTextColor(R.id.notice_item_like,R.color.red);
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
    }

    private void getSomeData() {
        NoticeModel model = new NoticeModel();
        User user = new User();
        user.userName = "测试者账号";
        model.user = user;
        model.time = 1475899596;
        model.content = "这是一条测试信息~";
        model.praiseCount = 30;
        model.commentCount = 51;
        mNoticeModelList.add(model);


        NoticeModel model3 = new NoticeModel();
        User user3 = new User();
        user3.userName = "测试者账号2";
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
        user2.userName = "测试者账号2";
        model2.user = user2;
        model2.time = 1475906894;
        model2.content = "这是一条公告信息测试~";
        model2.praiseCount = 24;
        model2.commentCount = 25;
        mNoticeModelList.add(model2);

        Log.e(TAG,model.toString());

    }

}
