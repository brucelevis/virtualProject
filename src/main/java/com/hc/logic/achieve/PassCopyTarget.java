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
	private Map<Integer, Integer> taskComplete = new HashMap<>();

	
	@Override
	public boolean checkTaskComplete(TaskConfig taskConfig) {
		for(Map.Entry<Integer, Integer> ent : taskComplete.entrySet()) {
			int amount = ent.getValue();
			if(amount != 1) return false;
		}
		return true;
	}
	
	@Override
	public String taskProgessDesc(TaskConfig taskConfig) {
		StringBuilder sb = new StringBuilder();
		sb.append("����[" + taskConfig.getName() +"]�Ľ�������: \n");
		for(Map.Entry<Integer, Integer> ent : taskComplete.entrySet()) {
			int cid = ent.getKey();
			CopysConfig cConfig = Context.getCopysParse().getCopysConfById(cid);
			int amount = ent.getValue();
			if(amount == 0) {
				sb.append("����["+cConfig.getName()+"] ���\n");
			}else{
				sb.append("����["+cConfig.getName()+"] δ���\n");
			}
		}
		return sb.toString();
	}
	
	@Override
	public void addComplete(int id) {
		taskComplete.put(id, 1);
	}

	public Map<Integer, Integer> getTaskComplete() {
		return taskComplete;
	}

	public void setTaskComplete(Map<Integer, Integer> taskComplete) {
		this.taskComplete = taskComplete;
	}
	
	
}
