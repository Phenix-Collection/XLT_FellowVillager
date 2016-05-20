/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.utils.messagetask;

import android.os.SystemClock;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.utils.ComparatorHowAddContact;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.utils.pinyin.PingYinUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  基于消息的乡邻助手任务处理
 * @author pengyang
 * @version v 1.0.0 2016/3/29 11:34  XLXZ Exp $
 */
public class XLAssistantDetailsTask extends MessageEventTask {

    private final MessageDBHandler messageDBHandler;
    private   ContactDBHandler contactDBHandler;
    private  MessageBean mMessageBean  ;

    public XLAssistantDetailsTask(MessageBean mb) {
        super(System.currentTimeMillis()+"");
        contactDBHandler = new ContactDBHandler(XLApplication.getInstance());
        messageDBHandler = new MessageDBHandler(XLApplication.getInstance());
        mMessageBean = mb;
    }

    @Override
    public void run() {

        //1.消息分配个各个角色

        for ( Map.Entry<String,FigureMode> entry : ContactManager.getInstance().getAllFigureTable().entrySet()) {
            FigureMode figureMode = entry.getValue();

            MessageBean b= (MessageBean) mMessageBean.clone();

            b.figureId=figureMode.getFigureUsersid();

            String key;

            if (figureMode == null) {
                key = mMessageBean.msgKey + Utils.getUniqueMessageId();
            } else {
                key = mMessageBean.msgKey + figureMode.getFigure_usersid_shortid();
            }
            b.msgKey = key;
            b.msgLocalKey = key;
            MessageHandler.getInstance().addMessage(b);

            SystemClock.sleep(50);//模拟正常消息间隔
            messageDBHandler.addReceivedMsg(b, true);
        }
        //2.加入名录表,给各个角色
        isAutoAddContacts();

        onEndTask();
    }

