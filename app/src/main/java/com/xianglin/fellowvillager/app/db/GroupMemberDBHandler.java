package com.xianglin.fellowvillager.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.db.DBUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 群成员表
 *
 * @author pengyang
 * @version v 1.0.0 2015/12/3 18:50  XLXZ Exp $
 */
public class GroupMemberDBHandler extends BaseBDHandler {


    public static final Uri SYNC_SIGNAL_URI = Uri.withAppendedPath(BASE_URI, "GroupMember_SYNC_SIGNAL_URI");

    public GroupMemberDBHandler(Context mContext) {
        super(mContext);
    }


    static String xlid = PersonSharePreference.getUserID() + "";

    /**
     * 添加一个群成员数据
     *
     * @param group
     * @return
     */
    public synchronized void add(GroupMember group) {
        ArrayList<GroupMember> list = new ArrayList<GroupMember>();
        list.add(group);
        addlist(list);
    }

    /**
     * 批量插入群成员
     *
     * @param list
     * @return
     */
    public synchronized void addlist(final List<GroupMember> list) {

        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {
                long count = -1L;

                String[] args = new String[1];

                for (int i = 0; i < list.size(); i++) {

                    GroupMember gm = list.get(i);

                    ContentValues cv = getContentValues(gm);

                    count = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[8], null, cv
                            , SQLiteDatabase.CONFLICT_IGNORE);

                    if (count <= 0) {

                        args[0] = getMemberId(gm.xlGroupId, gm.figureUsersId, gm.figureId);

                        count = db.update(DBSQLUtil.TABLES_NAME[8], cv, "MEMBERID = ?", args);
                    }
                }

                return count;
            }

            @Override
            public void endTransaction(long count) {

            }
        });
        //发送数据库变化的信号通知loader重新加载
        XLApplication.getInstance().getContentResolver().notifyChange(SYNC_SIGNAL_URI, null);
    }

    /**
     * 删除单个成员 todo 删除聊天信息?
     *
     * @param group
     * @return
     */
    public synchronized long del(GroupMember group) {
        return dbUtil.del(DBSQLUtil.TABLES_NAME[8], "MEMBERID = ?", new String[]{getMemberId(group.xlGroupId, group
                .figureUsersId, group.figureId)});
    }
    public synchronized long delForGroup(String  localGroupId) {
        return dbUtil.del(DBSQLUtil.TABLES_NAME[8], "LOCAL_GROUP_ID = ?", new String[]{localGroupId});
    }
    /**
     * 删除单个成员
     *
     * @param groupMemberID 群成员id
     * @return
     */
    public synchronized long del(String groupMemberID) {
        return dbUtil.del(DBSQLUtil.TABLES_NAME[8], "MEMBERID = ?", new String[]{groupMemberID});
    }

    public synchronized long delList(List<GroupMember> mDelMember) {
        long f = 0;
        for (int i = 0; mDelMember != null && i < mDelMember.size(); i++) {
            if (mDelMember.get(i).isOwner.equals("true")) {
                continue;
            } else {
                GroupMember groupMember = mDelMember.get(i);
                f += dbUtil.del(DBSQLUtil.TABLES_NAME[8], "MEMBERID = ?",
                        new String[]{getMemberId(groupMember.xlGroupId, groupMember.figureUsersId, groupMember
                                .figureId)});
            }
        }
        return f;
    }


    //
    //    /**
    //     * 更新群组数据
    //     * @param
    //     * @return
    //     */
    //    public long update(GroupMember group) {
    //        if (group == null) throw new NullPointerException("更新群组数据失败，group 不能为null");
    //        Group queryGroup = query(group);
    //        if (queryGroup != null && queryGroup.xlGroupID != null) {
    //            return dbUtil.update(DBSQLUtil.TABLES_NAME[2], getContentValues(group), "XLID = ? AND XLGROUPID = ? ",
    //                    new String[]{group.xlID, group.xlGroupID});
    //        }
    //        return -1L;
    //    }

    /**
     * 查询群列表
     *
     * @param local_group_id
     * @param
     * @return
     */
    public Cursor queryGroupMemberList(String local_group_id) {
        //
        Cursor cursor = null;
        try {
/*            cursor = dbUtil.query(DBSQLUtil.TABLES_NAME[8], new String[]{"XLID", "LOCAL_GROUP_ID"}, new String[]{xlid,
                    local_group_id}, 0, 0);*/

            String sql = "SELECT \n" +
                    "  * \n" +
                    "FROM \n" +
                    "  group_member_table a \n" +
                    "WHERE \n" +
                    "  a.LOCAL_GROUP_ID = ? \n" +
                    "ORDER BY \n" +
                    "  join_time ;";

            cursor = dbUtil.query(sql, new String[]{local_group_id});

        } catch (Exception e) {
            LogCatLog.e(TAG, "查询群组数据失败 ，错误信息 " + e.getLocalizedMessage());
        }
        return cursor;
    }


    /**
     * 查询群列表
     *
     * @param groupid
     * @param
     * @return
     */
    public ArrayList<GroupMember> queryGroupMemberToList(String groupid) {
        //
        ArrayList<GroupMember> mMemberList=new ArrayList<GroupMember>();

        Cursor cursor = queryGroupMemberList(groupid);
        GroupMemberCursor groupMemberCursor = new GroupMemberCursor(cursor);
        while (groupMemberCursor.moveToNext()) {
            GroupMember mGroupMember = groupMemberCursor.getGroupMember();
            mMemberList.add(mGroupMember);
        }

        groupMemberCursor.close();

        return  mMemberList;

    }


