package com.xianglin.fellowvillager.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.xianglin.fellowvillager.app.model.User;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 *
 * user 表 增加改查
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-12
 */
public class UserDBHandler extends BaseBDHandler {

    private Context mContext;

    public UserDBHandler(Context mContext){
        super(mContext);
    }

    /**
     * 添加用户数据
     * @param user
     * @return
     */
    public synchronized long add(User  user) {
        if(user  == null) throw  new NullPointerException("添加数据失败，user 不能为null");
        User queryUser = query(user);
        if(queryUser!= null) {
            if (queryUser.xlID .equals(user.xlID)) {//查询 user 对象不等于null
                return update(user);// 更新
            } else {
                return dbUtil.add(DBSQLUtil.TABLES_NAME[0], getContentValues(user));
            }
        }else{
            return dbUtil.add(DBSQLUtil.TABLES_NAME[0], getContentValues(user));
        }
    }

    /**
     * 删除用户数据
     * @param user
     * @return
     */
    public synchronized long del(User user) {
        if(user == null) throw  new NullPointerException("删除用户数据失败 ，user 不能为null");
        User queryUser = query(user);// 查询 User 对象不等于 null
        if(queryUser!= null) {
            if (queryUser.xlID .equals(user.xlID)) {
                return dbUtil.del(DBSQLUtil.TABLES_NAME[0], "XLID", new String[]{queryUser.xlID});// 删除
            }
        }
            return -1L;
    }

    /**
     * 更新用户数据
     * @param user
     * @return
     */
    public synchronized long update(User user) {
        if(user  == null)throw  new NullPointerException("更新用户数据失败，user 不能为null");
        User queryUser = query(user);// 查询 User 对象 不等于 null
        if(queryUser != null) {
            if (queryUser.xlID .equals(user.xlID)) {
                return dbUtil.update(DBSQLUtil.TABLES_NAME[0], getContentValues(user), "XLID", new String[]{user.xlID});// 更新
            }
        }
        return -1L;
    }


    /**保存最后一次使用的角色
     * @param figureId 用户角色id
     */
    public synchronized void saveLastFigure(String figureId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FIGURE_ID", figureId);
        dbUtil.update(DBSQLUtil.TABLES_NAME[0], contentValues, "XLID = ?", new String[]{PersonSharePreference.getUserID()+""});
    }

    /**
     * 查询单个用户数据
     * @param user
     * @return
     */
    public User query(User user) {
        User userQuery = null;
        Cursor cursor = null;

        try{
            cursor =dbUtil.query(DBSQLUtil.TABLES_NAME[0], new String[]{"XLID"},new String[]{user.xlID},0,0);

            while (cursor.moveToNext()){

                userQuery =  new User.Builder()
                        .xlID(cursor.getString(cursor.getColumnIndex("XLID")))
                        .deviceID(cursor.getString(cursor.getColumnIndex("DEVICEID")))
                        .xlUserName(cursor.getString(cursor.getColumnIndex("XLUSERNAME")))
                        .imagePath(cursor.getString(cursor.getColumnIndex("IMAGE_PATH")))
                        .figureId(cursor.getString(cursor.getColumnIndex("FIGURE_ID")))
                        .build();

            }

        }catch(Exception e){
            LogCatLog.e(TAG, "查询用户数据失败，错误消息 ＝"+e.getLocalizedMessage());
        }finally {
            dbUtil.colse(cursor);
        }

        return userQuery;
    }


    /**
     * 查询单个用户数据
     * @return
     */
    public User query() {
        User userQuery = null;
        Cursor cursor = null;
        try{
            cursor =dbUtil.query(DBSQLUtil.TABLES_NAME[0]);

            while (cursor.moveToNext()){

                userQuery =  new User.Builder()
                        .xlID(cursor.getString(cursor.getColumnIndex("XLID")))
                        .deviceID(cursor.getString(cursor.getColumnIndex("DEVICEID")))
                        .xlUserName(cursor.getString(cursor.getColumnIndex("XLUSERNAME")))
                        .imagePath(cursor.getString(cursor.getColumnIndex("IMAGE_PATH")))
                        .figureId(cursor.getString(cursor.getColumnIndex("FIGURE_ID")))
                        .build();

            }

        }catch(Exception e){
            LogCatLog.e(TAG, "查询用户数据失败，错误消息 ＝"+e.getLocalizedMessage());
        }finally {
            dbUtil.colse(cursor);
        }

        return userQuery;
    }


    /**
     * 获取 contentvalues
     * @param user
     * @return
     */
    public ContentValues getContentValues(User user){
        ContentValues contentValues = new ContentValues();

        contentValues.put("XLID",user.xlID);
        contentValues.put("DEVICEID",user.deviceID);
        contentValues.put("XLUSERNAME",user.xlUserName);
        contentValues.put("IMAGE_PATH",user.imagePath);
        contentValues.put("FIGURE_ID",user.figureId);
        contentValues.put("CREATEDATE",System.currentTimeMillis());
        contentValues.put("UPDATEDATE", System.currentTimeMillis());

        return contentValues;
    }
}
