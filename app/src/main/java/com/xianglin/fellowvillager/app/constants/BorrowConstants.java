package com.xianglin.fellowvillager.app.constants;

import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统常量
 *
 * @author songdiyuan
 * @version $Id: BorrowConstants.java, v 1.0.0 2015-8-7 下午4:17:56 xl Exp $
 */
public interface BorrowConstants {

    List<PhotoModel> pathList = new ArrayList<PhotoModel>();//图片路径列表
    String INNER_DBNAME = "xl.db";

    String DEVICE_TYPE = "1";//设备类型(0：IOS,1:ANDROID)

    int CHATTYPE_SINGLE = 0;//点对点
    int CHATTYPE_GROUP = 1;//群聊
    int CHATTYPE_SYS = 2;//系统消息 暂时改成0 现在改成2了
    String CHATTYPE_ADD = "group_add";//建群
    String CHATTYPE_JOIN = "group_join";//添加群成员
    int UPDATE_GROUP_TITLE = 0X100; //修改群名称;
    int MSGSTATUS_SEND = -1; //发送中
    int MSGSTATUS_OK = 0; //发送成功
    int MSGSTATUS_FAIL = 1; //发送失败
    int MSGSTATUS_READ = 2; //已读
    int MSGSTATUS_UNREAD = 3; //未读
    int MSGSTATUS_INPROGRESS = 4; //接收中
    int MSGSTATUS_RECEIVE_FAIL = 5; //接受失败

    int IS_NO_CONTACT = 0;//不是联系人
    int IS_CONTACT = 1;//是联系人

    String IS_JOIN_GROUP = "1";//在群中
    String IS_NO_JOIN_GROUP = "0";// 不在群中

    //经度
    String LOCATION_LONGITUDE = "loc_lngi";
    //纬度
    String LOCATION_LATITUDE = "loc_lati";
    //当前位置
    String LOCATION_ADDRESS = "loc_addr";

    String TYPE_INFO = "type_info";//个人资料
    String TYPE_ADD = "type_add";//新建角色

    String INTERFACE_URL_PRE1 = "com.xianglin.xlappcore.common.service.facade";
    String INTERFACE_URL_PRE2 = "com.xianglin.appserv.common.service.facade";
    String INTERFACE_URL_PRE3 = "com.xianglin.cif.common.service.facade";

    /**
     * 下面四个常量用于Activity之间跳转时的条件判断
     */
    String NEAR_PEOPLE = "near_people";// 附近的人
    String CHAT_CHAT = "chat_chat";
    String QRCODE_SCAN = "qrcode_scan";// 扫一扫
    String NEW_CONTACT = "new_contact";// 新联系人
    String MINGLU = "minglu";// 名录


    String CONTACT_KEY = "contact_" + PersonSharePreference.getUserID()
            + "_" + ContactManager.getInstance().getCurrentFigureID();
    String GROUP_KEY = "group_" + PersonSharePreference.getUserID()
            + "_" + ContactManager.getInstance().getCurrentFigureID();

    String SECRET_END_ACTION = "com.xianglin.secret_end_action";//私密消息结束ACTION

    /**
     * 接口参数 命名如：INTERFACE_URL_XXX_XXX_XXX_XXX
     */

    /* 系统登出 */
    String INTERFACE_URL_USERSERVICE_LOGIN = INTERFACE_URL_PRE1 + ".UserService.login";


    /**
     * 获取随机分配乡邻ID
     * 默认三个
     */
    String INTERFACE_URL_USERVERVICE_PREREGISTER = INTERFACE_URL_PRE1 + ".UserService.preRegister";

    /**
     * 用户注册
     */
    String INTERFACE_URL_USERVERVICE_REGISTER = INTERFACE_URL_PRE1 + ".UserService.register";


    /**
     * 联系人管理 提供[增、删、 改、 查] 查看联系人详情
     */
    String INTERFACE_URL_USERVERVICE_CONTACTMANAGE = INTERFACE_URL_PRE1 + ".UserService.contactManage";

    /**
     * 群组管理
     * 建群，加入群，踢人，退群，解散，查看群详情等
     */
    String INTERFACE_URL_USERVERVIC_TEAMMANAGE = INTERFACE_URL_PRE1 + ".UserService.teamManage";

    /**
     * 联系人列表
     * 操作类型，取联系人列表，群列表，联系人和群列表
     */
    String INTERFACE_URL_USERVERVIC_CONTACTLIST = INTERFACE_URL_PRE1 + ".UserService.contactList";


