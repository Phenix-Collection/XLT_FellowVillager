/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 名片处理db工具类
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/11 13:56  XLXZ Exp $
 */
public class CardDBHandler extends BaseBDHandler {

    public CardDBHandler() {
        super(XLApplication.getInstance());
    }

    private static final String TAG = "CardDBHandler";

    public static Uri SYNC_SIGNAL_URI = Uri
            .withAppendedPath(BASE_URI, "RECENT_CARD_SYNC_SIGNAL_URI"); //同步指定用户

    /**
     * 保存最后一次打开的时间
     *
     * @param msgtype    卡片类型
     * @param msgkey      消息key
     * @return
     */
    public synchronized long saveCardLastOpenTime(int msgtype,String msgkey) {
        long count = -1l;
        ContentValues cv = new ContentValues();
        cv.put("OPEN_LAST_TIME", System.currentTimeMillis());
        count = dbUtil.update("msg_table", cv, "MSG_KEY = ?", new String[]{msgkey});
          switch (msgtype){

              case  MessageChatAdapter.IDCARD :
              {
                  ContentValues contentValues = new ContentValues();
                  contentValues.put("N_ISOPEN", 1);
                  count=count+ dbUtil.update("NAME_CARD_TABLE", contentValues, "N_MSG_KEY = ?", new String[]{msgkey});
                  break;
              }
              case  MessageChatAdapter.REDBUNDLE :
              {
    /*              ContentValues contentValues = new ContentValues();
                    count=count+dbUtil.update("GOODS_DETAIL_TABLE", contentValues, "MSG_KEY = ?", new String[]{msgkey});
*/                  break;
              }
              case  MessageChatAdapter.WEBSHOPPING :
              {
                  ContentValues contentValues = new ContentValues();
                  contentValues.put("G_ISOPEN", 1);
                  count=count+ dbUtil.update("GOODS_DETAIL_TABLE", contentValues, "G_MSG_KEY = ?", new String[]{msgkey});
                  break;
              }
              case  MessageChatAdapter.NEWSCARD :
              {
                  ContentValues contentValues = new ContentValues();
                  contentValues.put("NS_ISOPEN", 1);
                  count=count+ dbUtil.update("NEWS_CARD_TABLE", contentValues, "NS_MSG_KEY = ?", new String[]{msgkey});
                  break;
              }

          }


        if (count > 0) {
            //通知界面刷新顺序
            XLApplication.getInstance().getContentResolver().notifyChange(
                    CardDBHandler.SYNC_SIGNAL_URI,
                    null
            );
        } else {
            LogCatLog.e(TAG, "保存卡片最后一次打开时间出错");
        }

        return count;
    }


    /**
     * 插入名片
     *
     * @param ncb 名片
     * @return 插入结果数 小于0插入失败
     */
    public synchronized long addNameCard(NameCardBean ncb) {

        if (ncb == null) {
            LogCatLog.e(TAG, "CardDBHandler:add() NameCardBean is null");
            return -1;
        }
        return dbUtil.add(DBSQLUtil.TABLES_NAME[10], getNameCardContentValues(ncb));
    }


    /**
     * 插入商品
     *
     * @param gdb 名片
     * @return 插入结果数 小于0插入失败
     */
    public synchronized long addGoodsDetail(GoodsDetailBean gdb) {

        if (gdb == null) {
            LogCatLog.e(TAG, "CardDBHandler:add() GoodsDetailBean is null");
            return -1;
        }
        return dbUtil.add(DBSQLUtil.TABLES_NAME[10], getGoodContentValues(gdb));
    }

