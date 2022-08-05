package com.example.minichat.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.minichat.R;
import com.example.minichat.controller.activity.LoginActivity;
import com.example.minichat.model.Model;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

//设置页面
public class SettingFragment extends Fragment {
    private Button bt_setting_out;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting,null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        bt_setting_out = (Button)view.findViewById(R.id.bt_setting_out);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        //在button上显示当前用户名称
        bt_setting_out.setText("退出登录（"+ EMClient.getInstance().getCurrentUser()+"）");

        //退出登录的逻辑处理
        bt_setting_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //退出环信服务器退出登录
                        EMClient.getInstance().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //关闭DBHelper
                                Model.getInstance().getDbManager().close();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新UI
                                        Toast.makeText(getActivity(),"退出成功",Toast.LENGTH_SHORT).show();
                                        //回到登陆页面
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });

                            }

                            @Override
                            public void onError(int code, String error) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),"推出失败"+error,Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                });
            }
        });
    }
}
