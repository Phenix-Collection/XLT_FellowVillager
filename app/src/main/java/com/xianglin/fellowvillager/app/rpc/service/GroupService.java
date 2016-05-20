package com.xianglin.fellowvillager.app.rpc.service;

import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.appserv.common.service.facade.model.GroupMemberDTO;
import com.xianglin.appserv.common.service.facade.model.GroupOperationRequest;
import com.xianglin.appserv.common.service.facade.model.UserFigureIdDTO;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.mobile.common.rpc.proxy.OperationType;

import java.util.List;
/**
 *
 * 群相关服务
 * @author chengshengli
 * @version v 1.0.0 2016/3/1 15:58 XLXZ Exp $
 */
public interface GroupService {

    /**
     * 创建群组
     * @param figureId
     * @param memberList
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_CREATE)
    GroupDTO create(String figureId, List<UserFigureIdDTO> memberList);

    /**
     * 解散群组
     * @param groupId
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_DEMISS)
    Boolean dismiss(String groupId);

    /**
     * 群详情
     * @param groupId
     * @param figureId
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_DETAIL)
    GroupDTO detail(String groupId,String figureId);

    /**
     *更新群组基本信息
     * @param groupDTO
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_UPDATE)
    Boolean update(GroupDTO groupDTO);

    /**
     * 查询当前用户的所有身份角色所在的群组的列表
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_LIST)
    List<GroupDTO> list();
    /**
     * 查询当前用户的指定身份角色所在的群组的列表
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_LISTBYFIGUREID)
    List<GroupDTO> list(String figureId);

    /**
     * 加群
     * @param figureId  当前用户身份角色唯一标识
     * @param token     二维码中的token信息
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_JOIN)
    Boolean join(String figureId, String token);

    /**
     * 退群
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_QUIT)
    Boolean quit(String figureId, String groupId);

    /**
     * 群成员列表
     * @param groupId
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_MEMBERS)
    List<GroupMemberDTO> members(String groupId);

    /**
     * 群移入黑名单
     * @param figureId
     * @param groupId
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_MOVETOBLACKLIST)
    Boolean moveIntoBlacklist(String figureId, String groupId);

    /**
     * 群移出黑名单
     * @param figureId
     * @param groupId
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_MOVEOUTBLACKLIST)
    Boolean moveOutofBlackList(String figureId, String groupId);

    /**
     * 加人
     * @param groupOperationRequest
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_INVITE)
    Boolean invite(GroupOperationRequest groupOperationRequest);

    /**
     * 踢人
     * @param groupOperationRequest
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_KICK)
    Boolean kick(GroupOperationRequest groupOperationRequest);

    /**
     * 查找共同群组
     * @param otherFigureId 另一用户的身份角色唯一标识
     * @return 共同群组的唯一标识列表（数组）
     */
    @OperationType(BorrowConstants.INTERFACE_URL_GROUPSERVICE_SAMEGROUPS)
    List<String> sameGroups(String otherFigureId);


}
