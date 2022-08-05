package com.example.minichat.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.minichat.controller.activity.ChatActivity;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;

//会话列表页面
public class ChatFragment extends EaseConversationListFragment {
    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        //传递参数
        EaseConversationInfo info = conversationListLayout.getItem(position);
        Object object = info.getInfo();

        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID,((EMConversation) object).conversationId());
        //判断是否为群聊
        if(((EMConversation) object).getType()==EMConversation.EMConversationType.GroupChat){
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
        }

        startActivity(intent);
    }


    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

    }

}
