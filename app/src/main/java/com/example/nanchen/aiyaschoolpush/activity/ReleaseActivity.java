package com.example.nanchen.aiyaschoolpush.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.nanchen.aiyaschoolpush.App;
import com.example.nanchen.aiyaschoolpush.CommunityEvent;
import com.example.nanchen.aiyaschoolpush.HomeworkEvent;
import com.example.nanchen.aiyaschoolpush.ImagePickerAdapter;
import com.example.nanchen.aiyaschoolpush.NoticeEvent;
import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.api.AppService;
import com.example.nanchen.aiyaschoolpush.config.AddConfig;
import com.example.nanchen.aiyaschoolpush.config.Consts;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 发布信息专用的Activity
 */
public class ReleaseActivity extends ActivityBase implements ImagePickerAdapter.OnRecyclerViewItemClickListener {
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数

    private static final int SCALE_SIZE = 400;

    private static final String TAG = "ReleaseActivity";
    private TitleView mTitleBar;
    private String mFrom;
    private WavyLineView mWavyLine;
    private EditText mEditText;
    private int infoType;

    private List<File> mSmallFiles;
    private List<File> mFiles;
    private List<String> mSmallUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);

        bindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void bindView() {
        names = new ArrayList<>();
        mFiles = new ArrayList<>();
        mSmallFiles = new ArrayList<>();
        mSmallUrls = new ArrayList<>();

        mFrom = getIntent().getStringExtra("name");
        mTitleBar = (TitleView) findViewById(R.id.release_title);
        mTitleBar.setLeftButtonAsFinish(this);
        Log.e(TAG, mFrom);
        switch (mFrom) {
            case AddConfig.NOTICE:
                mTitleBar.setTitle("发布公告");
                infoType = InfoType.NOTICE;
                break;
            case AddConfig.HOMEWORK:
                mTitleBar.setTitle("发布作业");
                infoType = InfoType.HOMEWORK;
                break;
            default:
                mTitleBar.setTitle("发布动态");
                infoType = InfoType.COMMUNITY;
                break;
        }
        mTitleBar.changeRightButtonTextColor(getResources().getColor(R.color.white3));
        mTitleBar.setRightButtonText(getResources().getString(R.string.send_back_right));
        mTitleBar.setRightButtonTextSize(25);
        mTitleBar.setFixRightButtonPadingTop();
        mTitleBar.setRightButtonOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoading(ReleaseActivity.this);
                tryDecodeSmallImg(); // 先压缩尺寸
                //鲁班压缩
                compressWithLs(new File(selImageList.get(0).path));
//                uploadPic();
            }
        });


        // 波浪线设置
        mWavyLine = (WavyLineView) findViewById(R.id.release_wavyLine);
        int initStrokeWidth = 1;
        int initAmplitude = 5;
        float initPeriod = (float) (2 * Math.PI / 60);
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
     * 压缩像素
     */
    private void tryDecodeSmallImg() {
        Log.e(TAG, "开始像素压缩:" + selImageList.size());
        for (int i = 0; i < selImageList.size(); i++) {
            String filePath = selImageList.get(i).path;
//            BitmapFactory.Options localOptions = new BitmapFactory.Options();
//            localOptions.inJustDecodeBounds = true;
//            int simpleSize = localOptions.outHeight / SCALE_SIZE;
//            if (simpleSize <= 0){
//                simpleSize = 1;
//            }
//            localOptions.inSampleSize = simpleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, getBitmapOption());//将图片的长和宽缩小味原来的1/simpleSize
            saveBitmapFile(bitmap, i,filePath);
        }
        Log.e(TAG, "当前需要压缩的图片数量:" + mFiles.size());
    }

    private Options getBitmapOption() {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
//        int inSampleSize = options.outHeight / SCALE_SIZE;
//        if (inSampleSize <= 0) {
//            inSampleSize = 1;
//        }
        int inSampleSize = 2;
        Log.e(TAG,"inSampleSize:"+inSampleSize);
        options.inSampleSize = inSampleSize;
        return options;
    }
    private static final int MIN_SAMPLE_SIZE = 2;
    private static final int MAX_IN_SAMPLE_SIZE = 2*2*2*2*2*2;
    private static final int MIN_LENGTH2 = 640;

    private int calculateInSampleSize(BitmapFactory.Options options) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 0;
        if (!checkBitmapNeedChange(width, height, MIN_LENGTH2)) {
            return inSampleSize;
        }
        inSampleSize = MIN_SAMPLE_SIZE;
        if (height > MIN_LENGTH2 || width > MIN_LENGTH2) {
            final int heightRatio =  (int) Math.ceil((float) height/ (float) MIN_LENGTH2);
            final int widthRatio = (int) Math.ceil((float) width / (float) MIN_LENGTH2);
            int temp = (heightRatio < widthRatio ? heightRatio : widthRatio);

            if (temp > inSampleSize) {
                inSampleSize = temp;
            }
            if (temp > MAX_IN_SAMPLE_SIZE) {
                inSampleSize = MAX_IN_SAMPLE_SIZE;
            }
        }
        return inSampleSize;
    }

    private boolean checkBitmapNeedChange(int width,int height,int size){
        if (width<size || height<size) {
            return false;
        }
        return true;
    }



    public void saveBitmapFile(Bitmap bitmap, int i,String filePath) {

        if(bitmap==null){
            return;//如果图片本身的大小已经小于这个大小了，就没必要进行压缩
        }
//        File file = new File(i + "_" + System.currentTimeMillis());//将要保存的文件名称
        Log.e(TAG,"文件路径:"+filePath);
        File file = new File(filePath);
        try {
            FileOutputStream bos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            mFiles.add(file);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"失败："+e.getMessage());
        }
    }

    private boolean isUploadPics;
    private List<String> names;
    private int index = 0;

    /**
     * 压缩单张图片 Listener 方式
     */
    private void compressWithLs(File file) {


//        mSmallFiles.addAll(mFiles);
//
//        for (int i = 0; i < selImageList.size(); i++) {
//            mSmallUrls.add(Consts.API_SERVICE_HOST + "/info/pic/" + mFiles.get(i).getName());
//        }
//        uploadPic();
        Log.e(TAG, "鲁班压缩开始下标：" + index);
        Luban.get(App.getAppContext())
                .load(file)
                .putGear(Luban.THIRD_GEAR)
                .setFilename(index + "_" + System.currentTimeMillis())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.e(TAG, "鲁班onStart");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.e(TAG, "鲁班onSuccess:" + file.getName());
                        mSmallFiles.add(file);
                        mSmallUrls.add(Consts.API_SERVICE_HOST + "/info/pic/" + file.getName());
                        if (++index < mFiles.size()) {
                            compressWithLs(mFiles.get(index));
                        } else {
                            uploadPic();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "鲁班onError:" + e.getMessage());
                    }
                }).launch();
    }


    /**
     * 把图片上传上去
     */
    private boolean uploadPic() {
//        Log.e(TAG, "size:" + mFiles.size() + "  ****   ");
//        names.clear();
//        for (int i = 0; i < mFiles.size(); i++) {
//            Log.e(TAG, "name:" + i + "_" + mFiles.get(i).getName());
//        }

//        showLoading(this);

//        for (int i = 0; i < mSmallFiles.size(); i++) {
//            mSmallUrls.add(mSmallFiles.get(i).getName());
//        }
//
        Log.e(TAG, "size:" + mSmallUrls.size() + "  ****   ");
        for (int i = 0; i < mSmallUrls.size(); i++) {
            Log.e(TAG, "小图名字:" + mSmallUrls.get(i));
        }

        AppService.getInstance().upLoadFileAsync(mSmallFiles, new JsonCallback<LslResponse<User>>() {
            @Override
            public void onSuccess(LslResponse<User> userLslResponse, Call call, Response response) {
                if (userLslResponse.code == LslResponse.RESPONSE_OK) {
                    UIUtil.showToast("图片上传成功");
                    Log.e(TAG, "图片上传成功");
                    isUploadPics = true;
                } else {
                    UIUtil.showToast("图片上传失败");
                    Log.e(TAG, "图片上传失败");
                    isUploadPics = false;
                }
                sendInfo();
//                stopLoading();
            }
        });
        return isUploadPics;
    }

    private void sendInfo() {
        final String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            UIUtil.showToast("发布内容不能为空！");
            return;
        }
        int classId = AppService.getInstance().getCurrentUser().classid;
        String username = AppService.getInstance().getCurrentUser().username;
        if (isUploadPics) {
            for (int i = 0; i < mFiles.size(); i++) {
                names.add(Consts.API_SERVICE_HOST + "/info/pic/" + mSmallFiles.get(i).getName());
            }
        }
        AppService.getInstance().addMainInfoAsync(classId, username, infoType, content, names, new JsonCallback<LslResponse<InfoModel>>() {

            @Override
            public void onSuccess(LslResponse<InfoModel> infoModelLslResponse, Call call, Response response) {
                if (infoModelLslResponse.code == LslResponse.RESPONSE_OK) {
                    UIUtil.showToast("发布信息成功！");
                    stopLoading();
                    Log.e(TAG, infoType + "");
                    if (infoType == InfoType.NOTICE) {
                        EventBus.getDefault().post(new NoticeEvent(infoModelLslResponse.data));
                        Log.e(TAG, "通知发起");
                    } else if (infoType == InfoType.HOMEWORK) {
                        EventBus.getDefault().post(new HomeworkEvent(infoModelLslResponse.data));
                        Log.e(TAG, "公告发起");
                    } else {
                        EventBus.getDefault().post(new CommunityEvent(infoModelLslResponse.data));
                    }
                    if (!ReleaseActivity.this.isFinishing()) {
                        stopLoading();
                    }
                    ReleaseActivity.this.finish();
                } else {
                    UIUtil.showToast("发布信息失败，请稍后再试！");
                    if (!ReleaseActivity.this.isFinishing()) {
                        stopLoading();
                    }
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
//                for (int i = 0; i < selImageList.size(); i++) {
//                    mFiles.add(new File(selImageList.get(i).path));
//                }

                //鲁班压缩
//                compressWithLs(new File(selImageList.get(0).path));
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
