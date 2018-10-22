package com.hc.frame.taskSchedule;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

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
	/**
	 * ����ʵ��һ�������Եĵ�������
	 */
	@Override
	public void run() {
		if(isActive.compareAndSet(false, true)) {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(this, 5, eInterval.get(), TimeUnit.SECONDS);
		}
		execute();
		
	}
	
	/**
	 * ͨ���������������һ���ض�����������Եĵ�����
	 */
	@Override
	public void exe(int interval) {
		eInterval.set(interval);
		run();
	}
	
	/**
	 * 
	 * ��������������Եĵ��á�
	 */
	public abstract void execute();
	
	

	
}
