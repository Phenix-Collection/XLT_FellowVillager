package com.xianglin.fellowvillager.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.db.DBUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 群组数据 db 处理
 * <p/>
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-13
 */
public class GroupDBHandler extends BaseBDHandler {

    public static final Uri SYNC_SIGNAL_URI = Uri.withAppendedPath(BASE_URI, "Group_SYNC_SIGNAL_URI");
    static String xlid = PersonSharePreference.getUserID() + "";

    public GroupDBHandler(Context mContext) {
        super(mContext);
    }

    /**
     * 添加群组数据
     *
     * @param group
     * @return
     */
    public synchronized long add(Group group) {
        ArrayList<Group> list = new ArrayList<Group>();
        list.add(group);
        return addlist(list, true);
    }

    public synchronized long addlist(final List<Group> list, boolean isNotifyChange) {
        final long[] count = {-1L};

        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {


                String[] args = new String[2];
                args[0] = PersonSharePreference.getUserID() + "";
                for (int i = 0; i < list.size(); i++) {

                    Group gm = list.get(i);
                    ContentValues cv = getContentValues(gm);

                    count[0] = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[2], null, cv
                            , SQLiteDatabase.CONFLICT_IGNORE);

                    LogCatLog.d(TAG, " 添加群=>插入" + count[0] + "条");

                    if (count[0] <= 0) {

                        args[1] = getGroupId(gm);

                        count[0] = db.update(DBSQLUtil.TABLES_NAME[2], cv, "XLID = ? AND LOCAL_GROUP_ID = ? ", args);

                        LogCatLog.d(TAG, " 添加群=>更新" + count[0] + "条");
                    }
                }

                return count[0];
            }