    /**
     * 卡片消息查询
     *
     * @param toCharID  groupid or xluserid
     * @param chatType  BorrowConstants.CHATTYPE_SINGLE 查询和某一位联系人之间全部的聊天消息 CHATTYPE_GROUP查询群的消息
     * @param pageIndex 当前页数 默认0
     * @param pageSize
     * @param isdesc    否是降序
     * @return
     */
    public Cursor queryChatCardHistory(String toCharID, int chatType, long pageIndex, long pageSize, boolean isdesc) {

        long pageCurrent = pageIndex * pageSize;

        if (chatType == BorrowConstants.CHATTYPE_GROUP) {

            return queryChatCardHistoryGroup(toCharID, pageCurrent, pageSize, isdesc);
        }

        String tochastr = null;
        String limitstr = null;

        ArrayList<String> args = new ArrayList<String>();

        args.add(PersonSharePreference.getUserID() + "");

        if (!TextUtils.isEmpty(toCharID)) {
            tochastr = "AND c.CONTACT_ID =?";

            args.add(toCharID);

            if (pageSize != 0) {
                limitstr = "Limit ?,?";
                args.add(pageCurrent + "");
                args.add(pageSize + "");

            } else {
                limitstr = "";
            }

        } else {
            tochastr = "";

            if (pageSize != 0) {
                limitstr = "Limit ?,?";
                args.add(pageCurrent + "");
                args.add(pageSize + "");

            } else {
                limitstr = "";

            }

        }

        String desc = isdesc ? "desc" : "";

        String strSql = "  select g.* FROM  ( SELECT \n" +
                "  c.xlid , \n" +
                "  c.CONTACT_ID , \n" +
                "  a.* ," +
                "  name.*, " +
                "  news.*, " +
                "  goods.*" +
                "FROM \n" +
                "  msg_table a LEFT JOIN \n" +
                "  CONTACT_MSG_TABLE c ON a.MSG_KEY = c.msg_key  LEFT JOIN  \n" +
                "  NAME_CARD_TABLE name ON name.N_MSG_KEY = c.msg_key LEFT JOIN" +
                "  GOODS_DETAIL_TABLE goods ON goods.G_MSG_KEY = c.msg_key LEFT JOIN " +
                "  NEWS_CARD_TABLE news ON news.NS_MSG_KEY = c.msg_key  " +
                "WHERE \n" +
                "  c.xlid = ?  " + tochastr + " and (msg_type = %s or  msg_type = %s or  msg_type = %s " +
                "or  msg_type = %s ) and ( MSGDIR = 0 or( N_ISOPEN = 1 or NS_ISOPEN = 1  or G_ISOPEN =1 ) ) GROUP BY  goods.G_GOODSID  ,name.N_XLID ,news.NS_NEWSID " +
                "ORDER BY \n" +
                " a.OPEN_LAST_TIME   ) g ORDER BY g.OPEN_LAST_TIME  " +
                desc + " " + limitstr +
                " ;";

        strSql = String.format(strSql, MessageChatAdapter.IDCARD, MessageChatAdapter.REDBUNDLE, MessageChatAdapter
                .WEBSHOPPING, MessageChatAdapter.NEWSCARD);

        //查询和某一位联系人之间全部的聊天消息
        String[] array = new String[args.size()];
        args.toArray(array);
        return dbUtil.query(strSql, array);
    }

    /**
     * 查询群卡片消息查询
     *
     * @param local_group_id
     * @return
     */
    private Cursor queryChatCardHistoryGroup(String local_group_id, long index, long pageSize, boolean isdesc) {


        String tochastr = null;
        String limitstr = null;

        ArrayList<String> args = new ArrayList<String>();

        args.add(PersonSharePreference.getUserID() + "");

        if (!TextUtils.isEmpty(local_group_id)) {
            tochastr = "AND b.LOCAL_GROUP_ID = ?";
            args.add(local_group_id);

            if (pageSize != 0) {
                limitstr = "Limit ?,?";
                args.add(index + "");
                args.add(pageSize + "");
            } else {
                limitstr = "";
            }

        } else {
            tochastr = "";

            if (pageSize != 0) {
                limitstr = "Limit ?,?";
                args.add(index + "");
                args.add(pageSize + "");
            } else {
                limitstr = "";
            }

        }

        String desc = isdesc ? "desc" : "";


        String strSql = " select   g.* FROM  ( SELECT \n" +
                "  a.*, \n" +
                "  b.CONTACT_ID , \n" +
                "  c.* ," +
                "  name.*, " +
                "  news.*, " +
                "  goods.*" +
                "FROM \n" +
                "  msg_table a LEFT JOIN \n" +
                "  contact_msg_table b ON a.MSG_KEY = b.MSG_KEY LEFT JOIN \n" +
                "  group_member_table c ON a.XLGROUPMEMBERID = c.MEMBERID  LEFT JOIN" +
                "  NAME_CARD_TABLE name ON  name.N_MSG_KEY = b.msg_key LEFT JOIN" +
                "  GOODS_DETAIL_TABLE goods ON goods.G_MSG_KEY = b.msg_key LEFT JOIN " +
                "  NEWS_CARD_TABLE news ON news.NS_MSG_KEY = b.msg_key  " +
                "WHERE \n" +
                "  b.XLID = ? " + tochastr + "  and (msg_type = %s or  msg_type = %s or  msg_type = %s  or" +
                "  msg_type = %s) and ( MSGDIR = 0 or( N_ISOPEN = 1 or NS_ISOPEN = 1  or G_ISOPEN =1 ) )   GROUP BY  goods.G_GOODSID  ,name.N_XLID ,news.NS_NEWSID" +
                " ORDER BY \n" +
                "    a.OPEN_LAST_TIME desc   ) g ORDER BY g.OPEN_LAST_TIME  " +
                desc + " " + limitstr +
                "   ;";

        strSql = String.format(strSql, MessageChatAdapter.IDCARD, MessageChatAdapter.REDBUNDLE, MessageChatAdapter
                .WEBSHOPPING, MessageChatAdapter.NEWSCARD);


        String[] array = new String[args.size()];
        args.toArray(array);
        return dbUtil.query(strSql, array);
    }

