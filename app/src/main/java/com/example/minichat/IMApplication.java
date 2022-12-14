package com.example.minichat;

import android.app.Application;
import android.content.Context;

import com.example.minichat.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseIM;

public class IMApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        //EaseIM 初始化
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);//设置需要 同意后才能接受邀请
        options.setAutoAcceptGroupInvitation(false);//设置需要同意后才能接受群邀请
        //EaseIM.getInstance().init(this,options);


        if(EaseIM.getInstance().init(this, options)){
            //在做打包混淆时，关闭 debug 模式，避免消耗不必要的资源
            EMClient.getInstance().setDebugMode(true);
            //EaseIM 初始化成功之后再调用注册消息监听的代码 ...
        }

        //初始化数据模型层类
        Model.getInstance().init(this);

        //初始化全局上下文对象
        mContext = this;
    }

    //获取全局上下文对象
    public static Context getGlobalApplication(){
        return mContext;
    }
}
