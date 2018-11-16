package com.hc.logic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class AchieveEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column
	private int atm2_5;   //ɱid=2�Ĺ���ĸ�������5��ɳɾ�	0��1��2����������ʶû�д�ɡ�-1��ʾ���
	@Column
	private int npc1;       //��npc id=1�Ի���ɳɾ�
	@Column
	private int levl2;      //�ȼ��״ε���2����ɳɾ�
	@Column
	private int tEquip5;     //����װ���ﵽ5������ɳɾ�
	@Column
	private int copy1;      //ͨ������1����ɳɾ�
	@Column
	private int el5;        //������װ���ȼ���5������ɳɾ�
	@Column
	private int friend1;    //��ӵ�һ�����Ѵ�ɳɾ�
	@Column
	private int group1;      //��һ����ӣ���ɳɾ�
	@Column
	private int party1;     //�����һ�����ᣬ��ɳɾ�
	@Column
	private int deal1;       //��һ����ɽ��ף���ɳɾ�
	@Column
	private int pk1;        //��һ��pkʤ������ɳɾ� 
	@Column
	private int gold500;     //��һ�ν�Ҵﵽ500����ɳɾ�    
	
	@OneToOne(mappedBy="achieveEntity")
	private PlayerEntity playerEntity;
	
	public AchieveEntity(PlayerEntity pe) {
		this.playerEntity = pe;
	}
	public AchieveEntity() {
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAtm2_5() {
		return atm2_5;
	}

	public void setAtm2_5(int atm2_5) {
		this.atm2_5 = atm2_5;
	}

	public int getNpc1() {
		return npc1;
	}

	public void setNpc1(int npc1) {
		this.npc1 = npc1;
	}

	public int gettEquip5() {
		return tEquip5;
	}

	public void settEquip5(int tEquip5) {
		this.tEquip5 = tEquip5;
	}

	public int getCopy1() {
		return copy1;
	}

	public void setCopy1(int copy1) {
		this.copy1 = copy1;
	}

	public int getEl5() {
		return el5;
	}

	public void setEl5(int el5) {
		this.el5 = el5;
	}

	public int getFriend1() {
		return friend1;
	}

	public void setFriend1(int friend1) {
		this.friend1 = friend1;
	}

	public int getGroup1() {
		return group1;
	}

	public void setGroup1(int group1) {
		this.group1 = group1;
	}

	public int getParty1() {
		return party1;
	}

	public void setParty1(int party1) {
		this.party1 = party1;
	}

	public int getDeal1() {
		return deal1;
	}

	public void setDeal1(int deal1) {
		this.deal1 = deal1;
	}

	public int getPk1() {
		return pk1;
	}

	public void setPk1(int pk1) {
		this.pk1 = pk1;
	}

	public int getGold500() {
		return gold500;
	}

	public void setGold500(int gold500) {
		this.gold500 = gold500;
	}
	public int getLevl2() {
		return levl2;
	}
	public void setLevl2(int levl2) {
		this.levl2 = levl2;
	}
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}
	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}

	
	
	
	
	
}
