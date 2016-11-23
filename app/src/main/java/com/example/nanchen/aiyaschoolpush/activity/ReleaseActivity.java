package com.example.nanchen.aiyaschoolpush.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.nanchen.aiyaschoolpush.CommunityEvent;
import com.example.nanchen.aiyaschoolpush.HomeworkEvent;
import com.example.nanchen.aiyaschoolpush.ImagePickerAdapter;
import com.example.nanchen.aiyaschoolpush.NoticeEvent;
import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.api.AppService;
import com.example.nanchen.aiyaschoolpush.config.AddConfig;
import com.example.nanchen.aiyaschoolpush.model.User;
import com.example.nanchen.aiyaschoolpush.model.info.InfoModel;
import com.example.nanchen.aiyaschoolpush.model.info.InfoType;
import com.example.nanchen.aiyaschoolpush.net.okgo.JsonCallback;
import com.example.nanchen.aiyaschoolpush.net.okgo.LslResponse;
import com.example.nanchen.aiyaschoolpush.utils.ScreenUtil;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.example.nanchen.aiyaschoolpush.view.TitleView;
import com.example.nanchen.aiyaschoolpush.view.WavyLineView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 发布信息专用的Activity
 */
public class ReleaseActivity extends ActivityBase implements ImagePickerAdapter.OnRecyclerViewItemClickListener{
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数


    private static final String TAG = "ReleaseActivity";
    private TitleView mTitleBar;
    private String mFrom;
    private WavyLineView mWavyLine;
    private EditText mEditText;
    private int infoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);

        bindView();
    }

    private void bindView() {

        mFrom = getIntent().getStringExtra("name");
        mTitleBar = (TitleView) findViewById(R.id.release_title);
        mTitleBar.setLeftButtonAsFinish(this);
        Log.e(TAG,mFrom);
        if (mFrom.equals(AddConfig.NOTICE)){
            mTitleBar.setTitle("发布公告");
            infoType = InfoType.NOTICE;
        }else if (mFrom.equals(AddConfig.HOMEWORK)){
            mTitleBar.setTitle("发布作业");
            infoType = InfoType.HOMEWORK;
        }else {
            mTitleBar.setTitle("发布动态");
            infoType = InfoType.COMMUNITY;
        }
        mTitleBar.changeRightButtonTextColor(getResources().getColor(R.color.white3));
        mTitleBar.setRightButtonText(getResources().getString(R.string.send_back_right));
        mTitleBar.setRightButtonTextSize(25);
        mTitleBar.setFixRightButtonPadingTop();
        mTitleBar.setRightButtonOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPic();
//                sendInfo();
            }
        });


        // 波浪线设置
        mWavyLine = (WavyLineView) findViewById(R.id.release_wavyLine);
        int initStrokeWidth = 1;
        int initAmplitude = 5;
        float initPeriod = (float)(2 * Math.PI / 60);
        mWavyLine.setPeriod(initPeriod);
        mWavyLine.setAmplitude(initAmplitude);
        mWavyLine.setStrokeWidth(ScreenUtil.dp2px(initStrokeWidth));


        mEditText = (EditText) findViewById(R.id.release_edit);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    /**
     * 把图片上传上去
     */
    private void uploadPic() {
//        String username = AppService.getInstance().getCurrentUser().username;
//        String fileName = username+"_"+System.currentTimeMillis();
        List<File> files = new ArrayList<>();
        for (int i = 0; i < selImageList.size(); i++) {
            files.add(new File(selImageList.get(i).path));
            Log.e(TAG,selImageList.get(i).name);
        }
        AppService.getInstance().upLoadFileAsync(files, new JsonCallback<LslResponse<User>>() {
            @Override
            public void onSuccess(LslResponse<User> userLslResponse, Call call, Response response) {
                if (userLslResponse.code == LslResponse.RESPONSE_OK){
                    UIUtil.showToast("图片上传成功");
                    Log.e(TAG,"图片上传成功");
                }else{
                    UIUtil.showToast("图片上传失败");
                    Log.e(TAG,"图片上传失败");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e(TAG,"图片上传异常"+e.getMessage());
            }
        });
    }

    private void sendInfo() {
        final String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            UIUtil.showToast("发布内容不能为空！");
            return;
        }
        showLoading(this);
        int classId = AppService.getInstance().getCurrentUser().classid;
        String username = AppService.getInstance().getCurrentUser().username;
        AppService.getInstance().addMainInfoAsync(classId, username, infoType, content, new JsonCallback<LslResponse<InfoModel>>() {

            @Override
            public void onSuccess(LslResponse<InfoModel> infoModelLslResponse, Call call, Response response) {
                if (infoModelLslResponse.code == LslResponse.RESPONSE_OK){
                    UIUtil.showToast("发布信息成功！");
                    stopLoading();
                    Log.e(TAG,infoType+"");
                    if (infoType == InfoType.NOTICE){
                        EventBus.getDefault().post(new NoticeEvent(infoModelLslResponse.data));
                        Log.e(TAG,"通知发起");
                    } else if (infoType == InfoType.HOMEWORK){
                        EventBus.getDefault().post(new HomeworkEvent(infoModelLslResponse.data));
                        Log.e(TAG,"公告发起");
                    } else {
                        EventBus.getDefault().post(new CommunityEvent(infoModelLslResponse.data));
                    }
                    ReleaseActivity.this.finish();
                }else {
                    UIUtil.showToast("发布信息失败，请稍后再试！");
                    stopLoading();
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(this, com.lzy.imagepicker.ui.ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                selImageList.clear();
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            }
        }
    }
}
