package com.example.nanchen.aiyaschoolpush.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.adapter.MyPagerAdapter;
import com.example.nanchen.aiyaschoolpush.view.TitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页
 *
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.fragment
 * @date 2016/09/30  14:31
 */

public class HomeFragment extends FragmentBase {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TitleView mTitleBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        mTitleBar = (TitleView) view.findViewById(R.id.home_titleBar);
        mTitleBar.setTitle("首页");

        mTabLayout = (TabLayout) view.findViewById(R.id.home_tabLayout);
        mViewPager = (ViewPager) view.findViewById(R.id.home_vp);

        List<String> nameList = new ArrayList<>();
        nameList.add("公告");
        nameList.add("作业");
        nameList.add("课表");

        List<Fragment> list = new ArrayList<>();
        list.add(new NoticeFragment());
        list.add(new HomeworkFragment());
        list.add(new TimeTableFragment());

        mViewPager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager(),nameList,list));
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
