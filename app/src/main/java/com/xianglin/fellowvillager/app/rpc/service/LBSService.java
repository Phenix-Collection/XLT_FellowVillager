package com.xianglin.fellowvillager.app.rpc.service;

import com.xianglin.appserv.common.service.facade.model.LocationInfoDTO;
import com.xianglin.appserv.common.service.facade.model.UserFigureDTO;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.mobile.common.rpc.proxy.OperationType;

import java.util.List;

/**
 * 地理位置相关服务
 * Created by zhanglisan on 3/2/16.
 */
public interface LBSService {

    /**
     * 地理位置信息上报
     *
     * @param mLocationInfoDTO
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_LBSSERVICE_REPORTLOCATION)
    Boolean reportLocation(LocationInfoDTO mLocationInfoDTO);

    /**
     * 查找附近联系人
     *
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_LBSSERVICE_FINDNEARBYUSERS)
    List<UserFigureDTO> findNearbyUsers(LocationInfoDTO mLocationInfoDTO);
}
