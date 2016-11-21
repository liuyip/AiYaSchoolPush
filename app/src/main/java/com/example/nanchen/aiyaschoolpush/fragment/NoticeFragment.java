package com.example.nanchen.aiyaschoolpush.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.activity.LookDetailActivity;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerAdapter;
import com.example.nanchen.aiyaschoolpush.adapter.CommonRecyclerHolder;
import com.example.nanchen.aiyaschoolpush.api.AppService;
import com.example.nanchen.aiyaschoolpush.model.PraiseModel;
import com.example.nanchen.aiyaschoolpush.model.info.InfoModel;
import com.example.nanchen.aiyaschoolpush.model.info.InfoType;
import com.example.nanchen.aiyaschoolpush.net.okgo.JsonCallback;
import com.example.nanchen.aiyaschoolpush.net.okgo.LslResponse;
import com.example.nanchen.aiyaschoolpush.utils.TimeUtils;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.XRecyclerView.LoadingListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 公告主贴页面
 *
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.fragment
 * @date 2016/10/08  08:57
 */

public class NoticeFragment extends FragmentBase {
    private static final String TAG = "NoticeFragment";
    private XRecyclerView mRecyclerView;
    private CommonRecyclerAdapter<InfoModel> mAdapter;
    private List<InfoModel> mInfoModels;
    private int start = 0;
    private int count = 3;//设置一次获取的条目数
    private View footerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.notice_recycler);

//        mNoticeModelList = new ArrayList<>();
        mInfoModels = new ArrayList<>();

        // 获取一些假数据
//        getSomeData();

        loadData(true);


        mAdapter = new CommonRecyclerAdapter<InfoModel>(getActivity(), mInfoModels, R.layout.layout_notice_item) {
            @Override
            public void convert(final CommonRecyclerHolder holder, final InfoModel item, final int position, boolean isScrolling) {
                if (TextUtils.isEmpty(item.user.avatar)) {
                    holder.setImageResource(R.id.notice_item_avatar, R.drawable.default_avatar);
                } else {
                    Log.e(TAG, item.user.avatar);
                    holder.setImageByUrl(R.id.notice_item_avatar, item.user.avatar);
                }
                holder.setText(R.id.notice_item_name, item.user.nickname);
                holder.setText(R.id.notice_item_time, TimeUtils.longToDateTime(item.time));
                holder.setText(R.id.notice_item_content, item.content);
                holder.setText(R.id.notice_item_like, "赞 " + item.praiseCount);
                holder.setText(R.id.notice_item_comment, "评论 " + item.commentCount);
                if (item.isIPraised){
                    holder.setTextColor(R.id.notice_item_like, getResources().getColor(R.color.red));
                }else{
                    holder.setTextColor(R.id.notice_item_like, getResources().getColor(R.color.gray));
                }
                Log.e(TAG,item.mainid+","+item.isIPraised);
                holder.setOnRecyclerItemClickListener(R.id.notice_item_like, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        insertPraised(item,holder);

                    }
                });

                holder.setOnRecyclerItemClickListener(R.id.notice_item_comment, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "你点击了评论，将进入详情页面！", Toast.LENGTH_SHORT).show();
                        LookDetailActivity.start(getActivity(), mInfoModels.get(position));
                    }
                });
            }
        };


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);


        mRecyclerView.setLoadingListener(new LoadingListener() {
            @Override
            public void onRefresh() {
                loadData(true);
                mRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
//                getSomeData();
                loadData(false);
                mRecyclerView.loadMoreComplete();
            }
        });

        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_not_more, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        mRecyclerView.addFootView(footerView);
//        footerView.setVisibility(View.GONE);

        // 设置下拉图片为自己的图片
        mRecyclerView.setArrowImageView(R.mipmap.refresh_icon);
    }



    /**
     * 把赞的信息提交到服务器
     */
    private void insertPraised(final InfoModel item,final CommonRecyclerHolder holder) {

        if (AppService.getInstance().getCurrentUser() == null) {
            return;
        }
        AppService.getInstance().updatePraiseAsync(item.mainid, AppService.getInstance().getCurrentUser().username
                , new JsonCallback<LslResponse<PraiseModel>>() {
                    @Override
                    public void onSuccess(LslResponse<PraiseModel> praiseLslResponse, Call call, Response response) {
                        if (praiseLslResponse.code == LslResponse.RESPONSE_OK){
                            Log.e(TAG,"更新赞的信息成功！");
                            int praiseCount = praiseLslResponse.data.praiseCount;
                            boolean isInsert = praiseLslResponse.data.isInsert;
                            if (!isInsert) {
//                                praiseCount--;
                                holder.setTextColor(R.id.notice_item_like, getResources().getColor(R.color.gray));
//                                item.isIPraised = false;
                            } else {
//                                praiseCount++;
                                holder.setTextColor(R.id.notice_item_like, getResources().getColor(R.color.red));
//                                item.isIPraised = true;
                            }
                            holder.setText(R.id.notice_item_like, "赞 " + praiseCount);
//                            mAdapter.notifyDataSetChanged();
                        }else{
                            Log.e(TAG,"更新赞的信息失败！");
                            UIUtil.showToast("更新赞的信息失败！");
                        }
                    }
                });
    }


    /**
     * 加载数据
     */
    private void loadData(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
        } else {
            start += count;
        }
        Log.e(TAG+"1", start +"");
        if (AppService.getInstance().getCurrentUser() != null) {
            int classId = AppService.getInstance().getCurrentUser().classid;
            String username = AppService.getInstance().getCurrentUser().username;
            AppService.getInstance().getNoticeAsync(classId, username,InfoType.NOTICE, start,count, new JsonCallback<LslResponse<List<InfoModel>>>() {
                @Override
                public void onSuccess(LslResponse<List<InfoModel>> listLslResponse, Call call, Response response) {
                    if (listLslResponse.code == LslResponse.RESPONSE_OK) {
                        mRecyclerView.setLoadingMoreEnabled(true);
                        if (isRefresh) {
                            mInfoModels.clear();
                            UIUtil.showToast("刷新成功！");
                            mAdapter.notifyDataSetChanged();
                        } else {
                            UIUtil.showToast("加载成功！");
                            mAdapter.notifyDataSetChanged();
                        }
                        mInfoModels.addAll(listLslResponse.data);
                        footerView.setVisibility(View.GONE);
                    } else {
                        UIUtil.showToast(listLslResponse.msg);
                        footerView.setVisibility(View.VISIBLE);
                        mRecyclerView.setLoadingMoreEnabled(false);
                    }
                }
            });
        }
    }

}
