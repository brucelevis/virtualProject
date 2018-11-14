package com.hc.logic.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.union.Union;

@Entity
public class UnionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column
	private String name;       //������
	
	@Column
	private long createTime;    //���ᴴ��ʱ��
	
	@Column
	private String originator;   //���ᴴʼ��
	
	@Column
	private int grade;            //����ȼ�
	
	@Column
	private int exp;             //���ᾭ��
	
	@Column
	private int pnum;              //�����е�����
	
	//����ֿ���Ʒ
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private List<GoodsEntity> goods = new ArrayList<>(); 
	
	@Column
	private int gold;  //�ֿ��еĽ��
	
	@Transient
	private Union union;
	
	public UnionEntity() {
		
	}
	public UnionEntity(String uname, String pname) {
		this.name = uname;
		this.originator = pname;
		this.pnum = 1;   //��������ʱ����������Ĭ��Ϊ1
		this.grade = 1;
		this.createTime = System.currentTimeMillis();
	}
	
	/**
	 * ���union
	 * @return
	 */
	synchronized public Union getUnion() {
		if(union == null) {
			String hql = "select u.goods from UnionEntity u "
					+ " where u.originator like : name";
			this.goods = new PlayerDaoImpl().find(hql, originator);
			//System.out.println("-------------��ʼ��goods-------" + (goods==null));
			union = new Union(this);
		}
		return union;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	public List<GoodsEntity> getGoods() {
		return goods;
	}
	public void setGoods(List<GoodsEntity> goods) {
		this.goods = goods;
	}
	public void delGoods(GoodsEntity ge) {
		this.goods.remove(ge);
	}
	public GoodsEntity delGoods(int gid) {
		for(GoodsEntity ge : goods) {
			if(ge.geteId() == gid)
				goods.remove(ge);
				return ge;
		}
		return null;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getPnum() {
		return pnum;
	}
	public void setPnum(int pnum) {
		this.pnum = pnum;
	}

	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	
	
}
