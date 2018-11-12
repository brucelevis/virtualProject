package com.hc.logic.config;

import java.util.ArrayList;
import java.util.List;

/**
 * ��Ʒ
 * @author hc
 *
 */
public class GoodsConfig{

	private int id;                //��Ʒid
	private int typeId;            //����id
	private String name;           //��Ʒ�� 
	private String description;    //��Ʒ����
	private int continueT;         //ʹ�ú�Ч������ʱ�䣻��������װ���������;ö�
	private int mp;                //ÿ�����ӷ���
	private int hp;                //ÿ������Ѫ��
	private int protect;            //���ӷ���  , ���ڲ�������װ������Ʒ
	private String sprotect;                   
	private List<Integer> protects = new ArrayList<>();  //������������װ������Ʒ
	private int attack;             //���ӹ���
	private String sattack;            
	private List<Integer> attacks = new ArrayList<>();
	private int superposi;          //�ڱ����еĿɵ�������
	private int price;             //����۸�
	
	
	
	public void convert() {
		if(typeId != 2 && typeId != 3) {
			//��������װ��
			convert(sprotect, protect);
			convert(sattack, attack);
		}else {
			//����װ��
			convert(sprotect, protects);
			convert(sattack, attacks);
		}
	}
	private void convert(String name, List<Integer> resu) {
		String[] names = name.split(",");
		for(int i = 0; i < names.length; i++) {
			resu.add(Integer.parseInt(names[i]));
		}
	}
	private void convert(String name, int resu) {
		//��������װ��
		resu = Integer.parseInt(name);
	}
	
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		//System.out.println("typeId &&&&&&&&&&&&&&&&&&&" + typeId);
		this.typeId = typeId;
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
	public int getContinueT() {
		return continueT;
	}
	public void setContinueT(int continueT) {
		this.continueT = continueT;
	}
	public int getMp() {
		return mp;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getSuperposi() {
		return superposi;
	}
	public void setSuperposi(int superposi) {
		this.superposi = superposi;
	}

	public void setSprotect(String sprotect) {
		this.sprotect = sprotect;
	}

	public void setSattack(String sattack) {
		this.sattack = sattack;
	}

	public int getProtect() {
		return protect;
	}
	public List<Integer> getProtects() {
		return protects;
	}
	/**
	 * ����ְҵ�����Ӧ����װ���ķ�����
	 * @param prof
	 * @return
	 */
	public int getProtectByPfog(int prof) {
		return protects.get(prof);
	}
	public void setProtects(List<Integer> protects) {
		this.protects = protects;
	}
	public int getAttack() {
		return attack;
	}
	/**
	 * ����ְҵ�����Ʒ�Ĺ�����
	 * @param prof
	 * @return
	 */
	public int getAttackByProf(int prof) {
		return attacks.get(prof);
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public List<Integer> getAttacks() {
		return attacks;
	}
	public void setAttacks(List<Integer> attacks) {
		this.attacks = attacks;
	}
	@Override
	public String toString() {
		return "good {id=" + id
				+ ", typeId=" + typeId
				+", name" + name +"}";
	}
}
