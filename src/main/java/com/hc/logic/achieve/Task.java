package com.hc.logic.achieve;

import java.util.HashMap;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.config.TaskConfig;

public class Task {

	private int tid;  //�����е�����id
	private TaskConfig taskConfig;  //��������
	private Target taskTarget;    //������ڵ�Ŀ��
	
	public Task(int tid) {  //����id
		this.tid = tid;
		this.taskConfig = Context.getTaskParse().getTaskConfigByid(tid);
		this.taskTarget = TargetType.getTargetById(taskConfig.getType());
	}
	//����ָ�
	public Task(int tid, Map<Integer, Integer> maps) {
		this(tid);
		taskTarget.setTaskComplete(maps);
	}
	
	/**
	 * ����������
	 * @param id
	 */
	public void addComplete(int id) {
		taskTarget.addComplete(id);
	}
	
	/**
	 * ��֤�Ƿ��������
	 * @return
	 */
	public boolean checkTaskComplete() {
		return taskTarget.checkTaskComplete(taskConfig);
	}
	
	/**
	 * �����������
	 * @return
	 */
	public String taskProgessDesc() {
		return  taskTarget.taskProgessDesc(taskConfig);
	}
	
	/**
	 * ��֤�Ƿ���ͬ������
	 * @param typeId
	 * @return
	 */
	public boolean isSameTaskType(int typeId) {
		return taskConfig.getType() == typeId;
	}
	
	public int getTid() {
		return tid;
	}

	public TaskConfig getTaskConfig() {
		return taskConfig;
	}

	public String getName() {
		return taskConfig.getName();
	}

	public Target getTaskTarget() {
		return taskTarget;
	}

	public void setTaskTarget(Target taskTarget) {
		this.taskTarget = taskTarget;
	}

	
}
