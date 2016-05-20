package com.xianglin.fellowvillager.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.xianglin.appserv.common.service.facade.model.ContactsDTO;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.db.DBUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 联系人 db 处理
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-12
 */
public class ContactDBHandler extends BaseBDHandler {


    public static final Uri SYNC_SIGNAL_URI = Uri.withAppendedPath(BASE_URI, "Contact_SYNC_SIGNAL_URI");

    public ContactDBHandler(Context mContext) {
        super(mContext);
    }

    /**
     * 插入一个陌生人到通讯录 ISCONTACT 为0 时是陌生人
     *
     * @param contact
     */
    public synchronized long addStarangerContact(Contact contact) {
        contact.isContact = "0";
        final String xlid = PersonSharePreference.getUserID() + "";
        long count = dbUtil.addOrUpdate(DBSQLUtil.TABLES_NAME[1], getContentValues(contact), "XLID = ? AND XLUSERID =" +
                        " ? ",
                new String[]{xlid, contact.xlUserID});
        return count;
    }


    /**
     * 添加一个联系人数据
     *
     * @param contact
     * @param isFresh  是否通知界面刷新
     * @param isUpdate 本地如果存在记录就直接忽略
     * @return
     */
    public synchronized void add(Contact contact, boolean isFresh, boolean isUpdate) {

        if (TextUtils.isEmpty(contact.xlID)) {
            LogCatLog.e(TAG, "自动添加联系人失败:" + contact.toString());
            return;
        }

        ArrayList arrayList = new ArrayList<Contact>();
        arrayList.add(contact);
        addlist(arrayList, isFresh, isUpdate);


        boolean isNotifyMessage=false;//是否需要刷新最近联系人界面

        if(!TextUtils.isEmpty( contact.isContact )&&"0".equals(contact.isContact)){
            isNotifyMessage=true;
        }
        if(isNotifyMessage) {
            XLApplication.getInstance().getContentResolver().notifyChange(MessageDBHandler.SYNC_SIGNAL_URI, null);
        }
    }

    /**
     * 批量插入联系人
     *
     * @param list
     * @param isFresh  是否通知界面刷新
     * @param isUpdate   false本地如果存在记录就直接忽略
     * @return //todo true 需要通知变化
     */
    public synchronized void addlist(final List<Contact> list, boolean isFresh, final boolean isUpdate) {

        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {
                long count = -1L;

                String[] args = new String[1];
               // args[0] = PersonSharePreference.getUserID() + "";

                for (int i = 0; i < list.size(); i++) {

                    Contact contact = list.get(i);


                    ContentValues cv = getContentValues(contact);

                    cv.remove("XLIMAGE_PATH");//// TODO: 2015/12/11  临时 网络下载新数据后不要更新这个字段,仅在本地操作

                    count = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[1], null, cv
                            , SQLiteDatabase.CONFLICT_IGNORE);

                    LogCatLog.d(TAG, " 添加联系人=>插入" + count + "条");

                    if (count <= 0&&isUpdate) {

                        args[0] = contact.contactId;

                        count = db.update(DBSQLUtil.TABLES_NAME[1], cv, " CONTACT_ID = ? ", args);

                        LogCatLog.d(TAG, " 添加联系人=>更新" + count + "条");
                    }
                }
                return count;
            }

