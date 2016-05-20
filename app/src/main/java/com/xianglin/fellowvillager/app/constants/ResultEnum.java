package com.xianglin.fellowvillager.app.constants;


/**
 * RPC通信层结果枚举
 * TODO 要修改的
 * @author apple
 */
public enum ResultEnum {

    NetworkUnavailableError(2,"网络断开","网络未连接，请检查网络"), //

    NetworkSocketError(5,"数据请求超时","数据请求超时，请稍后再试!"),//

    GatewayError(502,"网关错误","服务接口异常，请稍后再试!"),//

    ResultSuccess(1000, "操作成功"), //没有方案

    // 1001-1999 权限错误

    PermissionDeny(1001, "拒绝访问", "拒绝访问。"),

    InvokeExceedLimit(1002, "调用次数超过限制", "排队人太多了，请稍后再试。"), //提示重试

    // 2000-2999 通用业务错误

    SessionStatus(2000, "登录超时", "登录超时，请重新登录。"), //登录模块判断

    Newest(2001, "", "当前使用的已是最新版本。"),

    Newer(2002, "", "客户端已有新版本，建议您立即进行升级。"), //版本升级PD已跟进

    Old(2003, "", "客户端已有新版本，需升级才能继续使用。"), //版本升级PD已跟进

    OperationTypeMissed(3000, "缺少操作类型或者此操作类型不支持", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    RequestDataMissed(3001, "请求数据为空", "系统繁忙，请稍后再试。"), //提示重试

    ValueInvalid(3002, "数据格式有误", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    // 4001-4999 远程调用异常

    RequestTimeOut(4001, "服务请求超时", "请求超时，请稍后再试。"), //提示重试

    RemoteAccessException(4002, "远程调用业务系统异常", "网络繁忙，请稍后再试。"), //提示重试

    CreateProxyError(4003, "创建远程调用代理失败", "网络繁忙，请稍后再试。"), //提示重试

    UnknowError(5000, "未知错误", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    //=============6000-6999为spi包的RPC异常=============

    ServiceNotFound(6000, "RPC-目标服务找不到", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    MethodNotFound(6001, "RPC-目标方法找不到", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    ParamMissing(6002, "RPC-参数数目不正确", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    IllegalAccess(6003, "RPC-目标方法不可访问", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    JsonParserException(6004, "PRC-JSON解析异常", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    IllegalArgument(6005, "PRC-调用目标方法时参数不合法", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    BizException(6666, "RPC-业务抛出异常", "抱歉，暂时无法操作，请稍后再试。"), //弹窗让用户提交问题

    //=============接入第三方，验签的错误码=============

    PublicKeyNotFound(7000, "没有设置公钥"),

    SignaParamMissing(7001, "验签的参数不够"),

    SignVerifyFailed(7002, "验签失败");

    /** 结果码 */
    private final int    code;

    /** 结果描述 */
    private final String memo;

    /** 用户友好提示 */
    private final String tips;

    /**
     * @param code 结果码
     * @param memo 调试描述信息
     * @param tips 用户提示信息
     */
    private ResultEnum(int code, String memo, String tips) {
        this.code = code;
        this.memo = memo;
        this.tips = tips;
    }

    /**
     * 默认构造函数
     * 
     * @param code 结果码
     * @param memo 用户提示信息
     */
    private ResultEnum(int code, String memo) {
        this.code = code;
        this.memo = memo;
        this.tips = null;
    }

    /**
     * Getter method for property <tt>code</tt>.
     * 
     * @return property value of code
     */
    public int getCode() {
        return code;
    }

    /**
     * Getter method for property <tt>memo</tt>.
     * 
     * @return property value of memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * Getter method for property <tt>tips</tt>.
     * 
     * @return property value of tips
     */
    public String getTips() {
        return tips;
    }

    /**
     * 根据code获取ResultEnum
     * 
     * @param code 结果码
     * @return 结果枚举
     */
    public static ResultEnum getResultEnumByCode(int code) {
        for (ResultEnum resultEnum : values()) {
            if (resultEnum.code == code) {
                return resultEnum;
            }
        }
        return null;
    }

}