    /** 根据消息自动添加联系人
     */
    public void isAutoAddContacts() {

        Contact contact=null;

         for (Map.Entry<String, FigureMode> entry : ContactManager.getInstance().getAllFigureTable().entrySet()) {
                contact = ContactManager.getInstance()
                        .getContact(ContactDBHandler.getContactId(mMessageBean.figureUsersId, entry.getValue().getFigureUsersid()));
                if(contact!=null){
                    break;
                }
          }


        if (contact == null) {
            //不在联系人列表就获取详细信息插入插入联系人列表
            addContact(mMessageBean, Contact.ContactLevel.UMKNOWN);

        } else if (contact.contactLevel == Contact.ContactLevel.UMKNOWN) {
            synchronized (MessageHandler.getInstance().getConversations()) {
                //如果是陌生人要判断是否能加好友了,//发送失败也加为好友
                boolean isNeedAdd = isAutoAddNewContacts(ContactDBHandler.getContactId(mMessageBean.figureUsersId, mMessageBean.figureId));
                if (isNeedAdd) {
                    //如果一个角色下超过15人就把这个陌生人加成NORMAL
                    List<Map.Entry<String, Contact>> list = new ArrayList<Map.Entry<String, Contact>>(ContactManager
                            .getInstance().getContactTable().entrySet());
                    int j = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getValue().figureId.equals(mMessageBean.figureId)) {
                            if (list.get(i).getValue().contactLevel != Contact.ContactLevel.UMKNOWN) {
                                j++;
                            }
                        }
                    }
                    LogCatLog.d(TAG, j + "");
                    addContact(mMessageBean, j < 15 ? Contact.ContactLevel.HIGH : Contact.ContactLevel.NORMAL);
                }
            }
        } else {

            //对于好友开始按消息数动态排序

            synchronized (MessageHandler.getInstance().getConversations()) {
                List<Map.Entry<String, XLConversation>> list = new ArrayList<Map.Entry<String, XLConversation>>
                        (MessageHandler.getInstance().getConversations().entrySet());

                //过滤群信息

                Iterator<Map.Entry<String, XLConversation>> it = list.iterator();

                while (it.hasNext()) {
                    Map.Entry<String, XLConversation> value = it.next();
                    if ( value.getValue().isGroup()) {
                        it.remove();
                        continue;
                    }

                    Contact contact1= ContactManager.getInstance().getContact(value.getValue().getChatID()) ;
                    if(contact1==null||!contact1.figureId.equals(mMessageBean.figureId)){
                        it.remove();
                        continue;
                    }

                }
                //排序
                new ComparatorHowAddContact().sort(list);

                //设置相应频率
                for (int i = 0; i < list.size(); i++) {

                    XLConversation xlConversation = list.get(i).getValue();

                    Contact contact1 = ContactManager.getInstance().getContact(xlConversation.getChatID());


                    if (contact1 != null&&contact1.contactLevel!= Contact.ContactLevel.UMKNOWN) {
                        if (i < 15) {
                            //要求前15个人设置成高频
                            contact1.contactLevel = Contact.ContactLevel.HIGH;
                            contactDBHandler.add(contact1, false, true);
                            LogCatLog.d(TAG, "联系人高频动态排序:" + list.get(i).getKey() + ":" + contact1.getUIName() + " 消息数:" +
                                    list.get(i).getValue().getMsgCount());
                        } else {
                            contact1.contactLevel = Contact.ContactLevel.NORMAL;
                            contactDBHandler.add(contact1, false, true);
                            LogCatLog.d(TAG, "联系人普通动态排序:" + list.get(i).getKey() + ":" + contact1.getUIName() + " 消息数:" +
                                    list.get(i).getValue().getMsgCount());
                        }


                    } else {
                        // 不处理接收失败的信息
                        //不处理群的消息数
                    }

                }

            }

        }

    }

    private void addContact(MessageBean messageBean, Contact.ContactLevel umknown) {

        ArrayList<FigureMode> list=new ArrayList<>( ContactManager.getInstance().getAllFigureTable().values());

        if(!TextUtils.isEmpty(messageBean.figureId)){
            FigureMode f= ContactManager.getInstance().getFigureTable().get(messageBean.figureId);
            if(f!=null){
                Contact c=  createContact(list, f, messageBean, umknown);
                ContactManager.getInstance().addContactInternal(c);
                MessageHandler.getInstance().notifyNewContact(c);
            }
        }else {

            for (Map.Entry<String, FigureMode> entry : ContactManager.getInstance().getAllFigureTable().entrySet()) {
                Contact c = createContact(list, entry.getValue(), messageBean, umknown);
                ContactManager.getInstance().addContactInternal(c);
                MessageHandler.getInstance().notifyNewContact(c);

            }

        }
    }

    private Contact createContact(ArrayList<FigureMode> list,FigureMode f,MessageBean messageBean, Contact.ContactLevel umknown) {

        Contact c = new Contact.Builder(Contact.ITEM)
                .contactLevel(umknown)
                .figureUsersId(TextUtils.isEmpty(messageBean.figureUsersId)?messageBean.xlID:messageBean.figureUsersId)
                .xlReMarks("乡邻助手")
                .xlUserName("乡邻助手")
                .file_id("0")
                .gender("UNKNOWN")
                .xlUserId(messageBean.xlID)
                .figureId(f.getFigureUsersid())
                .info("乡邻助手")
                .score("100")
                .sexualorientation("UNKNOWN")
                .isContact(umknown== Contact.ContactLevel.UMKNOWN?BorrowConstants.IS_NO_CONTACT+"":BorrowConstants.IS_CONTACT+"")
                .relationshipInfo(Contact.RelationEstablishType.DEFAULT)
                .xlID(PersonSharePreference.getUserID() + "")
                .relationshipTime(System.currentTimeMillis()+"")
                .figureGroup(list)
                .createdate(System.currentTimeMillis()+"")
                .build();
        c.pinying = PingYinUtil.getSection(c.getUIName());
        c.contactId=ContactDBHandler.getContactId(c);
        return  c;
    }

    /**
     * 统计两个人之间消息一来一回的次数,判断是否需要自动添加联系人
     *
     * @param contactId 联系人id
     * @return true需要自动添加 ,false不需要
     */
    public static boolean isAutoAddNewContacts(String contactId) {

        XLConversation xlConversation = MessageHandler.getInstance().getConversation(contactId, false, XLConversation.ConversationType.Chat);
        List<MessageBean> list = xlConversation.getAllMessages();

        synchronized (list) {

            if (list == null || list.size() < 2) {
                return false;
            }

            int countReceive = 0;//统计两个人之间消息一来一回的次数
            int countSend = 0;//统计两个人之间消息一来一回的次数

            for (int i = 0; i < list.size(); i++) {
                MessageBean messageBean = list.get(i);

                if (messageBean.direct == MessageBean.Direct.RECEIVE) {
                    countReceive++;
                }
                if (messageBean.direct == MessageBean.Direct.SEND) {
                    countSend++;
                }

                if (countReceive > 0 && countSend > 0) {
                    return true;
                }
            }
        }

        return false;

    }

}
