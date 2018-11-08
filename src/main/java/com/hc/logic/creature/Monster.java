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
	
	MonstConfig monstConfig;	
	
	public Monster(int mId) {
		this.monstId = mId; 
		monstConfig = Context.getSceneParse().getMonsters().getMonstConfgById(monstId);
		this.Hp = monstConfig.getHp();
		this.attack = monstConfig.getAttack();
	}
	
	/**
	 * �ж��ܲ��ܴ�
	 * @param skillid
	 * @param player
	 * @return  �Ƿ񹥻��ɹ�
	 */
	public boolean canAttack(int skillid, Player player) {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			int reduce = player.AllAttack(skillid);
		    int diff = Hp - reduce;
		    if(Hp > 0) {  //δ�������Թ���
		    	if(diff > 0) {
		    		Hp = diff;
		    	}else {
		    		Hp = 0;
		    		isAlive = false;
		    	}
		    	return true;
		    }else {
		    	return false;
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
