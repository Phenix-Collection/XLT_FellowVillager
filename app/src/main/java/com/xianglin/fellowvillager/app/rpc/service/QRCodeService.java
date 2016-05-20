package com.xianglin.fellowvillager.app.rpc.service;

import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.mobile.common.rpc.proxy.OperationType;
import com.xianglin.appserv.common.service.facade.model.UserFigureDTO;
import com.xianglin.appserv.common.service.facade.model.GroupDTO;

/**
 * Created by yangjibin on 16/3/17.
 */
public interface QRCodeService {

    @OperationType(BorrowConstants.INTERFACE_URL_QRCODESERVICE_GETUSERTOKEN)
    String getUserToken(String figureId);

    @OperationType(BorrowConstants.INTERFACE_URL_QRCODESERVICE_GETGROUPTOKEN)
    String getGroupToken(String groupId);

    @OperationType(BorrowConstants.INTERFACE_URL_QRCODESERVICE_GETUSERFIGUREBYTOKEN)
    UserFigureDTO getUserFigureByToken(String token);

    @OperationType(BorrowConstants.INTERFACE_URL_QRCODESERVICE_GETGROUPBYTOKEN)
    GroupDTO getGroupByToken(String token);
}
