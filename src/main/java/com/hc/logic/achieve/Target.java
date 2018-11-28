package com.hc.logic.achieve;

import java.util.Map;

import com.hc.logic.config.TaskConfig;

public interface Target {
	
	/**
	 * ��֤�Ƿ��������
	 */
	boolean checkTaskComplete(TaskConfig taskConfig);
	/**
	 * �����������
	 * @param taskConfig: ��������
	 */
	String taskProgessDesc(TaskConfig taskConfig);
	/**
	 * ���������ȣ�Ҳ������ɵ���
	 * @param id.��Ӧ��id�������id����Ʒid������id
	 */
	void addComplete(int id);
	
	Map<Integer, Integer> getTaskComplete();
	void setTaskComplete(Map<Integer, Integer> taskComplete);
}