/*    *//**
     *  根据消息key查群成员
     *
     * @param memberid
     * @return
     *//*
    public GroupMember query(String msgkey) {
        Cursor cursor = null;
        GroupMember cm = null;
        String xlID = PersonSharePreference.getUserID() + "";
        try {

          String sql     =" SELECT\n" +
                    "    b.*\n" +
                    "    FROM\n" +
                    "    contact_msg_table a LEFT JOIN\n" +
                    "    group_member_table b ON a.XLGROUPMEMBERID = b.MEMBERID\n" +
                    "            WHERE\n" +
                    "    a.msg_key = ? and a.XLID = ? ";

            cursor = dbUtil.query(sql,new String[]{msgkey,xlID});

            GroupMemberCursor memberCursor= new GroupMemberCursor(cursor);

            if (cursor != null && memberCursor.moveToNext()) {
                return memberCursor.getGroupMember();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.colse(cursor);
        }
        return null;
    }*/

    /**
     * 查询单个群成员
     */
    public GroupMember queryGroupMember(String memberid) {
        Cursor cursor = null;
        GroupMember cm = null;
        String xlID = PersonSharePreference.getUserID() + "";
        try {

            String sql = "SELECT \n" +
                    "  * \n" +
                    "FROM \n" +
                    "  group_member_table a \n" +
                    "WHERE \n" +
                    "  a.MEMBERID = ? ;";

            cursor = dbUtil.query(sql, new String[]{memberid});

            GroupMemberCursor memberCursor = new GroupMemberCursor(cursor);

            if (cursor != null && memberCursor.moveToNext()) {
                return memberCursor.getGroupMember();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.colse(cursor);
        }
        return null;
    }

    /**
     * 通过Cursor获取当前的Group
     */
    public static class GroupMemberCursor extends CursorWrapper {
        public GroupMemberCursor(Cursor c) {
            super(c);
        }

        public GroupMember getGroupMember() {
            if (isBeforeFirst() || isAfterLast()) return null;

            GroupMember agroup = new GroupMember.Builder()
                    .xluserid(getString(getColumnIndex("XLUSERID")))
                    .xlGroupId(getString(getColumnIndex("XLGROUPID")))
                     .localgroupId(getString(getColumnIndex("LOCAL_GROUP_ID")))
                    .xlUserName(getString(getColumnIndex("XLUSERNAME")))
                    .xlRemarkName(getString(getColumnIndex("XLREMARKS")))
                    .xlImgPath(getString(getColumnIndex("XLIMAGE_PATH")))
                    .isContact(getString(getColumnIndex("ISCONTACT")))
                    .file_id(getString(getColumnIndex("FILE_ID")))
                    .figureUsersId(getString(getColumnIndex("FIGURE_USERSID")))
                    .figureId(getString(getColumnIndex("FIGURE_ID")))
                    .groupmemberid(getString(getColumnIndex("MEMBERID")))
                    .gender(getString(getColumnIndex("GENDER")))
                    .sexualOrientation(getString(getColumnIndex("SEXUALORIENTATION")))
                    .individualitySignature(getString(getColumnIndex("INDIVIDUALITYSIGNATURE")))
                    .joinTime(getLong(getColumnIndex("JOIN_TIME")))

                    .isOwner(getString(getColumnIndex("ISOWNER"))).build();

            return agroup;
        }
    }

    /**
     * 获取 contentvalues
     *
     * @param group
     * @return
     */
    public ContentValues getContentValues(GroupMember group) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("XLID", xlid);
        contentValues.put("XLGROUPID", group.xlGroupId);
        contentValues.put("XLUSERID", group.xluserid);
        contentValues.put("XLIMAGE_PATH", group.xlImgPath);
        contentValues.put("XLUSERNAME", group.xlUserName);
        contentValues.put("XLREMARKS", group.xlRemarkName);
        contentValues.put("ISCONTACT", group.isContact);
        contentValues.put("ISOWNER", group.isOwner);
        contentValues.put("FILE_ID", group.file_id);
        contentValues.put("MEMBERID", getMemberId(group.xlGroupId, group.figureUsersId, group.figureId));
        contentValues.put("FIGURE_USERSID", group.figureUsersId);
        contentValues.put("FIGURE_ID", group.figureId);
        contentValues.put("LOCAL_GROUP_ID", GroupDBHandler.getGroupId(group.xlGroupId,group.figureId));

        contentValues.put("GENDER", group.gender);
        contentValues.put("SEXUALORIENTATION", group.sexualOrientation);
        contentValues.put("INDIVIDUALITYSIGNATURE", group.individualitySignature);
        contentValues.put("JOIN_TIME", group.joinTime);


        return contentValues;
    }

/*
    */
    /**
     * 生成每个群成员的唯一的key
     *
     * @param group
     * @return
     *//*

    private static String getMemberId(GroupMember group) {

        return xlid + group.xlGroupId + group.xluserid+group.figureUsersId;
    }
*/

    /**
     * 生成每个群成员的唯一的key
     *
     * @param
     * @param figureid
     * @return
     */
    public static String getMemberId(String groupid, String figureuserid, String figureid) {
        return xlid + groupid + figureuserid + figureid;
    }

/*    public static String getMemberId(String groupid) {

         if(ContactManager.getInstance().getCurrentFigure()==null){
             new RuntimeException("当前角色为null,处于全部状态下");
         }
        return xlid + groupid + xlid+ ContactManager.getInstance().getCurrentFigure().getFigureUsersid();
    }*/
}
