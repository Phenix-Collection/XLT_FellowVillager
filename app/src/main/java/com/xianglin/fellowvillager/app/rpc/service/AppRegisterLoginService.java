package com.xianglin.fellowvillager.app.rpc.service;

import com.xianglin.cif.common.service.facade.model.DeviceInfo;
import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.cif.common.service.facade.model.LoginInfo;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.mobile.common.rpc.proxy.OperationType;

import java.util.List;

/**
 * 设备激活、注册、登录相关服务
 * Created by yangjibin on 2016/2/27.
 */
public interface AppRegisterLoginService {

    @OperationType(BorrowConstants.INTERFACE_URL_APPREGISTERLOGINSERVICE_RIGISTER)
    FigureDTO register(String paramString1, String paramString2);
//    Response<FigureDTO> register(String paramString1, String paramString2);

    @OperationType(BorrowConstants.INTERFACE_URL_APPREGISTERLOGINSERVICE_LOGIN)
    String login(LoginInfo paramLoginInfo, DeviceInfo paramDeviceInfo);

    @OperationType(BorrowConstants.INTERFACE_URL_APPREGISTERLOGINSERVICE_AUTOLOGIN)
    String autoLogin(LoginInfo paramLoginInfo, DeviceInfo paramDeviceInfo);

    @OperationType(BorrowConstants.INTERFACE_URL_APPREGISTERLOGINSERVICE_LOGOUT)
    Boolean logout();

    @OperationType(BorrowConstants.INTERFACE_URL_APPREGISTERLOGINSERVICE_GETUNUSEDFIGUREIDS)
    List<String> getUnusedFigureIds(int paramInt);

    @OperationType(BorrowConstants.INTERFACE_URL_APPREGISTERLOGINSERVICE_ACTIVATEDEVICE)
    String activateDevice(DeviceInfo deviceInfo);
}
