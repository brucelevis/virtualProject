package com.hc.frame.taskSchedule;

import java.util.concurrent.*;

import org.springframework.stereotype.Component;


/**
 * �������
 * @author hc
 *
 */
public class TaskProducer{
	//һ���������У����������������ͻᱻִ�С�
	private static BlockingQueue<Runnable> task;
	
	public TaskProducer(BlockingQueue<Runnable> task) {
		this.task = task;
	}

	/**
	 * �������
	 */
	public static void addTask(Runnable ru) {
		System.out.println("�����һ������");
		try {
			task.put(ru);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	}

}
