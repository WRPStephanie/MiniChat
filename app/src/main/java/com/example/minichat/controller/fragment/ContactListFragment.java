package com.example.minichat.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.fragment.app.Fragment;

import com.example.minichat.R;
import com.example.minichat.controller.activity.AddContactActivity;
import com.example.minichat.controller.activity.ChatActivity;
import com.example.minichat.controller.activity.GroupListActivity;
import com.example.minichat.controller.activity.InviteActivity;
import com.example.minichat.model.Model;
import com.example.minichat.model.bean.UserInfo;
import com.example.minichat.utils.Constant;
import com.example.minichat.utils.SpUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.contact.EaseContactListLayout;
import com.hyphenate.easeui.modules.menu.EasePopupMenuHelper;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//联系人列表页面
public class ContactListFragment extends EaseContactListFragment {
    private EaseTitleBar titleBar;
    private ListView listView;
    private ImageView iv_contact_red;
    private LocalBroadcastManager mLBM;
    private LinearLayout ll_contact_invite;
    private LinearLayout ll_contact_group;
    private String mHxid;
    private EaseContactListLayout contactList;

    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点信息
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
        }
    };
    private BroadcastReceiver ContactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 刷新页面
            refreshContact();
        }
    };
    private BroadcastReceiver GroupChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 显示红点
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
        }
    };

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = (EaseTitleBar)findViewById(R.id.title_bar);
        //listView = (ListView)findViewById(R.id.contact_list);//存疑
        contactList = contactLayout.getContactList();

        //布局显示+
        titleBar = (EaseTitleBar)findViewById(R.id.title_bar);
        titleBar.setRightImageResource(R.drawable.em_add);

        titleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddContactActivity.class);
                startActivity(intent);
            }
        });


        //添加头布局
        View headerView = View.inflate(getActivity(),R.layout.header_fragment_contact,null);
        //listView.addHeaderView(headerView);
        contactLayout.getContactList().addHeaderView(headerView);


        //获取红点对象
        iv_contact_red = (ImageView)headerView.findViewById(R.id.iv_contact_red);
        
        //初始化红点显示
        boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE,false);
        iv_contact_red.setVisibility(isNewInvite?View.VISIBLE:View.GONE);
        
        //注册广播
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(ContactInviteChangeReceiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(ContactChangeReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        mLBM.registerReceiver(GroupChangeReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));

        //获取邀请信息条目对象
        ll_contact_invite = (LinearLayout)headerView.findViewById(R.id.ll_contact_invite);
        //邀请信息条目的点击事件
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //红点处理
                iv_contact_red.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,false);
                //跳转到邀请信息列表页面
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
            }
        });

        //群组条目的点击事件
        ll_contact_group = (LinearLayout)headerView.findViewById(R.id.ll_contact_group);
        //跳转到群组列表页面
        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);

                startActivity(intent);
            }
        });

        //从环信服务器获取所有联系人的信息
        getContactFromHxServer();

        // 绑定listview和contextmenu
//        registerForContextMenu(listView);
//        registerForContextMenu(contactLayout.getContactList());

        //条目点击事件
//        contactLayout.getContactList().setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                //传递参数
//                intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID,contactLayout.getContactList().getItem(position).getUsername());
//                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_SINGLE);
//                startActivity(intent);
//            }
//        });

    }

    //条目点击事件
    //开发者如果使用 EaseContactListFragment 及其子类，可以重写 onItemClick(View view, int position) 方法即可。

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        if(position == 0) {
            return;
        }
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        //传递参数
        EaseUser user = contactLayout.getContactList().getItem(position-1);//头布局的position为0，故此处要-1
        if(user==null){
            return;
        }
        //mHxid = user.getUsername();
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID,user.getUsername());
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_SINGLE);
        startActivity(intent);
    }


//    @Override
//    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        //获取环信id
//        int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
//        EaseUser easeUser = contactLayout.getContactList().getItem(position);
//        mHxid = easeUser.getUsername();
//        //添加布局
//        getActivity().getMenuInflater().inflate(R.menu.delete,menu);
//    }


    @Override
    public void onMenuPreShow(EasePopupMenuHelper menuHelper, int position) {
        super.onMenuPreShow(menuHelper, position);
        menuHelper.addItemMenu(1,R.id.contact_delete,1,"删除");
    }

    //如果开发者使用的是 EaseContactListFragment 及其子类，则直接重写 onMenuItemClick(MenuItem item, int position) 即可。
    //用这个实现删除
    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        // 添加具体的点击事件实现逻辑，并返回 'true'

        EaseUser user = contactLayout.getContactList().getItem(position);
        mHxid = user.getUsername();
        if(item.getItemId()==R.id.contact_delete){
            //执行删除选中联系人
            deleteContact();
            return true;

        }
        return super.onMenuItemClick(item, position);


    }

    //执行删除选中联系人
    //TODO:进入函数后无法执行删除操作，且安卓模拟器闪退
    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 从环信服务器中删除联系人
                try {
                    EMClient.getInstance().contactManager().deleteContact(mHxid);

                    // 本地数据库的更新
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);

                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // toast提示
                            Toast.makeText(getActivity(), "删除" + mHxid + "成功", Toast.LENGTH_SHORT).show();

                            // 刷新页面
                            refreshContact();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();

                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除" + mHxid + "失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void getContactFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    //获取所有好友的环信id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //校验
                    if(hxids!=null&&hxids.size()>=0){
                        List<UserInfo> contacts = new ArrayList<UserInfo>();

                        // 转换
                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }

                        // 保存好友信息到本地数据库
                        Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts, true);

                        if (getActivity() == null) {
                            return;
                        }

                        // 刷新页面
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新页面的方法
                                refreshContact();
                            }
                        });

                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void refreshContact() {
//        // 获取数据
//        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
//
//        // 校验
//        if (contacts != null && contacts.size() >= 0) {
//
//            // 设置数据
//            Map<String, EaseUser> contactsMap = new HashMap<>();
//
//            // 转换
//            for (UserInfo contact : contacts) {
//                EaseUser easeUser = new EaseUser(contact.getHxid());
//
//                contactsMap.put(contact.getHxid(), easeUser);
//            }


//            setContactsMap(contactsMap);
//
//            // 刷新页面
//            refresh();
//        }
        contactLayout.onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
        mLBM.unregisterReceiver(ContactChangeReceiver);
        mLBM.unregisterReceiver(GroupChangeReceiver);
    }

    //    @Override
//    public void onItemClick(View view, int position) {
//        super.onItemClick(view, position);
//
////        titleBar.getRightLayout().setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View view, MotionEvent motionEvent) {
////                return false;
////            }
////        });
//        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(),AddContactActivity.class);
//                startActivity(intent);
//            }
//        });
////        titleBar.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View view, MotionEvent motionEvent) {
////                view.getParent().requestDisallowInterceptTouchEvent(true);
////                switch (motionEvent.getAction()){
////                    case MotionEvent.ACTION_UP:
////                        Intent intent = new Intent(getActivity(),AddContactActivity.class);
////                        startActivity(intent);
////                        break;
////                }
////                return false;
////            }
////        });
//
//    }
}
