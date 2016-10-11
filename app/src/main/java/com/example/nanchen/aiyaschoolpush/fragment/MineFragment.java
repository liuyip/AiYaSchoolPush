package com.example.nanchen.aiyaschoolpush.fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.nanchen.aiyaschoolpush.CropOption;
import com.example.nanchen.aiyaschoolpush.R;
import com.example.nanchen.aiyaschoolpush.activity.AboutActivity;
import com.example.nanchen.aiyaschoolpush.activity.ActivityBase;
import com.example.nanchen.aiyaschoolpush.activity.ChildInfoActivity;
import com.example.nanchen.aiyaschoolpush.adapter.CommonAdapter;
import com.example.nanchen.aiyaschoolpush.adapter.ViewHolder;
import com.example.nanchen.aiyaschoolpush.utils.IntentUtil;
import com.example.nanchen.aiyaschoolpush.utils.UIUtil;
import com.example.nanchen.aiyaschoolpush.view.LinearLayoutListItemView;
import com.example.nanchen.aiyaschoolpush.view.OnLinearLayoutListItemClickListener;
import com.example.nanchen.aiyaschoolpush.view.RoundImageView;
import com.example.nanchen.aiyaschoolpush.view.SelectDialog;
import com.example.nanchen.aiyaschoolpush.view.SelectDialog.SelectDialogListener;
import com.example.nanchen.aiyaschoolpush.view.TitleView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static android.app.Activity.RESULT_OK;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.fragment
 * @date 2016/10/08  08:57
 */

public class MineFragment extends FragmentBase{
    private TitleView mTitleBar;
    private RoundImageView mHeadImage;
    private LinearLayoutListItemView mMenuReviseData;
    private LinearLayoutListItemView mMenuMyRoom;
    private LinearLayoutListItemView mMenuMyBaby;
    private LinearLayoutListItemView mMenuAbout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine,container,false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        mTitleBar = (TitleView) view.findViewById(R.id.mine_titleBar);
        mTitleBar.setTitle("我的");

