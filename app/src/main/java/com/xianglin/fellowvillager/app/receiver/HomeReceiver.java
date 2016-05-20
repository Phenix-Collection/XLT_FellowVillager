package com.xianglin.fellowvillager.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xianglin.fellowvillager.app.XLApplication;

/**
 * home键的监听
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-08
 */
public class HomeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
            String reason = intent.getStringExtra("reason");

            if(reason != null){
                if(reason.equals("homekey")){
                    XLApplication.isHome = true;
                }else if(reason.equals("recentapps")){
                    XLApplication.isHome = true;
                }
            }
        }
    }
}
