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
	private String completeAchieve;  //����ɵĳɾ�id���ö��Ÿ���
	@Column
	private String progressAchieve;  //��δ��ɵĳɾ�id��num����ʽ��id��num;������
		
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

	
	public String getCompleteAchieve() {
		return completeAchieve;
	}
	public void setCompleteAchieve(String completeAchieve) {
		this.completeAchieve = completeAchieve;
	}
	public String getProgressAchieve() {
		return progressAchieve;
	}
	public void setProgressAchieve(String progressAchieve) {
		this.progressAchieve = progressAchieve;
	}
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}
	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}

	
	
	
	
	
}
