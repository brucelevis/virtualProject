package com.hc.logic.achieve;

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
		System.out.println("��ʼ����target��" + taskTarget.getTaskComplete().toString());
		init();
	}
	//����ָ�
	public Task(int tid, Map<Integer, Integer> maps) {
		this(tid);
		taskTarget.setTaskComplete(maps);
	}
	
	public void init() {
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			taskTarget.getTaskComplete().put(ent.getKey(), 0);
			//System.out.println("taskConfig���У�" + ent.getKey() + ", " + ent.getValue());
		}
		System.out.println("����"+ taskConfig.getName() +"������" + taskTarget.getTaskComplete().toString());
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
	 * ��֤�Ƿ���ͬһ����
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

	@Override
	public String toString() {
		return	tid+"";
	}
}
