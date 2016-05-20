/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.rpc.remote;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.xlappcore.common.service.facade.base.CommonReq;
import com.xianglin.xlappcore.common.service.facade.vo.ContactListVo;
import com.xianglin.xlappcore.common.service.facade.vo.TeamVo;

import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 封装群接口
 *
 * @author pengyang
 * @version v 1.0.0 2015/12/5 15:33  XLXZ Exp $
 */
public class GroupSync {

    public void groupSync(final GroupSync.CallBack calback) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "GroupSync") {
            @Override
            public void execute() {
                try {
                    getGroupList(calback);
                } catch (Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread
                            .currentThread(), e);
                }
            }
        });
    }

    public   void getGroupList(final GroupSync.CallBack calback) {
        final CommonReq commonReq = new CommonReq();
        final long xlid = PersonSharePreference.getUserID();
        commonReq.setBody(new HashMap<String, Object>() {
            {
                put("xlid", xlid);
                put("operateType", "TEAMLIST");//CONTACTLIST,TEAMLIST,ALL
            }
        });
        SyncApi.getInstance().contactList(XLApplication.getInstance(), commonReq, new SyncApi.CallBack<ContactListVo>
                () {
            @Override
            public void success(ContactListVo mode) {
                List<TeamVo> teamVoList = mode.getTeamList();
                GroupDBHandler groupDBHandler = new GroupDBHandler(XLApplication.getInstance());
                ArrayList<Group> list = new ArrayList<Group>();
                for (int i = 0; teamVoList != null && i < teamVoList.size(); i++) {
                    TeamVo teamVo = teamVoList.get(i);
                    Group agroup = new Group.Builder()
                            .xlID(PersonSharePreference.getUserID() + "")
                            .xlGroupID(teamVo.getTeamId() + "")
                            .xlGroupImagePath(teamVo.getImgId() + "")
                            .xlGroupName(teamVo.getRemarkName() + "")
                            .groupType(teamVo.getTeamType() + "")
                            .xlGroupNumMax(teamVo.getTeamNum() + "")
                            .xlGroupCurrentNum(teamVo.getCurrentNum() + "")
                            .isJoin("1")
                            .build();
                    list.add(agroup);

                    //自动群成员信息
                    new GroupMemberSync(agroup.xlGroupID).autoRequestGroupMember();

                }
                groupDBHandler.addlist(list, false);

                LogCatLog.d("GroupSync","获取群信息success"+list.toString());

                calback.success(list);

            }

            @Override
            public void failed(String errMsg, int type) {

                calback.failed(errMsg,type);
            }
        });
    }


    /**
     * 网络回调
     */
    public static interface CallBack {

        void success(ArrayList<Group> list );

        void failed(String errMsg, int type);

    }


}