    //--------------------------------------------- 全部----------------------------------------------

    /**
     * 卡片消息查询
     *
     * @param toCharID  groupid or xluserid
     * @param chatType  BorrowConstants.CHATTYPE_SINGLE 查询和某一位联系人之间全部的聊天消息 CHATTYPE_GROUP查询群的消息
     * @param pageIndex 当前页数 默认0
     * @param pageSize
     * @param isdesc    否是降序
     * @return
     */
    public Cursor queryChatCardHistoryALL(String toCharID, int chatType, long pageIndex, long pageSize, boolean isdesc) {


        long pageCurrent = pageIndex * pageSize;


        if (chatType == BorrowConstants.CHATTYPE_GROUP) {

            return queryChatCardHistoryGroupAll(toCharID, pageCurrent, pageSize, isdesc);
        }

        String tochastr = null;
        String limitstr = null;

        ArrayList<String> args = new ArrayList<String>();

        args.add(PersonSharePreference.getUserID() + "");

        if (!TextUtils.isEmpty(toCharID)) {
            tochastr = "AND c.CONTACT_ID =?";

            args.add(toCharID);

            if (pageSize != 0) {
                limitstr = "Limit ?,?";
                args.add(pageCurrent + "");
                args.add(pageSize + "");

            } else {
                limitstr = "";
            }

        } else {
            tochastr = "";

            if (pageSize != 0) {
                limitstr = "Limit ?,?";
                args.add(pageCurrent + "");
                args.add(pageSize + "");

            } else {
                limitstr = "";

            }

        }

        String desc = isdesc ? "desc" : "";

        String strSql = "  select g.* FROM  ( SELECT \n" +
                "  c.xlid , \n" +
                "  c.CONTACT_ID , \n" +
                "  a.* ," +
                "  name.*, " +
                "  news.*, " +
                "  goods.*" +
                "FROM \n" +
                "  msg_table a LEFT JOIN \n" +
                "  CONTACT_MSG_TABLE c ON a.MSG_KEY = c.msg_key  LEFT JOIN  \n" +
                "  NAME_CARD_TABLE name ON name.N_MSG_KEY = c.msg_key LEFT JOIN" +
                "  GOODS_DETAIL_TABLE goods ON goods.G_MSG_KEY = c.msg_key LEFT JOIN " +
                "  NEWS_CARD_TABLE news ON news.NS_MSG_KEY = c.msg_key  " +
                "WHERE \n" +
                "  c.xlid = ?  " + tochastr + " and (msg_type = %s or  msg_type = %s or  msg_type = %s " +
                "or  msg_type = %s ) GROUP BY  goods.G_GOODSID  ,name.N_XLID ,news.NS_NEWSID " +
                "ORDER BY \n" +
                " a.OPEN_LAST_TIME   ) g ORDER BY g.OPEN_LAST_TIME  " +
                desc   +" "+limitstr+
                " ;";

        strSql = String.format(strSql, MessageChatAdapter.IDCARD, MessageChatAdapter.REDBUNDLE, MessageChatAdapter
                .WEBSHOPPING, MessageChatAdapter.NEWSCARD);

        //查询和某一位联系人之间全部的聊天消息
        String[] array = new String[args.size()];
        args.toArray(array);
        return dbUtil.query(strSql, array);
    }

