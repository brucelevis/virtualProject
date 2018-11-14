package com.hc.logic.creature;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hc.frame.Context;
import com.hc.logic.config.MonstConfig;

public class Monster extends LiveCreature{

	private int monstId;
	private int Hp;  //����Ѫ��
	private int attack; //������
	private boolean isAlive = true; //��ʼʱ��Ĭ���ǻ��
	private Lock lock = new ReentrantLock();
	
	MonstConfig monstConfig;	
	
	public Monster(int mId) {
		this.monstId = mId; 
		monstConfig = Context.getSceneParse().getMonsters().getMonstConfgById(monstId);
		this.Hp = monstConfig.getHp();
		this.attack = monstConfig.getAttack();
	}
	
	/**
	 * �ж��ܲ��ܴ�.
	 * ����ʧ�ܣ����ﱻ�����һ�ɱ��-1
	 * �����ɹ�������û���Լ���ɱ��0
	 * �����ɹ������ﱻ�Լ���ɱ��1
	 * @param skillid
	 * @param player
	 * @return  �Ƿ񹥻��ɹ�
	 */
	public int canAttack(int skillid, Player player) {
		int reduce = player.AllAttack(skillid);
		System.out.println("------------Monster.canAttack-------");
	    return attack(reduce);
	}
	/**
	 * ���й���
	 * ����ʧ�ܣ����ﱻ�����һ�ɱ��-1
	 * �����ɹ�������û���Լ���ɱ��0
	 * �����ɹ������ﱻ�Լ���ɱ��1
	 * @param attack
	 * @return
	 */
	public int attack(int attack) {
		lock.lock();
		try {
			int diff = Hp - attack;
		    if(Hp > 0) {  //δ�������Թ���
		    	if(diff > 0) {
		    		Hp = diff;
		    	}else {
		    		Hp = 0;
		    		isAlive = false;
		    		return 1;
		    	}
		    	return 0;
		    }else {
		    	return -1;
		    }
		}finally {
			lock.unlock();
		}
	}
	
	
	public int pAttackM() {
		return 0;
	}
	
	public String getName() {
		return monstConfig.getName();
	}
	public String getDescription() {
		return monstConfig.getDescription();
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public int getExp() {
		return monstConfig.getExp();
	}
	public int getAttackP() {
		return monstConfig.getAttackP();
	}
	public MonstConfig getMonstConfig() {
		return monstConfig;
	}
	public void setMonstConfig(MonstConfig monstConfig) {
		this.monstConfig = monstConfig;
	}
	public int getGold() {
		return this.monstConfig.getGold();
	}



	public int getMonstId() {
		return monstId;
	}
	public void setMonstId(int monstId) {
		this.monstId = monstId;
	}
	public int getHp() {
		return Hp;
	}
	public void setHp(int hp) {
		Hp = hp;
	}
    public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public String bossSkillList() {
		return this.monstConfig.bossSkillList();
	}


	@Override
	public void setDescribe() {
		this.describe = getDescription();
	}
	
	@Override
	public void setcId() {
		this.cId = monstId;
	}

	@Override
	public String toString() {
		return monstConfig.getName();
	}
}
