/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.utils.messagetask;

import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务管理线程
 * @author pengyang
 * @version v 1.0.0 2016/3/28 16:38  XLXZ Exp $
 */
public class MessageEventManagerThread implements Runnable {
  
    private MessageEventTaskManager mMessageEventTaskManager;

    private ExecutorService pool;  

   // private final int POOL_SIZE = 16;//需要改成动态

    private volatile boolean stopRequested;

    private Thread runThread;

    public MessageEventManagerThread() {
        mMessageEventTaskManager = MessageEventTaskManager.getInstance();
        pool =  new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                20, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());

    }

    @Override
    public void run() {

        runThread =Thread.currentThread();

        while (!stopRequested) {

            synchronized (mMessageEventTaskManager.getMessageEventTasks()) {

                while(mMessageEventTaskManager.getMessageEventTasks().size() == 0) {
                    try {
                        LogCatLog.d("MessageEventManagerThread","等待MessageEventTask加入队列");
                        mMessageEventTaskManager.getMessageEventTasks().wait();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        mMessageEventTaskManager.getMessageEventTasks().notify();
                    }
                }

                   // mMessageEventTaskManager.getMessageEventTasks().notify();

                    MessageEventTask messageEventTask = mMessageEventTaskManager.getMessageEventTaskTask();
                    if (messageEventTask != null) {
                        pool.execute(messageEventTask);
                        LogCatLog.d("MessageEventManagerThread","处理MessageEventTask");
                    }

                }

        }
        if (stopRequested) {
            pool.shutdown();
        }

    }


    public void setStop(boolean isStop) {
        this.stopRequested = isStop;
    }
}  

  
