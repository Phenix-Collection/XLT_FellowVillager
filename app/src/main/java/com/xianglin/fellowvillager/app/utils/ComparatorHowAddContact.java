/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.utils;

import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *  判断添加联系人是否置为高频或普通联系人,时按消息数排序,如果消息数相同着按创建时间排序
 * @author pengyang
 * @version v 1.0.0 2016/3/23 20:02  XLXZ Exp $
 */
public class ComparatorHowAddContact {

    public List<Comparator<Map.Entry<String, XLConversation>>> mCmpList = new ArrayList<Comparator<Map.Entry<String, XLConversation>>>();
    public ComparatorHowAddContact(){

        mCmpList.add(new XLConversationComparable());
        mCmpList.add(new ContactComparable());

    }

    public void sort(List<Map.Entry<String, XLConversation>> list) {

        Comparator<Map.Entry<String, XLConversation>> cmp = new Comparator<Map.Entry<String, XLConversation>>() {
            @Override
            public int compare(Map.Entry<String, XLConversation> o1, Map.Entry<String, XLConversation> o2) {
                for (Comparator<Map.Entry<String, XLConversation>> comparator : mCmpList) {
                    if (comparator.compare(o1, o2) > 0) {
                        return 1;
                    } else if (comparator.compare(o1, o2) < 0) {
                        return -1;
                    }
                }
                return 0;
            }
        };
        Collections.sort(list, cmp);
    }

    /**
     * 对XLConversation按创建时间排序
     * @author pengyang
     * @version v 1.0.0 2016/3/16 13:12  XLXZ Exp $
     */
    class ContactComparable implements Comparator<Map.Entry<String, XLConversation>> {

        @Override
        public int compare(Map.Entry<String, XLConversation> lhs, Map.Entry<String, XLConversation> rhs) {

          Contact lcontact= ContactManager.getInstance().getContact(lhs.getValue().getChatID());
          Contact rcontact= ContactManager.getInstance().getContact(rhs.getValue().getChatID());

            if(lcontact!=null&&rcontact!=null){
                long lc= Utils.parseLong(lcontact.createdate);

                long rc=Utils.parseLong(rcontact.createdate);

                return lc<rc?1:-1;
            }else{
                LogCatLog.e("Comparator","getValue().getChatID() get Contact is null ,ChatID is : "+lhs.getValue().getChatID()+"&"+lhs.getValue().getChatID());
            }

            return 0;
        }
    }


    /**
     * 对XLConversation消息数排序 降序排序
     * @author pengyang
     * @version v 1.0.0 2016/3/16 13:12  XLXZ Exp $
     */
      class XLConversationComparable implements Comparator<Map.Entry<String, XLConversation>> {

        @Override
        public int compare(Map.Entry<String, XLConversation> lhs, Map.Entry<String, XLConversation> rhs) {

            if(lhs.getValue().getMsgCount()>rhs.getValue().getMsgSendCount()){
                return -1;
            }
            if(lhs.getValue().getMsgCount()<rhs.getValue().getMsgSendCount()){
                return  1;
            }
            return 0;
        }
    }


}
