/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.utils.messagetask;

import com.xianglin.appserv.common.service.facade.model.ContactsRelationRequest;
import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.ComparatorHowAddContact;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  请求联系人详情
 * @author pengyang
 * @version v 1.0.0 2016/3/28 10:32  XLXZ Exp $
 */
public class ContactDetailsTask  extends MessageEventTask{

    private   ContactDBHandler contactDBHandler;
    private  MessageBean mMessageBean  ;

    public ContactDetailsTask(MessageBean messageBean) {
        super(System.currentTimeMillis()+"");
        contactDBHandler = new ContactDBHandler(XLApplication.getInstance());
        mMessageBean = messageBean;
    }

    @Override
    public void run() {
            isAutoAddContacts();

          onEndTask();
    }


    /** 根据消息自动添加联系人
     */
    public void isAutoAddContacts() {

        Contact contact = ContactManager.getInstance()
                .getContact(ContactDBHandler.getContactId(mMessageBean.figureUsersId, mMessageBean.figureId));
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

    /**
     * 获取联系人详情
     *
     //* @param isContact 是否是用户角色的联系人
     */
    void getDetailContact(final MessageBean bean,
                          final Contact.ContactLevel contactLevel) {

        LogCatLog.d(TAG, "getDetailContact:请求联系人详情:" + bean.figureUsersId);

        SyncApi.getInstance().detail(bean.figureUsersId,
                XLApplication.context, new SyncApi.CallBack<FigureDTO>() {
                    @Override
                    public void success(FigureDTO mode) {
                        Contact contacts = ContactManager.getInstance()
                                .swapFigureDTOtoContact(mode, bean.figureId, contactLevel);

                        //contacts.figureGroup
                        // setFigureGroup(args, contact);
                        String[] args = {contacts.figureId};
                        ContactManager.getInstance().setFigureGroup(args, contacts);

                        ContactManager.getInstance().addContactInternal(contacts);

                        MessageHandler.getInstance().notifyNewContact(ContactManager.getInstance().getContactTable().get(contacts.contactId));

                        LogCatLog.d(TAG, "getDetailContact:请求陌生人:" + contacts.contactId);
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        //                        tip(errTip);
                        LogCatLog.e(TAG, "getDetailContact:failed 请求陌生人:" + bean.figureUsersId);
                    }
                }
        );
    }

    /**
     * 根据消息自动添加联系人
     *
     * @param messageBean
     */
    private void addContact(final MessageBean messageBean, final Contact.ContactLevel contactLevel) {

        LogCatLog.e(TAG, "自动添加联系人--开始");
/*
        //直接激活本地已经存在的联系人
        Contact contact= ContactManager.getInstance().getContact(figureid);
        if (contact != null) {
            contact.isContact = BorrowConstants.IS_CONTACT + "";
            contact.contactLevel= Contact.ContactLevel.LOW;
        }
*/
        ContactsRelationRequest mContactsReationRequest = new ContactsRelationRequest();
        mContactsReationRequest.setUserId(null);
        mContactsReationRequest.setFigureId(messageBean.figureId);
        mContactsReationRequest.setContactsUserId(messageBean.xlID);
        mContactsReationRequest.setContactsFigureId(messageBean.figureUsersId);
        mContactsReationRequest.setContactsType(contactLevel.name());

        Contact contact = ContactManager.getInstance().getTempContact(messageBean.figureUsersId);
        if (contact != null && contact.relationshipInfo != null) {
            mContactsReationRequest.setRelationEstablishType(contact.relationshipInfo.name());
        } else {
            mContactsReationRequest.setRelationEstablishType(Contact.RelationEstablishType.DEFAULT.name());
        }

       // mContactsReationRequest.setToken(null);


        Boolean bool4 = SyncApi.getInstance().add(mContactsReationRequest,
                XLApplication.context, new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                        getDetailContact(messageBean,  contactLevel);
                    }
                    @Override
                    public void failed(String errTip, int errCode) {
                    }
                });


        LogCatLog.e(TAG, "自动添加联系人--接受" + bool4);
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
