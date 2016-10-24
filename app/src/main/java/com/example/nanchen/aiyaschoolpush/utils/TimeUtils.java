package com.example.nanchen.aiyaschoolpush.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.utils
 * @date 2016/10/08  13:45
 */

public class TimeUtils {

    public static String longToDateTime(long longTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(new Date(longTime*1000));
    }
}
