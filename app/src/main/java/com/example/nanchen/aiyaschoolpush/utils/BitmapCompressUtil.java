package com.example.nanchen.aiyaschoolpush.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.utils
 * @date 2016/11/25  09:22
 */

public class BitmapCompressUtil {

    private static final int MIN_LENGTH = 720;
    private static final int MIN_LENGTH2 = 640;
    private static final int MIN_LENGTH3 = 480;
    private static final int MAX_IN_SAMPLE_SIZE = 2*2*2*2*2*2;

    private static boolean checkBitmapNeedChange(int width,int height,int size){
        if (width < size || height < size) {
            return false;
        }
        return true;
    }



    public static Bitmap tryNewOptions(Options options, String path) throws LowMemoryException{
        if (options.inSampleSize==0) {
            options.inSampleSize = 2;
        }else{
            options.inSampleSize = options.inSampleSize*2;
        }
        while (options.inSampleSize<MAX_IN_SAMPLE_SIZE) {
            //System.out.println("decodeFile");
            Bitmap bitmap = null;
            try{
                bitmap = BitmapFactory.decodeFile(path, options);
            }catch (Throwable throwable){
                bitmap = null;
                options.inSampleSize = options.inSampleSize*2;
            }
            if (null!=bitmap) {
                //System.out.println("get bitmap");
                return bitmap;
            }
        }
        return null;
    }
}
