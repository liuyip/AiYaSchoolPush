package com.example.nanchen.aiyaschoolpush.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.ui.fragment.CommunityFragment;
import com.example.nanchen.aiyaschoolpush.ui.fragment.DiscoverFragment;
import com.example.nanchen.aiyaschoolpush.ui.fragment.HomeFragment;
import com.example.nanchen.aiyaschoolpush.ui.fragment.MineFragment;
import com.example.nanchen.aiyaschoolpush.ui.fragment.MsgFragment;
import com.example.nanchen.aiyaschoolpush.helper.QiYuCloudServerHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

/**
 * 主页面
 */
public class MainActivity extends ActivityBase {

    private static final String TAG = "MainActivity";
    private String []tabNames = {"主页","消息","发现","我的"};
    private int []tabIcons = {R.drawable.tab_home,R.drawable.tab_msg
    ,R.drawable.tab_discover,R.drawable.tab_mine};
    private SpaceNavigationView mTab;
    private HomeFragment mHomeFragment;
    private MsgFragment mMsgFragment;
    private DiscoverFragment mDiscoverFragment;
    private MineFragment mMineFragment;
    private CommunityFragment mCommunityFragment;
    private Fragment mFragment;
    private final int CONTENT_ID = R.id.main_content;
    private FragmentManager fg;
//    private DataFragment mDataFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** 设置七鱼客服用户信息 **/
        QiYuCloudServerHelper.setUserInfo(true);

        fg = getSupportFragmentManager();

        // 初次加载的时候显示首页布局
        mHomeFragment = new HomeFragment();
        fg.beginTransaction().add(CONTENT_ID,mHomeFragment).commit();


        bindView();


        // 下面是开源底部导航栏
        for (int i = 0; i < tabNames.length; i++) {
            mTab.addSpaceItem(new SpaceItem(tabNames[i],tabIcons[i]));
        }
        mTab.showIconOnly();
        mTab.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
//                Toast.makeText(MainActivity.this,"点击了中间的按钮",Toast.LENGTH_SHORT).show();
                gotoCenterFragment();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
//                Toast.makeText(MainActivity.this,"你点击了"+itemName+"("+itemIndex+")",Toast.LENGTH_SHORT).show();
                gotoOtherFragment(itemName);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
//                Toast.makeText(MainActivity.this,"重复点击",Toast.LENGTH_SHORT).show();
                gotoOtherFragment(itemName);
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mTab.onSaveInstanceState(outState);
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

    /**
     * 进入中间的Fragment社区
     */
    private void gotoCenterFragment() {
        hideFragment();
        if (mCommunityFragment == null){
            mCommunityFragment = new CommunityFragment();
            fg.beginTransaction().add(CONTENT_ID,mCommunityFragment).commit();
        }else {
            fg.beginTransaction().show(mCommunityFragment).commit();
        }
//        if (mDataFragment == null){
//            mDataFragment = new DataFragment();
//            fg.beginTransaction().add(CONTENT_ID,mDataFragment).commit();
//        }else {
//            fg.beginTransaction().show(mDataFragment).commit();
//        }
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
        if (mCommunityFragment != null){
            fg.beginTransaction().hide(mCommunityFragment).commit();
        }
//        if (mDataFragment != null){
//            fg.beginTransaction().hide(mDataFragment).commit();
//        }
    }

    private void bindView() {
        mTab = (SpaceNavigationView) findViewById(R.id.main_tab);

//        unreadLabel = (TextView) findViewById(R.id.main_msg_number);
    }

    // 保存用户按返回键的时间
    private long mExitTime = 0;

    /**
     * 重写onBackPressed方法用于提示用户是否再次退出
     */
    @Override
    public void onBackPressed() {
        Log.e(TAG,System.currentTimeMillis()+"");
        Log.e(TAG,mExitTime+"");

        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT)
                    .show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            // System.exit(0);
        }
    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        for(EMConversation conversation:EMClient.getInstance().chatManager().getAllConversations().values()){
            if(conversation.getType() == EMConversationType.ChatRoom)
                chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal-chatroomUnreadMsgCount;
    }

    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
//            unreadLabel.setText(String.valueOf(count));
//            unreadLabel.setVisibility(View.VISIBLE);
            mTab.showBadgeAtIndex(1,count,getResources().getColor(R.color.red));
        } else {
//            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }

    private TextView unreadLabel;

}