            @Override
            public void endTransaction(long count) {

            }
        });

        if (isNotifyChange) {
            //发送数据库变化的信号通知loader重新加载
            XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
            //发送数据库变化的信号通知loader重新加载
            XLApplication.getInstance().getContentResolver().notifyChange(ContactDBHandler.SYNC_SIGNAL_URI, null);
        }

        LogCatLog.d(TAG, " 添加群=>完成");
        return count[0];

    }

    /**
     * 判断当前用户是不是这个群的群主
     * 查询群全部消息
     * 仅能在初始建群且未发送任何信息的情况下才可以修改群名称
     *
     * @param groupid
     * @return
     */
    private long queryChatHistoryGroup(String groupid) {

        Cursor cursor = null;
        String sql = "";
        String[] args = new String[]{groupid};
        try {
            sql = "SELECT \n" +
                    "  count( LOCAL_GROUP_ID ) num \n" +
                    "FROM \n" +
                    "  contact_msg_table \n" +
                    "WHERE \n" +
                    "  LOCAL_GROUP_ID = ? ;";

            cursor = dbUtil.query(sql, args);
            if (cursor != null && cursor.moveToNext()) {
                return Utils.parseLong(cursor.getString(cursor.getColumnIndex("num")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.colse(cursor);
        }

        return 1L;
    }

    /**
     * 查看‘我参与的群’排序，排序逻辑应为：按群内消息数倒序排列，如条数相同，则按发送时间倒序排列
     */
    public ArrayList<Group> queryGroupWithMsgCount() {


        ArrayList list = new ArrayList();
        String xlid = PersonSharePreference.getUserID() + "";
        Cursor cursor = null;
        try {

            String sql;
            String[] selectionArgs;
            selectionArgs = new String[]{xlid};
            sql = "SELECT \n" +
                    "  * \n" +
                    "FROM \n" +
                    "  GROUP_TABLE g LEFT JOIN \n" +
                    "  ( \n" +
                    "    SELECT \n" +
                    "      c.Local_group_id , \n" +
                    "      count( c.Local_group_id ) msgcount , \n" +
                    "      CASE m.MSGDIR \n" +
                    "        WHEN 0 THEN max( m.msg_createdate ) \n" +
                    "      END Last_SEND_msg_createdate , \n" +
                    "      CASE m.MSGDIR \n" +
                    "        WHEN 1 THEN max( m.msg_createdate ) \n" +
                    "      END Last_RECEIVE_msg_createdate \n" +
                    "    FROM \n" +
                    "      contact_msg_table c LEFT JOIN \n" +
                    "      msg_table m ON c.msg_key = m.msg_key \n" +
                    "    GROUP BY \n" +
                    "      c.Local_group_id \n" +
                    "  ) s ON g.Local_group_id = s.Local_group_id \n" +
                    "WHERE \n" +
                    "  xlid = ? \n" +
                    "ORDER BY \n" +
                    "  s.msgcount DESC , \n" +
                    "  s.Last_SEND_msg_createdate DESC , \n" +
                    "  s.Last_RECEIVE_msg_createdate DESC ;";


            cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return list;
            }
            GroupCursor groupCursor = new GroupCursor(cursor);

            do {
                Group group = groupCursor.getGroup();
                list.add(group);
            } while (cursor.moveToNext());


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.colse(cursor);
        }

        return list;

    }


    /**
     * 判断当前用户是不是这个群的群主
     *
     * @param groupid
     * @return
     */
    public boolean isGroupOwner(String groupid) {

        String xlid = PersonSharePreference.getUserID() + "";
        Cursor cursor = null;
        String sql = "";
        String[] args = new String[]{xlid, groupid};
        try {
            sql = "SELECT \n" +
                    "  * \n" +
                    "FROM \n" +
                    "  group_table \n" +
                    "WHERE \n" +
                    "   xlid = ? AND LOCAL_GROUP_ID = ? AND xlgrouptype = 'O' COLLATE NOCASE ";

            cursor = dbUtil.query(sql, args);
            if (cursor != null) {
                return cursor.getCount() > 0 ? true : false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.colse(cursor);
        }

        return false;
    }

    /**
     *  退出群组
     * 不要循环调用
     * @param
     * @return
     */
    public synchronized long exitGroup(final String local_group_id) {
        final String xlid = PersonSharePreference.getUserID() + "";
        final long[] count = {-1};
        // TODO: 2015/12/12 需要事物来控制
        //0false
        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {

                ContentValues contentValues = new ContentValues();
                contentValues.put("isjoin", BorrowConstants.IS_NO_JOIN_GROUP);
                count[0] = dbUtil.update(DBSQLUtil.TABLES_NAME[2], contentValues,
                        "LOCAL_GROUP_ID = ?  AND XLID = ?  ", new String[]{local_group_id, xlid});

                return count[0];
            }

            @Override
            public void endTransaction(long count) {

            }
        });

        //删除群成员

/*        if(count>0){
            XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
        }*/
        //发送数据库变化的信号通知loader重新加载
        XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
        //发送数据库变化的信号通知loader重新加载
        XLApplication.getInstance().getContentResolver().notifyChange(ContactDBHandler.SYNC_SIGNAL_URI, null);
        return count[0];
    }

    /**
     *  解散群组
     * 不要循环调用
     *
     * @param
     * @return
     */
    public synchronized long dismissGroup(final String local_group_id) {
        final String xlid = PersonSharePreference.getUserID() + "";
        final long[] count = {-1};
        // TODO: 2015/12/12 需要事物来控制
        //0false
        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {

                ContentValues contentValues = new ContentValues();
                contentValues.put("STATUS", "DISMISS");
                count[0] = dbUtil.update(DBSQLUtil.TABLES_NAME[2], contentValues,
                        "LOCAL_GROUP_ID = ?  AND XLID = ?  ", new String[]{local_group_id, xlid});


                return count[0];
            }

            @Override
            public void endTransaction(long count) {

            }
        });

        //删除群成员

