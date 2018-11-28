package com.hc.logic.achieve;

import java.util.HashMap;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.config.GoodsConfig;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.TaskConfig;

public class SerchGoodsTarget implements Target {

	/**
	 * ������������Ӧ����Ӧ���������е�need�
	 * ����type=1�Ļ�ɱ����key����Ҫ�ɼ�����Ʒid��value������
	 */
	private Map<Integer, Integer> taskComplete = new HashMap<>();

	@Override
	public boolean checkTaskComplete(TaskConfig taskConfig) {
		for(Map.Entry<Integer, Integer> ent : taskConfig.getNeeded().entrySet()) {
			int gid = ent.getKey();
			int amount = ent.getValue();
			if(taskComplete.get(new Integer(gid)) == null) return false;
			if(taskComplete.get(new Integer(gid)) < amount) return false;
		}
		return true;
	}
	
	@Override
	public String taskProgessDesc(TaskConfig taskConfig) {
		System.out.println("----------taskprogerss---serchgoodstask");
		StringBuilder sb = new StringBuilder();
		sb.append("����[" + taskConfig.getName() +"]�Ľ�������: \n");
		for(Map.Entry<Integer, Integer> ent :taskConfig.getNeeded().entrySet()) {
			int gid = ent.getKey();
			//MonstConfig mConfig = Context.getSceneParse().getMonsters().getMonstConfgById(mid);
			GoodsConfig gConfig = Context.getGoodsParse().getGoodsConfigById(gid);
			int amount = ent.getValue();
			taskComplete.get(new Integer(gid));
			int nam = ((taskComplete.get(new Integer(gid)) == null) ? 0 : taskComplete.get(new Integer(gid))) ;
			sb.append("�ɼ�[" + gConfig.getName() + "]: " + nam + "/" + amount +"\n");
		}

		return sb.toString();
	}
	
	@Override
	public void addComplete(int id) {
		if(!taskComplete.containsKey(new Integer(id))) return;
		taskComplete.put(id, taskComplete.getOrDefault(id, 0) + 1);
	}
	
	public Map<Integer, Integer> getTaskComplete(){
		return taskComplete;
	}

	public void setTaskComplete(Map<Integer, Integer> taskComplete) {
		for(Map.Entry<Integer, Integer> ent : taskComplete.entrySet()) {
			for(int i = 0; i < ent.getValue(); i++) {
				addComplete(ent.getKey());
			}
		}
	}

}
