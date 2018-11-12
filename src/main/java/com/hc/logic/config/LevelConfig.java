package com.hc.logic.config;

import java.util.ArrayList;
import java.util.List;

public class LevelConfig {

	private int id;  //Ҳ��ʾ�ȼ�
	private String exp; //�����
	private List<Integer> exps = new ArrayList<>();
	private String hp;  //���Ѫ��
	private List<Integer> hps = new ArrayList<>();
	private String mp;  //�����
	private List<Integer> mps = new ArrayList<>();
	private int uHp; //ÿ��ָ�Ѫ��
	private int uMp; //ÿ��ָ�����
	private String lAttack; //�ȼ���Ӧ�Ĺ�����
	private List<Integer> lAttacks = new ArrayList<>();
	
	
	
	/**
	 * ת����Ӧ��string����Ӧ��list
	 */
	public void convert() {
		convert(exp, exps);
		convert(hp, hps);
		convert(mp, mps);
		convert(lAttack, lAttacks);
	}	
	private void convert(String cont, List<Integer> resu) {
		String[] conts = cont.split(",");
		for(int i = 0; i < conts.length; i++) {
			resu.add(Integer.parseInt(conts[i]));
		}
	}
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
    public int getuHp() {
		return uHp;
	}
	public void setuHp(int uHp) {
		this.uHp = uHp;
	}
	public int getuMp() {
		return uMp;
	}
	public void setuMp(int uMp) {
		this.uMp = uMp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public void setHp(String hp) {
		this.hp = hp;
	}
	public void setMp(String mp) {
		this.mp = mp;
	}
	public void setlAttack(String lAttack) {
		this.lAttack = lAttack;
	}
	/**
	 * ����ְҵ��õȼ���Ӧ�ľ���ֵ
	 * @param prof
	 * @return
	 */
	public int getExpByProf(int prof) {
		return exps.get(prof);
	}
	public List<Integer> getExps() {
		return exps;
	}
	/**
	 * ����ְҵ��õȼ���Ӧ�����Ѫ��
	 * @param prof
	 * @return
	 */
	public int getHpByProf(int prof) {
		return hps.get(prof);
	}
	public List<Integer> getHps() {
		return hps;
	}
	/**
	 * ����ְҵ��õȼ����ڵ������
	 * @param prof
	 * @return
	 */
	public int getMpByProf(int prof) {
		return mps.get(prof);
	}
	public List<Integer> getMps() {
		return mps;
	}
	/*
	 * ����ְҵ��õȼ���Ӧ�Ĺ�����
	 */
	public int getAttackByProf(int prof) {
		return lAttacks.get(prof);
	}
	public List<Integer> getlAttacks() {
		return lAttacks;
	}
	
	
	
	
	
}
