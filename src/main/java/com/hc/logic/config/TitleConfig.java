package com.hc.logic.config;

public class TitleConfig {

	private int id;
	private String name;
	private int allow;                // �Ƿ��д�����������Ȩ�ޡ�0��û�У�1���� 
	private int promotion;            //�Ƿ��������¼�ְλ��Ȩ�ޡ�0��û�У�1����.����Ȩ�޺����һ��
	private int donate;               //������Ʒ�����ᣬ��õľ��� 
	private int exp;                  //�ڹ����е�ְλ������Ҫ�ľ���
	private int acquire;              //�ɴӹ���ֿ��еõ�����Ʒ��
	
	
	
	
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
	public int getAllow() {
		return allow;
	}
	public void setAllow(int allow) {
		this.allow = allow;
	}
	public int getPromotion() {
		return promotion;
	}
	public void setPromotion(int promotion) {
		this.promotion = promotion;
	}
	public int getDonate() {
		return donate;
	}
	public void setDonate(int donate) {
		this.donate = donate;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getAcquire() {
		return acquire;
	}
	public void setAcquire(int acquire) {
		this.acquire = acquire;
	}
	
	
	
	
}
