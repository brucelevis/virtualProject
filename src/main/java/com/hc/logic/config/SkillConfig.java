package com.hc.logic.config;

public class SkillConfig {

	private int skillId;   
	private String name;
	private String description;
	private int cd;  //��ȴʱ��
	private int attack;  //������
	private int protect;  //������ı����������ܼӷ�����
	private int mp;      //���ĵķ���
	private int weapon;   //��Ҫ������(��Ʒid)
	private int continueT;   //(��)���ܳ���ʱ��
	
	
	//bossר��
	private int scope;  //��ͬʱ�����������;0�����ޣ�1��һ��,�� �� ��
	
	
	
	
	
	
	public int getSkillId() {
		return skillId;
	}
	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCd() {
		return cd;
	}
	public void setCd(int cd) {
		this.cd = cd;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public int getProtect() {
		return protect;
	}
	public void setProtect(int protect) {
		this.protect = protect;
	}
	public int getMp() {
		return mp;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}
	public int getContinueT() {
		return continueT;
	}
	public void setContinueT(int continueT) {
		this.continueT = continueT;
	}
	
	
	
	public int getScope() {
		return scope;
	}
	public void setScope(int scope) {
		this.scope = scope;
	}
	public int getWeapon() {
		return weapon;
	}
	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}
	@Override
	public String toString() {
		return name;
	}
	
	
}
