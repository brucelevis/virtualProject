package com.hc.logic.config;

public class SkillConfig {

	private int skillId;   
	private String name;
	private String description;
	private int cd;  //��ȴʱ��
	private int attack;  //������
	private int protect;  //������ı����������ܼӷ�����
	private int cure;     //����ʦ��ר�У��ָ�����ֵ
	private int mp;      //���ĵķ���
	private int weapon;   //��Ҫ������(��Ʒid)
	private int continueT;   //(��)���ܳ���ʱ��
	private int dizziness;  //ѣ�Σ��룩���ڼ䲻��ʹ�ü���
	private int profession; //��ѧ�˼��ܵ�ְҵ��0��1��2��3��սʿ����ʦ����ʦ���ٻ�ʦ��10������ְҵ������ѧ
	private int summonBoss;  //���ٻ�ʦ���ٻ���boss id
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
	
	public int getCure() {
		return cure;
	}
	public void setCure(int cure) {
		this.cure = cure;
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
	public int getDizziness() {
		return dizziness;
	}
	public void setDizziness(int dizziness) {
		this.dizziness = dizziness;
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
	
	public int getProfession() {
		return profession;
	}
	public void setProfession(int profession) {
		this.profession = profession;
	}
	
	public int getSummonBoss() {
		return summonBoss;
	}
	public void setSummonBoss(int summonBoss) {
		this.summonBoss = summonBoss;
	}
	@Override
	public String toString() {
		return name;
	}
	
	
}
