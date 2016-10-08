package com.example.nanchen.aiyaschoolpush.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.nanchen.aiyaschoolpush.helper.CoderHelper;
import com.example.nanchen.aiyaschoolpush.helper.LocalStorageHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 照片相关功能
 * 
 * @author Jin
 * @date 2015-1-29上午9:23:27
 */
public class PhotoUtil {
	/* 用来标识请求照相功能 */
	public static final int CAMERA_WITH_DATA = 50;
	/* 用来标识裁剪的返回 */
	public static final int CUT_PHOTO = 70;
	/* 用来标识修改成功返回 */
	public static final int PHOTO_SCUESS = 20;
	/* 用来标识从相册获取头像 */
	public static final int PHOTO_PICKED_WITH_DATA = 60;

	/**
	 * 调用相机拍照
	 */
	public static Uri camera(Activity activity) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File tempFile = LocalStorageHelper.createTempFile(".jpg");
		if (null == tempFile) {
			Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
			return null;
		}
		String temppath = tempFile.getAbsolutePath();
		Uri tempuri = Uri.fromFile(tempFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
		try {
			activity.startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (android.content.ActivityNotFoundException e) {
			Toast.makeText(activity,
					"您的系统没有相机应用,请安装！",
					Toast.LENGTH_SHORT).show();
		}
		return tempuri;

	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param
	 * @吴立富
	 * @2014-12-4 12:40
	 */
	public static Uri startPhotoZoom(Activity activity, Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 120);
		intent.putExtra("scale", true);
		intent.putExtra("outputY", 120);

		intent.putExtra("return-data", false);
		Uri zoomUri=getTempUri();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, zoomUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		try {
			activity.startActivityForResult(intent, CUT_PHOTO);
		} catch (android.content.ActivityNotFoundException e) {
			Toast.makeText(activity,
					"您的系统没有剪裁相片应用,请安装！",
					Toast.LENGTH_SHORT).show();
		}
		return zoomUri;
	}

	private static Uri getTempUri() {
		return Uri.fromFile(LocalStorageHelper.createTempFile(".jpg"));
	}

	/**
	 * 裁剪图片方法实现(宽比高大一倍)
	 * 
	 * @param
	 * @吴立富
	 * @2014-12-4 12:40
	 */
	public static void startLongPhotoZoom(Activity activity, Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 160);
		intent.putExtra("return-data", true);
		try {
			activity.startActivityForResult(intent, CUT_PHOTO);
		} catch (android.content.ActivityNotFoundException e) {
			Toast.makeText(activity,
					"您的系统没有剪裁相片应用,请安装！",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 从手机相册获取头像
	 * 
	 * @param activity
	 *            void 返回类型
	 * @throws
	 */
	public static void getIconFromPhoto(Activity activity) {
		if (openPhotosNormal(activity)) {
			if (openPhotosBrowser(activity)) {
				openPhotosFinally(activity);
			}
		}
	}

	/**
	 * 使用系统当前日期加以调整作为照片的名称
	 * 
	 * @param
	 * @吴立富
	 * @2014-12-4 13:18
	 */
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param
	 * @吴立富
	 * @2014-12-4 12:40
	 */
	public static File setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			File finalfile = LocalStorageHelper.createTempFile(".jpg");
			Bitmap photo = extras.getParcelable("data");
			saveImg(photo, finalfile);
			return finalfile;

		}
		return null;
	}

	public static boolean saveImg(Bitmap bitmap, File file) {
		if (bitmap == null || null == file) {
			return false;
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)) {
				out.flush();
			}
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CoderHelper.close(out);
		}
		return false;
	}

	public static String getImageUrlFromActivityResult(Activity a, Uri photoUri) {
		// 需要从content provider中获取真正的图片路径
		String imgPath = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = a.managedQuery(photoUri, proj, null, null,
				null);
		if (actualimagecursor == null || actualimagecursor.getCount() == 0) {
			// 如果数据库查不到就判断是不是file schema的，通过file schema可以直接得到文件路径
			boolean bFindPath = false;
			if (photoUri.getScheme().equals("file")) {
				imgPath = photoUri.getPath();
				File file = new File(imgPath);
				if (null != file && file.exists()) {
					bFindPath = true;
				}
			}
			if (!bFindPath) {
				return null;
			}
		} else {
			int actualIndex = actualimagecursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			imgPath = actualimagecursor.getString(actualIndex);
			if (TextUtils.isEmpty(imgPath) || !new File(imgPath).exists()) {
				// Toast.makeText(this, R.string.file_invalid,
				// Toast.LENGTH_SHORT).show();
				return null;
			}
		}
		return imgPath;
	}

	/**
	 * 打开本地相册
	 * 
	 * @param activity
	 * @return true - 没有本地相册 false - 成功打开本地相册
	 */
	private static boolean openPhotosNormal(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		try {
			activity.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (android.content.ActivityNotFoundException e) {
			return true;
		}catch (Exception e){
			return true;
		}
		return false;
	}

	/**
	 * 打开文件浏览器，如果没有本地相册的话
	 * 
	 * @param activity
	 * @return true - 没有文件浏览器 false - 成功打开文件浏览器
	 */
	private static boolean openPhotosBrowser(Activity activity) {
		Toast.makeText(activity,
				"没有相册软件，运行文件浏览器!",
				Toast.LENGTH_LONG).show();
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		Intent wrapperIntent = Intent.createChooser(intent, null);
		try {
			activity.startActivityForResult(wrapperIntent,
					PHOTO_PICKED_WITH_DATA);
		} catch (android.content.ActivityNotFoundException e) {
			return true;
		}
		return false;
	}

	/**
	 * 找不到本地相册及文件浏览器，仅给出提示
	 * 
	 * @param activity
	 * @return
	 */
	private static boolean openPhotosFinally(Activity activity) {
		Toast.makeText(activity,
				"您的系统没有文件浏览器或则相册支持,请安装！",
				Toast.LENGTH_LONG).show();
		return false;
	}

}