            @Override
            public void endTransaction(long count) {

            }
        });
        if(isFresh){
            //发送数据库变化的信号通知loader重新加载
            XLApplication.getInstance().getContentResolver().notifyChange(ContactDBHandler.SYNC_SIGNAL_URI, null);
        }
        LogCatLog.d(TAG, " 添加联系人=>完成 isFresh:"+isFresh);
    }

  /*  *//*
    public long addlist(List<Contact> list) {
        ContentValues[] contentValues = new ContentValues[list.size()];


        String str = PersonSharePreference.getUserID() + "";

        for (int i = 0; i < list.size(); i++) {
            contentValues[i] = getContentValues(list.get(i));
        }
        long count = dbUtil.addOrUpdate(DBSQLUtil.TABLES_NAME[1], contentValues, "XLID = ? AND XLUSERID = ? ",
                new String[]{str, list.get(0).xlUserID});

        if (count > 0) {
            //发送数据库变化的信号通知loader重新加载
            XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
        }
        return count;
    }
*/
    /**
     *  修改联系人等级
     * @param figureId     联系人角色id
     * @param contactLevel 联系人等级
     */
    public synchronized void moveContactLevel(String figureId,Contact.ContactLevel contactLevel) {

     /*   dbUtil.execSQL("update contact_table set iscontact = 0 where xluserid = ?  AND XLID = ? ",
                new String[]{xluserid, xlid})*/

        final String xlid = PersonSharePreference.getUserID() + "";

        dbUtil.execSQL("update contact_table set CONTACT_LEVEL = ? where CONTACT_ID = ?  AND XLID = ? ",
                new String[]{contactLevel.ordinal()+"",figureId, xlid});

        //通知联系人列表刷新
        XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
        //通知聊天界面刷新
     //   XLApplication.getInstance().getContentResolver().notifyChange(MessageDBHandler.SYNC_SIGNAL_URI, null);

    }
    /**
     * 建群时 查询全部联系人数据 判断角色和是否是联系人
     * @param figureId    figureId 和 group 为null时查询全部
     *                     figureId 为null group不为null 查群中
     *
     * @return LinkedList list
     */
    public Cursor queryContactForGroup(String figureId,Group group,Contact.ContactLevel mContactLevel) {
        LinkedList linkedList = new LinkedList();
        String xlid = PersonSharePreference.getUserID() + "";
        String groupid = "0";
        String mfigureId = figureId;

        if(group!=null){
            groupid=GroupDBHandler.getGroupId(group);
            mfigureId=group.figureId;
        }

        try {

            String sql;
            String[] selectionArgs;

            if(TextUtils.isEmpty(mfigureId)){

                sql=    "SELECT \n" +
                        "  c.*, " +
                        "  group_concat( c.FIGURE_ID ) figuregroup , \n" +
                        "  e.ISCONTACT ISGROUPMEMBER \n" +
                        "FROM \n" +
                        "    ( \n" +
                        "    SELECT \n" +
                        "      * \n" +
                        "    FROM \n" +
                        "      contact_table \n" +
                        "    ORDER BY \n" +
                        "       \n" +
                        "      CREATEDATE DESC \n" +
                        "  )  c LEFT JOIN \n" +
                        "  ( \n" +
                        "    SELECT \n" +
                        "      a.FIGURE_USERSID , \n" +
                        "      a.ISCONTACT \n" +
                        "    FROM \n" +
                        "      GROUP_MEMBER_TABLE a \n" +
                        "    WHERE \n" +
                        "      a.XLID = ? AND a.LOCAL_GROUP_ID = ? AND a.ISCONTACT = 'true' " +
                        "  ) e ON e.FIGURE_USERSID = c.FIGURE_USERSID \n" +
                        "WHERE \n" +
                        "  c.XLID = ?  " +
                        "and c.CONTACT_LEVEL <>? " +
                        "AND c.CONTACT_LEVEL <>? " +
                        "AND c.CONTACT_LEVEL <= ? " +
                        "AND c.XLUSERID <> ? \n" +
                        "  GROUP BY c.FIGURE_USERSID " +
                        "ORDER BY \n" +
                        "  c.pinying ,CONTACT_LEVEL ;" ;
                selectionArgs= new String[]{xlid,groupid,xlid,Contact.ContactLevel.BLACK.ordinal()+"",Contact.ContactLevel.BLACK.ordinal()+"",mContactLevel.ordinal()+"",xlid};
            }else{

                sql=    "SELECT \n" +
                        "  c.*, \n" +
                        "  group_concat( c.FIGURE_ID ) figuregroup , \n" +
                        "  e.ISCONTACT ISGROUPMEMBER \n" +
                        "FROM \n" +
                        "  ( \n" +
                        "    SELECT \n" +
                        "      * \n" +
                        "    FROM \n" +
                        "      contact_table \n" +
                        "    ORDER BY \n" +
                        "       \n" +
                        "      CREATEDATE DESC \n" +
                        "  ) c LEFT JOIN \n" +
                        "  ( \n" +
                        "    SELECT \n" +
                        "      a.FIGURE_USERSID , \n" +
                        "      a.ISCONTACT \n" +
                        "    FROM \n" +
                        "      GROUP_MEMBER_TABLE a \n" +
                        "    WHERE \n" +
                        "      a.XLID = ? AND a.LOCAL_GROUP_ID = ? AND a.ISCONTACT = 'true'   " +
                        "  ) e ON e.FIGURE_USERSID = c.FIGURE_USERSID \n" +
                        "WHERE \n" +
                        "  c.XLID = ?  " +
                        "and c.CONTACT_LEVEL <>? " +
                        "AND c.CONTACT_LEVEL <>? " +
                        "AND c.CONTACT_LEVEL <=? " +
                        "AND c.XLUSERID <> ?  " +
                        "and  c.FIGURE_ID = ? " +
                        "GROUP BY c.FIGURE_USERSID " +
                        "ORDER BY \n" +
                        "  c.pinying ,CONTACT_LEVEL ;" ;

                selectionArgs= new String[]{xlid,groupid,xlid,Contact.ContactLevel.BLACK.ordinal()+"",Contact.ContactLevel.UMKNOWN.ordinal()+"",mContactLevel.ordinal()+"",xlid,mfigureId};
            }


            Cursor cursor = dbUtil.query(sql, selectionArgs);

            return  cursor;
     /*       if (!cursor.moveToFirst()) {
                cursor.close();
                return linkedList;
            }
            ContactCursor contactCursor = new ContactCursor(cursor);

            do {
                Contact contact = contactCursor.converCursorToContact();
                linkedList.add(contact);
            } while (cursor.moveToNext());

            dbUtil.colse(cursor); */

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 删除联系人数据 同时删除聊天记录,和临时联系人
     *
     * @param contact
     * @return
     */
    public synchronized void delWithChatRecord(Contact contact) {
        final String xlid = PersonSharePreference.getUserID() + "";
        final String xluserid = contact.xlUserID + "";

        dbUtil.execSQL("update contact_table set iscontact = 0 where xluserid = ?  AND XLID = ? ",
                new String[]{xluserid, xlid});
        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {
                //在联系人表中删除
   /*             db.delete(DBSQLUtil.TABLES_NAME[1], "XLID = ? AND XLUSERID = ? ", new String[]{xlid, xluserid
                });*/

                //在最近联系人表中删除
                long count = MomentDialogueDBHandler.delXLUser(db, xluserid);
                //删除聊天记录
                MessageDBHandler.delToXLUserAllMsg(db, xluserid);

                return count;
            }

            @Override
            public void endTransaction(long count) {

            }
        });
        //通知联系人列表刷新
        XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
        //通知聊天界面刷新
        XLApplication.getInstance().getContentResolver().notifyChange(MessageDBHandler.SYNC_SIGNAL_URI, null);
    }

    /**
     * 更新联系人数据 待优化
     * @param
     * @return
     */
    @Deprecated
    public  synchronized long update(Contact contact) {
        if (contact == null) throw new NullPointerException("更新联系人数据失败，contact 不能为null");
        Contact queryContact = query(contact.contactId);
        if (queryContact != null) {
            if (queryContact.contactId.equals(contact.contactId)) {
                return dbUtil.update(DBSQLUtil.TABLES_NAME[1], getContentValues(contact), "XLID = ? AND CONTACT_ID = ? ",
                        new String[]{contact.xlID, contact.contactId});
            }
        }
        return 1L;
    }


    /** 更新最后联系时间
     * @param contact
     * @return
     */
    public  synchronized long updateContactUpdateTime(Contact contact) {
        final String xlid = PersonSharePreference.getUserID() + "";
        ContentValues contentValues =new ContentValues();
        contentValues.put("UPDATEDATE",contact.updatedate);

        return  dbUtil.update(DBSQLUtil.TABLES_NAME[1], contentValues, "CONTACT_ID = ? AND XLID = ? ", new String[]{contact.contactId,xlid});

    }

    public void updateMsgState(String msgkey, ContentValues contentValues) {
        dbUtil.update(DBSQLUtil.TABLES_NAME[5], contentValues, "MSG_KEY = ?", new String[]{msgkey});
    }


