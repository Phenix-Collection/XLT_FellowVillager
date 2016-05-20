package com.xianglin.fellowvillager.app.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

	static ExecutorService cachedService = null;
	static ExecutorService singleService = null;

	/**
	 * @Description 不固定线程池
	 * @author <a href="http://t.cn/RvIApP5">ceychen@foxmail.com</a>
	 * @date 2014-8-19 15:46:57
	 */
	public static ExecutorService getCachedThreadPool() {
		if (cachedService == null) {
			synchronized (ThreadPool.class) {
				if (cachedService == null)
					cachedService = Executors.newCachedThreadPool();
			}
		}
		return cachedService;
	}
	/**
	 * @Description 单线程池
	 * @author <a href="http://t.cn/RvIApP5">ceychen@foxmail.com</a>
	 * @date 2014-8-19 15:47:02
	 */
	public static ExecutorService getSingleThreadPool() {
		if (singleService == null) {
			synchronized (ThreadPool.class) {
				if (singleService == null)
					singleService = Executors.newSingleThreadExecutor();
			}
		}
		return singleService;
	}
}
