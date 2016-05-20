/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.xianglin.fellowvillager.app.adapter.FragmentMessageAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.widget.FragmentContactListView;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 *  会话列表
 * @author 彭阳
 * @version 0.1, 2016-02-26
 */
public class ConversationListFragment extends BaseFragment {

    private static final int MSG_REFRESH =1 ;
    protected List<XLConversation> conversationList = new ArrayList<XLConversation>();

    protected FragmentContactListView mListView;
    protected  FragmentMessageAdapter mAdapter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        conversationList.addAll(loadConversationList());
        LogCatLog.d("s",conversationList.toString());
    }

    /**
     * 获取会话列表
     *
     * @return
    +    */
    protected List<XLConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Hashtable<String, XLConversation> conversations = ChatManager.getInstance().getAllConversations();

        List<Pair<Long, XLConversation>> sortList = new ArrayList<Pair<Long, XLConversation>>();
        synchronized (conversations) {
            for (XLConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, XLConversation>(Utils.DateStr2timeStamp(conversation.getLastMessage().msgDate), conversation));
                }
            }
        }
        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<XLConversation> list = new ArrayList<XLConversation>();
        for (Pair<Long, XLConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     */
    private void sortConversationByLastChatTime(List<Pair<Long, XLConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, XLConversation>>() {
            @Override
            public int compare(final Pair<Long, XLConversation> con1, final Pair<Long, XLConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
    /**
     * 刷新页面
     */
    public void refresh() {
        handler.sendEmptyMessage(MSG_REFRESH);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_REFRESH:
                    if (mAdapter != null) {
                        conversationList.clear();
                        conversationList.addAll(loadConversationList());
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
