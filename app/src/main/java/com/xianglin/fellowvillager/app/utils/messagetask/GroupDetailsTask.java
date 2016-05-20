package com.xianglin.fellowvillager.app.utils.messagetask;

import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.appserv.common.service.facade.model.GroupMemberDTO;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.List;

/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
public class GroupDetailsTask extends MessageEventTask {
    private final MessageBean mMessageBean;

    private final GroupDBHandler contactDBHandler;

    public GroupDetailsTask(MessageBean mb) {
        super(System.currentTimeMillis()+"");
        contactDBHandler = new GroupDBHandler(XLApplication.getInstance());
        mMessageBean = mb;
    }

    @Override
    public void run() {

        Group group = GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(mMessageBean.xlID, mMessageBean.figureId));

        LogCatLog.e(TAG, "group=" + group + ",toChatID=" + GroupDBHandler.getGroupId(mMessageBean.xlID, mMessageBean.figureId));
        if (group == null) {
            //本地没有群信息的情况
            group = getDetailGroup(mMessageBean);//获取群详情 和群成员列表

            getMembers(group, mMessageBean);

            XLApplication.getInstance().getContentResolver().notifyChange(MessageDBHandler.SYNC_SIGNAL_URI, null);


        } else if (group.isJoin.equals(BorrowConstants.IS_NO_JOIN_GROUP)) {

            //群被解散或者退出群后又收到消息的情况:
            //1.这是个服务器bug
            //2.这个人又被拉进群了

            getDetailGroup(mMessageBean);//获取群详情 和群成员列表
        } else {

            GroupMember groupMember = GroupManager.getInstance().getMember(mMessageBean.xlgroupmemberid);

            if (groupMember == null) {
                //收到群成员(被别人拉进群的成员)发的消息
                getDetailGroup(mMessageBean);//获取群详情 和群成员列表
            }
        }

        onEndTask();
    }

    public Group getDetailGroup(MessageBean mb) {

        GroupDTO groupDto = SyncApi.getInstance().detail(mb.xlID, mb.figureId, XLApplication.context,
                new SyncApi.CallBack<GroupDTO>() {
                    @Override
                    public void success(GroupDTO mode) {
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                    }
                });

        Group group = GroupManager.getInstance().swapGroupDTOtoGroup(groupDto,true);

        GroupManager.getInstance().addGroup(group);
        MessageHandler.getInstance().notifyNewGroup(GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(group)));
        return group;
    }

    /**
     * 获取群成员
     *
     * @param mb
     */
    private void getMembers(Group group, MessageBean mb) {

        if (group == null) {
            return;
        }
        List<GroupMemberDTO> list = SyncApi.getInstance().members(mb.xlID, XLApplication.context,
                new SyncApi.CallBack<List<GroupMemberDTO>>() {
                    @Override
                    public void success(List<GroupMemberDTO> mode) {
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                    }
                });

        if (list != null) {
            GroupManager.getInstance().addGroupMemberList(group, list);
        } else {
            LogCatLog.e(TAG, "getMembers() return null");
        }

    }



}
