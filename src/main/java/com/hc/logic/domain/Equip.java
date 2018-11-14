package com.hc.logic.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("equip")
public class Equip extends GoodsEntity{
	
	@Column
	private int duraion;  //�;ö�
	
	@Column 
	private int state;  //��Ʒ��ǰ��״̬,0�������У�1���Ѵ���
	
	
	

	
	public Equip() {
		
	}
	/**
	 * 
	 * @param eId ��Ʒid
	 * @param dr  �;ö�
	 */
	public Equip(int eId, int dr, PlayerEntity pe, UnionEntity ue) {
		super(eId, pe, ue);
	    this.duraion = dr;
	    this.state = 0;  //�¼ӵ���ƷĬ�Ϸ��ڱ���
	}

	public int getDuraion() {
		return duraion;
	}

	public void setDuraion(int duraion) {
		this.duraion = duraion;
	}
	
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "Equip{ "
				+ ", duration=" + duraion
				+" }";
	}
	
    
	
}
