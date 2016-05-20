/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.utils.messagetask;

import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
  *  任务管理类
  * @author pengyang
  * @version v 1.0.0 2016/3/28 13:18  XLXZ Exp $
  */
public class MessageEventTaskManager {
    private static final String TAG="MessageEventTaskManager";

     public ConcurrentLinkedQueue<MessageEventTask> getMessageEventTasks() {
         return mMessageEventTasks;
     }

     private ConcurrentLinkedQueue<MessageEventTask> mMessageEventTasks;


     private Vector<String> taskIdSet;

     private static MessageEventTaskManager eventTaskManager;



     private MessageEventTaskManager() {

        mMessageEventTasks = new ConcurrentLinkedQueue<MessageEventTask>();
        taskIdSet = new Vector<String>();
          
    }  
  
    public static synchronized MessageEventTaskManager getInstance() {
        if (eventTaskManager == null) {
            eventTaskManager = new MessageEventTaskManager();
        }  
        return eventTaskManager;
    }



    public void addMessageEventTaskTask(MessageEventTask task ) {
        synchronized (mMessageEventTasks) {
            if (!isTaskRepeat(task.getTaskId())) {
                mMessageEventTasks.offer(task);
                mMessageEventTasks.notify();
            }else{
                LogCatLog.e(TAG,"任务已经存在taskid:"+task.getTaskId());
            }
        }  
  
    }  
    public boolean isTaskRepeat(String taskid) {

        synchronized (taskIdSet) {
            if (taskIdSet.contains(taskid)) {
                return true;
            } else {

                taskIdSet.add(taskid);
                return false;
            }
        }
    }

     public void removeTaskIdSet(String taskId) {
         synchronized (mMessageEventTasks) {
             if (taskIdSet.size() > 0) {
                 taskIdSet.remove(taskId);
             }
         }
     }
    public MessageEventTask getMessageEventTaskTask() {
        synchronized (mMessageEventTasks) {
            if (mMessageEventTasks.size() > 0) {
                MessageEventTask task = mMessageEventTasks.poll();
                return task;
            }  
        }  
        return null;  
    }

}  