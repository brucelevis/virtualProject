package com.hc.logic.config;

public class GoodsConfig {

	private int id;              
	private int typeId;            //����id
	private String name;           //��Ʒ�� 
	private String description;    //��Ʒ����
	private int continueT;         //ʹ�ú�Ч������ʱ��
	private int mp;                //ÿ�����ӷ���
	private int hp;                //ÿ������Ѫ��
	private int protect;           //���ӷ���
	private int attack;            //���ӹ���
	private int superposi;          //�ڱ����еĿɵ�������
	private int price;             //����۸�
	
	
	
	
	
	
	
	
	
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
	public int getProtect() {
		return protect;
	}
	public void setProtect(int protect) {
		this.protect = protect;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
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
	
	
}
