package com.xianglin.fellowvillager.app.utils;

import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 对联系人按拼音和等级排序
 * @author pengyang
 * @version v 1.0.0 2016/3/22 13:08  XLXZ Exp $
 */
public class ComparatorContact {

    public List<Comparator<Contact>> mCmpList = new ArrayList<Comparator<Contact>>();
    public ComparatorContact(){

        mCmpList.add(comparePingingASC);
        mCmpList.add(compareLevelASC);
        mCmpList.add(compareMsgCountDesc);

    }
    public void sort(List<Contact> list) {

        Comparator<Contact> cmp = new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                for (Comparator<Contact> comparator : mCmpList) {
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
     * 按拼音排序
     */
    private Comparator<Contact> comparePingingASC = new Comparator<Contact>() {

        @Override
        public int compare(Contact lhs, Contact rhs) {
     /*      if (lhs.pinying.equals(rhs.pinying)) {
                return lhs.getUIName().compareTo(rhs.getUIName());
            } else {*/
                if ("#".equals(lhs.pinying)) {
                    return -1;
                } else if ("#".equals(rhs.pinying)) {
                    return 1;
                }

                return lhs.pinying.compareTo(rhs.pinying);
            }
    //   }
    };

    /**
     * 按等级排序
     */
    private Comparator<Contact> compareLevelASC = new Comparator<Contact>() {

        @Override
        public int compare(Contact lhs, Contact rhs) {

            if(lhs.contactLevel.ordinal() == rhs.contactLevel.ordinal()) return 0;

            return lhs.contactLevel.ordinal() < rhs.contactLevel.ordinal() ? -1 : 1;
        }
    };
    /**
     * 按发送消息排序
     */
    private Comparator<Contact> compareMsgCountDesc = new Comparator<Contact>() {

        @Override
        public int compare(Contact lhs, Contact rhs) {

            if(MessageHandler.getInstance().getConversations().containsKey(lhs.contactId)&&
                    MessageHandler.getInstance().getConversations().containsKey(rhs.contactId)    ){

            long lcount= MessageHandler.getInstance().getConversations().get(lhs.contactId).getMsgSendCount();
            long rcount= MessageHandler.getInstance().getConversations().get(rhs.contactId).getMsgSendCount();

            if(lcount == rcount) return 0;

            return lcount < rcount? 1 : -1;

            }else{
                return 0;
            }
        }
    };


}