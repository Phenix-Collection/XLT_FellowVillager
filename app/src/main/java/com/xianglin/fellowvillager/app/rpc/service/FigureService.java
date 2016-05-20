package com.xianglin.fellowvillager.app.rpc.service;

/**
 * Created by yangjibin on 16/2/29.
 */
/**
 *
 */

import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.mobile.common.rpc.proxy.OperationType;

import java.util.List;

/**
 * 用户身份角色相关服务
 *
 * @author pengpeng 2016年2月18日下午4:24:28
 */
public interface FigureService {

    /**
     * 当前用户新建身份角色
     *
     * @param figureId 身份角色唯一标识
     * @param open     是否公开
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_FIGURESERVICE_CREATE)
    FigureDTO create(String figureId, boolean open);

    /**
     * 更新当前用户的身份角色信息
     *
     * @param figureDTO
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_FIGURESERVICE_UPDATE)
    Boolean update(FigureDTO figureDTO);

    /**
     * 获取当前用户的指定身份角色的详情
     *
     * @param figureId
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_FIGURESERVICE_DETAIL)
    FigureDTO detail(String figureId);

    /**
     * 查询当前用户的所有身份角色列表
     *
     * @return
     */
    @OperationType(BorrowConstants.INTERFACE_URL_FIGURESERVICE_LIST)
    List<FigureDTO> list();


}
