/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat.controller;

import android.content.Context;
import android.text.TextUtils;

import com.xianglin.appserv.common.service.facade.model.ContactsDTO;
import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.FigureDbHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.pinyin.PingYinUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 联系人管理
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/28 19:23  XLXZ Exp $
 */
public class ContactManager {

    private static final String TAG = "ContactManager";

    private static ContactManager instance = null;

    private Context context;

    private boolean isIntied = false;


    /**
     * 黑名单
     */
    private Map<String, Contact> blackList = new Hashtable(24);


    /**
     * 冻结角色
     */
    private Map<String, FigureMode> freezeFigureTable = new Hashtable(24);

    /**
     * 当前
     */
    private Map<String, Contact> contactTable = new Hashtable(200);


    public Map<String, Contact> getTempContactTable() {
        return tempContactTable;
    }

    /**
     * 临时联系人,用于存储那些没有figureId的联系人(附近的人 扫一扫,等)
     */
    private Map<String, Contact> tempContactTable = new Hashtable(10);

    /**
     * 当前活跃角色列表
     */
    private Map<String, FigureMode> activeFigureTable = new Hashtable(24);

    /**
     * 当前所有角色列表
     */
    private Map<String, FigureMode> allFigureTable = new Hashtable(50);


    /**
     * 当前角色
     */
    private FigureMode currentFigure = null;

    /**
     * 联系人工具类
     */
    private ContactDBHandler mContactDBHandler = new ContactDBHandler(XLApplication.getInstance());

    private FigureDbHandler mFigureDbHandler = new FigureDbHandler(XLApplication.getInstance());


    /**
     * 获取指定角色详情
     *
     * @param figureid
     * @return
     */
    public FigureMode getCurrentFigure(String figureid) {
        if (TextUtils.isEmpty(figureid)) {
            return null;
        }
        return allFigureTable.get(figureid);
    }

    /**
     * 获取当前联系人
     *
     * @return
     */
    public FigureMode getCurrentFigure() {
        return currentFigure;
    }

    /**
     * 获取已经冻结的角色
     *
     * @return
     */
    public Map<String, FigureMode> getFreezeFigureTable() {
        return freezeFigureTable;
    }

    /***
     * 获取当前活跃角色列表
     *
     * @return
     */
    public Map<String, FigureMode> getFigureTable() {
        return activeFigureTable;
    }

    /***
     * 获取当前所有角色列表
     *
     * @return
     */
    public Map<String, FigureMode> getAllFigureTable() {
        return allFigureTable;
    }

    /**
     * 获取当前联系人集合
     *
     * @return
     */
    public Map<String, Contact> getContactTable() {
        return contactTable;
    }


    /**
     * 获取实列
     *
     * @return
     */
    private ContactManager() {

    }

    public static ContactManager getInstance() {

        if (instance == null) {
            instance = new ContactManager();
        }
        return instance;
    }

    /**
     * 初始化联系人,需要在子线程中完成
     *
     * @param context
     * @param figureId 登录用户角色id
     */
    public void init(Context context, final String figureId) {
        if (!this.isIntied) {

            this.context = context;


            BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                                           @Override
                                           public void execute() {
                                               try {
                                                   initUserFigure(figureId);
                                                   loadContacts();


                                               } catch (Throwable e) {
                                                   Thread.getDefaultUncaughtExceptionHandler().uncaughtException
                                                           (Thread.currentThread(), e);
                                               }
                                           }
                                       }
            );

