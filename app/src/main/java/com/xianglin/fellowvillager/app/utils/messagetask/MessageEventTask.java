/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.utils.messagetask;
/**
 * 基于消息的任务,请求联系人,加好友,请求群,控制私密消息销毁
 * @author pengyang
 * @version v 1.0.0 2016/3/28 10:28  XLXZ Exp $
 */
abstract class MessageEventTask implements Runnable{
    public static final String TAG="MessageEventTask";
    private String taskName;
    public MessageEventTask(String taskName){
        this.taskName=taskName;
    }  

    public String getTaskId(){
        return taskName;
    }

    /**
     * 任务结束时需要清空任务
     */
   public void onEndTask(){
       MessageEventTaskManager.getInstance().removeTaskIdSet(taskName);
   }

}  