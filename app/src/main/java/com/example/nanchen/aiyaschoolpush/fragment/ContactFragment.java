package com.example.nanchen.aiyaschoolpush.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.example.nanchen.aiyaschoolpush.R;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.fragment
 * @date 2016/10/08  08:57
 */

public class ContactFragment extends FragmentBase {

    private RecyclerView mRecyclerView;
    private QuickSideBarView mQuickSideBar;
    private QuickSideBarTipsView mQuickSideBarTips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list,container,false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mQuickSideBar = (QuickSideBarView) view.findViewById(R.id.quickSideBarView);
        mQuickSideBarTips = (QuickSideBarTipsView) view.findViewById(R.id.quickSideBarTipsView);

    }

}