            this.isIntied = true;
        }

    }

    /**
     * 切换角色
     *
     * @param figureMode
     */
    public void switchCurrentUserFigure(final FigureMode figureMode) {
        if (figureMode == null) {
            this.currentFigure = null;
            return;
        }
        figureMode.setUpdateDate(System.currentTimeMillis());
        this.currentFigure = activeFigureTable.get(figureMode.getFigureUsersid());

        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
            @Override
            public void execute() {
                try {
                    FigureDbHandler figureDbHandler = new FigureDbHandler(XLApplication.getInstance());
                    figureDbHandler.add(figureMode);
                } catch (Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException
                            (Thread.currentThread(), e);
                }
            }
        });
    }

    /**
     * 添加角色
     *
     * @param figureMode
     */
    public void addFigureTable(FigureMode figureMode) {
        activeFigureTable.put(figureMode.getFigureUsersid(), figureMode);
        allFigureTable.put(figureMode.getFigureUsersid(), figureMode);
        freezeFigureTable.remove(figureMode.getFigureUsersid());
        if (mFigureDbHandler == null) {
            mFigureDbHandler = new FigureDbHandler(XLApplication.getInstance().getApplicationContext());
        }
        mFigureDbHandler.add(figureMode);
    }

    /**
     * 冻结角色并从活跃角色列表移除
     *
     * @param figureMode
     */
    public void freezeFigureTable(FigureMode figureMode) {
        freezeFigureTable.put(figureMode.getFigureUsersid(), figureMode);
        activeFigureTable.remove(figureMode.getFigureUsersid());
        mFigureDbHandler.add(figureMode);
    }

    /**
     * 获取当前角色
     *
     * @return 返回""时为 全部状态
     */
    public String getCurrentFigureID() {
        if (currentFigure == null) {
            return "";
        }
        return currentFigure.getFigureUsersid();
    }

    /**
     * 初始化当前联系人的身份
     *
     * @param figureId
     */
    public synchronized void initUserFigure(String figureId) {

        LogCatLog.d(TAG, "初始化当前联系人的身份-开始");
        if (mFigureDbHandler == null)
            mFigureDbHandler = new FigureDbHandler(XLApplication.getInstance().getApplicationContext());

        List<FigureMode> list = mFigureDbHandler.queryFigure("");

        for (int i = 0; i < list.size(); i++) {
            FigureMode figureMode = list.get(i);

            if (figureMode.getFigureStatus() == FigureMode.Status.FREEZE) {
                freezeFigureTable.put(figureMode.getFigureUsersid(), figureMode);
            } else {
                activeFigureTable.put(figureMode.getFigureUsersid(), figureMode);
            }
            allFigureTable.put(figureMode.getFigureUsersid(), figureMode);
        }

        FigureMode currentFigure = activeFigureTable.get(figureId);

        this.currentFigure = currentFigure;

        //根据角色产生一个联系人对象
/*        currentFigure = new Contact.Builder(Contact.ITEM)
                .xlID(currentFigure.getXlId())
                .figureId(currentFigure.getFigureUsersid())
                .xlUserName(currentFigure.getFigureName())
                .file_id(currentFigure.getFigureImageid())
                .xlImagePath(currentFigure.getImagePathThumbnail())
                .build();*/

        LogCatLog.d(TAG, "初始化当前联系人的身份-结束");

    }

    /**
     * 加载通讯录  //,
     */
    public synchronized void loadContacts() {
        LogCatLog.d(TAG, " 加载联系人-开始");
        if (mContactDBHandler == null)
            mContactDBHandler = new ContactDBHandler(XLApplication.getInstance().getApplicationContext());
        //暂时加载好友
        List<Contact> contactList = mContactDBHandler.queryContact();

        Iterator iterator = contactList.iterator();

        while (iterator.hasNext()) {

            Contact contact = (Contact) iterator.next();

            if (contact.contactLevel == Contact.ContactLevel.BLACK) {
                addUserToBlackList(contact.contactId);
            } else {
                contactTable.put(contact.contactId, contact);
            }

        }

        LogCatLog.d(TAG, " 加载联系人-结束");
    }


    public Contact getContact(String contactId) {

        Contact contact = (Contact) this.contactTable.get(contactId);

        if (mContactDBHandler == null)
            mContactDBHandler = new ContactDBHandler(context);

        if (contact == null) {
            contact = mContactDBHandler.query(contactId);
        }
        return contact;
    }

    /** 获取临时联系人
     * @param figureUserid
     * @return
     */
    public Contact getTempContact(String figureUserid) {


        if (tempContactTable == null || TextUtils.isEmpty(figureUserid)) {
            return null;
        }

        if(tempContactTable.containsKey(figureUserid)) {
            Contact contact = tempContactTable.get(figureUserid);
            return contact;
        }
        return null;
    }
    public void addTempContact(String figureUserid,Contact contact) {
        tempContactTable.put(figureUserid,contact);
    }

    /**
     * 根据userid获取联系人对象
     *
     * @param contactId 联系人contactId=figureid+figureusersId
     * @return
     */
    public Contact getContactByUserID(String contactId) {

        Contact contact = (Contact) this.contactTable.get(contactId);

        if (contact == null) {
            contact = mContactDBHandler.query(contactId);
        } else {
            // TODO: 2016/1/29  临时代码
            contact = new Contact.Builder(Contact.ITEM)
                    .figureUsersId(contactId)
                    .build();
        }
/*
        //// TODO: 2016/1/29  临时代码
        if (contact == null) {
            contact = new Contact.Builder(Contact.ITEM)
                    .xlUserId(xlUserID)
                    .build();
        }
*/

        return contact;
    }

    /**
     * 添加联系人
     *
     * @param contact
     */
    public void addContactInternal(Contact contact) {

        tempContactTable.remove(contact.figureUsersId);

        this.contactTable.put(contact.contactId, contact);

        mContactDBHandler.add(contact, true, true);

    }

    /**
     * 把联系人加入黑名单
     *
     * @param contactId
     */
    public void deleteContactInternal(String contactId) {

        Contact contact = contactTable.get(contactId);

        if (contact != null) {
            contact.contactLevel = Contact.ContactLevel.BLACK;
            mContactDBHandler.moveContactLevel(contactId, Contact.ContactLevel.BLACK);
            addUserToBlackList(contactId);
        } else {
            LogCatLog.w(TAG, "删除失败contactTable中不存在" + contactId);
        }
        //删除对话列表?
    }

    /**
     * 把联系人从黑名单移除
     *
     * @param contactId
     */
    public void recoveryContactInternal(String contactId) {
        Contact contact = getContact(contactId);
        if (contact != null) {
            contact.contactLevel = Contact.ContactLevel.NORMAL;
            mContactDBHandler.moveContactLevel(contactId, Contact.ContactLevel.NORMAL);
            contactTable.put(contact.contactId,contact);
            recoveryUserFromBlackList(contactId);
        } else {
            LogCatLog.w(TAG, "恢复联系人失败:" + contactId);
        }
        //删除对话列表?
    }

    /**
     * 获取黑名单列表
     *
     * @return 黑名单列表
     */
    public   List<Contact> getBlackList() {

        List<Contact> mBlackListGroup = new ArrayList<Contact>(blackList.values());

        return mBlackListGroup;
    }


    private void addUserToBlackList(String contactId) {
        if (!this.blackList.containsKey(contactId)) {
            this.blackList.put(contactId,getContact(contactId));
        }
    }

    /**
     * 恢复联系人
     * @param contactId
     */
    private void recoveryUserFromBlackList(String contactId) {
        if (this.blackList.containsKey(contactId)) {
            this.blackList.remove(contactId);
        }
    }

    /**
     * 设置联系人所属于用户的哪些身份
     *
     * @param arg
     * @param contact
     */
    public void setFigureGroup(String[] arg, Contact contact) {

        ArrayList<FigureMode> figureModes = new ArrayList<FigureMode>();
        for (int i = 0; i < arg.length; i++) {
           FigureMode figureMode= activeFigureTable.get(arg[i]);
            if(figureMode!=null){
                figureModes.add(figureMode);
            }
        }
        contact.figureGroup = figureModes;
    }


    /**
     * 将联网获取的联系人列表载入本地数据库和内存中
     *
     * @param contactList 联系人列表
     * @param isContact   是否是联系人
     */
    public void loadContacts(
            List<ContactsDTO> contactList,
            boolean isContact
    ) {

        Iterator iterator = contactList.iterator();

        List<Contact> list = new ArrayList<Contact>();
        while (iterator.hasNext()) {

            ContactsDTO contactdto = (ContactsDTO) iterator.next();

            Contact contact = swapContactDTOtoContact(contactdto, isContact);

            contact.contactId = ContactDBHandler.getContactId(contact);

            Contact localContact= getContact(contact.contactId);
            
            //// TODO: 2016/3/21  如果不想服务器的信息覆盖掉本地,可以在这里覆盖掉服务器的信息
            //------------start--------------
            contact.contactLevel=localContact.contactLevel;

            contact.updatedate=localContact.updatedate;

            String[] args = {contact.figureId};
            setFigureGroup(args, contact);
            contactTable.put(contact.contactId, contact);
            //------------end--------------


            list.add(contact);
        }

        mContactDBHandler.addlist(list, true, true);

    }

    /**
     * 联系人对象转换contactsDTO 转Contact
     *
     * @param contactsDTO 被转换的对象
     * @param isContact   被转换的对象是否是联系人
     * @return Contact
     */
    public Contact swapContactDTOtoContact(ContactsDTO contactsDTO, boolean isContact) {

        ArrayList list = new ArrayList<>();
        FigureMode figureMode= activeFigureTable.get(contactsDTO.getFigureId());
        if(figureMode!=null){
            list.add(figureMode);
        }
        //可以判断imageid是否发生变化,来决定是否重新下载.(db和内存都需要判断)

        Contact c = new Contact.Builder(Contact.ITEM)
                .contactLevel(!TextUtils.isEmpty(contactsDTO.getContactsType()) ?
                        Enum.valueOf(Contact.ContactLevel.class, contactsDTO.getContactsType()) : Contact.ContactLevel.UMKNOWN)
                .figureUsersId(contactsDTO.getContactsFigureId())
                .xlReMarks(contactsDTO.getRemarkName())
                .xlUserName(contactsDTO.getNickName())
                .file_id(contactsDTO.getAvatarUrl())
                .gender(contactsDTO.getGender())
                .xlUserId(contactsDTO.getContactsUserId())
                .figureId(contactsDTO.getFigureId())
                .info(contactsDTO.getIndividualitySignature())
                .score(contactsDTO.getScore() + "")
                .sexualorientation(contactsDTO.getSexualOrientation())
                .isContact(isContact ? BorrowConstants.IS_CONTACT + "" : BorrowConstants.IS_NO_CONTACT + "")
                .relationshipInfo(!TextUtils.isEmpty(contactsDTO
                        .getRelationEstablishType()) ? Enum.valueOf(Contact.RelationEstablishType.class, contactsDTO
                        .getRelationEstablishType()) : Contact.RelationEstablishType.DEFAULT)
                .xlID(PersonSharePreference.getUserID() + "")
                .relationshipTime(contactsDTO.getRelationEstablishTime()+"")
                .contactId(ContactDBHandler.getContactId(contactsDTO))
                .figureGroup(list)
                 .createdate(System.currentTimeMillis()+"")
                .build();

        c.pinying = PingYinUtil.getSection(c.getUIName());

        return c;
    }

    /**
     * FigureDTO转Contact
     *
     * @param figureDTO 角色
     * @param figureid  用户角色
    // * @param isContact 是否是用户的联系人
     * @return
     */
    public Contact swapFigureDTOtoContact(FigureDTO figureDTO, String figureid,final Contact.ContactLevel contactLevel) {

        Contact c = new Contact.Builder(Contact.ITEM)

                .figureUsersId(figureDTO.getFigureId())
                .xlUserName(figureDTO.getNickName())
                .file_id(figureDTO.getAvatarUrl())
                .gender(figureDTO.getGender())
                .xlUserId(figureDTO.getPartyId())
                .figureId(figureid)
                .info(figureDTO.getIndividualitySignature())
                .contactId(ContactDBHandler.getContactId(figureDTO.getFigureId(), figureid))
                .sexualorientation(figureDTO.getSexualOrientation())
                .xlID(PersonSharePreference.getUserID() + "")
                 .contactLevel(contactLevel)
                 .isContact(contactLevel== Contact.ContactLevel.UMKNOWN?BorrowConstants.IS_NO_CONTACT+"":BorrowConstants.IS_CONTACT+"")
                 .createdate(figureDTO.getCreateTime()+"")
                 .updatedate(figureDTO.getUpdateTime()+"")
                 .relationshipTime(figureDTO.getCreateTime()+"")
                 .build();

        //// TODO: 2016/3/18  可能会覆盖服务器的值  
         if (c.isContact.equals(BorrowConstants.IS_NO_CONTACT)) {
            c.relationshipInfo = Contact.RelationEstablishType.DEFAULT;
            //c.contactLevel = Contact.ContactLevel.LOW;
            c.score = "";
         }

        c.pinying =PingYinUtil.getSection(c.getUIName());

        return c;
    }





    /**
     * 是否当前角色的联系人
     * @param currentFigureId 当前本人使用的角色
     * @param figureUserId 对方角色id
     * @return
     */
    public boolean isCurrentFigureContact(String currentFigureId,String figureUserId){


        if(TextUtils.isEmpty(currentFigureId)){//全部状态不显示联系人关系
            return false;
        }
        String contactId=ContactDBHandler.getContactId(figureUserId,currentFigureId);
        if(contactTable.get(contactId)!=null){
            return true;
        }
        return  false;
    }