    /**
     * 查询群卡片消息查询
     *
     * @param groupid
     * @return
     */
    private Cursor queryChatCardHistoryGroupAll(String groupid, long index, long pageSize, boolean isdesc) {


        String tochastr = null;
        String limitstr = null;

        ArrayList<String> args = new ArrayList<String>();

        args.add(PersonSharePreference.getUserID() + "");

        if (!TextUtils.isEmpty(groupid)) {
            tochastr = "AND b.LOCAL_GROUP_ID = ?";
            args.add(groupid);

            if (pageSize != 0) {
                limitstr = "Limit ?,?";
                args.add(index + "");
                args.add(pageSize + "");
            } else {
                limitstr = "";
            }

        } else {
            tochastr = "";

            if (pageSize != 0) {
                limitstr = "Limit ?,?";
                args.add(index + "");
                args.add(pageSize + "");
            } else {
                limitstr = "";
            }

        }

        String desc = isdesc ? "desc" : "";


        String strSql = " select   g.* FROM  ( SELECT \n" +
                "  a.*, \n" +
                "  b.CONTACT_ID , \n" +
                "  c.* ," +
                "  name.*, " +
                "  news.*, " +
                "  goods.*" +
                "FROM \n" +
                "  msg_table a LEFT JOIN \n" +
                "  contact_msg_table b ON a.MSG_KEY = b.MSG_KEY LEFT JOIN \n" +
                "  group_member_table c ON a.XLGROUPMEMBERID = c.MEMBERID  LEFT JOIN" +
                "  NAME_CARD_TABLE name ON  name.N_MSG_KEY = b.msg_key LEFT JOIN" +
                "  GOODS_DETAIL_TABLE goods ON goods.G_MSG_KEY = b.msg_key LEFT JOIN " +
                "  NEWS_CARD_TABLE news ON news.NS_MSG_KEY = b.msg_key  " +
                "WHERE \n" +
                "  b.XLID = ? " + tochastr + "  and (msg_type = %s or  msg_type = %s or  msg_type = %s  or" +
                "  msg_type = %s) GROUP BY  goods.G_GOODSID  ,name.N_XLID ,news.NS_NEWSID" +
                " ORDER BY \n" +
                "    a.OPEN_LAST_TIME desc   ) g ORDER BY g.OPEN_LAST_TIME  " +
                desc  +" "+limitstr+
                "   ;";

        strSql = String.format(strSql, MessageChatAdapter.IDCARD, MessageChatAdapter.REDBUNDLE, MessageChatAdapter
                .WEBSHOPPING, MessageChatAdapter.NEWSCARD);


        String[] array = new String[args.size()];
        args.toArray(array);
        return dbUtil.query(strSql, array);
    }











    /**
     * 查询全部联系人数据和统计群组的数量
     *
     * @return
     */
    public Cursor queryNameCard() {
        Cursor cursor = null;

        return cursor;
    }

    /**
     * 通过Cursor 转 对象
     */
    public static class NameCardCursor extends CursorWrapper {

        public NameCardCursor(Cursor c) {
            super(c);
        }

        public List<NameCardBean> getContactList() {

            List<NameCardBean> nameCardList = new ArrayList<NameCardBean>();

            for (moveToFirst(); !isAfterLast(); moveToNext()) {
                nameCardList.add(converCursorToNameCardBean(this));
            }
            return nameCardList;
        }
    }

    /**
     * 通过Cursor当前的position获取聊天记录MessageBean
     */
    public static class MessageCursor extends CursorWrapper {
        public MessageCursor(Cursor c) {
            super(c);
        }

        public List<MessageBean> getMessageBeanList() {

            List<MessageBean> messageBeanList = new ArrayList<MessageBean>();

            for (moveToFirst(); !isAfterLast(); moveToNext()) {
                messageBeanList.add(getMessage());
            }

            return messageBeanList;
        }

        public MessageBean getMessage() {

            MessageBean.Builder builder = new MessageBean.Builder()
                    .figureId("FIGURE_ID")
                    .figureUsersId("FIGURE_USERSID")
                    .msgType(getInt(getColumnIndex("MSG_TYPE")))
                    .msgKey(getString(getColumnIndex("MSG_KEY")))
                    .msgDate(getString(getColumnIndex("MSG_DATE"))); // 信息类型


            MessageBean messageBean = builder.build();

            switch (messageBean.msgType) {
                case MessageChatAdapter.IDCARD://名片
                    NameCardBean nameCardBean = converCursorToNameCardBean(this);
                    messageBean.idCard = nameCardBean;
                    break;
                case MessageChatAdapter.WEBSHOPPING://卡片
                    GoodsDetailBean goodsDetailBean = converCursorToGoodsCardBean(this);
                    messageBean.goodsCard = goodsDetailBean;
        /*        NameCardBean nameCardBean =JSON.parseObject(message.msgContent, NameCardBean.class);
                  mCardDBHandler.add(nameCardBean);*/
                    break;
                case MessageChatAdapter.NEWSCARD://卡片
                    NewsCard newsCard = converCursorToNewsCardBean(this);
                    messageBean.newsCard = newsCard;
        /*        NameCardBean nameCardBean =JSON.parseObject(message.msgContent, NameCardBean.class);
                  mCardDBHandler.add(nameCardBean);*/
                    break;
            }

            return messageBean;

        }
    }


