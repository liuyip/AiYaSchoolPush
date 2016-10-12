package com.example.nanchen.aiyaschoolpush.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.TestActivity;
import com.example.nanchen.aiyaschoolpush.fragment.DiscoverFragment;
import com.example.nanchen.aiyaschoolpush.fragment.HomeFragment;
import com.example.nanchen.aiyaschoolpush.fragment.MineFragment;
import com.example.nanchen.aiyaschoolpush.fragment.MsgFragment;
import com.example.nanchen.aiyaschoolpush.utils.CircularAnimUtil;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class MainActivity extends ActivityBase {

    private String []tabNames = {"主页","消息","发现","我的"};
    private int []tabIcons = {R.drawable.tab_home,R.drawable.tab_msg
    ,R.drawable.tab_discover,R.drawable.tab_mine};
    private SpaceNavigationView mTab;
    private HomeFragment mHomeFragment;
    private MsgFragment mMsgFragment;
    private DiscoverFragment mDiscoverFragment;
    private MineFragment mMineFragment;
    private Fragment mFragment;
    private final int CONTENT_ID = R.id.main_content;
    private FragmentManager fg;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fg = getSupportFragmentManager();

        // 初次加载的时候显示首页布局
        mHomeFragment = new HomeFragment();
        fg.beginTransaction().add(CONTENT_ID,mHomeFragment).commit();


        bindView();


        // 下面是开源底部导航栏
        mTab.initWithSaveInstanceState(savedInstanceState);

        for (int i = 0; i < tabNames.length; i++) {
            mTab.addSpaceItem(new SpaceItem(tabNames[i],tabIcons[i]));
        }
        mTab.showIconOnly();
        mTab.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(MainActivity.this,"点击了中间的按钮",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
//                Toast.makeText(MainActivity.this,"你点击了"+itemName,Toast.LENGTH_SHORT).show();
                gotoOtherFragment(itemName);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
//                Toast.makeText(MainActivity.this,"重复点击",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void swithFragment(Fragment fragment){
        if (mFragment != fragment){
            if (!fragment.isAdded()){
                fg.beginTransaction().hide(mFragment)
                        .add(CONTENT_ID,fragment).commit();
            }else {
                fg.beginTransaction().hide(mFragment).show(fragment).commit();
            }
            mFragment = fragment;
        }
    }

    private void gotoOtherFragment(String itemName) {
        hideFragment();
        switch (itemName){
            case "主页":
                if (mHomeFragment == null){
                    mHomeFragment = new HomeFragment();
                    fg.beginTransaction().add(CONTENT_ID,mHomeFragment).commit();
                }else {
                    fg.beginTransaction().show(mHomeFragment).commit();
                }
                break;
            case "消息":
                if (mMsgFragment == null){
                    mMsgFragment = new MsgFragment();
                    fg.beginTransaction().add(CONTENT_ID,mMsgFragment).commit();
                }else {
                    fg.beginTransaction().show(mMsgFragment).commit();
                }
                break;
            case "发现":
                if (mDiscoverFragment == null){
                    mDiscoverFragment = new DiscoverFragment();
                    fg.beginTransaction().add(CONTENT_ID,mDiscoverFragment).commit();
                }else {
                    fg.beginTransaction().show(mDiscoverFragment).commit();
                }
                break;
            case "我的":
                if (mMineFragment == null){
                    mMineFragment = new MineFragment();
                    fg.beginTransaction().add(CONTENT_ID,mMineFragment).commit();
                }else {
                    fg.beginTransaction().show(mMineFragment).commit();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏所有Fragment
     */
    private void hideFragment() {
        if (mHomeFragment != null){
            fg.beginTransaction().hide(mHomeFragment).commit();
        }
        if (mMsgFragment != null){
            fg.beginTransaction().hide(mMsgFragment).commit();
        }
        if (mDiscoverFragment != null){
            fg.beginTransaction().hide(mDiscoverFragment).commit();
        }
        if (mMineFragment != null){
            fg.beginTransaction().hide(mMineFragment).commit();
        }
    }

    private void bindView() {
        mTab = (SpaceNavigationView) findViewById(R.id.main_tab);

        mFab = (FloatingActionButton) findViewById(R.id.main_fab);

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                CircularAnimUtil.startActivity(MainActivity.this, intent, mFab,
                        R.color.colorAccent);
            }
        });
    }




}