/*    *//**
     * 查询全部联系人数据
     *
     * @return
     *//*
    public Cursor query() {
        Cursor cursor = null;
        String str = PersonSharePreference.getUserID() + "";
        try {
            cursor = dbUtil.query(DBSQLUtil.TABLES_NAME[1], new String[]{"XLID"}, new String[]{str}, 0, 0);
         a.ISCONTACT = 1
            return cursor;
        } catch (Exception e) {
            LogCatLog.e(TAG, "查询联系人数据失败，错误消息" + e.getLocalizedMessage());
        }
        return null;
    }*/

/*
    */
/**
     * 查询全部联系人数据和统计群组的数量
     *
     * @return
     *//*

    public   Cursor queryContactAndGroup() {
        Cursor cursor = null;
        String str = PersonSharePreference.getUserID() + "";
        try {

            String sql = "SELECT \n" +
                    "  a.*, \n" +
                    "  groupnum.num groupnum \n" +
                    "FROM \n" +
                    "  CONTACT_TABLE a , \n" +
                    "  ( \n" +
                    "    SELECT \n" +
                    "      COUNT( b.XLID ) num \n" +
                    "    FROM \n" +
                    "      group_table b \n" +
                    "    WHERE \n" +
                    "      b.XLID = ? and isjoin = 1 \n" +
                    "  ) groupnum \n" +
                    "WHERE \n" +
                    "  a.XLID = ?  and a.ISCONTACT = ?   and XLUSERID <> ? order by a.pinying ";

            cursor = dbUtil.query(sql, new String[]{str, str,BorrowConstants.IS_CONTACT+"",str});

            return cursor;
        } catch (Exception e) {
            LogCatLog.e(TAG, "查询联系人数据失败，错误消息" + e.getLocalizedMessage());
        }
        return null;
    }
*/