/*        if(count>0){
            XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
        }*/
        //发送数据库变化的信号通知loader重新加载
        XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
        //发送数据库变化的信号通知loader重新加载
        XLApplication.getInstance().getContentResolver().notifyChange(ContactDBHandler.SYNC_SIGNAL_URI, null);
        return count[0];
    }




    /**
     * 查询`所有好友,包括黑名单,分组显示,
     * @return LinkedList list
     */

 
    public ArrayList<Group> queryAllFigureCommonGroup() {

        ArrayList<Group> list = new ArrayList();
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
                    "      GROUP_TABLE \n" +
                    "    ORDER BY \n" +
                    "      CREATEDATE \n" +
                    "  ) \n" +
                    "WHERE \n" +
                    "  XLID = ? \n AND" +
                    "  STATUS <> ? \n" +

                    "GROUP BY \n" +
                    "  XLGROUPID \n" +
                    "ORDER BY \n" +
                    "  CREATEDATE ;") ;
            selectionArgs= new String[]{xlid,"DISMISS"};

            Cursor cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return list;
            }
            GroupCursor groupCursor = new GroupCursor(cursor);

            do {
                Group contact = groupCursor.getGroup();
                list.add(contact);
            } while (cursor.moveToNext());

            dbUtil.colse(cursor);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }




