package com.xianglin.fellowvillager.app.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

 /**
  * 高优先级线程Factory
  * @author pengyang
  * @version v 1.0.0 2016/1/8 20:15  XLXZ Exp $
  */
public class PriorityThreadFactory implements ThreadFactory {

	private final int mPriority;
	private final AtomicInteger mNumber = new AtomicInteger();
	private final String mName;

	public PriorityThreadFactory(String name, int priority) {
		mName = name;
		mPriority = priority;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, mName + '-' + mNumber.getAndIncrement()) {
			@Override
			public void run() {
				android.os.Process.setThreadPriority(mPriority);
				super.run();
			}
		};
	}
}