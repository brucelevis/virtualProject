package com.hc.logic.achieve;

import java.util.HashMap;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.config.CopysConfig;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.TaskConfig;

/**
 * ͨ������Ŀ��
 * @author hc
 *
 */
public class PassCopyTarget implements Target{

	
	/**
	 * ������������Ӧ����Ӧ���������е�need����ڸ�������valueֻ��0��1����
	 * ����type=3�ĸ���ͨ������key������id��value��0
	 */
	private Map<Integer, Integer> taskComplete;

	public PassCopyTarget() {
		taskComplete = new HashMap<>();
	}
	
	@Override
	public boolean checkTaskComplete(TaskConfig taskConfig) {
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			int amount = ent.getValue();
			System.out.println("----��������allthing" + taskComplete.toString());
			if(taskComplete.get(ent.getKey()) == null) return false;
			if(taskComplete.get(ent.getKey()) <amount) return false;
		}
		return true;
	}
	
	@Override
	public String taskProgessDesc(TaskConfig taskConfig) {
		StringBuilder sb = new StringBuilder();
		sb.append("����[" + taskConfig.getName() +"]�Ľ�������: \n");
		System.out.println("-----------������������---" + taskComplete == null);
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			System.out.println("-----------������������---" + taskComplete.toString());
			Integer cid = ent.getKey();
			CopysConfig cConfig = Context.getCopysParse().getCopysConfById(cid);
			int num = 0;
			if(taskComplete.get(cid) != null) num = taskComplete.get(cid);
			sb.append("��ɸ���["+cConfig.getName()+"]��" + num + " /1 \n");
		}
		return sb.toString();
	}
	
	@Override
	public void addComplete(int id) {
		System.out.println("��֤�Ƿ�����������Ŀ��: " + id);
		System.out.println("��֤�Ƿ�����������Ŀ��: " + taskComplete.toString());
		if(!taskComplete.containsKey(new Integer(id))) return;
		taskComplete.put(id, 1);
	}

	public Map<Integer, Integer> getTaskComplete() {
		return taskComplete;
	}

	public void setTaskComplete(Map<Integer, Integer> taskComplete) {
		//this.taskComplete = taskComplete;
		for(Map.Entry<Integer, Integer> ent : taskComplete.entrySet()) {
			for(int i = 0; i < ent.getValue(); i++) {
				addComplete(ent.getKey());
			}
		}
	}
	
	
}
