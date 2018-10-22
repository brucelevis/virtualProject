package com.hc.frame.taskSchedule;

import java.util.concurrent.*;

/**
 * �������
 * @author hc
 *
 */
public class TaskProducer extends TaskConsume{

	//һ���̳߳�
	ExecutorService exec = Executors.newFixedThreadPool(10);
	//һ���������У����������������ͻᱻִ�С�
	BlockingQueue<Runnable> task = new LinkedBlockingQueue<>();
	
	public TaskProducer() {
		exe(5);//���������Ե�������5������
	}
	

	
	
	
	/**
	 * 
	 * ������ύӦ�ñ�����һ�������Ե��ȵģ��������Ե��ύ��Ҫ���������
	 * @param aTask
	 */
	@Override
	public void execute() {
		
		if(task.peek() != null) {
			exec.submit(task.poll());
		}
		
	}
	
	/**
	 * �������
	 */
	public void addTask(Runnable ru) {
		System.out.println("�����һ������");
		try {
			task.put(ru);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}

}