    /**
     * 附近联系人
     * 获取App位置信息
     */
    String INTERFACE_URL_USERVERVIC_SUBMITLOCATION = INTERFACE_URL_PRE1 + ".XlAppService.submitLocation";


    /**
     * 附近联系人
     * 查询附近联系人
     */
    String INTERFACE_URL_USERVERVIC_FINDNEARBYS = INTERFACE_URL_PRE1 + ".XlAppService.findNearbys";

    /**
     * 群成员
     */
    String INTERFACE_URL_USERVERVIC_MEMBERLIST = INTERFACE_URL_PRE1 + ".UserService.memberList";


    /**
     * 登录测试
     */
    String INTERFACE_URL_APPREGISTERLOGINSERVICE_LOGIN = INTERFACE_URL_PRE3 + ".AppRegisterLoginService.login";

    /**
     * 注册测试
     */
    String INTERFACE_URL_APPREGISTERLOGINSERVICE_RIGISTER = INTERFACE_URL_PRE3 + ".AppRegisterLoginService.register";

    /**
     * 登测试
     */
    String INTERFACE_URL_APPREGISTERLOGINSERVICE_AUTOLOGIN = INTERFACE_URL_PRE3 + ".AppRegisterLoginService.autoLogin";

    /**
     * 登出测试
     */
    String INTERFACE_URL_APPREGISTERLOGINSERVICE_LOGOUT = INTERFACE_URL_PRE3 + ".AppRegisterLoginService.logout";

    /**
     * 获取figureID
     */
    String INTERFACE_URL_APPREGISTERLOGINSERVICE_GETUNUSEDFIGUREIDS = INTERFACE_URL_PRE3 + ".AppRegisterLoginService.getUnusedFigureIds";

    /**
     * 获取设备ID
     */
    String INTERFACE_URL_APPREGISTERLOGINSERVICE_ACTIVATEDEVICE = INTERFACE_URL_PRE3 + ".DeviceInfoService.activateDevice";

    /**
     * 创建角色
     */
    String INTERFACE_URL_FIGURESERVICE_CREATE = INTERFACE_URL_PRE3 + ".FigureService.create";


    /**
     * 角色信息更新
     */
    String INTERFACE_URL_FIGURESERVICE_UPDATE = INTERFACE_URL_PRE3 + ".FigureService.update";

    /**
     * 获取角色信息
     */
    String INTERFACE_URL_FIGURESERVICE_DETAIL = INTERFACE_URL_PRE3 + ".FigureService.detail";

    /**
     * 获取角色列表
     */
    String INTERFACE_URL_FIGURESERVICE_LIST = INTERFACE_URL_PRE3 + ".FigureService.list";

    /**
     * 创建群组
     */
    String INTERFACE_URL_GROUPSERVICE_CREATE = INTERFACE_URL_PRE2 + ".GroupService.create";
    /**
     * 解散群组
     */
    String INTERFACE_URL_GROUPSERVICE_DEMISS = INTERFACE_URL_PRE2 + ".GroupService.dismiss";
    /**
     * 群组基本信息
     */
    String INTERFACE_URL_GROUPSERVICE_DETAIL = INTERFACE_URL_PRE2 + ".GroupService.detail";
    /**
     * 群组更新
     */
    String INTERFACE_URL_GROUPSERVICE_UPDATE = INTERFACE_URL_PRE2 + ".GroupService.update";
    /**
     * 查询当前用户的所有身份角色所在的群组的列表
     */
    String INTERFACE_URL_GROUPSERVICE_LIST = INTERFACE_URL_PRE2 + ".GroupService.list";
    /**
     * 查询当前用户的指定身份角色所在的群组的列表
     */
    String INTERFACE_URL_GROUPSERVICE_LISTBYFIGUREID = INTERFACE_URL_PRE2 + ".GroupService.listByFigureId";

