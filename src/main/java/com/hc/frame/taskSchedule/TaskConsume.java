package com.hc.frame.taskSchedule;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;

/**
 * 任务执行
 */
@Component
public class TaskConsume implements TaskManage{
	
	private AtomicBoolean isActive = new AtomicBoolean(false);
	private AtomicInteger eInterval = new AtomicInteger(10);
	
	private ScheduledExecutorService schedule = Executors.newScheduledThreadPool(20);
	
	
	private String taskId = "";
	/**
	 * 这里实现一个周期性的调度器。
	 */
	@Override
	public void run() {
		if(isActive.compareAndSet(false, true)) {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			
			Future future = service.scheduleAtFixedRate(this, 5, eInterval.get(), TimeUnit.SECONDS);
			
		}
		//execute();		
	}
	
	/**
	 * 启动 一个特定间隔的周期性的调度器
	 */
	@Override
	public void exe(int interval, String taskId, Runnable runnable) {
		Future future = schedule.scheduleAtFixedRate(runnable, 1, interval, TimeUnit.SECONDS);
		if(taskId.length() > 5 && taskId.substring(0, 5).equals("copys")) {
			Context.getWorld().getFutureMap().put(taskId, future);
		}
		if(taskId.length() > 4 && taskId.substring(0, 4).equals("boss")) {
			Context.getWorld().getFutureMap().put(taskId, future);
		}
		if(taskId.length() > 6 && taskId.substring(0, 6).equals("summon")) {
			Context.getWorld().getFutureMap().put(taskId, future);
		}
	}
		
}
