package com.hc.logic.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * ��Ʒʵ�壬������������װ��
 * @author hc
 *
 */
@Entity
@Table(name = "goods")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("goods")
public class GoodsEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;   //��Ʒid	
	/**
	 * ��Ʒid
	 */
	@Column(name="goodId")
	private int eId;

	@ManyToOne
	private PlayerEntity playerEntity;
	
	
	
	
	
		
	public GoodsEntity() {
		
	}
	/**
	 * 
	 * @param goo  ��Ʒ
	 * @param pe   ���ʵ��
	 */
	public GoodsEntity(int gId, PlayerEntity pe) {
		this.eId = gId;
		this.playerEntity = pe;
	}
	
	


	public int getId() {
		return id;
	}
	public void setId(int idd) {
		this.id = idd;
	}
	public int geteId() {
		return eId;
	}
	public void seteId(int eId) {
		this.eId = eId;
	}
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}
	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	@Override
	public String toString() {
		return "GoodsEntity {id=" + id
	           + ", eId=" + eId
	           + ", playerId=" + playerEntity.getId() + "}";
	}

	

	
}