        mHeadImage = (RoundImageView) view.findViewById(R.id.mine_image);
        mHeadImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });


        mMenuReviseData = (LinearLayoutListItemView) view.findViewById(R.id.mine_revise_data);
        mMenuMyRoom = (LinearLayoutListItemView) view.findViewById(R.id.mine_my_room);
        mMenuMyBaby = (LinearLayoutListItemView) view.findViewById(R.id.mine_my_baby);
        mMenuAbout = (LinearLayoutListItemView) view.findViewById(R.id.mine_about);

        mMenuReviseData.setOnLinearLayoutListItemClickListener(new OnLinearLayoutListItemClickListener() {
            @Override
            public void onLinearLayoutListItemClick(Object object) {
                Crouton.makeText(getActivity(),"你点击了个人信息", Style.ALERT).show();
            }
        });

        mMenuMyRoom.setOnLinearLayoutListItemClickListener(new OnLinearLayoutListItemClickListener() {
            @Override
            public void onLinearLayoutListItemClick(Object object) {
                Crouton.makeText(getActivity(),"你点击了我的空间", Style.ALERT).show();
            }
        });

        mMenuMyBaby.setOnLinearLayoutListItemClickListener(new OnLinearLayoutListItemClickListener() {
            @Override
            public void onLinearLayoutListItemClick(Object object) {
                Crouton.makeText(getActivity(),"你点击了我的宝贝", Style.ALERT).show();
                IntentUtil.newIntent(getActivity(), ChildInfoActivity.class);
            }
        });

        mMenuAbout.setOnLinearLayoutListItemClickListener(new OnLinearLayoutListItemClickListener() {
            @Override
            public void onLinearLayoutListItemClick(Object object) {
                IntentUtil.newIntent(getActivity(), AboutActivity.class);
            }
        });

    }


    private final int PHOTO_PICKED_FROM_CAMERA = 1; // 用来标识头像来自系统拍照
    private final int PHOTO_PICKED_FROM_FILE = 2; // 用来标识从相册获取头像
    private final int CROP_FROM_CAMERA = 3;

    private void getIconFromPhoto(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICKED_FROM_FILE);
    }

    /**
     * 弹出选择图片或者拍照
     */
    private void selectPhoto() {
        List<String> list = new ArrayList<>();
        list.add("拍照");
        list.add("相册");
        showDialog(new SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        getIconFromCamera();
                        break;
                    case 1:
                        getIconFromPhoto(); // 从系统相册获取
                        break;
                    default:
                        break;
                }
            }
        },list);

    }

    private Uri imgUri; // 由于android手机的图片基本都会很大，所以建议用Uri，而不用Bitmap

    /**
     * 调用系统相机拍照
     */
    private void getIconFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "avatar_"+String.valueOf(System.currentTimeMillis())+".png"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,PHOTO_PICKED_FROM_CAMERA);
    }

    private SelectDialog showDialog(SelectDialogListener listener, List<String> list){
        SelectDialog dialog = new SelectDialog(getActivity(),
                R.style.transparentFrameWindowStyle,listener,list);
        if (((ActivityBase)getActivity()).canUpdateUI()){
            dialog.show();
        }
        return dialog;
    }


    /**
     * 尝试裁剪图片
     */
    private void doCrop(){
        final ArrayList<CropOption> cropOptions = new ArrayList<>();
        final Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent,0);
        int size = list.size();
        if (size == 0){
            UIUtil.showToast(getActivity(),"当前不支持裁剪图片!");
            return;
        }
        intent.setData(imgUri);
        intent.putExtra("outputX",300);
        intent.putExtra("outputY",300);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("scale",true);
        intent.putExtra("return-data",true);

        // only one
        if (size == 1){
            Intent intent1 = new Intent(intent);
            ResolveInfo res = list.get(0);
            intent1.setComponent(new ComponentName(res.activityInfo.packageName,res.activityInfo.name));
            startActivityForResult(intent1,CROP_FROM_CAMERA);
        }else {
            // 很多可支持裁剪的app
            for (ResolveInfo res : list) {
                CropOption co = new CropOption();
                co.title = getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                co.icon = getActivity().getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                co.appIntent = new Intent(intent);
                co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName,res.activityInfo.name));
                cropOptions.add(co);
            }

            CommonAdapter<CropOption> adapter = new CommonAdapter<CropOption>(getActivity(),cropOptions,R.layout.layout_crop_selector) {
                @Override
                public void convert(ViewHolder holder, CropOption item) {
                    holder.setImageDrawable(R.id.iv_icon,item.icon);
                    holder.setText(R.id.tv_name,item.title);
                }
            };

            AlertDialog.Builder builder = new Builder(getActivity());
            builder.setTitle("choose a app");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(cropOptions.get(which).appIntent,CROP_FROM_CAMERA);
                }
            });
            builder.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (imgUri != null){
                        getActivity().getContentResolver().delete(imgUri,null,null);
                        imgUri = null;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }
        switch (requestCode) {
            case PHOTO_PICKED_FROM_CAMERA:
                doCrop();
                break;
            case PHOTO_PICKED_FROM_FILE:
                imgUri = data.getData();
                doCrop();
                break;
            case CROP_FROM_CAMERA:
                if (data != null){
                    setCropImg(data);
                }
                break;
            default:
                break;
        }
    }

    private void setCropImg(Intent picData){
        Bundle bundle = picData.getExtras();
        if (bundle != null){
            Bitmap mBitmap = bundle.getParcelable("data");
            mHeadImage.setImageBitmap(mBitmap);
            saveBitmap(Environment.getExternalStorageDirectory() + "/crop_"
                    +System.currentTimeMillis() + ".png",mBitmap);
        }
    }

    private void saveBitmap(String fileName,Bitmap bitmap){
        File file = new File(fileName);
        FileOutputStream fout = null;
        try {
            file.createNewFile();
            fout = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG,100,fout);
            fout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fout != null;
                fout.close();
                UIUtil.showToast(getActivity(),"保存成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
