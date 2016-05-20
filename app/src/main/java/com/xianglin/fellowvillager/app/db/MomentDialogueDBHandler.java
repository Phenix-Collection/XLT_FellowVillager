package com.xianglin.fellowvillager.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.model.MomentDialogue;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 临时对话数据表处理
 * Javadoc
 *
 * @author james
 * @version 0.2, 彭阳  2015-11-26
 */
public class MomentDialogueDBHandler extends BaseBDHandler {

    private static final String TAG = MomentDialogueDBHandler.class.getSimpleName();

    public MomentDialogueDBHandler(Context mContext) {
        super(mContext);
    }

    /**
     * 添加最近联系人
     *
     * @param momentDialogue
     * @return
     */
    public synchronized long add(MomentDialogue momentDialogue) {
        if (momentDialogue == null)
            throw new NullPointerException("添加临时对话数据失败，momentDialogue 不能为null");

        StringBuffer buffer = new StringBuffer();
        buffer.append(" XLID = ? ");
        String[] strArray = new String[2];
        strArray[0] = PersonSharePreference.getUserID() + "";
        if (!TextUtils.isEmpty(momentDialogue.figureUsersId)) {
            buffer.append(" AND CONTACT_ID = ?");
            strArray[1] = momentDialogue.figureUsersId;
        } else if (!TextUtils.isEmpty(momentDialogue.localGroupId)) {
            buffer.append(" AND LOCAL_GROUP_ID = ?");
            strArray[1] = momentDialogue.localGroupId;
        }

        return dbUtil.addOrUpdate(DBSQLUtil.TABLES_NAME[6], getContentValues(momentDialogue) ,
                buffer.toString(),
                strArray);
/*
        List<MomentDialogue> queryMomentDialogueList = query(momentDialogue,true);
        if(queryMomentDialogueList != null){
            if(queryMomentDialogueList.size() != 0){
                return update(momentDialogue);
            }else{
                return dbUtil.add(DBSQLUtil.TABLES_NAME[6], getContentValues(momentDialogue));
            }
        }else{
            return dbUtil.add(DBSQLUtil.TABLES_NAME[6], getContentValues(momentDialogue));
        }
*/

    }

    /**
     * 删除临时对话联系人和群组单条数据数据
     *
     * @param momentDialogue
     * @return
     */
    public synchronized long del(MomentDialogue momentDialogue) {
        if (momentDialogue == null)
            throw new NullPointerException("删除 删除临时对话联系人和群组单条数据数据失败 ，momentDialogue 不能为null");

        StringBuffer buffer = new StringBuffer();
        buffer.append(" XLID = ? ");
        String[] strArray = new String[2];
        strArray[0] = PersonSharePreference.getUserID() + "";
        if (!TextUtils.isEmpty(momentDialogue.contactId)) {
            buffer.append(" AND CONTACT_ID = ?");
            strArray[1] = momentDialogue.contactId;
        } else if (!TextUtils.isEmpty(momentDialogue.localGroupId)) {
            buffer.append(" AND LOCAL_GROUP_ID = ?");
            strArray[1] = momentDialogue.localGroupId;
        }
        long count = dbUtil.del(DBSQLUtil.TABLES_NAME[6], buffer.toString(), strArray);
        if (count > 0) {
            //发送数据库变化的信号通知loader重新加载
         //   XLApplication.getInstance().getContentResolver().notifyChange(MessageDBHandler.SYNC_SIGNAL_URI, null);
        }
        return count;

    }

    /**
     * 删除最近联系人,确保此方法在db类的事物中执行
     *
     * @return
     */
    public synchronized static long delXLUser(SQLiteDatabase db, String figureId) {
        return db.delete(DBSQLUtil.TABLES_NAME[6], " XLID = ? AND CONTACT_ID = ? ",
                new String[]{PersonSharePreference.getUserID() + "", figureId});
    }

