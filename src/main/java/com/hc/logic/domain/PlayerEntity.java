package com.hc.logic.domain;

import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * ���ʵ��
 * �������Եı仯��������player���set���������õġ�
 * @author hc
 *
 */
@Entity
@Table(name = "player")
public class PlayerEntity{
	
	@Id
	private int id;
    //����
	@Column
	private String name;
	//����
	@Column
	private String password;
	//�ȼ�
	@Column
	private int level;	
	//����
	@Column
	private int exp;
	//����id
	@Column
	private int sceneId;	
	//Ѫ��
	@Column
	private int hp;
	//����
	@Column
	private int mp;
	//���
	@Column
	private int gold = 100;
	
	@OneToOne(mappedBy="playerEntity", orphanRemoval = true, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private CopyEntity copyEntity;
	
	@Transient
	private boolean needDel=false;   //�Ƿ���Ҫ�����ݿ���ɾ����Ӧ��CopyEntity
	

	//����id
	@Column
	private String skills; //
	
	//��Ʒ
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	private Set<GoodsEntity> goods = new HashSet();  //����list����ִ���
	
	//����װ��/�������������Ѿ�������//orphanRemoval��������Զ���ɾ��Equipʱ������Equip����ɾ����Ӧ��Equip

	
	
	
	//private boolean isChanged = false;
	
	public PlayerEntity() {
		
	}
	public PlayerEntity(int id, int level, String name, String pass, int sceneId, 
			int hp, int mp, int exp, String skil, List<Equip> equips, GoodsEntity goods) {
		this.id = id;
		this.level = level;
		this.name = name;
		this.password = pass;
		this.sceneId = sceneId;
		this.hp = hp;
		this.mp = mp;
		this.exp = exp;
		this.skills = skil;
		//this.equips = new ArrayList<>(equips);  //�Ѵ�����װ��
		//this.goods = goods;
		
		//this.ortherEquips = new ArrayList<>();  //δ������װ����������Ʒ
	}

	
	
	
	/**
	 * ͨ�����ݿ��е����ʵ�壬����һ����Ҷ���
	 * @return
	 */
	public Player createPlayer(Session session) {
		String[] sk = skills.split(",");
		System.out.println("createPlayer--" + skills + "-8888-");
		int[] re = {};
		if(sk != null && sk.length > 0 && !skills.equals("")) {
			re = new int[sk.length];
			for(int i = 0; i < sk.length; i++) {
				re[i] = Integer.parseInt(sk[i]);
			}
		}

		Player pe = new Player(session, re, this); 
		return pe;
	}

	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getSceneId() {
		return sceneId;
	}
	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}

	public int getHp() {
		return hp;
	}


	public void setHp(int hp) {
		this.hp = hp;
	}


	public int getMp() {
		return mp;
	}


	public void setMp(int mp) {
		this.mp = mp;
	}

	
	
	public String getSkills() {
		return skills;
	}


	public void setSkills(List<Integer> skl) {
		StringBuilder sb = new StringBuilder();
		for(int ii : skl) {
			sb.append(ii + ",");
		}
		sb.deleteCharAt(sb.length()-1);

		this.skills = sb.toString();
	}
	
	public void addSkills(int sk) {
		StringBuilder sb = new StringBuilder(skills);
		sb.append("," + sk);
		skills = sb.toString();
	}

	
	
	public int getExp() {
		return exp;
	}


	public void setExp(int exp) {
		this.exp = exp;
	}
	
	

	public Set<GoodsEntity> getGoods() {
		return goods;
	}
	public void setGoods(Set<GoodsEntity> goods) {
		this.goods = goods;
	}
	public void delGoods(GoodsEntity ge) {
		this.goods.remove(ge);
	}
	
	


	public boolean isNeedDel() {
		return needDel;
	}
	public void setNeedDel(boolean needDel) {
		this.needDel = needDel;
	}
	public CopyEntity getCopEntity() {
		return copyEntity;
	}
	public void setCopEntity(CopyEntity copys) {
		this.copyEntity = copys;
	}
	public void addCopyEntity(CopyEntity copyEntity) {
		copyEntity.setPlayerEntity(this);
		this.copyEntity = copyEntity;
	}
	public void removeCopyEntity() {
		if(copyEntity != null) {
			copyEntity.setPlayerEntity(null);
			this.copyEntity = null;
		}
	}
	
	
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("���֣�" + name + "\n");
		sb.append("�ȼ�: " + level+ "\n");
		sb.append("���飺" + exp+ "\n");
		sb.append("Ѫ��hp: " + hp+ "\n");
		sb.append("����mp: " + mp);
		sb.append("��Ʒ��" + goods.toString());
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass() != this.getClass()) return false;
		PlayerEntity p = (PlayerEntity)o;
		if(p.getName().equals(name)) return true;
		return false;
	}
}
