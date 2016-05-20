package com.xianglin.fellowvillager.app.rpc.remote;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.xianglin.appserv.common.service.facade.model.ContactsDTO;
import com.xianglin.appserv.common.service.facade.model.ContactsRelationRequest;
import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.appserv.common.service.facade.model.GroupMemberDTO;
import com.xianglin.appserv.common.service.facade.model.GroupOperationRequest;
import com.xianglin.appserv.common.service.facade.model.LocationInfoDTO;
import com.xianglin.appserv.common.service.facade.model.UserFigureDTO;
import com.xianglin.appserv.common.service.facade.model.UserFigureIdDTO;
import com.xianglin.cif.common.service.facade.model.DeviceInfo;
import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.cif.common.service.facade.model.LoginInfo;
import com.xianglin.cif.common.service.facade.model.enums.DevicePlatform;
import com.xianglin.cif.common.service.facade.model.enums.SystemType;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.constants.ENVController;
import com.xianglin.fellowvillager.app.constants.ResultEnum;
import com.xianglin.fellowvillager.app.db.UserDBHandler;
import com.xianglin.fellowvillager.app.model.User;
import com.xianglin.fellowvillager.app.rpc.service.AppRegisterLoginService;
import com.xianglin.fellowvillager.app.rpc.service.ContactsService;
import com.xianglin.fellowvillager.app.rpc.service.FigureService;
import com.xianglin.fellowvillager.app.rpc.service.GroupService;
import com.xianglin.fellowvillager.app.rpc.service.LBSService;
import com.xianglin.fellowvillager.app.rpc.service.QRCodeService;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.mobile.common.rpc.Config;
import com.xianglin.mobile.common.rpc.RpcException;
import com.xianglin.mobile.common.transport.Transport;
import com.xianglin.mobile.framework.service.common.HttpTransportSevice;
import com.xianglin.mobile.framework.service.common.RpcService;
import com.xianglin.mobile.framework.service.common.impl.HttpTransportSeviceImpl;
import com.xianglin.mobile.framework.service.common.impl.RpcServiceImpl;
import com.xianglin.xlappcore.common.service.facade.base.CommonReq;

import java.util.HashMap;
import java.util.List;

/**
 * 远程服务接口调用
 *
 * @author songdiyaun
 * @version $Id: SyncApi.getInstance().java, v 1.0.0 2015-8-7 下午6:19:43 xl Exp $
 */
public class SyncApi {
    private static SyncApi INSTANCE;
    private final static String TAG = SyncApi.class.getSimpleName();
    private RpcService mRpcService;


    private SyncApi() {
        mRpcService = new RpcServiceImpl(mConfig);
    }

