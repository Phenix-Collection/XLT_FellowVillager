package com.xianglin.fellowvillager.app.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.Extras;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author james
 */
public class NoticeGetGroupOrContactInfoUtil {


    private Context context;
    private static final String TAG = NoticeGetGroupOrContactInfoUtil.class.getSimpleName();

    private Handler handler;

    public NoticeGetGroupOrContactInfoUtil(Context context, Handler handler) {

        this.context = context;
        this.handler = handler;
    }

    /**
     * 获取DB 联系人数据
     *
     * @param mExtras
     */
    public void getDBContact(Extras mExtras) {
        try {
            ContactDBHandler contactDBHandler = new ContactDBHandler(context);
            Contact contact = ContactManager.getInstance().getContact(contactDBHandler.getContactId(mExtras.getFfid(), mExtras.getTfid()));
            if (contact != null) {
                String fileid = contact.file_id;
                String name = contact.getUIName();
                ChatMainActivity_.intent(context)
                        .titleName(name)
                        .headerImgId(fileid)
                        //
                        .toChatId(mExtras.getFfid()) // contact figure id && group id
                        .currentFigureId(mExtras.getTfid())// this user  figure id
                        .toChatXlId(mExtras.getFid())// contact user id

                        .chatType(BorrowConstants.CHATTYPE_SINGLE)
                        .start();
            } else {
                ChatMainActivity_.intent(context)

                        .toChatId(mExtras.getFfid()) // contact figure id && group id
                        .currentFigureId(mExtras.getTfid())// this user  figure id
                        .toChatXlId(mExtras.getFid())// contact user id

                        .chatType(BorrowConstants.CHATTYPE_SINGLE)
                        .start();
            }
        } catch (Exception e) {
            LogCatLog.e(TAG, "从数据库拉取联系人数据失败" + e);
            ChatMainActivity_.intent(context)

                    .toChatId(mExtras.getFfid()) // contact figure id && group id
                    .currentFigureId(mExtras.getTfid())// this user  figure id
                    .toChatXlId(mExtras.getFid())// contact user id

                    .chatType(BorrowConstants.CHATTYPE_SINGLE)
                    .start();
        }

    }


    /**
     * 查询群列表 所对应的figure ID ，
     * 得出 figureID 发送的消息次数，
     * 进行比较，得出最大的发送的消息次数对应figureID
     */
    public void getLocalGroupListAndBubbleSort(Extras mExtras) {
        try {
            // 获取当前用户的群列表
            Map<String, Group> map = GroupManager.getInstance().getAllFigureGroupTable();
            if (map != null) {
                final Map<Long, Long> figureIds = new LinkedHashMap<Long, Long>();//  TODO 消息数量为key（ map key唯一性）
                for (Map.Entry<String, Group> maps : map.entrySet()) {
                    Group group = maps.getValue();
                    if (mExtras.getGid().equals(group.xlGroupID)) {
                        if (group.figureId != null) {
                            long figureId = Long.parseLong(group.figureId);
                            figureIds.put(PersonSharePreference.getChatFidCount(group.figureId), figureId);
                        } else {
                            LogCatLog.e(TAG, "group figureID is  null");
                        }
                    }
                }

//            // 判断
                if (figureIds.size() == 0) {//网络
                    getNetWorkGroupListAndBubbleSort(mExtras, figureIds);
                } else if (figureIds.size() > 0) {// 本地
                    getCountBubbleSort(mExtras, figureIds);// 进行跳转
                }

            } else {
                LogCatLog.e(TAG, "group list  is  null");
                toExceptionChatActivity(mExtras);
            }
        } catch (Exception e) {
            LogCatLog.e(TAG, "离线消息获取群详情失败" + e);
            toExceptionChatActivity(mExtras);
        }


    }

