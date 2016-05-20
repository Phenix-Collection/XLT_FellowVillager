package com.xianglin.fellowvillager.app.rpc.remote;

import com.xianglin.mobile.common.utils.SerialExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 任务管理
 * 
 * @author alex
 *
 */
public class HttpTaskManager{
    private static HttpTaskManager INSTANCE;
    //-------------------线程池设置-----------------
    private static final int CORE_SIZE = 5;
    private static final int POOL_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 10;
    private static final int QUEUE_SIZE = 128;
    /**
     * 并行执行
     */
    private ThreadPoolExecutor mParallelExecutor;
    /**
     * 串行执行
     */
    private SerialExecutor mSerialExecutor;

    private static final String TAG = HttpTaskManager.class.getSimpleName();
    
    private HttpTaskManager() {
        mSerialExecutor = new SerialExecutor(TAG);
        mParallelExecutor = new ThreadPoolExecutor( CORE_SIZE, 
									        		POOL_SIZE, 
									        		KEEP_ALIVE_TIME,
									        		TimeUnit.SECONDS,
									        		new ArrayBlockingQueue<Runnable>(QUEUE_SIZE), 
									        		THREADFACTORY,
									        		new ThreadPoolExecutor.CallerRunsPolicy() );
    }
    
    public static synchronized HttpTaskManager getInstance(){
        if(INSTANCE==null){
            INSTANCE = new HttpTaskManager();
        }
        return INSTANCE;
    }

    private static final ThreadFactory THREADFACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(0);

        public Thread newThread(Runnable r) {
            return new Thread(r, TAG+" #" + mCount.incrementAndGet());
        }
    };

    /**
     * 串行执行
     * 
     * @param command Runnable
     */
    public void serialExecute(Runnable command) {
        mSerialExecutor.execute(command);
    }

    /**
     * 并行执行
     * 
     * @param command Runnable
     */
    public void parallelExecute(Runnable command) {
        mParallelExecutor.execute(command);
    }
}
