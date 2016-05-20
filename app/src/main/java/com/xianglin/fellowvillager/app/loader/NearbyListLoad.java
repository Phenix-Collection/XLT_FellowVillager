
/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.os.Looper;

import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.widget.dialog.LoadingDialog;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.xlappcore.common.service.facade.base.CommonReq;
import com.xianglin.xlappcore.common.service.facade.vo.LocationVo;
import com.xianglin.xlappcore.common.service.facade.vo.UserLocationVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 加载附近联系人
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/20 13:06  XLXZ Exp $
 */
public class NearbyListLoad extends DataLoader<List<UserLocationVo>> {

    private LoadingDialog mLoadingDialog;
    private Context mContext;

    public NearbyListLoad(Context context) {
        super(context);
        mLoadingDialog = new LoadingDialog(context);
        mContext = context;
    }

    @Override
    public List<UserLocationVo> loadInBackground() {

        final List<UserLocationVo> list = new ArrayList<UserLocationVo>();


        final double longitude = DeviceInfo.getInstance().getLongitudeDoube();
        final double latitude = DeviceInfo.getInstance().getLatitudeDoube();
        //final double longitude = Double.parseDouble(Utils.getValue(BorrowConstants.LOCATION_LONGITUDE));
        //final double latitude =  Double.parseDouble(Utils.getValue(BorrowConstants.LOCATION_LATITUDE));

        CommonReq commonReq = new CommonReq();
        commonReq.setBody(new HashMap<String, Object>() {
            {
                if (Utils.isValidLatAndLon(longitude, latitude)) {
                    put("longitude", longitude);
                    put("latitude", latitude);
                }
            }

        });

        Looper.prepare();
        mLoadingDialog.show();
        Looper.loop();

        SyncApi.getInstance().findNearbys(mContext, commonReq, new SyncApi.CallBack<LocationVo>() {

            @Override
            public void success(LocationVo mode) {
                list.addAll(mode.getNearbys());
                Looper.prepare();

                mLoadingDialog.dismiss();
                Looper.loop();
            }

            @Override
            public void failed(String errMsg, int type) {
                if (!(mContext instanceof BaseActivity)) {
                    return;
                }
                ((BaseActivity)mContext).tip(errMsg);
                ((BaseActivity)mContext).runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                mLoadingDialog.dismiss();
                            }
                        }
                );
            }
        });

        return list;
    }

}