/*
    "

    /**
     * 筛选联系人 ,添加的群里面
     * @param figureId
     * @return
     */


    /**
     * 查询全部新联系人数据
     * @param figureId    figureId 为null时查询全部
     * @return LinkedList list
     */
    public ArrayList<Contact> queryNewContact(String figureId) {
        ArrayList list = new ArrayList();
        String xlid = PersonSharePreference.getUserID() + "";

        try {

            String sql;
            String[] selectionArgs;

            if(TextUtils.isEmpty(figureId)){

                sql=String.format("SELECT * , group_concat( FIGURE_ID ) figuregroup   " +
                        " FROM ( \n" +
                        "    SELECT \n" +
                        "      * \n" +
                        "    FROM \n" +
                        "      contact_table \n" +
                        "    ORDER BY \n" +
                        "      CREATEDATE , \n" +
                        "      pinying  \n" +
                        "  ) " +
                        " WHERE  XLID = ? " +
                        "AND ISCONTACT = ? " +
                        "and CONTACT_LEVEL <> ? " +
                        " and CONTACT_LEVEL <> ? " +
                        "and XLUSERID <> ?   " +
                        " GROUP BY FIGURE_USERSID ORDER BY CREATEDATE   " +
                        "  ") ;
                selectionArgs= new String[]{
                        xlid,
                        BorrowConstants.IS_CONTACT+"",
                        Contact.ContactLevel.BLACK.ordinal()+"",
                        Contact.ContactLevel.UMKNOWN.ordinal()+"",
                        xlid};
            }else{
                sql=String.format("SELECT * , group_concat( FIGURE_ID ) figuregroup   " +
                        " FROM ( \n" +
                        "    SELECT \n" +
                        "      * \n" +
                        "    FROM \n" +
                        "      contact_table \n" +
                        "    ORDER BY \n" +
                        "      CREATEDATE   , \n" +
                        "      pinying  \n" +
                        "  )  " +
                        " WHERE  XLID = ? " +
                        "AND ISCONTACT = ? " +
                        "and CONTACT_LEVEL <> ? " +
                        "and CONTACT_LEVEL <> ? " +
                        "and XLUSERID <> ? " +
                        "and FIGURE_ID = ?  " +
                        " GROUP BY FIGURE_USERSID ORDER BY CREATEDATE " +
                        " ") ;
                selectionArgs= new String[]{
                        xlid,
                        BorrowConstants.IS_CONTACT+"",
                        Contact.ContactLevel.BLACK.ordinal()+"",
                        Contact.ContactLevel.UMKNOWN.ordinal()+"",
                        xlid,
                        figureId};
            }


            Cursor cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return list;
            }
            ContactCursor contactCursor = new ContactCursor(cursor);

            do {
                Contact contact = contactCursor.converCursorToContact();
                list.add(contact);
            } while (cursor.moveToNext());

            dbUtil.colse(cursor);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * 查询`所有好友,包括黑名单,分组显示,
     * @return LinkedList list
     */
    public LinkedList<Contact> queryAllFigureCommonContact() {
        LinkedList linkedList = new LinkedList();
        String xlid = PersonSharePreference.getUserID() + "";

        try {

            String sql;
            String[] selectionArgs;

            //  if(TextUtils.isEmpty(figureId)){

            sql=String.format("SELECT * , group_concat( FIGURE_ID ) figuregroup   " +
                    " FROM   ( \n" +
                    "    SELECT \n" +
                    "      * \n" +
                    "    FROM \n" +
                    "      contact_table \n" +
                    "    ORDER BY \n" +
                    "      UPDATEDATE   \n" +
                    "  ) " +
                    " WHERE  XLID = ?  and XLUSERID <> ? " +
                    " GROUP BY FIGURE_USERSID ORDER BY  pinying ,CONTACT_LEVEL " +
                    "  ") ;
            selectionArgs= new String[]{xlid,xlid};

            Cursor cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return linkedList;
            }
            ContactCursor contactCursor = new ContactCursor(cursor);

            do {
                Contact contact = contactCursor.converCursorToContact();
                linkedList.add(contact);
            } while (cursor.moveToNext());

            dbUtil.colse(cursor);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkedList;
    }


    /**
     * 查询全部联系人数据
     * @param contactLevel    联系人级别
     * @return LinkedList list
     */
    public LinkedList<Contact> queryAllFigureContact(Contact.ContactLevel contactLevel) {
        LinkedList linkedList = new LinkedList();
        String xlid = PersonSharePreference.getUserID() + "";

        try {

            String sql;
            String[] selectionArgs;

            //  if(TextUtils.isEmpty(figureId)){

            sql=String.format("SELECT \n" +
                    "  * , \n" +
                    "  group_concat( FIGURE_ID ) figuregroup \n" +
                    "FROM \n" +
                    "  ( \n" +
                    "    SELECT \n" +
                    "      * \n" +
                    "    FROM \n" +
                    "      contact_table c LEFT JOIN \n" +
                    "      figure_table f ON ( \n" +
                    "        c.figure_id = f.FIGURE_USERSID \n" +
                    "      ) \n" +
                    "    WHERE \n" +
                    "      f.FIGURE_STATUS = ? \n" +
                    "    ORDER BY \n" +
                    "      c.UPDATEDATE \n" +
                    "  ) sm LEFT JOIN \n" +
                    "  ( \n" +
                    "    SELECT \n" +
                    "      cst.CONTACT_ID , \n" +
                    "      count( cst.CONTACT_ID ) msgcount , \n" +
                    "      CASE m.MSGDIR \n" +
                    "        WHEN 0 THEN max( m.msg_createdate ) \n" +
                    "      END Last_SEND_msg_createdate , \n" +
                    "      CASE m.MSGDIR \n" +
                    "        WHEN 1 THEN max( m.msg_createdate ) \n" +
                    "      END Last_RECEIVE_msg_createdate \n" +
                    "    FROM \n" +
                    "      contact_msg_table cst LEFT JOIN \n" +
                    "      msg_table m ON cst.msg_key = m.msg_key \n" +
                    "    GROUP BY \n" +
                    "      cst.CONTACT_ID \n" +
                    "  ) msg ON sm.CONTACT_ID = msg.CONTACT_ID \n" +
                    "WHERE \n" +
                    "  sm.XLID = ? AND XLUSERID <> ? AND CONTACT_LEVEL <> ? AND CONTACT_LEVEL <= ? \n" +
                    "GROUP BY \n" +
                    "  sm.FIGURE_USERSID \n" +
                    "ORDER BY \n" +
                    "  pinying , \n" +
                    "  CONTACT_LEVEL , \n" +
                    "  msg.Last_SEND_msg_createdate DESC ;   ") ;
            selectionArgs= new String[]{FigureMode.Status.ACTIVE.ordinal()+"",xlid,xlid,Contact.ContactLevel.BLACK.ordinal()+"",contactLevel.ordinal()+""};

            Cursor cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return linkedList;
            }
            ContactCursor contactCursor = new ContactCursor(cursor);

            do {
                Contact contact = contactCursor.converCursorToContact();
                linkedList.add(contact);
            } while (cursor.moveToNext());

            dbUtil.colse(cursor);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkedList;
    }





    /**
     * 查询全部联系人数据
     *
     * @return LinkedList list
     */
    public LinkedList<Contact> queryContact() {
        LinkedList linkedList = new LinkedList();
        String xlid = PersonSharePreference.getUserID() + "";

        try {

            String sql;
            String[] selectionArgs;

          //  if(TextUtils.isEmpty(figureId)){

                sql=String.format("SELECT * , group_concat( FIGURE_ID ) figuregroup   FROM CONTACT_TABLE " +
                        " WHERE  XLID = ?  and XLUSERID <> ?   " +
                        " GROUP BY CONTACT_ID   " +
                        " ORDER BY  pinying,CONTACT_LEVEL") ;
                selectionArgs= new String[]{xlid,xlid};
 /*           }
            else{
                sql=String.format("SELECT * , group_concat( FIGURE_ID ) figuregroup   FROM CONTACT_TABLE " +
                        " WHERE  XLID = ?  and XLUSERID <> ? and FIGURE_ID = ?  " +
                        " GROUP BY FIGURE_USERSID  " +
                        " ORDER BY  pinying ") ;
                selectionArgs= new String[]{xlid,xlid,figureId};
            }*/

            Cursor cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return linkedList;
            }
            ContactCursor contactCursor = new ContactCursor(cursor);

            do {
                Contact contact = contactCursor.converCursorToContact();
                linkedList.add(contact);
            } while (cursor.moveToNext());

            dbUtil.colse(cursor);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkedList;
    }

    /**
     * 通过Cursor 转 对象
     */
    public static class ContactCursor extends CursorWrapper {

        public ContactCursor(Cursor c) {
            super(c);
        }

        public List<Contact> getContactList() {

            if(isAfterLast()){
                moveToPosition(-1);
            }

            List<Contact> contacts = new ArrayList<Contact>();
            while (moveToNext()) {
                Contact contactQuery = converCursorToContact();
                contacts.add(contactQuery);
            }
            return contacts;
        }

        private Contact converCursorToContact() {
            int i = getColumnIndex("ISGROUPMEMBER");
            int j = getColumnIndex("groupnum");

            int figuregroup = getColumnIndex("figuregroup");

            boolean isgroupmember = false;
            int groupnum = 0;

            if (i > 0) {
                isgroupmember = Utils.parseBoolean(getString(i));
            } else {
                isgroupmember = false;
            }

            if (j > 0) {
                groupnum = Utils.parseInt(getString(j));
            } else {
                groupnum = 0;
            }
            LogCatLog.d(TAG,"PRIVATE_SESSION_DATE = "+getString(getColumnIndex("PRIVATE_SESSION_DATE")));
            Contact contact=new Contact.Builder(Contact.ITEM)
                    .xlID(getString(getColumnIndex("XLID")))
                    .xlUserId(getString(getColumnIndex("XLUSERID")))
                    .xlUserName(getString(getColumnIndex("XLUSERNAME")))
                    .xlImagePath(getString(getColumnIndex("XLIMAGE_PATH")))
                    .xlReMarks(getString(getColumnIndex("XLREMARKS")))
                    .file_id(getString(getColumnIndex("FILE_ID")))
                    .isContact(getString(getColumnIndex("ISCONTACT")))
                    .pinying(getString(getColumnIndex("PINYING")))
                    .contactId(getString(getColumnIndex("CONTACT_ID")))
                    .figureUsersId(getString(getColumnIndex("FIGURE_USERSID")))
                    .figureId(getString(getColumnIndex("FIGURE_ID")))
                    .score(getString(getColumnIndex("SCORE")))
                    .sexualorientation(getString(getColumnIndex("SEXUALORIENTATION")))
                    .info(getString(getColumnIndex("INFO")))
                    .contactLevel(Contact.ContactLevel.valueOf(getInt(getColumnIndex("CONTACT_LEVEL"))))
                    .gender(getString(getColumnIndex("GENDER")))
                    .relationshipInfo(Contact.RelationEstablishType.valueOf(getInt(getColumnIndex("RELATIONSHIP_INFO"))))
                    .imagePathThumbnail(getString(getColumnIndex("IMAGE_PATH_THUMBNAIL")))
                    .relationshipTime(getString(getColumnIndex("RELATIONSHIP_TIME")))
                    .updatedate(getString(getColumnIndex("UPDATEDATE")))
                    .createdate(getString(getColumnIndex("CREATEDATE")))
                    .isPrivateSession(getInt(getColumnIndex("ISPRIVATE_SESSION")) == 1 ? true :false)// TODO: 16/3/29 james 新增字段
                    .privateSessionDate(getString(getColumnIndex("PRIVATE_SESSION_DATE")))// TODO: 16/3/29  james 新增字段
                    .isgroupmember(isgroupmember)
                    .groupnumber(groupnum)
                    .build();
            if(figuregroup>0){
                ContactManager.getInstance().setFigureGroup( getString(getColumnIndex("figuregroup")).split(","),contact);
            }
            return contact;
        }
    }


    /**
     * 查询指定id是否在联系人表(黑名单用户也算在联系人表)
     *
     * @param contactId
     * @return
     */
    public Contact query(String contactId) {
        return query(contactId, null);
    }

    /**
     * 查询单个联系人数据
     *
     * @param contactId  需要查询的联系人id
     * @param isContact 是否查询是好友关系的联系人 0 false 陌生人 ; 1 true 好友
     * @return 联系人对象
     */
    public Contact query(String contactId, String isContact) {
        Cursor cursor = null;
        String str = PersonSharePreference.getUserID() + "";
        Contact contactQuery = null;

        String[] column = null;
        String[] selectionArgs = null;

        if (TextUtils.isEmpty(isContact)) {

            column = new String[]{"XLID", "CONTACT_ID"};
            selectionArgs = new String[]{str, contactId};

        }else if(isContact.equals(BorrowConstants.IS_NO_CONTACT+"")||isContact.equals(BorrowConstants.IS_CONTACT+"")){

            column = new String[]{"XLID", "CONTACT_ID","ISCONTACT"};
            selectionArgs = new String[]{str, contactId,isContact};
        }

        try {
            cursor = dbUtil.query(DBSQLUtil.TABLES_NAME[1],column ,selectionArgs,
                    0, 0);
            ContactCursor contactCursor=new ContactCursor(cursor);
            if (cursor.moveToNext()) {

                contactQuery=  contactCursor.converCursorToContact();
/*                contactQuery = new Contact.Builder(Contact.ITEM)
                        .xlID(cursor.getString(cursor.getColumnIndex("XLID")))
                        .xlUserId(cursor.getString(cursor.getColumnIndex("XLUSERID")))
                        .xlUserName(cursor.getString(cursor.getColumnIndex("XLUSERNAME")))
                        .xlImagePath(cursor.getString(cursor.getColumnIndex("XLIMAGE_PATH")))
                        .xlReMarks(cursor.getString(cursor.getColumnIndex("XLREMARKS")))
                        .file_id(cursor.getString(cursor.getColumnIndex("FILE_ID")))
                        .isContact(cursor.getString(cursor.getColumnIndex("ISCONTACT")))
                        .pinying(cursor.getString(cursor.getColumnIndex("PINYING")))
                        .build();*/
            }
            return contactQuery;
        } catch (Exception e) {
            LogCatLog.e(TAG, "查询联系人数据失败，错误消息" + e.getLocalizedMessage());
        } finally {
            dbUtil.colse(cursor);
        }

        return null;
    }

    /**
     * SELECT
     * c.*,
     * e.iscontact isgroupmember
     * FROM
     * contact_table c LEFT JOIN
     * (
     * SELECT
     * a.XLUSERID ,
     * a.iscontact
     * FROM
     * group_member_table a
     * WHERE
     * a.XLID = 10102 AND a.xlgroupid = 196 AND a.ISCONTACT = 'true'
     * ) e ON e.XLUSERID = c.XLUSERID
     * WHERE
     * c.XLID = 10102 ;
     * 判断联系人是否在群里面
     *
     * @param groupid
     * @return
     */
    public Cursor queryContactWithGroup(String groupid) {

        String sql = "SELECT \n" +
                "  c.*, \n" +
                "  e.iscontact ISGROUPMEMBER \n" +
                "FROM \n" +
                "  contact_table c LEFT JOIN \n" +
                "  ( \n" +
                "    SELECT \n" +
                "      a.CONTACT_ID , \n" +
                "      a.iscontact \n" +
                "    FROM \n" +
                "      group_member_table a \n" +
                "    WHERE \n" +
                "      a.XLID = ? AND a.LOCAL_GROUP_ID = ? AND a.ISCONTACT = 'true' \n" +
                "  ) e ON e.CONTACT_ID = c.CONTACT_ID \n" +
                "WHERE \n" +
                "  c.XLID = ? and c.ISCONTACT = 1 ";


        String str = PersonSharePreference.getUserID() + "";
        return dbUtil.query(sql, new String[]{str, groupid, str});

    }



    /**
     * 获取 contentvalues
     *
     * @param contact
     * @return
     */
    public ContentValues getContentValues(Contact contact) {

        if(contact.contactLevel==null){
            contact.contactLevel= Contact.ContactLevel.UMKNOWN;
        }
        if(contact.relationshipInfo==null){
            contact.relationshipInfo= Contact.RelationEstablishType.DEFAULT;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put("XLID", contact.xlID);
        contentValues.put("XLUSERID", contact.xlUserID);
        contentValues.put("XLUSERNAME", contact.getXlUserName());
        contentValues.put("XLIMAGE_PATH", contact.xlImagePath);
        contentValues.put("XLREMARKS", contact.getXlReMarks());
        contentValues.put("FILE_ID", contact.file_id);
        contentValues.put("ISCONTACT", contact.isContact);
        contentValues.put("PINYING", contact.pinying);

        contentValues.put("INFO", contact.info);

        contentValues.put("CONTACT_LEVEL", contact.contactLevel.ordinal());
        contentValues.put("GENDER", contact.gender);
        contentValues.put("RELATIONSHIP_INFO", contact.relationshipInfo.ordinal());
        contentValues.put("IMAGE_PATH_THUMBNAIL", contact.imagePathThumbnail);
        contentValues.put("XLIMAGE_PATH", contact.xlImagePath);
        contentValues.put("RELATIONSHIP_TIME", contact.relationshipTime);

        contentValues.put("FIGURE_USERSID", contact.figureUsersId);
        contentValues.put("FIGURE_ID", contact.figureId);
        contentValues.put("SCORE", contact.score);
        contentValues.put("SEXUALORIENTATION", contact.sexualorientation);
        contentValues.put("CONTACT_ID", ContactDBHandler.getContactId(contact));
        contentValues.put("CREATEDATE", System.currentTimeMillis());
        if(!TextUtils.isEmpty( contact.updatedate)){
            contentValues.put("UPDATEDATE",contact.updatedate);
        }


        return contentValues;
    }

    public static String getContactId(Contact contact){

        return contact.figureUsersId+contact.figureId;
    }
    public static String getContactId(ContactsDTO contactsDTO ){

        return contactsDTO.getContactsFigureId()+contactsDTO.getFigureId();
    }
    public static String getContactId(MessageBean messageBean ){

        return messageBean.figureUsersId+messageBean.figureId;
    }

    /**
     * 获取拼接后的唯一联系人Id
     * @param figureUsersId 联系人的figureId
     * @param figureId 自己当前的figureId
     * @return
     */
    public static String getContactId(String figureUsersId,String figureId ){

        return figureUsersId+figureId;
    }


    /**
     * 设置私密聊天消息时间
     * @param contactFigureId 对应联系人角色ID
     * @param contactId 联系人ID
     * @param thisFigureId  当前角色ID
     * @param isPrivate 是否为私密聊天 可不传值 （暂留）
     * @param lifeTime  消息销毁时间[以秒为单位]
     * @return 0 为设置失败 1  为设置成功
     *
     */
    public int setPrivateMsgDate(String contactFigureId,String  contactId,String thisFigureId,boolean isPrivate,long  lifeTime){
        try{
            ContentValues contentValues =new ContentValues();
            contentValues.put("ISPRIVATE_SESSION",isPrivate == true ?1:0);
            contentValues.put("PRIVATE_SESSION_DATE",lifeTime);
            return dbUtil.update(DBSQLUtil.TABLES_NAME[1], contentValues, " XLUSERID = ? AND FIGURE_USERSID = ? AND FIGURE_ID = ? ", new String[]{contactId,contactFigureId,thisFigureId});
        }catch (Exception e){
            LogCatLog.e(TAG,"设置私密聊天时间失败 ，失败消息"+e);
        }
        return 0;
    }

    /**
     * 获取当前联系人 是否为私密聊天（暂留）
     * @param contactId
     * @return
     */
    public Contact getThisContactIsPrivate(long contactId){
        try{

        }catch(Exception e){

        }
        return null;
    }


}
