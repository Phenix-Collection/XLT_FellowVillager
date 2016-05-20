package com.xianglin.fellowvillager.app.rpc.service;

import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.mobile.common.rpc.proxy.OperationType;
import com.xianglin.xlappcore.common.service.facade.base.CommonReq;
import com.xianglin.xlappcore.common.service.facade.base.CommonResp;
import com.xianglin.xlappcore.common.service.facade.vo.ContactListVo;
import com.xianglin.xlappcore.common.service.facade.vo.ContactVo;
import com.xianglin.xlappcore.common.service.facade.vo.LocationVo;
import com.xianglin.xlappcore.common.service.facade.vo.TeamVo;
import com.xianglin.xlappcore.common.service.facade.vo.UserVo;
import com.xianglin.xlappcore.common.service.facade.vo.XlidVo;

/**
 * 用户服务
 *
 * @author songdiyuan
 * @version $Id: UserService.java, v 1.0.0 2015-11-4 下午5:09:02 xl Exp $
 */
public interface UserService {


    /**
     * 执行登录
     *
     * @param loginReq
     * @return
     */

    @OperationType(BorrowConstants.INTERFACE_URL_USERSERVICE_LOGIN)
    CommonResp<UserVo> login(CommonReq loginReq);

    /**
     * 随机获取 乡邻ID ［数量 ：3］
     *
     * @param proRegistReq
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_USERVERVICE_PREREGISTER)
    CommonResp<XlidVo> preRegist(CommonReq proRegistReq);

    /**
     * 用户注册
     *
     * @param register
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_USERVERVICE_REGISTER)
    CommonResp<UserVo> register(CommonReq register);

    /**
     * 联系人管理 提供［增、删、改、查］ 联系人详情
     *
     * @param contactManage
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_USERVERVICE_CONTACTMANAGE)
    CommonResp<ContactVo> contactManage(CommonReq contactManage);

    /**
     * 群组管理
     * 建群，加入群，踢人，退群，解散，查看群详情等
     *
     * @param teamManage
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_USERVERVIC_TEAMMANAGE)
    CommonResp<TeamVo> teamManage(CommonReq teamManage);

    /**
     * 联系人列表
     * 操作类型，取联系人列表，群列表，联系人和群列表
     *
     * @param contactList
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_USERVERVIC_CONTACTLIST)
    CommonResp<ContactListVo> contactList(CommonReq contactList);

    /**
     * 联系人列表
     * 获取App位置信息
     *
     * @param location
     * @return
     */
    @Deprecated
    @OperationType(BorrowConstants.INTERFACE_URL_USERVERVIC_SUBMITLOCATION)
    CommonResp<LocationVo> submitLocation(CommonReq location);

    /**
     * 联系人列表
     * 获取附近的人信息
     *
     * @param nearby 附近的人
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_USERVERVIC_FINDNEARBYS)
    CommonResp<LocationVo> findNearbys(CommonReq nearby);


    /**
     * 群成员
     * @param nearby
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_USERVERVIC_MEMBERLIST)
    CommonResp<ContactListVo> memberList(CommonReq nearby);



}