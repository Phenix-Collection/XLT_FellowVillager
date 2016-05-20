package com.xianglin.fellowvillager.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.db.DBUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
public class FigureDbHandler extends BaseBDHandler {

    public FigureDbHandler(Context mContext) {
        super(mContext);
    }

    /**
     * 添加一个角色
     *
     * @param figureMode
     */
    public synchronized void add(FigureMode figureMode) {
        ArrayList arrayList = new ArrayList<FigureMode>();
        arrayList.add(figureMode);
        addlist(arrayList);
    }

    /**
     * 批量插入联系人
     *
     * @param list
     * @return
     */
    public synchronized void addlist(final List<FigureMode> list) {
        LogCatLog.d(TAG, " 添加角色开始");
        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {
                long count = -1L;

                String[] args = new String[1];

                for (int i = 0; i < list.size(); i++) {

                    FigureMode figureMode = list.get(i);

                    ContentValues cv = getContentValues(figureMode);

                    //    cv.remove("XLIMAGE_PATH");//// TODO: 2015/12/11  临时 网络下载新数据后不要更新这个字段,仅在本地操作

                    count = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[12], null, cv
                            , SQLiteDatabase.CONFLICT_IGNORE);

                    LogCatLog.d(TAG, " 添加角色=>插入" + count + "条");

                    if (count <= 0) {
                        args[0] = figureMode.getFigureUsersid();

                        count = db.update(DBSQLUtil.TABLES_NAME[12], cv, " FIGURE_USERSID = ? ", args);

                        LogCatLog.d(TAG, " 更新角色=>插入" + count + "条");
                    }

                }
                return count;
            }

            @Override
            public void endTransaction(long count) {

            }
        });
