package com.example.nanchen.aiyaschoolpush.api;

import com.example.nanchen.aiyaschoolpush.config.Consts;
import com.example.nanchen.aiyaschoolpush.model.User;
import com.example.nanchen.aiyaschoolpush.net.okgo.JsonCallback;
import com.example.nanchen.aiyaschoolpush.net.okgo.LslResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;
import java.util.HashMap;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.api
 * @date 2016/11/09  11:19
 */

public class AppService {
    private static final String TAG = "AppService";

    private static AppService instance;
    private User mCurrentUser;

    private AppService(){
    }

    public static AppService getInstance(){
        if (instance == null){
            instance = new AppService();
        }
        return instance;
    }

    /**
     * 退出登录时重置此类
     */
    public static void resetInstance(){
        instance = null;
    }

    /**
     * 获取当前登录的用户
     * @return  当前用户
     */
    public User getCurrentUser(){
        return mCurrentUser;
    }

    public void setCurrentUser(User user){
        this.mCurrentUser = user;
    }

    /***************    用户系统 Begin    ******************/


    /**
     * 用户手机号验证是否已经注册
     * @param mobile    手机号
     * @param callback  回调
     */
    public void isUsableMobileAsync(String mobile, JsonCallback<LslResponse<User>> callback){
        String url = Consts.API_SERVICE_HOST+"/user/usable_mobile.php?mobile="+mobile;
        OkGo.get(url).execute(callback);
    }


    /**
     * 用户注册
     * @param username  用户名
     * @param password  密码
     * @param nickname  昵称
     * @param birthday  生日
     * @param callback  回调
     */
    public void registerAsync(String username,String password,String nickname,String birthday,String iconUrl,JsonCallback<LslResponse<User>> callback){
        String url = Consts.API_SERVICE_HOST + "/user/register.php";
        HashMap<String,String> postParams = new HashMap<>();
        postParams.put("username",username);
        postParams.put("password",password);
        postParams.put("nickname",nickname);
        postParams.put("birthday",birthday);
        postParams.put("avatar",iconUrl);
        OkGo.post(url).params(postParams).execute(callback);
    }

    /**
     * 异步用户登录
     * @param username  用户名
     * @param password  用户密码
     * @param callback  回调
     */
    public void loginAsync(String username,String password,JsonCallback<LslResponse<User>> callback){
        String url = Consts.API_SERVICE_HOST + "/user/login.php";
        HashMap<String,String> postParams = new HashMap<>();
        postParams.put("username",username);
        postParams.put("password",password);
        OkGo.post(url).params(postParams).execute(callback);
    }




    /**
     * 异步用户修改密码
     * @param username  用户名
     * @param password  新密码
     * @param callback  回调
     */
    public void resetPwdAsync(String username,String password,JsonCallback<LslResponse<User>> callback){
        String url = Consts.API_SERVICE_HOST + "/user/reset_pwd.php";
        HashMap<String,String> postParams = new HashMap<>();
        postParams.put("username",username);
        postParams.put("password",password);
        OkGo.post(url).params(postParams).execute(callback);
    }

    /**
     * 异步用户头像上传
     * @param file      文件
     * @param callback  回调
     */
    public void upLoadAvatarAsync(File file,JsonCallback<LslResponse<User>> callback){
        String url = Consts.API_SERVICE_HOST + "/user/avatar.php";
        OkGo.post(url).params("avatar",file,file.getName()).execute(callback);
    }

    /**
     * 异步用户头像上传
     * @param file      文件
     * @param callback  回调  返回json字符串，只是为了测试
     */
    public void upLoadAvatarAsync(File file, StringCallback callback){
        String url = Consts.API_SERVICE_HOST + "/user/avatar.php";
        OkGo.post(url).params("avatar",file).execute(callback);
    }

    /**
     * 更新用户头像信息
     *
     * @param username  用户名
     * @param iconUrl   头像地址
     * @param type      传递类型
     * @param callback  回调
     */
    public void updateAvatarUrlAsync(String username,String iconUrl,int type,JsonCallback<LslResponse<User>> callback){
        String url = Consts.API_SERVICE_HOST+"/user/update_avatar.php?username="+username
                +"&iconUrl="+iconUrl+"&type="+type;
        OkGo.get(url).execute(callback);
    }



    /***************    用户系统 End    ******************/


    /***************    信息系統 Begin    ******************/


//    public void getNoticeAsync(String username,int type,JsonCallback)

    /***************    信息系統 End      ******************/

}
