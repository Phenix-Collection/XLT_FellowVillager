/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.rpc.remote;

import android.content.Context;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.User;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.xlappcore.common.service.facade.base.CommonReq;
import com.xianglin.xlappcore.common.service.facade.vo.UserVo;

/**
 * 封装登录接口
 *
 * @author pengyang
 * @version v 1.0.0 2015/12/5 15:33  XLXZ Exp $
 */
public class LoginSync {

    private static final String TAG = LoginSync.class.getSimpleName();
    private long mBeginTime;
    private long mEndTime;
    private Context mContext;

    public void login(final Context context,final User user,
                      final LoginSync.CallBack calback) {
        this.mContext = context;
        mBeginTime = System.currentTimeMillis();
        CommonReq<UserVo> commonReq = new CommonReq<UserVo>();

        final double longitude = DeviceInfo.getInstance().getLongitudeDoube();
        final double latitude = DeviceInfo.getInstance().getLatitudeDoube();

        UserVo userVo = new UserVo();

        userVo.setXlid(Utils.parseLong(user.xlID));
        userVo.setPassword("123456");
        userVo.setDeviceId(PersonSharePreference.getDeviceId());
        userVo.setDeviceType(BorrowConstants.DEVICE_TYPE);

        if (Utils.isValidLatAndLon(longitude, latitude)) {
            userVo.setLatitude(latitude);
            userVo.setLongitude(longitude);
        }
        commonReq.setBody(userVo);

//        SyncApi.getInstance().login(XLApplication.getInstance(), commonReq, new SyncApi.CallBack<UserVo>() {
//
//            @Override
//            public void success(UserVo mode) {
//                if (calback != null) {
//                    calback.loginSuccess(mode);
//                    beginPolling(mode);
//                }
//                mEndTime = System.currentTimeMillis();
//                LogCatLog.i(TAG, (mEndTime - mBeginTime) + "ms");
//            }
//
//            @Override
//            public void failed(String errMsg, int type) {
//                mEndTime = System.currentTimeMillis();
//                LogCatLog.i(TAG, (mEndTime - mBeginTime) + "ms");
//                if (calback != null) {
//                    calback.loginFailed(errMsg, type);
//                }
//            }
//
//        });
    }

    /**
     * 网络回调
     */
    public interface CallBack {
        void loginSuccess(UserVo mode);

        void loginFailed(String errMsg, int type);
    }


    private void beginPolling(UserVo mode) {
        PersonSharePreference.setUserID(mode.getXlid());
        PersonSharePreference.setUserNickName(mode.getTrueName());
        PersonSharePreference.setLogin(true);
    }

}