/*    public ArrayList<FigureMode> sortfigureTable() {
*//*
        ArrayList <Map.Entry<String,FigureMode>>    mappingList = new ArrayList<Map.Entry<String,FigureMode>>(activeFigureTable.entrySet());

        Collections.sort(mappingList, new Comparator<Map.Entry<String,FigureMode>>() {
            @Override
            public int compare( Map.Entry<String,FigureMode> lhs,  Map.Entry<String,FigureMode> rhs) {

                    return lhs.getValue().equals(rhs.getValue());

        });

    }
    }*//*
    }*/

    /**
     * 获取所有角色id list
     * @return
     */
    public List<String> getAllFigureIdList() {
        List<String> figureIdList;
        Map<String, FigureMode> allFigureTable = ContactManager.getInstance().getAllFigureTable();
        if (allFigureTable == null) {
            return null;
        }
        figureIdList = new ArrayList<>();
        for (Map.Entry<String, FigureMode> entry :
                allFigureTable.entrySet()) {
            if (entry == null) {
                continue;
            }
            String figureid = entry.getValue().getFigureUsersid();
            if (TextUtils.isEmpty(figureid)) {
                continue;
            }
            figureIdList.add(figureid);
        }
        return  figureIdList;
    }

    /**
     * 排序所有角色,包括已冻结角色
     */
    public List<FigureMode> sortAllFigure(List<FigureMode> figureModeList) {
        if (figureModeList == null) {
            return null;
        }
        List<FigureMode> allFigures = new ArrayList<>();
        ArrayList<FigureMode> activeFigures = new ArrayList<>();
        ArrayList<FigureMode> frozenFigures = new ArrayList<>();
        for (FigureMode mode :
                figureModeList) {
            if (mode == null) {
                continue;
            }
            if (mode.getFigureStatus() == FigureMode.Status.ACTIVE) {
                activeFigures.add(mode);
            } else if (mode.getFigureStatus() == FigureMode.Status.FREEZE) {
                frozenFigures.add(mode);
            }
        }
        if (!activeFigures.isEmpty()) {
            sortFigureByCreateTime(activeFigures);
        }
        if (!frozenFigures.isEmpty()) {
            sortFigureByUpdateTime(frozenFigures);
        }
        allFigures.addAll(activeFigures);
        allFigures.addAll(frozenFigures);
        return allFigures;
    }

    /**
     * 按照创建时间先后排序角色
     */
    public void sortFigureByCreateTime(List<FigureMode> figureModeList) {
        if (figureModeList == null || figureModeList.isEmpty()) {
            return;
        }
        Collections.sort(figureModeList, new Comparator<FigureMode>() {
            @Override
            public int compare(FigureMode lhs, FigureMode rhs) {
                return Long.valueOf(lhs.getCreateDate()).compareTo(rhs.getCreateDate());
            }
        });
    }

    /**
     * 按照更新时间最近排序角色
     */
    public void sortFigureByUpdateTime(List<FigureMode> figureModeList) {
        if (figureModeList == null || figureModeList.isEmpty()) {
            return;
        }
        Collections.sort(figureModeList, new Comparator<FigureMode>() {
            @Override
            public int compare(FigureMode lhs, FigureMode rhs) {
                return Long.valueOf(rhs.getUpdateDate()).compareTo(lhs.getUpdateDate());
            }
        });
    }


}
