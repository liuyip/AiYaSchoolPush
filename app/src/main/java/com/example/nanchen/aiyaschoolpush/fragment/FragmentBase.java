package com.example.nanchen.aiyaschoolpush.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.nanchen.aiyaschoolpush.view.Loading;
import com.example.nanchen.aiyaschoolpush.view.Loading.OnReturnListener;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.fragment
 * @date 2016/10/08  09:00
 */

public class FragmentBase extends Fragment {

    private Dialog mDialog;

    @Override
    public void onAttach(Context context) {
        Log.e(this.getClass().getSimpleName(),"onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(this.getClass().getSimpleName(),"onCreate");
        super.onCreate(savedInstanceState);
    }

    public void showLoading(Context context, String text, OnReturnListener listener){
        Loading loading = new Loading();
        loading.showLoading(context,text,listener,Loading.LOGOSTYLE);
    }

    public void showLoading(Context context,String text){
        Loading loading = new Loading();
        loading.showLoading(context,text,null,Loading.LOGOSTYLE);
    }

    public void showLoading(Context context){
        Loading loading = new Loading();
        loading.showLoading(context,null,null,Loading.LOGOSTYLE);
    }

    public void stopLoading(){
        Loading loading = new Loading();
        loading.dialogDismiss(mDialog);
    }
}
