package com.xianglin.fellowvillager.app.constants;

import android.content.Context;

import com.xianglin.fellowvillager.app.utils.NativeEncrypt;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * URL控制
 * 
 * @author songdiyuan
 * @version $Id: ENVController.java, v 1.0.0 2015-8-8 下午7:12:26 xl Exp $
 */
public class ENVController {

	// ENV key
	/** 本地 */
	public static final String ENV_LOCAL = "ENV_LOCAL";
	/** 开发 */
	public static final String ENV_DEV = "ENV_DEV";
	/** 测试 */
	public static final String ENV_TEST = "ENV_TEST";
	/** 测试2 */
	public static final String ENV_TEST_2 = "ENV_TEST_2";
	/** 联调 */
	public static final String ENV_LT = "ENV_LT";
	/** 生产 */
	public static final String ENV_PRODUCT = "ENV_PRODUCT";
	/** 预生产 */
	public static final String ENV_PP_PRODUCT = "ENV_PP_PRODUCT";

	/** 接口服务URL地址 */
	public static String URL = "";

	
	/** 二维码MD5加密key */
	public static String MMGWSECRET_FOR_MD5="";
	
	/** 二维码AES加密key */
	public static String MMGWSECRET_FOR_AES="";


	/**文件地址*/
	public static String FILE_HOST = "";
	/**文件端口*/
	public static String FILE_PORT = "";

	/**长连接地址*/
	public static String LONGLINK_HOST = "";
	/**长连接端口*/
	public static String LONGLINK_PORT = "";

	public static String ENV = "";

	/**
	 * 
	 * @param context
	 * @param env
	 */
	public static void initEnv(Context context, String env) {
		ENV = env;
		MMGWSECRET_FOR_MD5= NativeEncrypt.nativeEncryptInstance(context).getRsaByKey("MMGWSECRET_FOR_MD5_KEY");
		MMGWSECRET_FOR_AES = NativeEncrypt.nativeEncryptInstance(context).getRsaByKey("MMGWSECRET_FOR_AES_KEY");
		LogCatLog.i("ENVController","测试！");
		if (ENV_PRODUCT.equals(env)) {
			URL = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey(
					"PRODUCT_URL"); // 生产


		} else if (ENV_PP_PRODUCT.equals(env)) {
			URL = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey(
					"PP_PRODUCT_URL"); // 预生产

			/**文件 、长连接 开发地址*/
			FILE_HOST = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_PP_FILE_HOST");
			FILE_PORT = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_PP_FILE_PORT");
			LONGLINK_HOST = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_PP_LONGLINK_HOST");
			LONGLINK_PORT = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_PP_LONGLINK_PORT");
			/**文件 、长连接 开发地址*/

		} else if (ENV_LOCAL.equals(env)) {
			URL = "http://127.0.0.1:8080/ggw/mgw.htm";// 本地

			FILE_HOST = "";
			FILE_PORT = "";
			LONGLINK_HOST = "";
			LONGLINK_PORT = "";

		} else if (ENV_DEV.equals(env)) {
			URL = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey(
					"ENV_DEV_URL"); // 开发
			URL = "http://appgw.dev.xianglin.com/api.json";

//			URL = "http://appgw.dev.xianglin.com/api.json";
//			URL = "http://appgw-dev.xianglin.cn/api.json";(OK)

			/**文件 、长连接 开发地址*/
			FILE_HOST = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_DEV_FILE_HOST");
			FILE_PORT = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_DEV_FILE_PORT");
			LONGLINK_HOST = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_DEV_LONGLINK_HOST");
			LONGLINK_PORT = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_DEV_LONGLINK_PORT");
			/**文件 、长连接 开发地址*/


		} else if (ENV_TEST.equals(env)) {
			URL = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey(
					"ENV_TEST_URL"); // 测试



			/**文件 、长连接 测试地址*/
			FILE_HOST = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_TEST_FILE_HOST");
			FILE_PORT = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_TEST_FILE_PORT");
			LONGLINK_HOST = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_TEST_LONGLINK_HOST");
			LONGLINK_PORT = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_TEST_LONGLINK_PORT");
			/**文件 、长连接 测试地址*/



		} else if (ENV_TEST_2.equals(env)) {
			URL = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey(
					"ENV_TEST_2_URL"); // 测试2

			/**文件 、长连接 测试地址*/
			FILE_HOST = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_TEST_2_FILE_HOST");
			FILE_PORT = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_TEST_2_FILE_PORT");
			LONGLINK_HOST = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_TEST_2_LONGLINK_HOST");
			LONGLINK_PORT = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey("ENV_TEST_2_LONGLINK_PORT");
			/**文件 、长连接 测试地址*/

		} else if (ENV_LT.equals(env)) {
			URL = NativeEncrypt.nativeEncryptInstance(context).getUrlByKey(
					"ENV_LT_URL"); // 联调

		}

	}

}
