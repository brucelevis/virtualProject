package com.hc.logic.config;

public class AchieveConfig {

	private int id;       
	private String name;      //�ɾ���
	private String desc;      //�ɾ�����
	private String charac;    //���ֵ�ͳɾ�ʵ���е��ֶ���ͬ
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getCharac() {
		return charac;
	}
	public void setCharac(String charac) {
		this.charac = charac;
	}
	
	@Override
	public String toString() {
		return "{name=" + name
				+ ", charac=" + charac + "}";
	}
	
}
