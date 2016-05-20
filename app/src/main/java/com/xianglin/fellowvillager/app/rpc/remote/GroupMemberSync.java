/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.rpc.remote;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.db.GroupMemberDBHandler;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.xlappcore.common.service.facade.base.CommonReq;
import com.xianglin.xlappcore.common.service.facade.vo.ContactListVo;
import com.xianglin.xlappcore.common.service.facade.vo.MemberVo;

import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 获取群的成员列表
 *
 * @author pengyang
 * @version v 1.0.0 2015/12/15 16:55  XLXZ Exp $
 */
public class GroupMemberSync {

    private String groupid;
    private GroupMemberDBHandler mGroupMemberDBHandler;

    public GroupMemberSync(String groupid) {
        this.groupid = groupid;
        this.mGroupMemberDBHandler = new GroupMemberDBHandler(XLApplication.getInstance());
    }

    public void groupMemberSync(final GroupMemberSync.CallBack calback) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "GroupMemberSync") {
            @Override
            public void execute() {
                try {
                    getGroupMember(calback);
                } catch (Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread
                            .currentThread(), e);
                }
            }
        });
    }


    /**
     * 请求群后自动开始请求群成员列表 必须在子线程中调用
     */
  public  void autoRequestGroupMember() {

      LogCatLog.d("GroupMemberSync", "autoRequestGroupMember:开始自动获取群成员");
        getGroupMember(new CallBack() {
            @Override
            public void success(List<GroupMember> list) {
                LogCatLog.d("GroupMemberSync", "autoRequestGroupMember:自动获取群成员success"+list.toString());
            }
            @Override
            public void failed(String errMsg, int type) {
                LogCatLog.e("GroupMemberSync", "autoRequestGroupMember:自动获取群成员failed"+errMsg);
            }
        });
    }

    /**
     * 获取群成员
     */
    void getGroupMember(final GroupMemberSync.CallBack calback) {
        final CommonReq commonReq = new CommonReq();
        final long xlid = PersonSharePreference.getUserID();
        commonReq.setBody(new HashMap<String, Object>() {
            {
                put("xlid", xlid);
                put("teamId", groupid);
            }
        });
        SyncApi.getInstance().teamMember(XLApplication.getInstance(), commonReq, new SyncApi.CallBack<ContactListVo>() {

            @Override
            public void success(ContactListVo mode) {

                calback.success(loadData(mode));//将网络数据插入数据库

                LogCatLog.d("GroupMemberSync", "获取群成员信息success");
            }

            @Override
            public void failed(String errMsg, int type) {
                calback.failed(errMsg, type);
                LogCatLog.e("GroupMemberSync", "获取群成员信息failed:" + errMsg);
            }
        });
    }

    /**
     * 加载数据;
     *
     * @param mode
     */
    public List<GroupMember> loadData(ContactListVo mode) {

        List<GroupMember> gm = new ArrayList<GroupMember>();

        List<MemberVo> mMemberVoList = mode.getMemberList();

        for (int i = 0; mMemberVoList != null && i < mMemberVoList.size(); i++) {
            MemberVo memberVo = mMemberVoList.get(i);
            GroupMember mGroupMember = new GroupMember.Builder()
                    .xluserid(memberVo.getXlid() + "")
                    .xlGroupId(memberVo.getTeamId() + "")
                    .xlRemarkName(memberVo.getRemarkName() + "")
                    .file_id(memberVo.getImgId() + "")
                    .isContact(memberVo.getIsContact() + "")
                    .isOwner(memberVo.getIsOwner() + "")
                    .sortLetters("")
                    .groupmemberid(GroupMemberDBHandler.getMemberId(memberVo.getTeamId()+"",memberVo.getXlid() + "", ""))
                    .build();
            gm.add(mGroupMember);
        }

        mGroupMemberDBHandler.addlist(gm);

        return gm;

    }

    /**
     * 网络回调
     */
    public static interface CallBack {

        void success(List<GroupMember> list);

        void failed(String errMsg, int type);

    }

}