    /**
     * 解析名片
     *
     * @param c CursorWrapper 当前游标位置
     * @return NameCardBean
     */
    public static NameCardBean converCursorToNameCardBean(CursorWrapper c) {
        NameCardBean ncb = new NameCardBean();
        ncb.setFigureId(c.getString(c.getColumnIndex("N_XLID")));
        ncb.setName(c.getString(c.getColumnIndex("N_NAME_CARD_ID")));
        ncb.setType(c.getInt(c.getColumnIndex("N_TYPE")));
        ncb.setIsopen(c.getInt(c.getColumnIndex("N_ISOPEN")));
        ncb.setMsg_key(c.getString(c.getColumnIndex("N_MSG_KEY")));
        ncb.setName(c.getString(c.getColumnIndex("N_NAME")));
        ncb.setUserId(c.getString(c.getColumnIndex("N_USERID")));
        ncb.setImgId(c.getString(c.getColumnIndex("N_IMGID")));
        ncb.setHead_image_path(c.getString(c.getColumnIndex("N_HEAD_IMAGE_PATH")));
        ncb.setRemarks(c.getString(c.getColumnIndex("N_REMARKS")));
        return ncb;
    }

    /**
     * 解析新闻
     *
     * @param c CursorWrapper 当前游标位置
     * @return NameCardBean
     */
    public static NewsCard converCursorToNewsCardBean(CursorWrapper c) {
        NewsCard nc = new NewsCard();
        nc.setXlid(c.getString(c.getColumnIndex("NS_XLID")));
        nc.setNewsid(c.getString(c.getColumnIndex("NS_NEWSID")));
        nc.setTitle(c.getString(c.getColumnIndex("NS_TITLE")));
        nc.setImgurl(c.getString(c.getColumnIndex("NS_IMGURL")));
        nc.setSummary(c.getString(c.getColumnIndex("NS_SUMMARY")));
        nc.setUrl(c.getString(c.getColumnIndex("NS_URL")));
        nc.setIsopen(c.getInt(c.getColumnIndex("NS_ISOPEN")));
        nc.setMsg_key(c.getString(c.getColumnIndex("NS_MSG_KEY")));
        return nc;
    }

    /**
     * 解析商品
     *
     * @param c CursorWrapper 当前游标位置
     * @return NameCardBean
     */
    public static GoodsDetailBean converCursorToGoodsCardBean(CursorWrapper c) {
        GoodsDetailBean gdb = new GoodsDetailBean();
        gdb.setGoodsId(c.getString(c.getColumnIndex("G_GOODSID")));//.getGoodsId());// NOT NULL ," +
        gdb.setXlid(c.getString(c.getColumnIndex("G_XLID")));//.getFigureId());// NOT NULL ," +
        gdb.setName(c.getString(c.getColumnIndex("G_NAME")));//.getName());// ," +  //标题
        gdb.setName_2(c.getString(c.getColumnIndex("G_NAME_2")));//.getName_2());// ," +  //子标题
        gdb.setPrice(c.getString(c.getColumnIndex("G_PRICE")));//.getPrice());// ," +  //价格
        gdb.setPrice_unit(c.getString(c.getColumnIndex("G_PRICE_UNIT")));//.getPrice_unit());//," + //价格单位
        gdb.setAbstraction(c.getString(c.getColumnIndex("G_ABSTRACTION")));//.getAbstraction());//," + //详情
        gdb.setOriginPrice(c.getString(c.getColumnIndex("G_OFFLINE_PRICE")));//.getOffline_price());// ," +//过期价格
        gdb.setTitle_image_path(c.getString(c.getColumnIndex("G_TITLE_IMAGE_PATH")));//.getTitle_image_path());// ,"
        // +//商品标题
        gdb.setCategory_id(c.getString(c.getColumnIndex("G_CATEGORY_ID")));//.getCategory_id());// ," +//商品分类
        gdb.setSys_tags(c.getString(c.getColumnIndex("G_SYS_TAGS")));//.getSys_tags());// ," +   //促销活动
        gdb.setSales_vol(c.getString(c.getColumnIndex("G_SALES_VOL")));//.getSales_vol());// ," +//销售量
        gdb.setStock_status(c.getString(c.getColumnIndex("G_STOCK_STATUS")));//.getStock_status());// ," + //库存状态
        gdb.setUrl(c.getString(c.getColumnIndex("G_URL")));//.getUrl());//," +//促销活动URL
        gdb.setMsg_key(c.getString(c.getColumnIndex("G_MSG_KEY")));//.getMsg_key());// ," +//消息KEY
        gdb.setImgURL(c.getString(c.getColumnIndex("G_IMGURL")));//.getImgId());// ," +//图片ID
        gdb.setGoodsCount(c.getString(c.getColumnIndex("G_GOODSCOUNT")));//.getGoodsCount());// ," +//商品数量
        gdb.setIsOpen(c.getInt(c.getColumnIndex("G_ISOPEN")));//.getIsOpen());// NOT NULL DEFAULT 0 , "+//G_ +//是否打开过
        return gdb;
    }