    public static synchronized SyncApi getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SyncApi();
        }
        return INSTANCE;
    }

    private Config mConfig = new Config() {

        @Override
        public String getUrl() {
            return ENVController.URL;
        }

        @Override
        public Transport getTransport() {
            // TODO by alex 直接生成实例
            Object t = RpcServiceImpl.mService.get(HttpTransportSevice.class
                    .getName());
            if (t == null) {
                t = new HttpTransportSeviceImpl();
                RpcServiceImpl.mService.put(
                        HttpTransportSevice.class.getName(), t);
            }
            return (Transport) t;
        }
    };

    private <T> T getRpcProxy(Class<T> clazz) {
        // 先要取得远程服务，然后才是调用该服务中的方法
        return mRpcService.getRpcProxy(clazz);
    }

    /**********************************************************************************
     * **********************设备激活、注册、登录相关接口 start********************************
     ********************************************************************************/

    /**
     * 激活设备
     *
     * @param deviceInfo 当前的设备信息
     * @param context
     * @param callBack
     */
    public void activateDevice(DeviceInfo deviceInfo, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        AppRegisterLoginService service = getRpcProxy(AppRegisterLoginService.class);

        String initDataResp = null;
        try {
            initDataResp = service.activateDevice(deviceInfo);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 获取未使用的角色id
     *
     * @param IdNum    一次申请figureid的个数
     * @param context
     * @param callBack
     */
    public void getUnusedFigureIds(int IdNum, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        AppRegisterLoginService service = getRpcProxy(AppRegisterLoginService.class);

        List<String> initDataResp = null;
        try {
            initDataResp = service.getUnusedFigureIds(IdNum);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 退出
     *
     * @param context
     * @param callBack
     */
    public void logout(Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        AppRegisterLoginService service = getRpcProxy(AppRegisterLoginService.class);

        Boolean initDataResp = null;
        try {
            initDataResp = service.logout();
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 注册
     *
     * @param figureId 角色名称
     * @param password 角色密码
     * @param context
     * @param callBack
     */
    public void register(String figureId, String password,
                         Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        AppRegisterLoginService service = getRpcProxy(AppRegisterLoginService.class);

        FigureDTO initDataResp = null;
        try {
            initDataResp = service.register(figureId, password);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 登录
     *
     * @param paramLoginInfo  登录信息
     * @param paramDeviceInfo 设备信息
     * @return
     */
    public void login(LoginInfo paramLoginInfo, DeviceInfo paramDeviceInfo,
                      Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        AppRegisterLoginService service = getRpcProxy(AppRegisterLoginService.class);

        String initDataResp = null;
        try {
            initDataResp = service.login(paramLoginInfo, paramDeviceInfo);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 自动登录
     *
     * @param paramLoginInfo  登录信息
     * @param paramDeviceInfo 设备信息
     * @param context
     * @param callBack
     */
    public void autoLogin(LoginInfo paramLoginInfo, DeviceInfo paramDeviceInfo,
                          Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        AppRegisterLoginService service = getRpcProxy(AppRegisterLoginService.class);

        String initDataResp = null;
        try {
            initDataResp = service.autoLogin(paramLoginInfo, paramDeviceInfo);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**********************************************************************************
     ***********************设备激活、注册、登录相关接口 end********************************
     * ********************************************************************************/


    /**********************************************************************************
     ***********************用户身份角色相关接口 start*************************************
     * ********************************************************************************/

    /**
     * 当前用户新建身份角色
     *
     * @param figureId 身份角色唯一标识
     * @param open     是否公开
     */
    public void create(String figureId, boolean open, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        FigureService service = getRpcProxy(FigureService.class);

        FigureDTO initDataResp = null;
        try {
            initDataResp = service.create(figureId, true);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 更新当前用户的身份角色信息
     *
     * @param figureDTO
     */
    public void update(FigureDTO figureDTO, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        FigureService service = getRpcProxy(FigureService.class);

        Boolean initDataResp = null;
        try {
            LogCatLog.e(TAG, "111111 SYNCAPI FIGUREDTO = " + figureDTO);
            initDataResp = service.update(figureDTO);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 获取当前用户的指定身份角色的详情
     *
     * @param figureId
     */
    public void detail(String figureId, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        FigureService service = getRpcProxy(FigureService.class);

        FigureDTO initDataResp = null;
        try {
            initDataResp = service.detail(figureId);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 查询用户所有身份角色列表
     */
    public void list(Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        FigureService service = getRpcProxy(FigureService.class);

        List<FigureDTO> initDataResp = null;
        try {
            initDataResp = service.list();
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 查询指定的联系人信息
     *
     * @param <T>
     * @return
     */
    public <T> void getByContacts(Context context, String contactsUserId, String contactsFigureId, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        ContactsService service = getRpcProxy(ContactsService.class);

        List<ContactsDTO> initDataResp = null;
        try {
            initDataResp = service.getByContacts(contactsUserId, contactsFigureId);
            if (initDataResp != null)
                callBack.success(initDataResp);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 查找相同联系人
     *
     * @param otherFigureId 另一用户的身份角色唯一标识
     * @param context       上下文
     * @param callBack      回调函数
     */
    public void sameContacts(
            String otherFigureId,
            Context context,
            CallBack callBack
    ) {
        ContactsService service = getRpcProxy(ContactsService.class);
        List<String> response;
        try {
            response = service.sameContacts(
                    otherFigureId
            );
            LogCatLog.e(TAG, "sameContacts response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }


    /**********************************************************************************
     ***********************用户身份角色相关接口 end*************************************
     * ********************************************************************************/

    /**********************************************************************************
     ***********************地理位置相关接口 start*************************************
     * ********************************************************************************/

    /**
     * 地理位置信息上报
     *
     * @param lidto
     * @return
     */
    public void reportLocation(LocationInfoDTO lidto, Context context, CallBack callBack) {
        LBSService service = getRpcProxy(LBSService.class);
        Boolean response = false;
        try {
            response = service.reportLocation(lidto);
            LogCatLog.e(TAG, "response=" + response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 查找附近联系人
     *
     * @return
     */
    public void findNearbyUsers(LocationInfoDTO lidto, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "login" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        LBSService service = getRpcProxy(LBSService.class);
        List<UserFigureDTO> mList_UserFigureDTO = null;
        try {
            mList_UserFigureDTO = service.findNearbyUsers(lidto);
            callBack.success(mList_UserFigureDTO);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**********************************************************************************
     ***********************地理位置相关接口 end*************************************
     * ********************************************************************************/


    /**********************************************************************************
     ***********************群相关接口 start********************************************
     * ********************************************************************************/

    /**
     * 创建群
     *
     * @param figureId
     * @param memberList
     * @return
     */
    public GroupDTO create(
            Context context,
            String figureId,
            List<UserFigureIdDTO> memberList,
            CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        GroupDTO response = null;
        try {
            response = service.create(figureId, memberList);
            LogCatLog.e(TAG, "create(String figureId, List<UserFigureIdDTO> memberList) response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 解散群
     *
     * @param groupId
     * @return
     */
    public Boolean dismiss(String groupId, Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        Boolean response = false;
        try {
            response = service.dismiss(groupId);
            LogCatLog.e(TAG, "response=" + response);
            if (response != null)
                callBack.success(response);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 群详情
     *
     * @param groupId
     * @param figureId
     * @return
     */
    public GroupDTO detail(String groupId, String figureId, Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        GroupDTO response = null;
        try {
            response = service.detail(groupId, figureId);
            LogCatLog.e(TAG, "detail(String groupId,String figureId) response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 更新群
     *
     * @param groupDTO
     * @return
     */
    public Boolean update(GroupDTO groupDTO, Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        Boolean response = false;
        try {
            response = service.update(groupDTO);
            LogCatLog.e(TAG, "update(GroupDTO groupDTO) response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 查询当前用户的所有身份角色所在的群组的列表
     *
     * @return
     */
    public List<GroupDTO> listGroup(Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        List<GroupDTO> response = null;
        try {
            response = service.list();
            LogCatLog.e(TAG, "listGroup() response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 查询当前用户的指定身份角色所在的群组的列表
     *
     * @param figureId
     * @return
     */
    public List<GroupDTO> listGroup(String figureId, Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        List<GroupDTO> response = null;
        try {
            response = service.list(figureId);
            LogCatLog.e(TAG, "listGroup(String figureId) response=" + response);
            if (response != null) {
                callBack.success(response);
            } else {
                callBack.failed("网络数据错误", 0);
            }
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 用户主动加入群组
     *
     * @param figureId  当前用户身份角色唯一标识
     * @param token     二维码中的token信息
     * @return
     */
    public Boolean join(String figureId, String token, Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        Boolean response = false;
        try {
            response = service.join(figureId, token);
            LogCatLog.e(TAG, "join(GroupRelationRequest groupRelationRequest) response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 用户主动退出群组
     *
     * @return
     */
    public Boolean quit(String figureId, String groupId, Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        Boolean response = false;
        try {
            response = service.quit(figureId, groupId);
            LogCatLog.e(TAG, "quit(GroupRelationRequest groupRelationRequest) response=" + response);
            if (response != null)
                callBack.success(response);
            else
                callBack.failed("网络数据错误", 0);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 群详情
     *
     * @param groupId
     * @return
     */
    public List<GroupMemberDTO> members(String groupId, Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        List<GroupMemberDTO> response = null;
        try {
            response = service.members(groupId);
            LogCatLog.e(TAG, "members(String figureId) response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 批量加人
     *
     * @param groupOperationRequest
     * @return
     */
    public Boolean invite(GroupOperationRequest groupOperationRequest,
                          Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        Boolean response = false;
        try {
            response = service.invite(groupOperationRequest);
            LogCatLog.e(TAG, "invite(GroupOperationRequest groupOperationRequest) response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 批量踢人
     *
     * @param groupOperationRequest
     * @return
     */
    public Boolean kick(GroupOperationRequest groupOperationRequest,
                        Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        Boolean response = false;
        try {
            response = service.kick(groupOperationRequest);
            LogCatLog.e(TAG, "kick(GroupOperationRequest groupOperationRequest) response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /* 群移出黑名单
     * @param figureId
     * @param groupId
     * @return
     */
    public Boolean moveToBlackList(String figureId, String groupId,
                                   Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        Boolean response = false;
        try {
            response = service.moveIntoBlacklist(figureId, groupId);
            LogCatLog.e(TAG, "moveToBlackList(String figureId, String groupId) response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /* 群移出黑名单
    * @param figureId
    * @param groupId
    * @return
    */
    public void moveOutofBlackList(String figureId, String groupId,
                                   Context context, CallBack callBack) {
        GroupService service = getRpcProxy(GroupService.class);
        Boolean response = false;
        try {
            response = service.moveOutofBlackList(figureId, groupId);
            LogCatLog.e(TAG, "moveOutofBlackList response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 查找共同群组
     *
     * @param otherFigureId 另一用户的身份角色唯一标识
     * @param context       上下文
     * @param callBack      回调函数
     */
    public void sameGroups(
            String otherFigureId,
            Context context,
            CallBack callBack
    ) {
        GroupService service = getRpcProxy(GroupService.class);
        List<String> response;
        try {
            response = service.sameGroups(
                    otherFigureId
            );
            LogCatLog.e(TAG, "sameGroups response=" + response);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(
                    e,
                    context,
                    callBack
            );
        }
    }

    /**********************************************************************************
     ***********************群相关接口 end********************************************
     * ********************************************************************************/

    /**********************************************************************************
     ***********************联系人相关接口 start********************************************
     * ********************************************************************************/

    /**
     * 添加联系人
     *
     * @param requ
     * @return
     */
    public Boolean add(ContactsRelationRequest requ, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "add" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        ContactsService service = getRpcProxy(ContactsService.class);
        Boolean response = false;
        try {
            response = service.add(requ);
            callBack.success(response);
        } catch (Exception e) {
            /**为了兼容小米收消息功能,给陌生人发送一条消息会连调两次add方法,第二次报错,暂时注释掉*/
//            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 修改联系人的备注名
     *
     * @param figureId         当前用户所使用的身份角色唯一标识
     * @param contactsUserId   要修改的联系人的用户唯一标识
     * @param contactsFigureId 要修改的联系人的身份角色唯一标识
     * @param remarkName       联系人备注名
     * @return
     */
    public Boolean update(String figureId, String contactsUserId,
                          String contactsFigureId, String remarkName,
                          Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "update" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        ContactsService service = getRpcProxy(ContactsService.class);
        Boolean response = false;
        try {
            response = service.update(figureId, contactsUserId, contactsFigureId, remarkName);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return response;
    }

    /**
     * 获取当前用户的所有身份角色的联系人列表
     *
     * @param context  上下文
     * @param callBack 回调
     * @return 当前用户的所有身份角色的联系人列表
     */
    public List<ContactsDTO> lists(
            Context context,
            CallBack callBack
    ) {
        LogCatLog.i(TAG, "Interface  " + "lists" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        ContactsService service = getRpcProxy(ContactsService.class);
        List<ContactsDTO> mList = null;
        try {
            mList = service.lists();
            callBack.success(mList);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
        return mList;
    }

    /**
     * 获取当前用户的指定身份角色的联系人列表
     *
     * @param figureId 当前角色Id
     * @param context
     * @param callBack
     * @return
     */
    public List<ContactsDTO> listByFigureId(
            String figureId,
            Context context,
            CallBack callBack
    ) {
        if (TextUtils.isEmpty(figureId)) {
            return null;
        }
        LogCatLog.i(TAG, "Interface  " + "listByFigureId" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        ContactsService service = getRpcProxy(ContactsService.class);
        List<ContactsDTO> mList = null;
        try {
            mList = service.listByFigureId(figureId);
            callBack.success(mList);
        } catch (Exception e) {
            netWorkExceptionHandling(
                    e,
                    context,
                    callBack
            );
        }
        return mList;
    }

    /**
     * 移入黑名单
     *
     * @param figureId         当前用户身份角色唯一标识 可为null
     * @param contactsUserId   联系人用户唯一标识 可为null
     * @param contactsFigureId 联系人身份角色唯一标识
     * @return
     */
    public void moveIntoBlacklist(String figureId,
                                  String contactsUserId, String contactsFigureId,
                                  Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "moveIntoBlacklist" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        ContactsService service = getRpcProxy(ContactsService.class);
        Boolean response = false;
        try {
            response = service.moveIntoBlacklist(figureId, contactsUserId, contactsFigureId);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 移出黑名单
     *
     * @param figureId         当前用户身份角色唯一标识 可为null
     * @param contactsUserId   联系人用户唯一标识 可为null
     * @param contactsFigureId 联系人身份角色唯一标识
     * @return
     */
    public void moveOutofBlacklist(String figureId, String contactsUserId, String contactsFigureId,
                                   Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface  " + "moveOutofBlacklist" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        ContactsService service = getRpcProxy(ContactsService.class);
        Boolean response = false;
        try {
            response = service.moveOutofBlacklist(figureId, contactsUserId, contactsFigureId);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 二维码加联系人
     *
     * @param figureId 当前用户的身份角色唯一标识
     * @param token    二维码token信息
     * @return true 添加成功; false 添加失败
     */
    public void addByQRCode(String figureId, String token,
                            Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface " + "addByQRCode" + "-->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        ContactsService service = getRpcProxy(ContactsService.class);
        String response = null;
        try {
            response = service.addByQRCode(figureId, token);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**********************************************************************************
     ***********************联系人相关接口 end********************************************
     * ********************************************************************************/

    /**
     * 获取角色二维码token
     *
     * @param figureId 当前用户身份角色唯一标识
     */
    public void getUserToken(String figureId, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface -->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        QRCodeService service = getRpcProxy(QRCodeService.class);
        String response = null;
        try {
            response = service.getUserToken(figureId);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 获取群二维码token
     *
     * @param groupId  群的唯一标识
     */
    public void getGroupToken(String groupId, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface -->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        QRCodeService service = getRpcProxy(QRCodeService.class);
        String response = null;
        try {
            response = service.getGroupToken(groupId);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }
    /**
     * 根据角色二维码token获取角色详情
     *
     * @param token  角色token
     */
    public void getUserFigureByToken(String token, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface -->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        QRCodeService service = getRpcProxy(QRCodeService.class);
        UserFigureDTO response = null;
        try {
            response = service.getUserFigureByToken(token);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }
    /**
     * 根据群二维码token获取群详情
     *
     * @param token  群token
     */
    public void getGroupByToken(String token, Context context, CallBack callBack) {
        LogCatLog.i(TAG, "Interface -->>begin rpc: "
                + Thread.currentThread().getStackTrace()[2].getMethodName());
        QRCodeService service = getRpcProxy(QRCodeService.class);
        GroupDTO response = null;
        try {
            response = service.getGroupByToken(token);
            callBack.success(response);
        } catch (Exception e) {
            netWorkExceptionHandling(e, context, callBack);
        }
    }

    /**
     * 获取附近联系人
     *
     * @param context
     * @param commonReq
     * @param callBack
     */
    public <T> void findNearbys(final Context context, final CommonReq commonReq, final CallBack<T> callBack) {
    }

    /**
     * 用户注册
     *
     * @param mXlid     乡邻id
     * @param mTempXlid 乡邻临时id,用来恢复未注册的id
     * @param nickname  用户名
     * @param context
     * @param callBack
     */

    public <T> void register(final String mXlid, final String mTempXlid, final String imageId, final String nickname, final Context
            context, final CallBack<T> callBack) {
        final CommonReq commonReq = new CommonReq();
        commonReq.setBody(new HashMap<String, Object>() {
            {
                put("tempXlid", Long.parseLong(mTempXlid));
                put("xlid", Long.parseLong(mXlid));
                put("password", "123456");
                put("trueName", nickname);
                put("imgId", imageId);
                put("deviceType", BorrowConstants.DEVICE_TYPE);
                put("androidCpu", PersonSharePreference.getAndroidCpu());
                put("androidSerialId", PersonSharePreference.getAndroidSerialId());
                put("androidMac", PersonSharePreference.getAndroidMac());
                put("androidBtMac", PersonSharePreference.getAndroidBtMac());
                put("androidImei", PersonSharePreference.getAndroidImei());
                put("androidImsi", PersonSharePreference.getAndroidImsi());
            }

        });
    }

    /**
     * 联系人管理 提供［增、删、改、查］
     *
     * @param context
     * @param commonReq
     * @param callBack
     */
    public void contactManage(final Context context, final CommonReq commonReq, final CallBack callBack) {
    }


    /**
     * 群组管理
     * 建群，加入群，踢人，退群，解散，查看群详情等
     *
     * @param context
     * @param commonReq
     * @param callBack
     */
    public void teamManage(final Context context, final CommonReq commonReq, final CallBack callBack) {
    }

    /**
     * 群成员
     *
     * @param context
     * @param commonReq
     * @param callBack
     */
    public void teamMember(final Context context, final CommonReq commonReq, final CallBack callBack) {
    }

    /**
     * 联系人列表
     * 操作类型，取联系人列表，群列表，联系人和群列表
     *
     * @param context
     * @param commonReq
     * @param callBack
     */
    public void contactList(final Context context, final CommonReq commonReq, final CallBack callBack) {
    }

    /**
     * 网络异常统一处理
     */
    private void netWorkExceptionHandling(Exception e, Context context, CallBack callBack) {
        if (e != null) {
            LogCatLog.e(TAG, e);
            //// TODO: 2015/11/17  定义错误提示
            if (e instanceof RpcException) {
                RpcException rpcException = (RpcException) e;
                String msg = "";
                if (rpcException != null && context != null) {

                    switch (rpcException.getCode()) {
                        case RpcException.ErrorCode.SERVER_SESSIONSTATUS: // 登录超时
                            final User user = new UserDBHandler(context).query();
                            if (user != null) {
                                autoLoginBySessionTimeout(user, context, callBack);
                            } else {
                                msg = ResultEnum.SessionStatus.getTips();
                                callBack.failed(msg, rpcException.getCode());
                            }
                            break;
                        case RpcException.ErrorCode.SERVER_UNKNOWERROR: // 未知错误
                            msg = ResultEnum.UnknowError.getTips();
                            break;
                        case RpcException.ErrorCode.CLIENT_NETWORK_UNAVAILABLE_ERROR: //网络未连接
                            msg = ResultEnum.NetworkUnavailableError.getTips();
                            break;
                        case RpcException.ErrorCode.CLIENT_NETWORK_CONNECTION_ERROR:
                        case RpcException.ErrorCode.CLIENT_NETWORK_SOCKET_ERROR:// 请求数据超时
                            msg = ResultEnum.NetworkSocketError.getTips();
                            break;
                        case RpcException.ErrorCode.SERVER_GATEWAY_ERROR: // 网关错误
                            msg = ResultEnum.GatewayError.getTips();
                            break;
                        case RpcException.ErrorCode.CLIENT_HANDLE_ERROR: //
                            msg = rpcException.getMsg();
                            break;
                        default:
                            msg = rpcException.getMsg();
                            break;
                    }
                    if (rpcException.getCode() != RpcException.ErrorCode.SERVER_SESSIONSTATUS) {
                        Looper.prepare();
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        callBack.failed(msg, rpcException.getCode());
                    }
                }
            } else {//其他异常处理

            }
        }
    }

    /**
     * session 超时自动登录
     *
     * @param user
     * @param context
     * @param callBack
     */
    private void autoLoginBySessionTimeout(User user, Context context, CallBack callBack) {
        PersonSharePreference.setUserID(user.xlID);
        PersonSharePreference.setUserNickName(user.xlUserName);
        PersonSharePreference.setLogin(true);

        DeviceInfo baseDeviceInfo = new DeviceInfo();
        baseDeviceInfo.setPlatform(DevicePlatform.androidPhone.name());
        baseDeviceInfo.setSystemType(SystemType.ANDROID.name());
        baseDeviceInfo.setSystemVersion(
                com.xianglin.mobile.common.info.DeviceInfo.getInstance()
                        .getmSystemVersion());
        baseDeviceInfo.setWifiMac(PersonSharePreference.getAndroidMac());
        baseDeviceInfo.setBluetoothMac(PersonSharePreference.getAndroidBtMac());
        baseDeviceInfo.setImei(PersonSharePreference.getAndroidImei());
        baseDeviceInfo.setImsi(PersonSharePreference.getAndroidImsi());

        LoginInfo mLoginInfo = new LoginInfo();
        mLoginInfo.setFigureId(user.figureId);
        mLoginInfo.setPassword("");
        mLoginInfo.setClientId("1");
        mLoginInfo.setClientVersion(DeviceInfoUtil.getVersionName(context));
        autoLogin(mLoginInfo, baseDeviceInfo, context, callBack);
        LogCatLog.e(TAG, "111111 networkexception autologin ");
    }

    /**
     * 网络回调
     *
     * @param <T> 实体类
     */
    public interface CallBack<T> {

        /**
         * 成功
         */
        void success(T mode);

        /**
         * 失败
         *
         * @param errTip  错误提示
         * @param errCode 错误码(目前业务异常和网络异常都是resultcode)//errType 错误类型 0、业务异常 1、网络异常
         */
        void failed(String errTip, int errCode);

    }
/*
    *//**
     * 解析json
     *
     * @param jsonString json
     * @param cls        实体类型
     * @param <T>        对象
     * @return
     *//*
    public static <T> T createJsonBean(String jsonString, Class<T> cls) {
        T t = JSON.parseObject(jsonString, cls);
        return t;
    }*/

}