    public void getNetWorkGroupListAndBubbleSort(final Extras mExtras, final Map<Long, Long> figureIds) {

        ThreadPool.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                LogCatLog.d(TAG, "getNetWorkGroupListAndBubbleSort runnable start");
                SyncApi.getInstance().listGroup(context, new SyncApi.CallBack() {
                    @Override
                    public void success(Object mode) {

                        if (mode != null) {
                            List<GroupDTO> groupDTOs = (List<GroupDTO>) mode;
                            LogCatLog.d(TAG, "群组 size= " + groupDTOs.size());
                            for (GroupDTO groupDTO : groupDTOs) {
                                if (mExtras.getGid().equals(groupDTO.getGroupId())) {
                                    Long figureId = Long.parseLong(groupDTO.getFigureId());
                                    figureIds.put(PersonSharePreference.getChatFidCount(groupDTO.getFigureId()), figureId);
                                }
                            }
                            if (figureIds.size() > 0) {

                                getCountBubbleSort(mExtras, figureIds);// 进行跳转

                            } else {

                                toExceptionChatActivity(mExtras);
                            }
                        } else {
                            LogCatLog.e(TAG, "get groupdto is  null");
                        }
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        LogCatLog.e(TAG, "获取群列表错误");
                        toExceptionChatActivity(mExtras);
                    }
                });
            }
        });


    }


    public long getCountBubbleSort(final Extras mExtras, final Map<Long, Long> figureIds) {
        Long[] counts = new Long[figureIds.size()];
        int i = 0;
        for (Map.Entry<Long, Long> figureId : figureIds.entrySet()) {
            figureId.getValue();
            counts[i] = figureId.getKey();
            i++;
        }

        for (int j = 0; j < counts.length - 1; j++) {
            for (int l = 0; l < counts.length - j - 1; l++) {
                if (counts[l] < counts[l + 1]) {
                    long temp = counts[l];
                    counts[l] = counts[l + 1];
                    counts[l + 1] = temp;
                }
            }
        }

        Long message_count = counts[0];
        final long figureId = figureIds.get(message_count);
        Group group = GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(mExtras.getGid(), figureId + ""));
        if (group == null) {//网络
            SyncApi.getInstance().detail(mExtras.getGid(), figureId + "", context, new SyncApi.CallBack() {
                @Override
                public void success(Object mode) {

                    GroupDTO groupDTO = (GroupDTO) mode;

                    toChatActivity(mExtras, figureId, groupDTO.getGroupName());
                }

                @Override
                public void failed(String errTip, int errCode) {
                    LogCatLog.e(TAG, "获取群详情错误");
                    toExceptionChatActivity(mExtras);
                }
            });
        } else {// 本地
            toChatActivity(mExtras, figureId, group.xlGroupName);

        }

        return figureId;
    }


    public void toChatActivity(Extras mExtras, long figureId, String xlGroupName) {

        if (mExtras.isChatMain()){
            Message message = new Message();
            Bundle bundle = new Bundle();
            message.obj = mExtras;
            message.what = 1012;
            bundle.putLong("figureId", figureId);
            bundle.putString("groupName", xlGroupName);
            message.setData(bundle);
            handler.sendMessage(message);
            LogCatLog.d(TAG, "对比出来最大的figureid" + figureId);
        }else{
            Message message = new Message();
            Bundle bundle = new Bundle();
            message.obj = mExtras;
            message.what = 2;
            bundle.putLong("figureId", figureId);
            bundle.putString("groupName", xlGroupName);
            message.setData(bundle);
            handler.sendMessage(message);
            LogCatLog.d(TAG, "对比出来最大的figureid" + figureId);
        }

    }

    public void toExceptionChatActivity(Extras mExtras) {
        if (mExtras.isChatMain()) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            message.obj = mExtras;
            message.what = 1011;// 报错
            message.setData(bundle);
            handler.sendMessage(message);
        } else {
            Message message = new Message();
            Bundle bundle = new Bundle();
            message.obj = mExtras;
            message.what = 3;
            //bundle.putLong("figureId", figureId);// TODO: 16/3/31 如果报异常 figureID 是没有的
//        bundle.putString("groupName", xlGroupName);
            message.setData(bundle);
            handler.sendMessage(message);
        }

    }


}
