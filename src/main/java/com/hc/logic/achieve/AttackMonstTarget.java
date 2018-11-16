package com.hc.logic.achieve;

import java.util.HashMap;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.TaskConfig;

/**
 * ��������Ŀ��
 * @author hc
 *
 */
public class AttackMonstTarget implements Target{

	/**
	 * ������������Ӧ����Ӧ���������е�need�
	 * ����type=1�Ļ�ɱ����key������id��value������
	 */
	private Map<Integer, Integer> taskComplete = new HashMap<>();

	
	@Override
	public boolean checkTaskComplete(TaskConfig taskConfig) {
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			int gid = ent.getKey();
			int amount = ent.getValue();
			if(taskComplete.get(new Integer(gid)) < amount) return false;
		}
		return true;
	}
	
	@Override
	public String taskProgessDesc(TaskConfig taskConfig) {
		StringBuilder sb = new StringBuilder();
		sb.append("����[" + taskConfig.getName() +"]�Ľ�������: \n");
		for(Map.Entry<Integer, Integer> ent :taskConfig.getNeeded().entrySet()) {
			int mid = ent.getKey();
			MonstConfig mConfig = Context.getSceneParse().getMonsters().getMonstConfgById(mid);
			int amount = ent.getValue();
			taskComplete.get(new Integer(mid));
			int nam = ((taskComplete.get(new Integer(mid)) == null) ? 0 : taskComplete.get(new Integer(mid))) ;
			sb.append("��ɱ["+mConfig.getName()+"]: "+ nam +"/" + amount +"\n");
		}

		return sb.toString();
	}
	
	@Override
	public void addComplete(int id) {
		taskComplete.put(id, taskComplete.getOrDefault(id, 0) + 1);
	}
	
	public Map<Integer, Integer> getTaskComplete(){
		return taskComplete;
	}

	public void setTaskComplete(Map<Integer, Integer> taskComplete) {
		this.taskComplete = taskComplete;
	}
	
}