/*
    */
    /**
     * 标记一个群为加入状态
     *//*

    public synchronized void updateWithJoin(String local_group_id) {
        String xlid = PersonSharePreference.getUserID() + "";
        dbUtil.execSQL("update group_table set isjoin = 1 where LOCAL_GROUP_ID = ?  AND XLID = ?  ",
                new String[]{local_group_id, xlid});
        //发送数据库变化的信号通知loader重新加载
        XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
        //发送数据库变化的信号通知loader重新加载
        XLApplication.getInstance().getContentResolver().notifyChange(ContactDBHandler.SYNC_SIGNAL_URI, null);
    }
*/


    /**
     * 更新群组数据
     *
     * @param
     * @return
     */
    public synchronized long update(Group group) {
        String xlid = PersonSharePreference.getUserID() + "";
        long count = -1;

        count = dbUtil.addOrUpdate(DBSQLUtil.TABLES_NAME[2], getContentValues(group), "XLID = ? AND LOCAL_GROUP_ID = " +
                "? ",
                new String[]{xlid, getGroupId(group)});

        if (count > 0) {
            //发送数据库变化的信号通知loader重新加载
            XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
            //发送数据库变化的信号通知loader重新加载
            XLApplication.getInstance().getContentResolver().notifyChange(ContactDBHandler.SYNC_SIGNAL_URI, null);
        }
        LogCatLog.d(TAG, " 更新群信息" + count + "条" + group);
        return count;
    }

    /**
     * 查询群列表
     *
     * @return
     */
    public Cursor queryGrList() {
        //
        Cursor cursor = null;
        try {
            cursor = dbUtil.query(DBSQLUtil.TABLES_NAME[2], new String[]{"XLID", "ISJOIN"}, new
                    String[]{PersonSharePreference
                    .getUserID() + "", "1"}, 0, 0);
        } catch (Exception e) {
            LogCatLog.e(TAG, "查询群组数据失败 ，错误信息 " + e.getLocalizedMessage());
        }
        return cursor;
    }

    /**
     * 查询全部群组
     *
     * @param figureId figureId 为null时查询全部
     * @return LinkedList list
     */
    public LinkedList<Group> queryGroupList(String figureId) {
        LinkedList linkedList = new LinkedList();
        String xlid = PersonSharePreference.getUserID() + "";
        Cursor cursor = null;
        try {

            String sql;
            String[] selectionArgs;

            if (TextUtils.isEmpty(figureId)) {

                sql = "SELECT * " +
                        "FROM group_table  " +
                        "WHERE XLID = ? " +
                        "GROUP BY LOCAL_GROUP_ID ";

                selectionArgs = new String[]{xlid};
            } else {

                sql = "SELECT * " +
                        "FROM group_table  " +
                        "WHERE XLID = ? and FIGURE_ID = ?  " +
                        "GROUP BY LOCAL_GROUP_ID   ";

                selectionArgs = new String[]{xlid, figureId};
            }

            cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return linkedList;
            }
            GroupCursor groupCursor = new GroupCursor(cursor);

            do {
                Group group = groupCursor.getGroup();
                linkedList.add(group);
            } while (cursor.moveToNext());


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.colse(cursor);
        }

        return linkedList;
    }


    /**
     * 通过Cursor获取当前的Group
     */
    public static class GroupCursor extends CursorWrapper {
        public GroupCursor(Cursor c) {
            super(c);
        }

        public Group getGroup() {

            String xlId = getString(getColumnIndex("XLID"));
            String mTeamId = getString(getColumnIndex("XLGROUPID"));
            String mRemarkName = getString(getColumnIndex("XLGROUPNIKENAME"));
            String mTeamType = getString(getColumnIndex("XLGROUPTYPE"));
            String mImgPath = getString(getColumnIndex("XLGROUPIMAGEPATH"));
            String mTeamNumMax = getString(getColumnIndex("GROUPNUMMAX"));
            String mTeamNum = getString(getColumnIndex("GROUPNUMBER"));
            String FILE_ID = getString(getColumnIndex("FILE_ID"));
            String ISJOIN = getString(getColumnIndex("ISJOIN"));

            String ownerFigureId = getString(getColumnIndex("OWNER_FIGUREID"));
            String ownerUserId = getString(getColumnIndex("OWNER_USERID"));

            String xlimagePath = getString(getColumnIndex("XLIMAGE_PATH"));
            String description = getString(getColumnIndex("DESCRIPTION"));
            String status = getString(getColumnIndex("STATUS"));
            String updateGroupTime = getString(getColumnIndex("UPDATE_GROUP_TIME"));
            String createGroupTime = getString(getColumnIndex("CREATE_GROUP_TIME"));
            String local_group_id = getString(getColumnIndex("LOCAL_GROUP_ID"));
            String figure_id = getString(getColumnIndex("FIGURE_ID"));

            Group agroup = new Group.Builder()
                    .xlID(xlId)
                    .xlGroupID(mTeamId)
                    .xlGroupImagePath(mImgPath)
                    .xlGroupName(mRemarkName)
                    .groupType(mTeamType)
                    .xlGroupNumMax(mTeamNumMax)
                    .xlGroupCurrentNum(mTeamNum)
                    .file_id(FILE_ID)
                    .isJoin(ISJOIN)
                    .xlimagePath(xlimagePath)
                    .description(description)
                    .status(status)
                    .updateGroupTime(updateGroupTime)
                    .createGroupTime(createGroupTime)
                    .ownerUserId(ownerUserId)
                    .ownerFigureId(ownerFigureId)
                    .localGroupId(local_group_id)
                     .figureId(figure_id)
                    .build();
            int figuregroup = getColumnIndex("figuregroup");
            if(figuregroup>0){
                GroupManager.getInstance().setFigureGroup( getString(getColumnIndex("figuregroup")).split(","),agroup);
            }
            return agroup;
        }
    }

    /**
     * 查询群组数据
     *
     * @return
     */
    public Group query(String local_group_id) {
        Cursor cursor = null;
        Group groupQuery = null;
        String xlID = PersonSharePreference.getUserID() + "";
        try {
            cursor = dbUtil.query(DBSQLUtil.TABLES_NAME[2], new String[]{"XLID", "LOCAL_GROUP_ID"}, new String[]{
                    xlID, local_group_id}, 0, 0);

            while (cursor.moveToNext()) {
                groupQuery = new Group.Builder()
                        .xlID(cursor.getString(cursor.getColumnIndex("XLID")))
                        .xlGroupID(cursor.getString(cursor.getColumnIndex("XLGROUPID")))
                        .xlGroupName(cursor.getString(cursor.getColumnIndex("XLGROUPNIKENAME")))
                        .xlGroupImagePath(cursor.getString(cursor.getColumnIndex("XLGROUPIMAGEPATH")))
                        .xlGroupNumMax(cursor.getString(cursor.getColumnIndex("GROUPNUMMAX")))
                        .xlGroupCurrentNum(cursor.getString(cursor.getColumnIndex("GROUPNUMBER")))
                        .groupType(cursor.getString(cursor.getColumnIndex("XLGROUPTYPE")))
                        .file_id(cursor.getString(cursor.getColumnIndex("FILE_ID")))
                        .isJoin(cursor.getString(cursor.getColumnIndex("ISJOIN")))
                        .xlimagePath(cursor.getString(cursor.getColumnIndex("XLIMAGE_PATH")))
                        .description(cursor.getString(cursor.getColumnIndex("DESCRIPTION")))
                        .status(cursor.getString(cursor.getColumnIndex("STATUS")))
                        .updateGroupTime(cursor.getString(cursor.getColumnIndex("UPDATE_GROUP_TIME")))
                        .createGroupTime(cursor.getString(cursor.getColumnIndex("CREATE_GROUP_TIME")))
                        .ownerFigureId(cursor.getString(cursor.getColumnIndex("OWNER_FIGUREID")))
                        .ownerUserId(cursor.getString(cursor.getColumnIndex("OWNER_USERID")))
                        .localGroupId(cursor.getString(cursor.getColumnIndex("LOCAL_GROUP_ID")))
                        .figureId(cursor.getString(cursor.getColumnIndex("FIGURE_ID")))
                        .build();
            }
        } catch (Exception e) {
            LogCatLog.e(TAG, "查询群组数据失败 ，错误信息 " + e.getLocalizedMessage());
        } finally {
            dbUtil.colse(cursor);
        }
        return groupQuery;
    }

    /**
     * 获取 contentvalues
     *
     * @param group
     * @return
     */
    public ContentValues getContentValues(Group group) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("XLID", group.xlID);
        contentValues.put("XLGROUPID", group.xlGroupID);
        contentValues.put("XLGROUPNIKENAME", group.xlGroupName);
        contentValues.put("XLGROUPTYPE", group.groupType);
        contentValues.put("XLGROUPIMAGEPATH", group.xlGroupImagePath);
        contentValues.put("GROUPNUMMAX", group.xlGroupNumMax);
        contentValues.put("GROUPNUMBER", group.xlGroupCurrentNum);
        contentValues.put("FILE_ID", group.file_id);
        contentValues.put("ISJOIN", group.isJoin);
        contentValues.put("XLIMAGE_PATH", group.xlimagePath);
        contentValues.put("DESCRIPTION", group.description);
        contentValues.put("STATUS", group.status);
        contentValues.put("UPDATE_GROUP_TIME", group.updateGroupTime);
        contentValues.put("CREATE_GROUP_TIME", group.createGroupTime);
        contentValues.put("OWNER_USERID", group.ownerUserId);
        contentValues.put("OWNER_FIGUREID", group.ownerFigureId);
        contentValues.put("LOCAL_GROUP_ID", group.localGroupId);
        contentValues.put("FIGURE_ID", group.figureId);

        contentValues.put("CREATEDATE", System.currentTimeMillis());
        contentValues.put("UPDATEDATE", System.currentTimeMillis());

        return contentValues;
    }

    /**
     * @param groupid
     * @param figureid 本地角色id
     * @return
     */
    public static String getGroupId(String groupid, String figureid) {
        return xlid + groupid + figureid;
    }

    public static String getGroupId(Group group) {
        return xlid + group.xlGroupID + group.figureId;
    }
}
