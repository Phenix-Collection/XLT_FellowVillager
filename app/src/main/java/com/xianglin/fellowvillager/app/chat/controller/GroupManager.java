/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat.controller;

import android.content.Context;
import android.text.TextUtils;

import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.appserv.common.service.facade.model.GroupMemberDTO;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.adapter.GroupListInContactAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.GroupMemberDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.pinyin.PingYinUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 联系人管理
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/28 19:23  XLXZ Exp $
 */
public class GroupManager {

    private static final String TAG = "GroupManager";

    private static GroupManager instance = null;

    private boolean isIntied = false;

    /**
     * 当前用户所有角色的群列表
     */
    private Map<String, Group> allFigureGroupTable = new Hashtable(30);

    private Map<String, GroupMember> groupMemberTable = new Hashtable(200);

    private Context mContext;

    private GroupDBHandler mGroupDBHandler = new GroupDBHandler(XLApplication.getInstance());

    private GroupMemberDBHandler mGroupMemberDBHandler = new GroupMemberDBHandler(XLApplication.getInstance());

    public static GroupManager getInstance() {

        if (instance == null) {
            instance = new GroupManager();
        }
        return instance;
    }

    public Map<String, Group> getAllFigureGroupTable() {
        return allFigureGroupTable;
    }

    public void setAllFigureGroupTable(Map<String, Group> allFigureGroupTable) {
        if (allFigureGroupTable == null) {
            this.allFigureGroupTable.clear();
            return;
        }
        this.allFigureGroupTable = allFigureGroupTable;

    }

    /**
     * 获取当前角色的群列表
     *
     * @return 当前角色群列表
     */
    public Map<String, Group> getCurrentFigureGroupTable() {
        if (allFigureGroupTable == null) {
            return null;
        }
        FigureMode currentFigure = ContactManager.getInstance().getCurrentFigure();
        if (currentFigure == null) { // 当前角色为全部角色时,返回所有角色的群列表
            return allFigureGroupTable;
        }
        Map<String, Group> currentFigureGroupTable = new Hashtable(30);
        for (Map.Entry<String, Group> entry :
                allFigureGroupTable.entrySet()) {
            Group value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (value.figureId != null
                    && value.figureId.equals(currentFigure.getFigureUsersid())) {
                currentFigureGroupTable.put(
                        entry.getKey(),
                        entry.getValue()
                );
            }
        }
        return currentFigureGroupTable;
    }

    /**
     * 获取当前角色的群黑名单列表
     *
     * @return 当前角色群黑名单列表
     */
    public List<Group> getBlackListGroupByFigureId(/*String mFigureId*/) {
        if (allFigureGroupTable == null /*|| mFigureId == null*/)
            return null;

        Map<String, Group> blackListGroup = new Hashtable(30);
        for (Map.Entry<String, Group> entry : allFigureGroupTable.entrySet()) {
            Group value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (GroupListInContactAdapter.GROUP_TYPE_BLACK.equals(value.groupType)
            /* && value.figureId != null && value.figureId.equals(mFigureId)*/) {
                blackListGroup.put(entry.getKey(), entry.getValue());
            }
        }
        List<Group> mBlackListGroup = new ArrayList<Group>(blackListGroup.values());
        return mBlackListGroup;
    }

    /**
     * 添加群
     *
     * @param group
     */
    public void addGroup(Group group) {

        allFigureGroupTable.put(group.localGroupId, group);

        if (mGroupDBHandler == null) {
            mGroupDBHandler = new GroupDBHandler(XLApplication.getInstance().getApplicationContext());
        }
        mGroupDBHandler.add(group);
    }

    /**
     * 获取群信息
     *
     * @param localGroupId
     * @return
     */
    public Group getGroup(String localGroupId) {

        if (TextUtils.isEmpty(localGroupId)) {
            return null;
        }

        Group group = allFigureGroupTable.get(localGroupId);
        if (group == null) {
            group = mGroupDBHandler.query(localGroupId);
        }
        return group;
    }

    /**
     * 把群加入已解散的群组
     */
    public void dismissContactInternal(String localGroupId) {

        Group group = getAllFigureGroupTable().get(localGroupId);
        if (group != null) {
            group.status = "DISMISS";

            mGroupDBHandler.dismissGroup(localGroupId);
        } else {
            LogCatLog.w(TAG, "删除失败getGroupTable中不存在" + localGroupId);
        }
        //删除对话列表?
    }

    /** 退出群
     * @param localGroupId
     */
    public void exitContactInternal(String localGroupId) {

        Group group = getAllFigureGroupTable().get(localGroupId);
        if (group != null) {
            group.isJoin = BorrowConstants.IS_NO_JOIN_GROUP;
            mGroupDBHandler.exitGroup(localGroupId);
        } else {
            LogCatLog.w(TAG, "删除失败getGroupTable中不存在" + localGroupId);
        }
        //删除对话列表?
    }