/*        if(isFresh){
            //发送数据库变化的信号通知loader重新加载
            XLApplication.getInstance().getContentResolver().notifyChange(ContactDBHandler.SYNC_SIGNAL_URI, null);
        }*/
        LogCatLog.d(TAG, " 添加角色完成");
    }

    /**
     * 更新角色使用时间
     *
     * @param figureMode
     */
    public synchronized void userFigureTime(FigureMode figureMode) {

        if (figureMode == null) {
            LogCatLog.e(TAG, "figureMode is null");
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("UPDATEDATE", figureMode.getUpdateDate());
        dbUtil.update(DBSQLUtil.TABLES_NAME[12], contentValues, "FIGURE_USERSID = ?", new String[]{figureMode
                .getFigureUsersid()});

    }


    /** 查询全部各个角色下的未读消息数
     * @return
     */
    public  HashMap<String,Long> queryFigureWithMsgCount() {


        HashMap<String,Long> map = new HashMap<String,Long>();
        String xlid = PersonSharePreference.getUserID() + "";
        Cursor cursor = null;
        try {

            String sql;
            String[] selectionArgs;
            selectionArgs = new String[]{BorrowConstants.MSGSTATUS_UNREAD+"",FigureMode.Status.ACTIVE.ordinal()+"",xlid};

            sql = "SELECT \n" +
                    "  f.FIGURE_USERSID , \n" +
                    "  msgcount \n" +
                    "FROM \n" +
                    "  figure_table f LEFT JOIN \n" +
                    "  ( \n" +
                    "    SELECT \n" +
                    "      c.figure_id , \n" +
                    "      count( c.figure_id ) msgcount , \n" +
                    "      ft.FIGURE_STATUS \n" +
                    "    FROM \n" +
                    "      figure_table ft LEFT JOIN \n" +
                    "      contact_msg_table c ON ft.FIGURE_USERSID = c.figure_id LEFT JOIN \n" +
                    "      msg_table m ON c.msg_key = m.msg_key \n" +
                    "    WHERE \n" +
                    "      m.MSG_STATUS = ? AND ft.FIGURE_STATUS = ? \n" +
                    "    GROUP BY \n" +
                    "      c.figure_id \n" +
                    "  ) s ON f.FIGURE_USERSID = s.figure_id \n" +
                    "WHERE \n" +
                    "  xlid = ? ;";


            cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return map;
            }

            do {

                String figureid=cursor.getString(cursor.getColumnIndex("FIGURE_USERSID"));

                boolean isExisted= !cursor.isNull(cursor.getColumnIndex("msgcount"));

                long msgcount = isExisted?cursor.getLong( cursor.getColumnIndex("msgcount")):0L;

                map.put(figureid,msgcount);

            } while (cursor.moveToNext());


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.colse(cursor);
        }

           return  map;
    }


    /**
     * 查询全部角色数据
     *
     * @param figureId 需要查询的角色id  如果为空就返回全部
     * @return linkedList 角色列表
     */
    public List<FigureMode> queryFigure(String figureId) {
        LinkedList linkedList = new LinkedList();
        String xlid = PersonSharePreference.getUserID() + "";

        String sql;
        String[] selectionArgs;

        if (TextUtils.isEmpty(figureId)) {

            sql = String.format("SELECT *  FROM FIGURE_TABLE WHERE XLID = ? order by FIGURE_STATUS ");
            selectionArgs = new String[]{xlid};
        } else {
            sql = String.format("SELECT *  FROM FIGURE_TABLE WHERE XLID = ? and FIGURE_USERSID = ? ");
            selectionArgs = new String[]{xlid, figureId};
        }

        try {
            Cursor cursor = dbUtil.query(sql, selectionArgs);
            if (!cursor.moveToFirst()) {
                cursor.close();
            } else {
                FigureCursor contactCursor = new FigureCursor(cursor);
                do {
                    FigureMode figureMode = contactCursor.converCursorToFiguret();
                    linkedList.add(figureMode);
                } while (cursor.moveToNext());

                dbUtil.colse(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkedList;
    }

    /**
     * 通过Cursor 转 对象
     */
    public static class FigureCursor extends CursorWrapper {

        public FigureCursor(Cursor c) {
            super(c);
        }

        public List<FigureMode> getContactList() {

            if (isAfterLast()) {
                moveToPosition(-1);
            }

            List<FigureMode> figureModes = new ArrayList<FigureMode>();
            while (moveToNext()) {
                FigureMode figureMode = converCursorToFiguret();
                figureModes.add(figureMode);
            }
            return figureModes;
        }


        private FigureMode converCursorToFiguret() {

            FigureMode figureMode = new FigureMode();

            figureMode.setXlId(getString(getColumnIndex("XLID")));
            figureMode.setCreateDate(getLong(getColumnIndex("CREATEDATE")));
            figureMode.setFigureGroup(getString(getColumnIndex("FIGURE_GROUP")));
            figureMode.setFigureInfo(getString(getColumnIndex("FIGURE_INFO")));
            figureMode.setFigureName(getString(getColumnIndex("FIGURE_NAME")));
            figureMode.setFigureStatus(FigureMode.Status.valueOf(getInt(getColumnIndex("FIGURE_STATUS"))));
            figureMode.setFigureUsersid(getString(getColumnIndex("FIGURE_USERSID")));

            figureMode.setFigureImageid(getString(getColumnIndex("FIGURE_IMAGEID")));
            figureMode.setFigureGender(FigureMode.FigureGender.valueOf(getInt(getColumnIndex("FIGURE_GENDER"))));
            figureMode.setSexualOrientation(FigureMode.SexualOrientation.valueOf(getInt(getColumnIndex
                    ("SEXUALORIENTATION"))));
            figureMode.setFigureRelationship(getString(getColumnIndex("FIGURE_RELATIONSHIP")));
            figureMode.setImagePathThumbnail(getString(getColumnIndex("IMAGE_PATH_THUMBNAIL")));
            figureMode.setImagePpath(getString(getColumnIndex("IMAGE_PATH")));
            figureMode.setIsOpen(getString(getColumnIndex("ISOPEN")));
            figureMode.setFigureXlremarks(getString(getColumnIndex("FIGURE_XLREMARKS")));
            figureMode.setFigure_usersid_shortid(getString(getColumnIndex("FIGURE_USERSID_SHORTID")));
            figureMode.setUpdateDate(getLong(getColumnIndex("UPDATEDATE")));

            return figureMode;
        }

    }

    /**
     * 获取 contentvalues
     *
     * @param figureMode
     * @return
     */
    public ContentValues getContentValues(FigureMode figureMode) {
        ContentValues contentValues = new ContentValues();

        if (figureMode.getFigureStatus() == null) {
            new RuntimeException("figureMode.getFigureStatus is null");
        }
        if (figureMode.getFigureGender() == null) {
            new RuntimeException("figureMode.getFigureGender is null");
        }
        if (figureMode.getSexualOrientation() == null) {
            new RuntimeException("figureMode.getSexualOrientation is null");
        }

        contentValues.put("XLID", figureMode.getXlId());
        contentValues.put("FIGURE_USERSID", figureMode.getFigureUsersid());
        contentValues.put("FIGURE_STATUS", figureMode.getFigureStatus().ordinal());
        contentValues.put("FIGURE_NAME", figureMode.getFigureName());
        contentValues.put("FIGURE_XLREMARKS", figureMode.getFigureXlremarks());
        contentValues.put("FIGURE_INFO", figureMode.getFigureInfo());
        contentValues.put("FIGURE_GROUP", figureMode.getFigureGroup());

        contentValues.put("FIGURE_IMAGEID", figureMode.getFigureImageid());
        contentValues.put("FIGURE_GENDER", figureMode.getFigureGender().ordinal());
        contentValues.put("FIGURE_RELATIONSHIP", figureMode.getFigureRelationship());
        contentValues.put("IMAGE_PATH_THUMBNAIL", figureMode.getImagePathThumbnail());
        contentValues.put("IMAGE_PATH", figureMode.getImagePpath());

        contentValues.put("ISOPEN", figureMode.getIsOpen());
        contentValues.put("SEXUALORIENTATION", figureMode.getSexualOrientation().ordinal());
        contentValues.put("FIGURE_USERSID_SHORTID", figureMode.getFigure_usersid_shortid());

        contentValues.put("CREATEDATE", figureMode.getCreateDate());
        contentValues.put("UPDATEDATE", figureMode.getUpdateDate());

        return contentValues;
    }

}
