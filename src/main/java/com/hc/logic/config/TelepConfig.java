package com.hc.logic.config;

public class TelepConfig {

	private int teleId;
	private String description;
	private int sceneid;       //�ܴ��͵���Ŀ�곡��id
	private int level = 0;     //��ʹ�ô˴�����ĵȼ�
	private int task = 0;      //��Ҫ��ɴ��������ʹ�ô˴�����
	
	
	
	public int getTeleId() {
		return teleId;
	}
	public void setTeleId(int teleId) {
		this.teleId = teleId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getTask() {
		return task;
	}
	public void setTask(int task) {
		this.task = task;
	}
	public int getSceneid() {
		return sceneid;
	}
	public void setSceneid(int sceneid) {
		this.sceneid = sceneid;
	}
	
	
	
	
	
	
}