    /**
     * 初始化联系人,需要在子线程中完成
     *
     * @param context
     * @param figureId 登录用户角色id
     */
    public void init(Context context, final String figureId) {
        if (!this.isIntied) {

            this.mContext = context;


            BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                                           @Override
                                           public void execute() {
                                               try {

                                                   loadGroup(figureId);

                                               } catch (Throwable e) {
                                                   Thread.getDefaultUncaughtExceptionHandler().uncaughtException
                                                           (Thread.currentThread(), e);
                                               }
                                           }


                                       }
            );

            this.isIntied = true;
        }

    }


    /**
     * 启动时初始化群信息
     *
     * @param figureId
     */
    private void loadGroup(String figureId) {

        LogCatLog.d(TAG, " 加载群-开始");

        List groupList = mGroupDBHandler.queryGroupList("");

        Iterator iterator = groupList.iterator();

        while (iterator.hasNext()) {

            Group g = (Group) iterator.next();

            allFigureGroupTable.put(g.localGroupId, g);
        }

        LogCatLog.d(TAG, " 加载群-结束 size=" + groupList.size());

    }


    /**
     * 网络数据初始化群列表
     *
     * @param groups
     * @param isJoin
     */
    public long addGroups(List<GroupDTO> groups, boolean isJoin) {

        Iterator iterator = groups.iterator();

        List<Group> list = new ArrayList<Group>();
        while (iterator.hasNext()) {

            GroupDTO contactdto = (GroupDTO) iterator.next();

            Group group = swapGroupDTOtoGroup(contactdto, isJoin);

            allFigureGroupTable.put(group.localGroupId, group);

            list.add(group);
        }

        return mGroupDBHandler.addlist(list, true);

    }

    /**
     * 启动时初始化群成员
     *
     * @param groupId
     */
    public void loadGroupMember(String groupId) {

        LogCatLog.d(TAG, " 加载群成员-开始");

        List<GroupMember> groupList = mGroupMemberDBHandler.queryGroupMemberToList(groupId);

        Iterator iterator = groupList.iterator();

        while (iterator.hasNext()) {

            GroupMember g = (GroupMember) iterator.next();

            groupMemberTable.put(g.groupmemberid, g);
        }

        LogCatLog.d(TAG, " 加载群成员-结束 size=" + groupList.size());

    }

    /**
     * 添加群成员
     *
     * @param group
     * @param list
     */
    public void addGroupMemberList(Group group, List<GroupMemberDTO> list) {


        ArrayList<GroupMember> members = new ArrayList<GroupMember>();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            GroupMemberDTO g = (GroupMemberDTO) iterator.next();
            GroupMember groupMember = swapMemberDTOtoMember(group, g);
            members.add(groupMember);
            groupMemberTable.put(groupMember.groupmemberid, groupMember);
        }
        mGroupMemberDBHandler.delForGroup(group.localGroupId);
        mGroupMemberDBHandler.addlist(members);
    }

    /**
     * 对象转换
     *
     * @param groupDTO
     * @param isJoin
     * @return
     */
    public Group swapGroupDTOtoGroup(GroupDTO groupDTO, boolean isJoin) {

        Group mGroup = new Group.Builder()
                .xlID(PersonSharePreference.getUserID() + "")
                .figureId(groupDTO.getFigureId())
                .ownerFigureId(groupDTO.getOwnerFigureId())
                .ownerUserId(groupDTO.getOwnerUserId())
                .xlGroupName(groupDTO.getGroupName() + "")
                .xlGroupID(groupDTO.getGroupId() + "")
                .localGroupId(GroupDBHandler.getGroupId(groupDTO.getGroupId() + "", groupDTO.getFigureId() + ""))
                .groupType(groupDTO.getGroupType())
                .file_id(groupDTO.getAvatarUrl() + "")
                .status(groupDTO.getStatus())
                .description(groupDTO.getDescription())
                .updateGroupTime(groupDTO.getUpdateTime() + "")
                .createGroupTime(groupDTO.getCreateTime() + "")
                .isJoin(isJoin ? BorrowConstants.IS_JOIN_GROUP : BorrowConstants.IS_NO_JOIN_GROUP).build(); //表示有没有被删除

        return mGroup;

    }


    /**
     * 对象转换
     *
     * @param group
     * @param memberDTO
     * @return
     */
    public GroupMember swapMemberDTOtoMember(Group group, GroupMemberDTO memberDTO) {
        boolean isOwner = false;
        boolean isContact = false;
        String ContactId = ContactDBHandler.getContactId(memberDTO.getFigureId(), group.figureId);

        if (group.ownerFigureId.equals(memberDTO.getFigureId())) {
            isOwner = true;
        }
        if (ContactManager.getInstance().getContactTable()
                .containsKey(ContactId)) {
            Contact contact = ContactManager.getInstance().getContact(ContactId);
            if (contact.isContact.equals(BorrowConstants.IS_CONTACT)) {
                isContact = true;
            }
        }

        return swapMemberDTOtoMember(group, memberDTO, true, isOwner);

    }

    /**
     * @param group     群
     * @param memberDTO 服务器对象
     * @param isContact 群成员是否是你的联系人
     * @param isOwner   是否是群主
     * @return
     */
    public GroupMember swapMemberDTOtoMember(Group group,
                                             GroupMemberDTO memberDTO,
                                             boolean isContact,
                                             boolean isOwner) {
        //xlid db已经给了默认值
        GroupMember mGroupMember = new GroupMember.Builder()
                .groupmemberid(GroupMemberDBHandler.getMemberId(group.xlGroupID, memberDTO.getFigureId(), group.figureId))
                .xluserid(memberDTO.getUserId())
                .xlGroupId(group.xlGroupID + "")
                .localgroupId(GroupDBHandler.getGroupId(group.xlGroupID, group.figureId))
                .file_id(memberDTO.getAvatarUrl())
                .isOwner(isOwner + "")
                .isContact(isContact + "")
                .figureId(group.figureId)
                .figureUsersId(memberDTO.getFigureId())
                .xlRemarkName(memberDTO.getRemarkName())
                .xlUserName(memberDTO.getNickName())
                .gender(memberDTO.getGender())
                .individualitySignature(memberDTO.getIndividualitySignature())
                .joinTime(memberDTO.getJoinTime())
                .sexualOrientation(memberDTO.getSexualOrientation())
                .build();
        mGroupMember.sortLetters = PingYinUtil.getPingYin(mGroupMember.getUIName());

        return mGroupMember;
    }

    /**
     * 添加群成员
     *
     * @param member
     */
    public void addMembersInternal(GroupMember member) {

        this.groupMemberTable.put(member.groupmemberid, member);
        mGroupMemberDBHandler.add(member);

    }

    /**
     * 获取群成员id
     *
     * @param groupMemberId
     * @return
     */
    public GroupMember getMember(String groupMemberId) {


        GroupMember member = (GroupMember) this.groupMemberTable.get(groupMemberId);

        if (member == null) {
            if (mGroupMemberDBHandler == null)
                mGroupMemberDBHandler = new GroupMemberDBHandler(XLApplication.getInstance());

            member = mGroupMemberDBHandler.queryGroupMember(groupMemberId);
        }

        return member;
    }

    /**
     * 删除群成员
     */
    public void deleteMember(String memberid) {

        GroupMember member = groupMemberTable.get(memberid);
        groupMemberTable.remove(memberid);
        if (member != null) {
            mGroupMemberDBHandler.del(memberid);
        } else {
            LogCatLog.w(TAG, "删除失败groupMemberTable中不存在" + memberid);
        }
        //删除对话列表?
    }

    /**
     * 把群加入黑名单
     */

    public boolean moveToBlackList(String localGroupId) {

        Group group = getAllFigureGroupTable().get(localGroupId);

        if (group != null) {

            group.groupType = GroupListInContactAdapter.GROUP_TYPE_BLACK;

            return mGroupDBHandler.add(group) > 0 ? true : false;

        } else {
            LogCatLog.w(TAG, "删除失败getGroupTable中不存在" + localGroupId);
        }
        return false;
        //删除对话列表?
    }

    /**
     * 把群从黑名单恢复
     */

    public boolean recoveryFromBlackList(String localGroupId) {
        Group group = getAllFigureGroupTable().get(localGroupId);

        if (group != null) {
            group.groupType = GroupListInContactAdapter.GROUP_TYPE_NORMAL;
            return mGroupDBHandler.add(group) > 0 ? true : false;
        } else {
            LogCatLog.w(TAG, "恢复失败getGroupTable中不存在" + localGroupId);
        }
        return false;
    }


    public void setFigureGroup(String[] arg, Group group) {

        ArrayList<FigureMode> figureModes = new ArrayList<FigureMode>();
        for (int i = 0; i < arg.length; i++) {

           FigureMode figureMode = ContactManager.getInstance().getFigureTable().get(arg[i]);
            if(figureMode!=null){
                figureModes.add(figureMode);
            }
        }
        group.figureGroup = figureModes;
    }

}