    /**
     * 获取 contentvalues
     *
     * @param ncb
     * @return
     */
    public static ContentValues getNameCardContentValues(NameCardBean ncb) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("N_XLID", ncb.getFigureId());
        contentValues.put("N_NAME_CARD_ID", ncb.getName());
        contentValues.put("N_TYPE", ncb.getType());
        contentValues.put("N_ISOPEN", ncb.getIsopen());
        contentValues.put("N_MSG_KEY", ncb.getMsg_key());
        contentValues.put("N_NAME", ncb.getName());
        contentValues.put("N_USERID", ncb.getUserId());
        contentValues.put("N_IMGID", ncb.getImgId());
        contentValues.put("N_HEAD_IMAGE_PATH", ncb.getHead_image_path());
        contentValues.put("N_REMARKS", ncb.getRemarks());

        return contentValues;
    }

    /**
     * 获取 contentvalues
     *
     * @param nc
     * @return
     */
    public static ContentValues getNewsCardContentValues(NewsCard nc) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("NS_XLID", nc.getXlid());
        contentValues.put("NS_NEWSID", nc.getNewsid());
        contentValues.put("NS_TITLE", nc.getTitle());
        contentValues.put("NS_IMGURL", nc.getImgurl());
        contentValues.put("NS_SUMMARY", nc.getSummary());
        contentValues.put("NS_URL", nc.getUrl());
        contentValues.put("NS_ISOPEN", nc.getIsopen());
        contentValues.put("NS_MSG_KEY", nc.getMsg_key());


        return contentValues;
    }

    /**
     * 获取 contentvalues
     *
     * @param gdb
     * @return
     */
    public static ContentValues getGoodContentValues(GoodsDetailBean gdb) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("G_GOODSID", gdb.getGoodsId());// NOT NULL ," +
        contentValues.put("G_XLID", gdb.getXlid());// NOT NULL ," +
        contentValues.put("G_NAME", gdb.getName());// ," +  //标题
        contentValues.put("G_NAME_2", gdb.getName_2());// ," +  //子标题
        contentValues.put("G_PRICE", gdb.getPrice());// ," +  //价格
        contentValues.put("G_PRICE_UNIT", gdb.getPrice_unit());//," + //价格单位
        contentValues.put("G_ABSTRACTION", gdb.getAbstraction());//," + //详情
        contentValues.put("G_OFFLINE_PRICE", gdb.getOriginPrice());// ," +//过期价格
        contentValues.put("G_TITLE_IMAGE_PATH", gdb.getTitle_image_path());// ," +//商品标题
        contentValues.put("G_CATEGORY_ID", gdb.getCategory_id());// ," +//商品分类
        contentValues.put("G_SYS_TAGS", gdb.getSys_tags());// ," +   //促销活动
        contentValues.put("G_SALES_VOL", gdb.getSales_vol());// ," +//销售量
        contentValues.put("G_STOCK_STATUS", gdb.getStock_status());// ," + //库存状态
        contentValues.put("G_URL", gdb.getUrl());//," +//促销活动URL
        contentValues.put("G_MSG_KEY", gdb.getMsg_key());// ," +//消息KEY
        contentValues.put("G_IMGURL", gdb.getImgURL());// ," +//图片ID
        contentValues.put("G_GOODSCOUNT", gdb.getGoodsCount());// ," +//商品数量
        contentValues.put("G_ISOPEN", gdb.getIsOpen());// NOT NULL DEFAULT 0 , "+//G_ +//是否打开过

        return contentValues;
    }


}
