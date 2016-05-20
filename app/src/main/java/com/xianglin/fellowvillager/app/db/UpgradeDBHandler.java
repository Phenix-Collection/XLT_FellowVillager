/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.db.DBUtil;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.mobile.common.utils.TimeTag;

/**
 * 耗时的数据库升级工具类
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/22 12:31  XLXZ Exp $
 */
public class UpgradeDBHandler extends BaseBDHandler {

    public UpgradeDBHandler(Context mContext) {
        super(mContext);
    }
    /**
     * 给已经存在的字段添加约束  UNIQUE
     *
     * @param tableName  DBSQLUtil.TABLES_NAME id
     * @param columnName
     */
    public void addConstraintUNIQUE(final int tableName[],final String columnName[]) {

        TimeTag tag=new TimeTag();
        tag.start();
        LogCatLog.d(TAG,"addConstraintUNIQUE开始");

       dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {

                //db.execSQL(String.format("Alter Table %1$s Rename To  Temp_%1$s ;",tableName));

                for (int i = 0; i < tableName.length; i++) {
                    //对原表填充数据
                    db.execSQL(String.format("UPDATE %s  SET %s =  random()", DBSQLUtil
                            .TABLES_NAME[tableName[i]],columnName[i]),new String[]{});

                    //修改原来的表为temp
                    db.execSQL(String.format("Alter Table %1$s Rename To  Temp_%1$s ;", DBSQLUtil
                            .TABLES_NAME[tableName[i]]),new String[]{});

                    //重新创建表
                    db.execSQL(DBSQLUtil.TABLES_CREATE_SQL[tableName[i]],new String[]{});

                    //复制表数据
                    db.execSQL(String.format(" insert into %1$s select * from Temp_%1$s",DBSQLUtil
                            .TABLES_NAME[tableName[i]]),new String[]{});
                    //删除表temp
                    db.execSQL(String.format("drop table Temp_%1$s",DBSQLUtil
                            .TABLES_NAME[tableName[i]]),new String[]{});

                }
                return 0;
            }

            @Override
            public void endTransaction(long count) {

            }
        });
        LogCatLog.d(TAG,"addConstraintUNIQUE结束:"+tag.stop());

    }


}
