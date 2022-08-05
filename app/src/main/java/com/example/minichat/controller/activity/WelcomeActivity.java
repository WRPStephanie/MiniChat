package com.example.minichat.controller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;

import com.example.minichat.R;
import com.example.minichat.model.Model;
import com.example.minichat.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

//欢迎页面
public class WelcomeActivity extends /*AppCompatActivity*/ Activity {
    private Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            //如果当前activity已经退出，那么不处理handler中的消息
            if(isFinishing()){
                return;
            }

            //判断进入主页面还是登录页面
            toMainOrLogin();
        }
    };

    //判断进入主页面还是登录页面
    private void toMainOrLogin() {
//        new Thread(){
//            public void run(){
//
//            }
//        }.start();

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //判断当前帐号是否已经登录过
                if(EMClient.getInstance().isLoggedInBefore()){//登录过
                    //获取当前登录用户的信息
                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxid(EMClient.getInstance().getCurrentUser());
                    if(account==null){
                        //跳转到登录页面
                        Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        //登录成功后的方法
                        Model.getInstance().loginSuccess(account);

                        //跳转到主页面
                        Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                    //跳转到主页面
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else {//没登录过
                    //跳转到登录页面
                    Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                }

                //结束当前页面
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //发送2s的延时消息
        handler.sendMessageDelayed(Message.obtain(),2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
