package com.xianglin.fellowvillager.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.ContactMessageBean;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.MomentDialogue;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;
import com.xianglin.fellowvillager.app.model.RecentMessageBean;
import com.xianglin.fellowvillager.app.utils.DateUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.db.BaseDBHelper;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.db.DBUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * 消息数据处理
 * Javadoc
 *
 * @author james
 * @version 0.2, 彭阳 by  2015-11-23
 */
public class MessageDBHandler extends BaseBDHandler {
    private MomentDialogueDBHandler mMomentDialogueDBHandler;
    private CardDBHandler mCardDBHandler;
    private static final String TAG = "MessageDBHandler";

    public static Uri SYNC_SIGNAL_URI = Uri
            .withAppendedPath(BASE_URI, "RECENT_MESSAGE_SYNC_SIGNAL_URI"); //同步指定用户

    public MessageDBHandler(Context mContext) {
        super(mContext);

        mMomentDialogueDBHandler = new MomentDialogueDBHandler(mContext);
        mCardDBHandler = new CardDBHandler();
    }

    /**
     * 添加群消息数据
     *
     * @param message
     * @return
     */
    private synchronized long addGroup(final MessageBean message) {

        if (message.msgType != MessageChatAdapter.SYS && TextUtils.isEmpty(message.xlgroupmemberid)) {
            throw new RuntimeException("添加数据失败addGroup()，群消息时MessageBean中的memberid不能为null");
        }

        //群不能填写contactId,
        final ContactMessageBean gmb = new ContactMessageBean.Builder()
                .xlID(PersonSharePreference.getUserID() + "")
                .xlgroupID(message.xlID)
                .msgLocalKey(message.msgLocalKey)
                .msgKey(message.msgKey)
                .figureId(message.figureId)
                .xlUserID(null)//群消息时tochat用户id为0
                .contactId(null)
                .xlgroupmemberid(message.xlgroupmemberid)
                .localgroupId(GroupDBHandler.getGroupId(message.xlID, message.figureId))
                .build();
        //同时加入两张表

        final long[] counts = new long[1];
        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {

                counts[0] = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[3], null, MessageDBHandler
                        .getCMBContentValues(gmb), SQLiteDatabase.CONFLICT_IGNORE);
                counts[0] = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[5], null, MessageDBHandler.getContentValues
                        (message), SQLiteDatabase.CONFLICT_IGNORE);
                //如果插入卡片,红包,,名片
                addMsgTypeBean(db, message);

