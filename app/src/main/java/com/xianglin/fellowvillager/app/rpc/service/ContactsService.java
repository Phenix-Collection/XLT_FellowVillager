package com.xianglin.fellowvillager.app.rpc.service;

import com.xianglin.appserv.common.service.facade.model.ContactsDTO;
import com.xianglin.appserv.common.service.facade.model.ContactsRelationRequest;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.mobile.common.rpc.proxy.OperationType;

import java.util.List;

/**
 * 联系人相关服务
 * Created by zhanglisan on 3/2/16.
 */
public interface ContactsService {

    /**
     * 添加联系人
     *
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_ADD)
    Boolean add(ContactsRelationRequest mContactsReationRequest);

    /**
     * 修改联系人
     *
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_UPDATE)
    Boolean update(String figureId, String contactsUserId, String contactsFigureId, String remarkName);

    /**
     * 获取当前用户的所有身份角色的联系人列表
     *
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_LIST)
    List<ContactsDTO> lists();

    /**
     * 获取当前用户的指定身份角色的联系人列表
     *
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_LISTBYFIGUREID)
    List<ContactsDTO> listByFigureId(String figureId);

    /**
     * 移入黑名单
     *
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_MOVEINTOBLACKLIST)
    Boolean moveIntoBlacklist(String figureId, String contactsUserId, String contactsFigureId);

    /**
     * 移出黑名单
     *
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_MOVEOUTOFBLACKLIST)
    Boolean moveOutofBlacklist(String figureId, String contactsUserId, String contactsFigureId);

    /**
     * 获取指定联系人信息
     *
     * @param contactsUserId
     * @param contactsFigureId
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_GETBYCONTACTS)
    List<ContactsDTO> getByContacts(String contactsUserId, String contactsFigureId);

    /**
     * 查找相同联系人
     *
     * @param otherFigureId 另一用户的身份角色唯一标识
     * @return 共同联系人的身份角色唯一标识列表（数组）
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_SAMECONTACTS)
    List<String> sameContacts(String otherFigureId);

    /**
     * 二维码加联系人
     *
     * @param figureId 当前用户的身份角色唯一标识
     * @param token    二维码token信息
     * @return true 添加成功; false 添加失败
     */
    @OperationType(BorrowConstants.INTERFACE_URL_CONTACTSSERVICE_ADDBYQRCODE)
    String addByQRCode(String figureId, String token);

}
