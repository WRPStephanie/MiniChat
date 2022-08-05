package com.example.minichat.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.example.minichat.R;
import com.example.minichat.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

//会话详情页面
public class ChatActivity extends FragmentActivity {
    private String mHxid;
    private EaseChatFragment easeChatFragment;
    private LocalBroadcastManager mLBM;
    private int mChatType;
    private EaseTitleBar etb_chat_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        initData();

        initListener();
    }

    private void initListener() {
        if(mChatType==EaseConstant.CHATTYPE_GROUP){
            etb_chat_name.setRightLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChatActivity.this,GroupDetailActivity.class);
                    //群ID
                    intent.putExtra(Constant.GROUP_ID,mHxid);
                    startActivity(intent);
                }
            });
        }

        // 如果当前类型为群聊
        if(mChatType == EaseConstant.CHATTYPE_GROUP) {

            // 注册退群广播
            BroadcastReceiver ExitGroupReceiver  = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(mHxid.equals(intent.getStringExtra(Constant.GROUP_ID))) {
                        // 结束当前页面
                        finish();
                    }
                }
            };

            mLBM.registerReceiver(ExitGroupReceiver, new IntentFilter(Constant.EXIT_GROUP));
        }
    }

    private void initData() {
        //创建一个chatFragment
        easeChatFragment = new EaseChatFragment();
        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);

        etb_chat_name = (EaseTitleBar)findViewById(R.id.etb_chat_name);


        // 获取聊天类型
        mChatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);
        if(mChatType == EaseConstant.CHATTYPE_GROUP){
            etb_chat_name.setTitle(EMClient.getInstance().groupManager().getGroup(mHxid).getGroupName());
            etb_chat_name.setRightImageResource(R.drawable.em_group_icon);
        }else{
            etb_chat_name.setTitle(mHxid);
        }
        easeChatFragment.setArguments(getIntent().getExtras());

        //替换fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_chat,easeChatFragment).commit();

        // 获取发送广播的管理者
        mLBM = LocalBroadcastManager.getInstance(ChatActivity.this);
    }
}
