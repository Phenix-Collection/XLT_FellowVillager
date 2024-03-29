/**
 * 文件名: NetworkTool.java
 * 版    权：  Copyright  LiJinHua  All Rights Reserved.
 * 描    述: [常量类]
 * 创建人: LiJinHua
 * 创建时间:  2014-1-7
 * 
 * 修改人：LiJinHua
 * 修改时间:2014-1-7  下午3:20:05
 * 修改内容：[修改内容]
 */
package com.xianglin.fellowvillager.app.widget.webview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkTool {


	public NetworkTool() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 判断网络是否畅通
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
