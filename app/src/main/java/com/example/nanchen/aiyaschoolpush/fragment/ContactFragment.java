package com.example.nanchen.aiyaschoolpush.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.contact.CityListAdapter;
import com.example.nanchen.aiyaschoolpush.contact.DividerDecoration;
import com.example.nanchen.aiyaschoolpush.contact.constants.DataConstants;
import com.example.nanchen.aiyaschoolpush.contact.model.City;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.fragment
 * @date 2016/10/08  08:57
 */

public class ContactFragment extends FragmentBase implements OnQuickSideBarTouchListener{

    private RecyclerView mRecyclerView;
    private QuickSideBarView mQuickSideBar;
    private QuickSideBarTipsView mQuickSideBarTips;
    private HashMap<String,Integer> letters = new HashMap<>();

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



        //设置监听
        mQuickSideBar.setOnQuickSideBarTouchListener(this);


        //设置列表数据和浮动header
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        // Add the sticky headers decoration
        CityListWithHeadersAdapter adapter = new CityListWithHeadersAdapter();

        //GSON解释出来
        Type listType = new TypeToken<LinkedList<City>>(){}.getType();
        Gson gson = new Gson();
        LinkedList<City> cities = gson.fromJson(DataConstants.cityDataList, listType);

        ArrayList<String> customLetters = new ArrayList<>();

        int position = 0;
        for(City city: cities){
            String letter = city.getFirstLetter();
            //如果没有这个key则加入并把位置也加入
            if(!letters.containsKey(letter)){
                letters.put(letter,position);
                customLetters.add(letter);
            }
            position++;
        }

        //不自定义则默认26个字母
        mQuickSideBar.setLetters(customLetters);
        adapter.addAll(cities);
        mRecyclerView.setAdapter(adapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        mRecyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        mRecyclerView.addItemDecoration(new DividerDecoration(getActivity()));
    }



    @Override
    public void onLetterChanged(String letter, int position, float y) {
        mQuickSideBarTips.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if(letters.containsKey(letter)) {
            mRecyclerView.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        mQuickSideBarTips.setVisibility(touching? View.VISIBLE:View.INVISIBLE);
    }

    private class CityListWithHeadersAdapter extends CityListAdapter<ViewHolder>
            implements StickyRecyclerHeadersAdapter<ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item, parent, false);
            return new RecyclerView.ViewHolder(view) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView.findViewById(R.id.contact_name);
            textView.setText(getItem(position).getCityName());
        }

        @Override
        public long getHeaderId(int position) {
            return getItem(position).getFirstLetter().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_header, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(String.valueOf(getItem(position).getFirstLetter()));
            holder.itemView.setBackgroundColor(getRandomColor());
        }

        private int getRandomColor() {
            SecureRandom rgen = new SecureRandom();
            return Color.HSVToColor(150, new float[]{
                    rgen.nextInt(359), 1, 1
            });
        }

    }
}