                return counts[0];
            }

            @Override
            public void endTransaction(long count) {
                counts[0] = count;
            }
        });

        return counts[0];

    }

    /**
     * 添加收到的消息到数据库
     *
     * @param message                消息体
     * @param isUpdateMomentDialogue 是否添加到临时联系人
     * @return
     */
    public synchronized long addReceivedMsg(MessageBean message,
                                            boolean
                                                    isUpdateMomentDialogue) {
        return add(message, isUpdateMomentDialogue);
    }


    /**
     * 添加发送的消息到数据库
     *
     * @param message                消息体
     * @param isUpdateMomentDialogue 是否添加到临时联系人
     * @return
     */
    public synchronized long addSendMsg(
            MessageBean message,
            boolean isUpdateMomentDialogue
    ) {

        return add(
                message,
                isUpdateMomentDialogue
        );
    }

    /**
     * 添加消息数据  添加到最近联系人表
     *
     * @param message                消息体
     * @param isUpdateMomentDialogue 是否添加到临时联系人
     * @return
     */
    private synchronized long add(
            final MessageBean message,
            boolean isUpdateMomentDialogue
    ) {

        if (message.getChatType() == null) {
            throw new RuntimeException("ChatType is null");
        }

        final long[] count = {-1L};


        // 判断附近联系人是不是你的好友 如果是就添加到最近联系人中
        //添加到最近联系人表(群,个人)
        if (MessageBean.ChatType.GroupChat.getChatType() == message.getChatType().getChatType() ||
                isUpdateMomentDialogue) {// 群组

            MomentDialogue momentDialogue = new MomentDialogue
                    .Builder()
                    .xlID(PersonSharePreference.getUserID() + "")
                    .xlLastMsgData(message.msgDate)
                    .setToChatType(message.getChatType().getChatType(), message)

                    .build();

            count[0] = mMomentDialogueDBHandler.add(momentDialogue);

        }


        if (MessageBean.ChatType.GroupChat.getChatType() == message.getChatType().getChatType()) {// 群组
            //发送给群的消息
            count[0] = addGroup(message);
        } else {
            //单聊
            final ContactMessageBean contactQuery = new ContactMessageBean.Builder()
                    .xlID(PersonSharePreference.getUserID() + "")
                    .xlUserID(message.xlID)
                    .figureId(message.figureId)
                    .figureUsersId(message.figureUsersId)
                    .msgLocalKey(message.msgLocalKey)
                    .msgKey(message.msgKey)
                    .contactId(ContactDBHandler.getContactId(message.figureUsersId, message.figureId))
                    .localgroupId(null)
                    .xlgroupID(null)//单人消息时tochat

                    .build();
            //同时加入两张表

            dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
                @Override
                public long beginTransaction(SQLiteDatabase db) {

                    count[0] = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[3], null, MessageDBHandler
                            .getCMBContentValues(contactQuery), SQLiteDatabase.CONFLICT_IGNORE);
                    count[0] = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[5], null, MessageDBHandler.getContentValues
                            (message), SQLiteDatabase.CONFLICT_IGNORE);
                    //如果插入卡片,红包,,名片
                    addMsgTypeBean(db, message);

                    return count[0];
                }

                @Override
                public void endTransaction(long count) {

                }
            });
        }

        if (count[0] > 0) {
            //// TODO: 2016/2/25  需要重构 
            //发送数据库变化的信号通知loader重新加载
            XLApplication.getInstance().getContentResolver().notifyChange(
                    Uri.withAppendedPath(
                            MessageDBHandler.SYNC_SIGNAL_URI,
                            message.figureUsersId
                    ),
                    null
            );
        }


        return count[0];
    }


    /**
     * 插入名片,商品,红包 到db
     */
    private long addMsgTypeBean(SQLiteDatabase db, MessageBean messageBean) {

        long count = -1;
                  /*
                 不拆开
                //设置当前用户id
                 nameCardBean.setUserId(PersonSharePreference.getUserID()+"");
                 //设置msgkey
                nameCardBean.setMsg_key(message.msgKey);
                //设置聊天对象id
                nameCardBean.setToChatId(toChatId);
                //设置创建时间
                nameCardBean.setMsgDate(message.msgDate);*/

        switch (messageBean.msgType) {
            case MessageChatAdapter.IDCARD://名片
                NameCardBean nameCardBean = JSON.parseObject(messageBean.msgContent, NameCardBean.class);

                nameCardBean.setMsg_key(messageBean.msgKey);
                count = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[10], null, CardDBHandler
                        .getNameCardContentValues(nameCardBean), SQLiteDatabase.CONFLICT_IGNORE);

                break;
            case MessageChatAdapter.WEBSHOPPING://卡片
                GoodsDetailBean goodsDetailBean = JSON.parseObject(messageBean.msgContent, GoodsDetailBean
                        .class);
                goodsDetailBean.setMsg_key(messageBean.msgKey);
                goodsDetailBean.setXlid(PersonSharePreference.getUserID() + "");
                count = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[9], null, CardDBHandler
                        .getGoodContentValues(goodsDetailBean), SQLiteDatabase.CONFLICT_IGNORE);

                break;
            case MessageChatAdapter.NEWSCARD://新闻卡片
                NewsCard newsCard = JSON.parseObject(messageBean.msgContent, NewsCard
                        .class);
                newsCard.setMsg_key(messageBean.msgKey);
                newsCard.setXlid(PersonSharePreference.getUserID() + "");
                count = db.insertWithOnConflict(DBSQLUtil.TABLES_NAME[11], null, CardDBHandler
                        .getNewsCardContentValues(newsCard), SQLiteDatabase.CONFLICT_IGNORE);

                break;
        }

        //        if (count > 0 && messageBean.msgStatus != BorrowConstants.MSGSTATUS_UNREAD) {
        //            XLApplication.getInstance().getContentResolver().notifyChange(
        //                    CardDBHandler.SYNC_SIGNAL_URI,
        //                    null
        //            );
        //        }
        return count;
    }


    /**
     * 删除一条消息数据 需要mContactMessageDBHandler先查询一下获取contactMessageBean再调用此方法
     *
     * @param message
     * @return
     */
    public synchronized void del(MessageBean message) {
        if (message == null) throw new NullPointerException("删除消息数据失败 ，message 不能为null");

        final String[] tableNames = new String[]{DBSQLUtil.TABLES_NAME[3], DBSQLUtil.TABLES_NAME[5]};

        final String[] whereClauses = new String[]{"XLID = ? AND MSG_KEY = ?", "  MSG_KEY = ?"};

        final String[][] whereArgs = new String[][]{new String[]{
                PersonSharePreference.getUserID() + "", message.msgKey}, new String[]{
                message.msgKey}};

        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {
                db.delete(tableNames[0], whereClauses[0], whereArgs[0]);
                db.delete(tableNames[1], whereClauses[1], whereArgs[1]);
                return 0;

            }

            @Override
            public void endTransaction(long count) {

            }
        });

    }

    /**
     * 确保在线程和事物中执行 和其他语句调用时注意字段依赖
     * 删除两个用户之间全部的数据
     */
    public synchronized static void delToXLUserAllMsg(SQLiteDatabase db, String xlUserID) {

        String xlid = PersonSharePreference.getUserID() + "";
        String[] args = new String[]{xlid, xlUserID};

        String sql = "DELETE FROM msg_table \n" +
                "WHERE \n" +
                "  msg_table.MSG_KEY IN \n" +
                "    ( \n" +
                "      SELECT \n" +
                "        a.MSG_KEY \n" +
                "      FROM \n" +
                "        contact_msg_table a \n" +
                "      WHERE \n" +
                "        a.XLID = ? AND a.XLUSERID = ? \n" +
                "    ) ;";

        db.execSQL(sql, args);//删除消息表

        db.delete(DBSQLUtil.TABLES_NAME[3], " XLID = ? AND XLUSERID = ? ", args
        );       //删除消息记录表


    }


    /**
     * 消息查询
     *
     * @param toCharID groupid or CONTACT_ID
     * @param chatType BorrowConstants.CHATTYPE_SINGLE 查询和某一位联系人之间全部的聊天消息 CHATTYPE_GROUP查询群的消息
     * @return
     */
    public Cursor queryChatHistory(String toCharID, int chatType, long pageIndex) {

        long index = pageIndex * 30;

        if (chatType == BorrowConstants.CHATTYPE_GROUP) {

            return queryChatHistoryGroup(toCharID, index);
        }
        String strSql = "  select g.* FROM  ( SELECT \n" +
                "  c.xlid , \n" +
                "  c.XLUSERID , \n" +
                "  a.* ," +
                "  name.*, " +
                "  news.*, " +
                "  goods.* " +
                "FROM \n" +
                "  msg_table a LEFT JOIN \n" +
                "  CONTACT_MSG_TABLE c ON a.MSG_KEY = c.msg_key  LEFT JOIN  \n" +
                "  NAME_CARD_TABLE name ON name.N_MSG_KEY = c.msg_key LEFT JOIN" +
                "  GOODS_DETAIL_TABLE goods ON goods.G_MSG_KEY = c.msg_key LEFT JOIN " +
                "  NEWS_CARD_TABLE news ON news.NS_MSG_KEY = c.msg_key  " +
                "WHERE \n" +
                "  c.xlid = ? AND c.CONTACT_ID = ? \n" +
                "ORDER BY \n" +
                " a.MSG_CREATEDATE  desc Limit ?  ) g ORDER BY g.MSG_CREATEDATE ;";

        //查询和某一位联系人之间全部的聊天消息

        return dbUtil.query(strSql, new String[]{PersonSharePreference.getUserID() + "", toCharID, index + ""});
    }

    /**
     * @param toCharID  群id 或联系人id
     * @param chatType  聊天类型.群聊 单聊
     * @param pageIndex 当前页数
     * @param pageSize  分页数
     * @return
     */
    public Cursor queryChatCardHistory(String toCharID, int chatType, long pageIndex, long pageSize) {
        //查询和某一位联系人之间全部的聊天消息

        return null;
    }

    /**
     * 查询群全部消息
     *
     * @param local_group_id
     * @return
     */
    private Cursor queryChatHistoryGroup(String local_group_id, long index) {

        String strSql = " select   g.* FROM  ( SELECT \n" +
                "  a.*, \n" +
                "  b.XLUSERID , \n" +
                "  c.* ," +
                "  name.*, " +
                "  news.*, " +
                "  goods.* " +
                "FROM \n" +
                "  msg_table a LEFT JOIN \n" +
                "  contact_msg_table b ON a.MSG_KEY = b.MSG_KEY LEFT JOIN \n" +
                "  group_member_table c ON a.XLGROUPMEMBERID = c.MEMBERID  LEFT JOIN" +
                "  NAME_CARD_TABLE name ON name.N_MSG_KEY = b.msg_key LEFT JOIN" +
                "  GOODS_DETAIL_TABLE goods ON goods.G_MSG_KEY = b.msg_key  LEFT JOIN " +
                "  NEWS_CARD_TABLE news ON news.NS_MSG_KEY = b.msg_key  " +
                "WHERE \n" +
                "  b.XLID = ? AND b.LOCAL_GROUP_ID = ? \n" +
                "ORDER BY \n" +
                "    a.MSG_CREATEDATE desc Limit ?  ) g ORDER BY g.MSG_CREATEDATE   \n" +
                "   ;";

        return dbUtil.query(strSql, new String[]{PersonSharePreference.getUserID() + "", local_group_id, index + ""});
    }


    /**
     * 根据消息状态查询消息列表
     * BorrowConstants类中常量
     * GSTATUS_SEND =-1; //发送中
     * GSTATUS_OK =0; //发送成功
     * GSTATUS_FAIL =1; //发送失败
     * GSTATUS_READ = 2; //已读
     * GSTATUS_UNREAD =3; //未读
     * GSTATUS_RECEIVE =4; //接收中
     *
     * @return
     */
    public List<MessageBean> queryMsgWithState(int... state) {


/*         StringBuffer stringBuffer=new StringBuffer();
        for (int i = 0; i < state.length; i++) {

            if (i == 0) {
                stringBuffer.append(" msg_status " + " = ? ");
            } else {
                stringBuffer.append("or " + "msg_status" + " = ? ");
            }
        }*/


        Cursor cursor = null;
        ArrayList<MessageBean> arrayList = new ArrayList<MessageBean>();
        try {
            String sql = "SELECT \n" +
                    "  * \n" +
                    "FROM \n" +
                    "  contact_msg_table a LEFT JOIN \n" +
                    "  msg_table b ON a.MSG_KEY = b.msg_key \n" +
                    "WHERE \n" +
                    "  msg_status = ?  or   msg_status = ?  AND a.XLID = ? ;";
            String str = PersonSharePreference.getUserID() + "";

            cursor = dbUtil.query(sql, new String[]{state[0] + "", state[1] + "", str});

            MessageCursor messageCursor = new MessageCursor(cursor);

            while (messageCursor != null && messageCursor.moveToNext()) {
                MessageBean mb = messageCursor.getOldMessage();
                // GroupMember groupMember=  mMemberHashMap.get(mb.xlgroupmemberid);
                arrayList.add(mb);
            }
        } catch (Exception e) {
            LogCatLog.d(TAG, "当前聊天记录:" + e.getMessage());
        } finally {
            dbUtil.colse(cursor);
        }
        return arrayList;
    }

    public void updateMsgState(String msgkey, ContentValues contentValues) {
        dbUtil.update(DBSQLUtil.TABLES_NAME[5], contentValues, "MSG_KEY = ?", new String[]{msgkey});
    }

    /**
     * 把发送中的标记为发送失败
     */
    public synchronized void updateMsgState() {
        dbUtil.execSQL("update msg_table set MSG_STATUS = ? where  MSG_STATUS = ?",
                new String[]{BorrowConstants.MSGSTATUS_FAIL + "", BorrowConstants.MSGSTATUS_SEND + ""});
        //把接收中的标记为接收失败
        dbUtil.execSQL("update msg_table set MSG_STATUS = ? where  MSG_STATUS = ?",
                new String[]{BorrowConstants.MSGSTATUS_RECEIVE_FAIL + "", BorrowConstants.MSGSTATUS_INPROGRESS + ""});
    }

    public synchronized int updatePrivateMsgTime(MessageBean messageBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("DESTROYMESSAGEDATE", messageBean.currentlifetime);
        return dbUtil.update(DBSQLUtil.TABLES_NAME[5],
                contentValues, "MSG_KEY = ?", new String[]{messageBean.msgKey});
    }

    /**
     * 获取发送的状态
     *
     * @return
     */
    public List<MessageBean> querySendingMsg() {
        return queryMsgWithState(BorrowConstants.MSGSTATUS_SEND, BorrowConstants.MSGSTATUS_FAIL);
    }

    /**
     * 不需的要就传null
     * 全部就都留null
     *
     * @return
     */
    public String getRecentMsg() {

        Cursor cursor = null;
        String sql = "";
        String[] args = null;
        try {

            sql = "SELECT \n" +
                    "  max( a.msg_key ) m \n" +
                    "FROM \n" +
                    "  msg_table a LEFT JOIN \n" +
                    "  contact_msg_table b ON a.msg_key = b.msg_key \n" +
                    "WHERE \n" +
                    "  b.XLID = ? AND msg_status = 0 ;";
            args = new String[]{PersonSharePreference.getUserID() + ""};

            cursor = dbUtil.query(sql, args);
            if (cursor != null && cursor.moveToNext()) {
                return cursor.getString(cursor.getColumnIndex("m"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            dbUtil.colse(cursor);

        }

        return null;

    }

    /**
     * 自动把未读信息标记为已读
     *
     * @param toChatId CONTACT_ID 或者 groupid
     */
    public synchronized void autoReadMsg(String toChatId) {


        String sql = "UPDATE msg_table \n" +
                "SET \n" +
                "  msg_status = 2 \n" +
                "WHERE \n" +
                "  MSG_KEY IN \n" +
                "    ( \n" +
                "      SELECT \n" +
                "        b.MSG_KEY \n" +
                "      FROM \n" +
                "        CONTACT_MSG_TABLE a LEFT JOIN \n" +
                "        MSG_TABLE b ON a.MSG_KEY = b.MSG_KEY \n" +
                "      WHERE \n" +
                "        a.XLID = ? AND  ( a.CONTACT_ID = ? OR a.LOCAL_GROUP_ID = ? )  AND b.MSG_STATUS = 3 \n" +
                "    )  ";
        String str = PersonSharePreference.getUserID() + "";

        dbUtil.execSQL(sql, new String[]{str, toChatId, toChatId});

        //发送数据库变化的信号通知loader重新加载
        XLApplication.getInstance().getContentResolver().notifyChange(MessageDBHandler
                .SYNC_SIGNAL_URI, null);
    }


    /**
     * 语音为播放
     *
     * @param msgKey
     */
    public synchronized void updateIsPlayed(String msgKey) {
        dbUtil.execSQL("update msg_table set ISPLAYED = 1 where msg_key = ? ",
                new String[]{msgKey});
    }

    /**
     * 表现该消息为 当前消息(listview中显示在中间)
     *
     * @param
     */
    public synchronized void updateMsgCurrent(final String newmsgKey, final String oldmsgKey) {

        dbUtil.execSQLWithTransaction(new DBUtil.CallBack() {
            @Override
            public long beginTransaction(SQLiteDatabase db) {
                LogCatLog.d(TAG, "在聊天界面自动定位消息位置newmsgKey:" + newmsgKey + " oldmsgKey:" + oldmsgKey);
                dbUtil.execSQL("update msg_table set MSG_CURRENT = 0 where msg_key = ?  ",
                        new String[]{oldmsgKey});
                dbUtil.execSQL("update msg_table set MSG_CURRENT = 1 where msg_key = ? ",
                        new String[]{newmsgKey});

                return 0;
            }

            @Override
            public void endTransaction(long count) {

            }
        });

    }



    /**
     * 获取 contentvalues
     *
     * @param message
     * @return
     */
    public static ContentValues getContentValues(MessageBean message) {
        ContentValues contentValues = new ContentValues();

        String time= System.currentTimeMillis() + "";
        if(!TextUtils.isEmpty(message.msgCreatedate)){
            time=message.msgCreatedate;
        }

        contentValues.put("MSG_KEY", message.msgKey);
        contentValues.put("MSG_TYPE", message.msgType);
        contentValues.put("MSG_CONTENT", message.msgContent);
        contentValues.put("MSG_STATUS", message.msgStatus);
        contentValues.put("MSG_DATE", message.msgDate);
        contentValues.put("IMAGESIZE", message.imageSize);
        contentValues.put("ISPLAYED", message.isplayed);
        contentValues.put("RECORDLENGTH", message.recordlength);
        contentValues.put("THUMBNAIL", message.thumbnail);
        contentValues.put("XLGROUPMEMBERID", message.xlgroupmemberid);
        contentValues.put("MSG_CURRENT", message.msgcurrent);
        contentValues.put("NOTICE_TYPE", message.noticeType);
        contentValues.put("MSG_LOCAL_KEY", message.msgLocalKey);
        contentValues.put("MSGDIR", message.direct.ordinal());
        contentValues.put("MSG_CREATEDATE", time);
        contentValues.put("OPEN_LAST_TIME", System.currentTimeMillis() + "");
        if (message.lifetime > 0) {// 大于0 就是私密消息
            contentValues.put("ISPRIVATE", 1);// 是私密消息
            contentValues.put("PRIVATEDATE", message.msgDate);// 私密消息时间
            contentValues.put("DESTROYMESSAGEDATE", message.lifetime);// 私密消息销毁时间
        }
        return contentValues;
    }

    /**
     * 查询和某个联系人最近聊过天的角色集合
     *
     * @param contactId
     * @return
     */
    public List<String> queryRecentMsgToFigure(String contactId) {

        ArrayList<String> list = new ArrayList<String>();

        Cursor cursor = null;
        String sql = "";
        String[] args = null;
        try {
            sql = " SELECT  " +
                    "  FIGURE_ID  " +
                    "FROM  " +
                    "  CONTACT_MSG_TABLE c LEFT JOIN  " +
                    "  msg_table m ON c.msg_key = m.msg_key  " +
                    "WHERE  " +
                    "  contact_id = ?  " +
                    "GROUP BY  " +
                    "  figure_id  " +
                    "ORDER BY " +
                    "  m.MSG_CREATEDATE Desc ;";
            args = new String[]{contactId};

            cursor = dbUtil.query(sql, args);

            if (!cursor.moveToFirst()) {
                cursor.close();
            } else {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("FIGURE_ID"));
                    list.add(id);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.colse(cursor);
        }

        return list;
    }


    /**
     * 标记已发消息为发送 的各种 状态
     *
     * @param bean 发送成功时用服务器返回的key替换本地
     * @return
     */
    public synchronized static boolean sendMessageResult(MessageBean bean) {

        String[] table = new String[]{DBSQLUtil.TABLES_NAME[3], DBSQLUtil.TABLES_NAME[5], DBSQLUtil.TABLES_NAME[9],
                DBSQLUtil.TABLES_NAME[10], DBSQLUtil.TABLES_NAME[11]};

        ContentValues contentValues3 = new ContentValues();
        contentValues3.put("MSG_KEY", bean.msgKey);

        //重新标记消息状态和时间,以及fileid
        ContentValues contentValues5 = new ContentValues();
        contentValues5.put("MSG_KEY", bean.msgKey);
        contentValues5.put("MSG_STATUS", bean.msgStatus + "");
        if (bean.msgStatus != 1) {
            contentValues5.put("MSG_DATE", Utils.timeStamp2Date(bean.msgDate, "yyyy-MM-dd HH:mm"));
        }
        // contentValues5.put("FILE_ID", bean.file_id);

        ContentValues contentValues9 = new ContentValues();
        contentValues9.put("G_MSG_KEY", bean.msgKey);

        ContentValues contentValues10 = new ContentValues();
        contentValues10.put("N_MSG_KEY", bean.msgKey);

        ContentValues contentValues11 = new ContentValues();
        contentValues11.put("NS_MSG_KEY", bean.msgKey);



/*        if(bean.msgType== MessageChatAdapter.FILE||bean.msgType== MessageChatAdapter.IMAGE||bean.msgType==
                MessageChatAdapter.VOICE||bean.msgType== MessageChatAdapter.VIDEO){
            //非文本消息 都需要保存fileid到MSG_CONTENT
            contentValues5.put("MSG_CONTENT", bean.file_id);
        }*/

        ContentValues[] values = new ContentValues[]{contentValues3, contentValues5, contentValues9, contentValues10,
                contentValues11};

        String where3 = " XLID = ? AND MSG_KEY = ? AND MSG_LOCAL_KEY = ?";
        String where5 = "  MSG_KEY = ?";
        String where9 = "  G_MSG_KEY = ?";
        String where10 = "  N_MSG_KEY = ?";
        String where11 = "  NS_MSG_KEY = ?";
        String whereClause[] = new String[]{where3, where5, where9, where10, where11};

        String xlid = PersonSharePreference.getUserID() + "";
        String[] args3 = new String[]{xlid, bean.msgLocalKey, bean.msgLocalKey};
        String[] args5 = new String[]{bean.msgLocalKey};
        String[] args9 = new String[]{bean.msgLocalKey};
        String[] args10 = new String[]{bean.msgLocalKey};
        String[] args11 = new String[]{bean.msgLocalKey};
        String[][] whereArgs = new String[][]{args3, args5, args9, args10, args11};

        long count = DBUtil.updatas(table, values, whereClause, whereArgs);

        //卡片不会同时更新 为1
        if (count >= 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取 contentvalues
     *
     * @param cmb ContactMessageBean
     * @return
     */
    public static ContentValues getCMBContentValues(ContactMessageBean cmb) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("XLID", cmb.xlID);
        contentValues.put("XLUSERID", cmb.xlUserID);
        contentValues.put("MSG_KEY", cmb.msgKey);
        contentValues.put("MSG_LOCAL_KEY", cmb.msgLocalKey);
        contentValues.put("XLGROUPID", cmb.xlgroupID);
        contentValues.put("LOCAL_GROUP_ID", cmb.localgroupId);
        contentValues.put("XLGROUPMEMBERID", cmb.xlgroupmemberid);
        contentValues.put("FIGURE_USERSID", cmb.figureUsersId);
        contentValues.put("FIGURE_ID", cmb.figureId);
        contentValues.put("CONTACT_ID", cmb.contactId);


        return contentValues;
    }

    /**
     * 加载全部会话
     *
     * @param msgLength 加载多少条消息到内存
     * @return
     */
    public Hashtable<String, XLConversation> queryConversation(long msgLength) {

        Hashtable<String, XLConversation> hashtable = new Hashtable();
        MessageBean messageBean = null;

        try {
            SQLiteDatabase db = BaseDBHelper.getDb();

            //需要考虑卡片信息 得连表查询

            String singleChatSql = "SELECT \n" +
                    "  c.CONTACT_ID , \n" +
                    "  c.FIGURE_USERSID , \n" +
                    "  c.FIGURE_ID , \n" +
                    "  c.xlid , \n" +
                    "  c.XLUSERID , \n" +
                    "  a.*, \n" +
                    "  name.*, \n" +
                    "  news.*, \n" +
                    "  goods.*\n" +
                    "FROM \n" +
                    "  msg_table a LEFT JOIN \n" +
                    "  CONTACT_MSG_TABLE c ON a.MSG_KEY = c.msg_key LEFT JOIN \n" +
                    "  NAME_CARD_TABLE name ON name.N_MSG_KEY = c.msg_key LEFT JOIN \n" +
                    "  GOODS_DETAIL_TABLE goods ON goods.G_MSG_KEY = c.msg_key LEFT JOIN \n" +
                    "  NEWS_CARD_TABLE news ON news.NS_MSG_KEY = c.msg_key \n" +
                    "WHERE \n" +
                    "  LOCAL_GROUP_ID ISNULL \n" +
                    "ORDER BY \n" +
                    "  CONTACT_ID , \n" +
                    "  a.MSG_CREATEDATE DESC ;";

            String groupChatSql = "SELECT \n" +
                    "  a.*, \n" +
                    "  b.LOCAL_GROUP_ID, \n" +
                    "  b.XLGROUPID, \n" +
                    "  b.FIGURE_USERSID , \n" +
                    "  b.FIGURE_ID , \n" +
                    "  b.XLUSERID , \n" +
                    "  c.*, \n" +
                    "  name.*, \n" +
                    "  news.*, \n" +
                    "  goods.*\n" +
                    "FROM \n" +
                    "  msg_table a LEFT JOIN \n" +
                    "  contact_msg_table b ON a.MSG_KEY = b.MSG_KEY LEFT JOIN \n" +
                    "  group_member_table c ON a.XLGROUPMEMBERID = c.MEMBERID LEFT JOIN \n" +
                    "  NAME_CARD_TABLE name ON name.N_MSG_KEY = b.msg_key LEFT JOIN \n" +
                    "  GOODS_DETAIL_TABLE goods ON goods.G_MSG_KEY = b.msg_key LEFT JOIN \n" +
                    "  NEWS_CARD_TABLE news ON news.NS_MSG_KEY = b.msg_key \n" +
                    "WHERE \n" +
                    "  CONTACT_ID ISNULL \n" +
                    "ORDER BY \n" +
                    "  LOCAL_GROUP_ID , \n" +
                    "  a.MSG_CREATEDATE DESC ;";
            String[] sqls = new String[]{singleChatSql, groupChatSql};

            for (int i = 0; i < sqls.length; ++i) {

                String sql = sqls[i];

                Cursor cursor = db.rawQuery(sql, null);
                MessageDBHandler.MessageCursor mMessageCursor = new MessageDBHandler.MessageCursor(cursor);
                if (!mMessageCursor.moveToFirst()) {
                    mMessageCursor.close();
                } else {
                    LinkedList<MessageBean> linkedList = null;
                    String ToChatId = null;
                    long msgCount = 0L;

                    int unreadMsgCount = 0;

                    long msgSendCount = 0L;

                    long msgReceiveCount = 0L;

                    boolean isGroup = false;
                    XLConversation.ConversationType conversationType = XLConversation.ConversationType.Chat;

                    do {
                        String chatID = this.getChatIdForCursor(mMessageCursor);

                        if (ToChatId != null && ToChatId.equals(chatID)) {

                            //if (linkedList.size() < msgLength) {
                            messageBean = mMessageCursor.getMessage(isGroup ? BorrowConstants.CHATTYPE_GROUP : BorrowConstants.CHATTYPE_SINGLE);

                            if(!getMessageIsExpired(messageBean.lifetime,messageBean.privateDate)){
                                linkedList.add(messageBean);
                            }

                            //}
                            ++msgCount;

                            if (messageBean.msgStatus == BorrowConstants.MSGSTATUS_UNREAD) {
                                ++unreadMsgCount;
                            }
                            if (messageBean.direct == MessageBean.Direct.SEND) {
                                ++msgSendCount;
                            }
                            if (messageBean.direct == MessageBean.Direct.RECEIVE) {
                                ++msgReceiveCount;
                            }

                        } else if (ToChatId == null || !ToChatId.equals(chatID)) {
                            if (ToChatId != null) {
                                Collections.reverse(linkedList);
                                XLConversation xlConversation = new XLConversation(ToChatId, linkedList,
                                        conversationType,
                                        msgCount, msgSendCount, msgReceiveCount, unreadMsgCount);
                                hashtable.put(ToChatId, xlConversation);
                            }
                            isGroup = mMessageCursor.getColumnIndex("LOCAL_GROUP_ID") > 0;
                            MessageBean message = mMessageCursor.getMessage(isGroup ? BorrowConstants.CHATTYPE_GROUP : BorrowConstants.CHATTYPE_SINGLE);



                            message.chatType = isGroup ? MessageBean.ChatType.GroupChat : MessageBean.ChatType.Chat;
                            linkedList = new LinkedList<MessageBean>();
                            if(!getMessageIsExpired(message.lifetime,message.privateDate)){
                                linkedList.add(message);
                            }


                            ToChatId = chatID;

                            ++msgCount;

                            if (message.msgStatus == BorrowConstants.MSGSTATUS_UNREAD) {
                                ++unreadMsgCount;
                            }
                            if (message.direct == MessageBean.Direct.SEND) {
                                ++msgSendCount;
                            }
                            if (message.direct == MessageBean.Direct.RECEIVE) {
                                ++msgReceiveCount;
                            }


                            conversationType = XLConversation.msgType2ConversationType(message.getChatType());
                        }
                    } while (mMessageCursor.moveToNext());

                    if (ToChatId != null) {



                        Collections.reverse(linkedList);
                        XLConversation xlConversation = new XLConversation(ToChatId, linkedList, conversationType,
                                msgCount, msgSendCount, msgReceiveCount, unreadMsgCount);
                        hashtable.put(ToChatId, xlConversation);
                    }

                    mMessageCursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashtable;
    }

    private String getChatIdForCursor(MessageCursor cursor) {
        if (cursor == null) {
            return "";
        } else {
            boolean isGroup = cursor.getColumnIndex("LOCAL_GROUP_ID") > 0;
            String chatId = !isGroup ? cursor.getString(cursor.getColumnIndex("CONTACT_ID")) : cursor.getString(cursor
                    .getColumnIndex("LOCAL_GROUP_ID"));
            return chatId;
        }
    }



    /**
     * 通过Cursor当前的position获取聊天记录MessageBean
     */
    public static class MessageCursor extends CursorWrapper {
        public MessageCursor(Cursor c) {
            super(c);
        }

        public List<MessageBean> getMessageBeanList(int chatType) {

            List<MessageBean> messageBeanList = new ArrayList<MessageBean>();

            for (moveToFirst(); !isAfterLast(); moveToNext()) {
                messageBeanList.add(getMessage(chatType));
            }
            return messageBeanList;
        }

        public MessageBean getMessage(int chatType) {

            MessageBean.Builder builder = new MessageBean.Builder()
                    .msgKey(getString(getColumnIndex("MSG_KEY")))// 信息key
                    .msgType(getInt(getColumnIndex("MSG_TYPE"))) // 信息类型
                    .msgContent(getString(getColumnIndex("MSG_CONTENT")))// 信息内容
                    .msgStatus(getInt(getColumnIndex("MSG_STATUS")))// 信息状态
                    .msgDate(getString(getColumnIndex("MSG_DATE")))// 信息日期
                    .xlgroupmemberid(getString(getColumnIndex("XLGROUPMEMBERID")))// 信息日期
                    .recordlength(getString(getColumnIndex("RECORDLENGTH")))
                    .imageSize(getString(getColumnIndex("IMAGESIZE")))
                    .msgcurrent(getInt(getColumnIndex("MSG_CURRENT")))
                    .isplayed(getInt(getColumnIndex("ISPLAYED")))
                    .xlID(getString(getColumnIndex("XLUSERID")))
                    .noticeType(getInt(getColumnIndex("NOTICE_TYPE")))
                    .msgLocalKey(getString(getColumnIndex("MSG_LOCAL_KEY")))
                    .lasttime(getString(getColumnIndex("OPEN_LAST_TIME")))
                    .msgCreatedate(getString(getColumnIndex("MSG_CREATEDATE")))

                    .lifeTime(TextUtils.isEmpty(getString(getColumnIndex("DESTROYMESSAGEDATE")))?-1:getInt(getColumnIndex("DESTROYMESSAGEDATE")))// TODO: 16/3/30  当前是否为私密消息 life消息大于0 则为私密消息，否则不为私密消息

                    .thumbnail(getString(getColumnIndex("THUMBNAIL")));
            MessageBean messageBean = builder.build();


            if(getColumnIndex("FIGURE_ID")>0) {
                messageBean.figureId=getString(getColumnIndex("FIGURE_ID"));
            }

            if (chatType == BorrowConstants.CHATTYPE_GROUP) {
                //如果是群消息就在消息中附加群成员的信息
                builder.file_id(getString(getColumnIndex("FILE_ID")));//单聊不需要头像
                builder.xlName(getString(getColumnIndex("XLUSERNAME")));
                builder.xlImagePath(getString(getColumnIndex("XLIMAGE_PATH")));
                builder.xlID(getString(getColumnIndex("LOCAL_GROUP_ID")));
                messageBean.chatType = MessageBean.ChatType.GroupChat;
                if(getColumnIndex("XLGROUPID")>0) {
                    messageBean.xlID=getString(getColumnIndex("XLGROUPID"));
                }
            } else {
                messageBean.chatType = MessageBean.ChatType.Chat;
                messageBean.privateDate = getString(getColumnIndex("PRIVATEDATE"));
                messageBean.isExpired= getMessageIsExpired(messageBean.lifetime,   messageBean.privateDate);

                if(getColumnIndex("FIGURE_USERSID")>0) {
                    messageBean.figureUsersId=getString(getColumnIndex("FIGURE_USERSID"));
                }

            }

            messageBean.currentlifetime=messageBean.lifetime;



            int dir = getInt(getColumnIndex("MSGDIR"));
            if (dir == MessageBean.Direct.SEND.ordinal()) {
                messageBean.direct = MessageBean.Direct.SEND;
            } else {
                messageBean.direct = MessageBean.Direct.RECEIVE;
            }


            switch (messageBean.msgType) {
                case MessageChatAdapter.IDCARD://名片
                    NameCardBean nameCardBean = CardDBHandler.converCursorToNameCardBean(this);
                    messageBean.idCard = nameCardBean;

                    break;
                case MessageChatAdapter.WEBSHOPPING://卡片
                    GoodsDetailBean goodsDetailBean = CardDBHandler.converCursorToGoodsCardBean(this);
                    messageBean.goodsCard = goodsDetailBean;
                    break;
                case MessageChatAdapter.NEWSCARD://卡片
                    NewsCard newsCard = CardDBHandler.converCursorToNewsCardBean(this);
                    messageBean.newsCard = newsCard;
                    break;
            }

            return messageBean;

        }


        /**
         * 根据消息状态查询信息
         *
         * @return
         */
        private MessageBean getOldMessage() {


            String id = getString(getColumnIndex("CONTACT_ID"));
            if (TextUtils.isEmpty(id)) {
                id = getString(getColumnIndex("LOCAL_GROUP_ID"));
            }

            MessageBean.Builder builder = new MessageBean.Builder()
                    .msgKey(getString(getColumnIndex("MSG_KEY")))// 信息key
                    .msgLocalKey(getString(getColumnIndex("MSG_LOCAL_KEY")))
                    .msgType(getInt(getColumnIndex("MSG_TYPE"))) // 信息类型
                    .msgContent(getString(getColumnIndex("MSG_CONTENT")))// 信息内容
                    .msgStatus(getInt(getColumnIndex("MSG_STATUS")))// 信息状态
                    .msgDate(getString(getColumnIndex("MSG_DATE")))// 信息日期
                    .xlgroupmemberid(getString(getColumnIndex("XLGROUPMEMBERID")))// 信息日期
                    .recordlength(getString(getColumnIndex("RECORDLENGTH")))
                    .imageSize(getString(getColumnIndex("IMAGESIZE")))
                    .msgcurrent(getInt(getColumnIndex("MSG_CURRENT")))
                    .noticeType(getInt(getColumnIndex("NOTICE_TYPE")))
                    .isplayed(getInt(getColumnIndex("ISPLAYED")))
                    .msgCreatedate(getString(getColumnIndex("MSG_CREATEDATE")))
                    .lasttime(getString(getColumnIndex("OPEN_LAST_TIME")))
                    .lifeTime(isNull(getColumnIndex("DESTROYMESSAGEDATE"))?-1:getInt(getColumnIndex("DESTROYMESSAGEDATE")))// TODO: 16/3/30  当前是否为私密消息 life消息大于0 则为私密消息，否则不为私密消息
                    .xlID(id)
                    .thumbnail(getString(getColumnIndex("THUMBNAIL")));
            MessageBean messageBean = builder.build();
            int dir = getInt(getColumnIndex("MSGDIR"));
            if (dir == MessageBean.Direct.SEND.ordinal()) {
                messageBean.direct = MessageBean.Direct.SEND;
            } else {
                messageBean.direct = MessageBean.Direct.RECEIVE;
            }

            return messageBean;

        }
    }


    /**
     * 判断私密消息是否过期
     * @param lifetime  消息生存时间
     * @param privateDate 私密消息起始时间
     * @return
     */
    public static boolean getMessageIsExpired(int lifetime,String privateDate ) {
        boolean isExpired=false;
        if(lifetime<0){
            isExpired=false;
        }else if((lifetime==0)||(!TextUtils.isEmpty(privateDate)&&(DateUtil.dateDifference(privateDate,DateUtil.getCurrentTime())/(1000*60)>1))){
            //计秒到期或者30分钟到期都为true
            isExpired=true;
        }
        return  isExpired;
    }
    /**
     * 通过Cursor当前的position最近联系数据
     */
    public static class RecentMessageCursor extends CursorWrapper {

        public RecentMessageCursor(Cursor c) {
            super(c);
        }

        public RecentMessageBean getMessage() {

            RecentMessageBean bean = new RecentMessageBean();
            //-------------
            String xluserid = getString(getColumnIndex("XLUSERID"));

            String contact_id = getString(getColumnIndex("CONTACT_ID"));
            String figureId = getString(getColumnIndex("FIGURE_ID"));
            int msgStatus = getInt(getColumnIndex("MSG_STATUS"));

            String local_group_id = getString(getColumnIndex("LOCAL_GROUP_ID"));

            String xlremarks = getString(getColumnIndex("XLREMARKS"));
            String xlusername = getString(getColumnIndex("XLUSERNAME"));
            String xlgroupnikename = getString(getColumnIndex("XLGROUPNIKENAME"));

            String xlgroupimagepath = getString(getColumnIndex("XLGROUPIMAGEPATH"));
            String xlimage_path = getString(getColumnIndex("XLIMAGE_PATH"));
            String msg_type = getString(getColumnIndex("MSG_TYPE"));
            String file_id = getString(getColumnIndex("FILE_ID"));
            String isprivate = getString(getColumnIndex("ISPRIVATE"));
            int lifetime = getInt(getColumnIndex("DESTROYMESSAGEDATE"));
            String privateDate = getString(getColumnIndex("PRIVATEDATE"));

            String issend = getString(getColumnIndex("issend"));

/*            int dir =  getInt(getColumnIndex("MSGDIR"));
            if(dir == MessageBean.Direct.SEND.ordinal()) {
                messageBean.direct = MessageBean.Direct.SEND;
            } else {
                messageBean.direct = MessageBean.Direct.RECEIVE;
            }*/

            bean.lifetime=lifetime;

            bean.setExpired(getMessageIsExpired(bean.lifetime,privateDate));


            if(TextUtils.isEmpty(isprivate)||"0".equals(isprivate)){
                bean.setPrivate(false);
            }else if("1".equals(isprivate)){
                bean.setPrivate(true);
            }



            //-------判断是不是陌生人消息----
            int i = getColumnIndex("ISSTRANGER");
            boolean isstranger = false;
            if (i > 0) {
                isstranger = TextUtils.isEmpty(getString(i));
            } else {
                isstranger = false;
            }
            bean.setIsstranger(isstranger);
            //-----------end-----

            if (!TextUtils.isEmpty(xluserid)) {

                bean.setXlListType(BorrowConstants.CHATTYPE_SINGLE);
                //单聊
                bean.setXlListId(xluserid);
                //昵称或者备注
                if (!TextUtils.isEmpty(xlremarks)) {
                    bean.setXlListTitle(xlremarks);
                } else if (!TextUtils.isEmpty(xlusername)) {
                    bean.setXlListTitle(xlusername);
                }
                //头像
                bean.setXlImagePath(xlimage_path);

            } else if (!TextUtils.isEmpty(local_group_id)) {
                bean.setXlListType(BorrowConstants.CHATTYPE_GROUP);
                //群聊
                bean.setXlListId(local_group_id);
                //群名
                bean.setXlListTitle(xlgroupnikename);
                //头像
                bean.setXlImagePath(xlgroupimagepath);

            }

            bean.setXlLastMsg(getString(getColumnIndex("MSG_CONTENT")));
            bean.setXlLastTime(getString(getColumnIndex("MSG_DATE")));
            bean.setMsg_type(msg_type);
            bean.setFile_id(file_id);
            bean.setIssend(issend);
            bean.setFigureId(figureId);
            bean.setContactId(contact_id);
            bean.setMsgStatus(msgStatus);
            String s = getString(getColumnIndex("UNREAD_MSG_NUM"));
            bean.setXlMsgNum(TextUtils.isEmpty(s) ? "0" : s);

            return bean;

        }
    }

    /**
     * 清除消息内容
     *
     * @param messageBean
     * @return
     */
    public boolean cleanMessageContent(MessageBean messageBean) {
        synchronized (messageBean) {
            try {
                if (messageBean == null) throw new NullPointerException("清除消息内容的messagebean 为null");
                ContentValues contentValues = new ContentValues();
                contentValues.put("MSG_CONTENT", "");
                int count = dbUtil.update(DBSQLUtil.TABLES_NAME[5], contentValues, " MSG_KEY = ? ", new String[]{messageBean.msgKey});

                if (count > 0) {

                    boolean isRemove = false;
                    switch (messageBean.msgType) {
                        case MessageChatAdapter.IMAGE:
                            isRemove = true;
                            break;
                        case MessageChatAdapter.VIDEO:
                            isRemove = true;
                            break;
                        case MessageChatAdapter.VOICE:
                            isRemove = true;
                            break;
                        case MessageChatAdapter.FILE:
                            isRemove = true;
                            break;

                    }
                    if (isRemove) {
                        if (messageBean.msgContent != null) {
                            FileUtils.removeFile(messageBean.msgContent);
                            return true;
                        }
                    }

                } else {
                    LogCatLog.e(TAG, "清除消息失败");
                }

            } catch (Exception e) {
                LogCatLog.e(TAG, "清除消息失败，是失败消息" + e);
            }

            return false;
        }

    }



/*    *//**
     * 自动判断两个人是否聊过天,聊过着加入联系人列表(网络和本地)
     *
     * @param tocharid 聊天对象
     * @return
     *//*
    public long isAutoAddNewContacts(String tocharid) {
*//*        public static int MSGSTATUS_SEND =-1; //发送中
        public static int MSGSTATUS_OK =0; //发送成功
        public static int MSGSTATUS_FAIL =1; //发送失败
        public static int MSGSTATUS_READ = 2; //已读
        public static int MSGSTATUS_UNREAD =3; //未读*//*
        String sql = "SELECT \n" +
                "  a.XLID , \n" +
                "  a.CONTACT_ID , \n" +
                "     CASE b.MSGDIR  \n" +
                "    WHEN 1 THEN 1 \n" +
                "    WHEN 0 THEN 2 \n" +
                "  END issend , \n" +
                "  ( \n" +
                "    SELECT \n" +
                "      count( c.CONTACT_ID ) \n" +
                "    FROM \n" +
                "      contact_table c \n" +
                "    WHERE \n" +
                "      c.CONTACT_ID = ? AND c.XLID = ? AND c.ISCONTACT = 1 \n" +
                "  ) iscontact \n" +
                "FROM \n" +
                "  contact_msg_table a LEFT JOIN \n" +
                "  MSG_TABLE b ON a.MSG_KEY = b.msg_key \n" +
                "WHERE \n" +
                "  iscontact = 0 AND a.XLID = ? AND a.CONTACT_ID = ? and a.XLUSERID <> '1111'  \n" +
                "  group by  issend";

        //
        String str = PersonSharePreference.getUserID() + "";
        Cursor cursor = null;
        try {
            cursor = dbUtil.query(sql, new String[]{tocharid, str, str, tocharid});

            if (cursor != null) {

                return cursor.getCount();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }*/
}