    /**
     * 用户主动加入群组
     */
    String INTERFACE_URL_GROUPSERVICE_JOIN = INTERFACE_URL_PRE2 + ".GroupService.join";
    /**
     * 用户主动退出群组
     */
    String INTERFACE_URL_GROUPSERVICE_QUIT = INTERFACE_URL_PRE2 + ".GroupService.quit";
    /**
     * 群成员列表
     */
    String INTERFACE_URL_GROUPSERVICE_MEMBERS = INTERFACE_URL_PRE2 + ".GroupService.members";
    /**
     * 群移入黑名单
     */
    String INTERFACE_URL_GROUPSERVICE_MOVETOBLACKLIST = INTERFACE_URL_PRE2 + ".GroupService.moveIntoBlacklist";
    /**
     * 群移出黑名单
     */
    String INTERFACE_URL_GROUPSERVICE_MOVEOUTBLACKLIST = INTERFACE_URL_PRE2 + ".GroupService.moveOutofBlackList";

    /**
     * 批量邀请好友加入群
     */
    String INTERFACE_URL_GROUPSERVICE_INVITE = INTERFACE_URL_PRE2 + ".GroupService.invite";
    /**
     * 批量踢人
     */
    String INTERFACE_URL_GROUPSERVICE_KICK = INTERFACE_URL_PRE2 + ".GroupService.kick";
    /**
     * 查找共同群组
     */
    String INTERFACE_URL_GROUPSERVICE_SAMEGROUPS = INTERFACE_URL_PRE2 + ".GroupService.sameGroups";

    /**
     * 地理位置信息上报
     */
    String INTERFACE_URL_LBSSERVICE_REPORTLOCATION = INTERFACE_URL_PRE2 + ".LBSService.reportLocation";
    /**
     * 附近的人
     */
    String INTERFACE_URL_LBSSERVICE_FINDNEARBYUSERS = INTERFACE_URL_PRE2 + ".LBSService.findNearbyUsers";

    /**
     * 添加联系人
     */
    String INTERFACE_URL_CONTACTSSERVICE_ADD = INTERFACE_URL_PRE2 + ".ContactsService.add";

    /**
     * 修改联系人
     */
    String INTERFACE_URL_CONTACTSSERVICE_UPDATE = INTERFACE_URL_PRE2 + ".ContactsService.update";

    /**
     * 获取当前用户的所有身份角色的联系人列表
     */
    String INTERFACE_URL_CONTACTSSERVICE_LIST = INTERFACE_URL_PRE2 + ".ContactsService.list";

    /**
     * 获取当前用户的指定身份角色的联系人列表
     */
    String INTERFACE_URL_CONTACTSSERVICE_LISTBYFIGUREID = INTERFACE_URL_PRE2 + ".ContactsService.listByFigureId";

    /**
     * 移入黑名单
     */
    String INTERFACE_URL_CONTACTSSERVICE_MOVEINTOBLACKLIST = INTERFACE_URL_PRE2 + ".ContactsService.moveIntoBlacklist";

    /**
     * 移出黑名单
     */
    String INTERFACE_URL_CONTACTSSERVICE_MOVEOUTOFBLACKLIST = INTERFACE_URL_PRE2 + ".ContactsService.moveOutofBlacklist";

    /**
     * 查询指定的联系人信息
     */
    String INTERFACE_URL_CONTACTSSERVICE_GETBYCONTACTS = INTERFACE_URL_PRE2 + ".ContactsService.getByContacts";

    /**
     * 查找相同联系人
     */
    String INTERFACE_URL_CONTACTSSERVICE_SAMECONTACTS = INTERFACE_URL_PRE2 + ".ContactsService.sameContacts";

    /**
     * 扫描二维码添加好友
     */
    String INTERFACE_URL_CONTACTSSERVICE_ADDBYQRCODE = INTERFACE_URL_PRE2 + ".ContactsService.addByQRCode";

    /**
     * 获取用户二维码token
     */
    String INTERFACE_URL_QRCODESERVICE_GETUSERTOKEN = INTERFACE_URL_PRE2 + ".QRCodeService.getUserToken";

    /**
     * 获取群组二维码token
     */
    String INTERFACE_URL_QRCODESERVICE_GETGROUPTOKEN = INTERFACE_URL_PRE2 + ".QRCodeService.getGroupToken";
    /**
     * 根据角色二维码token获取角色详情
     */
    String INTERFACE_URL_QRCODESERVICE_GETUSERFIGUREBYTOKEN = INTERFACE_URL_PRE2 + ".QRCodeService.getUserFigureByToken";

    /**
     * 根据群二维码token获取群详情
     */
    String INTERFACE_URL_QRCODESERVICE_GETGROUPBYTOKEN = INTERFACE_URL_PRE2 + ".QRCodeService.getGroupByToken";
}
