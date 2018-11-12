package com.hc.frame.taskSchedule;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.hc.frame.Context;

/**
 * ����ִ��
 */
public abstract class TaskConsume implements TaskManage{
	
	private AtomicBoolean isActive = new AtomicBoolean(false);
	/**
	 * ��ǰ������������
	 * Ĭ��������10��
	 */
	private AtomicInteger eInterval = new AtomicInteger(10);
	
	private String taskId = "";
	/**
	 * ����ʵ��һ�������Եĵ�������
	 */
	@Override
	public void run() {
		if(isActive.compareAndSet(false, true)) {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			Future future = service.scheduleAtFixedRate(this, 5, eInterval.get(), TimeUnit.SECONDS);
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
		execute();		
	}
	
	/**
	 * ���� һ���ض�����������Եĵ�����
	 */
	@Override
	public void exe(int interval, String taskId) {
		this.taskId = taskId;
		eInterval.set(interval);
		run();
	}
	
	/**
	 * 
	 * ��������������Եĵ��á�
	 */
	public abstract void execute();
	
	

	
}