    /**
     * 更新临时对话联系人和群组单条数据数据
     *
     * @param momentDialogue
     * @return
     */
    public synchronized long update(MomentDialogue momentDialogue) {
        if (momentDialogue == null)
            throw new NullPointerException("更新临时对话联系人和群组单条数据失败，momentDialogue 不能为null");
        List<MomentDialogue> queryMomentDialogueList = query(momentDialogue, true);
        if (queryMomentDialogueList != null) {
            if (queryMomentDialogueList.size() != 0) {

                StringBuffer buffer = new StringBuffer();
                buffer.append(" XLID = ? ");
                String[] strArray = new String[2];
                strArray[0] = PersonSharePreference.getUserID() + "";
                if (!TextUtils.isEmpty(momentDialogue.figureUsersId)) {
                    buffer.append(" AND CREATEDATE = ?");
                    strArray[1] = momentDialogue.figureUsersId;
                } else if (!TextUtils.isEmpty(momentDialogue.localGroupId)) {
                    buffer.append(" AND LOCAL_GROUP_ID = ?");
                    strArray[1] = momentDialogue.localGroupId;
                }
                return dbUtil.update(DBSQLUtil.TABLES_NAME[6], getContentValues(momentDialogue), buffer.toString(),
                        strArray);

            }
        }
        return -1L;
    }


    /**
     * 获取 contentvalues
     *
     * @param momentDialogue
     * @return
     */
    public ContentValues getContentValues(MomentDialogue momentDialogue) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("XLID", PersonSharePreference.getUserID() + "");
        contentValues.put("XLUSERID", momentDialogue.xlUserID);
        contentValues.put("XLGROUPID", momentDialogue.xlGroupID);
        contentValues.put("LOCAL_GROUP_ID", momentDialogue.localGroupId);
        contentValues.put("LAST_MSG_DATE", momentDialogue.xlLastMsgDate);
        contentValues.put("FIGURE_USERSID", momentDialogue.figureUsersId);
        contentValues.put("FIGURE_ID", momentDialogue.figureId);
        contentValues.put("CONTACT_ID", momentDialogue.contactId);
        contentValues.put("CREATEDATE", System.currentTimeMillis());

        return contentValues;
    }

    public Cursor queryChatRecentMessage(String currentFigureID) {

        String sql;
        String[] args;

        if(!TextUtils.isEmpty(currentFigureID)){
            sql="Select * From  MOMENT_CHAT_TABLE where figure_id =?";
            args=new String []{currentFigureID};
        }else{
            sql="Select * From  MOMENT_CHAT_TABLE ";
            args=null;
        }

        return dbUtil.query(sql,args);

    }

    /**
     * 查询临时对话数据
     *
     * @param momentDialogue
     * @param isQuerySingle
     * @return
     */
    public List<MomentDialogue> query(MomentDialogue momentDialogue, boolean isQuerySingle) {
        Cursor cursor = null;
        MomentDialogue momentDialogueQuery = null;
        try {
            String[] column = null;
            String[] strArray = null;
            if (isQuerySingle) {
                column = new String[2];
                column[0] = "XLID";
                strArray = new String[2];
                strArray[0] = PersonSharePreference.getUserID() + "";


                if (!TextUtils.isEmpty(momentDialogue.figureUsersId)) {
                    column[1] = "CREATEDATE";
                    strArray[1] = momentDialogue.figureId;
                } else if (!TextUtils.isEmpty(momentDialogue.localGroupId)) {
                    column[1] = "LOCAL_GROUP_ID";
                    strArray[1] = momentDialogue.localGroupId;
                }
            } else {
                column = new String[1];
                column[0] = "XLID";
                strArray = new String[1];
                strArray[0] = PersonSharePreference.getUserID() + "";
            }

            cursor = dbUtil.query(DBSQLUtil.TABLES_NAME[6], column, strArray, 0, 0);
            List<MomentDialogue> momentDialogueList = new ArrayList<MomentDialogue>();
            while (cursor.moveToNext()) {


                momentDialogueList.add(momentDialogue);
            }
            return momentDialogueList;
        } catch (Exception e) {
            LogCatLog.e(TAG, "查询临时对话联系人和群组数据失败 ，错误信息 " + e.getMessage());
        } finally {
            dbUtil.colse(cursor);
        }
        return null;
    }

    public String getChatIdForCursor(Cursor cursor) {
        if (cursor == null) {
            return "";
        } else {
            boolean isGroup =isGroup(cursor)  ;
            String chatId = !isGroup ? cursor.getString(cursor.getColumnIndex("CONTACT_ID")) : cursor.getString(cursor
                    .getColumnIndex("LOCAL_GROUP_ID"));
            return chatId;
        }
    }
    public boolean isGroup(Cursor cursor) {
        return !cursor.isNull(cursor.getColumnIndex("LOCAL_GROUP_ID"))  ;
    }

}
