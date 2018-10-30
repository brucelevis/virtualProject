package com.hc.logic.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ����
 * @author hc
 *
 */
public class CopysConfig {

	private int id;
	private String name;            //������
	private String description;     //��������
	private int times;              //ÿ���ܽ��븱������
	private int condition;          //���븱�������������ǵȼ�����
	private int place;              //���븱���ĳ���id��ͬʱҲ�ǴӸ����г�����Ŀ�ĵ�
	private int continueT;          //�����ӣ���������ʱ�䣬����ʱ�仹û��ɣ���ʧ��
	private String sBosses;          //���������е�boss��id,�Զ��ŷָ�
	private List<Integer> bosses;
	private String sRewords;         //��ɸ�����õĽ���
	private Map<String, Integer> rewords;   //��ʽ��key����������value������
	
	
	
	public void convert() {
		if(sBosses != null) {
			bosses = new ArrayList<>();
			String[] sbo = sBosses.split(",");
			for(String ss : sbo) {
				bosses.add(Integer.parseInt(ss));
			}
		}
		if(sRewords != null) {
			rewords = new HashMap<>();
			String[] sRe = sRewords.split(";");
			for(String ss : sRe) {
				String[] rewo = ss.split(":");
				rewords.put(rewo[0], Integer.parseInt(rewo[1]));
			}
		}
	}
	
	/**
	 * v�ж�bid���boss֮���Ƿ���boss�����У��򷵻� boss id�����򣬷��� -1
	 * 
	 * ����������ǰ�˳��������
	 * @param bid boss��id
	 * @return
	 */
	public int moreBoss(int bid) {
		System.out.println("-----------------moreBoss--bid=" + bid);
		int bIndex = bosses.indexOf(new Integer(bid));
		if(bIndex >= (bosses.size()-1)) return -1;
		System.out.println("-----------------moreBoss-��-index=" + (bIndex+1) + " bosses�б�" + bosses.toString());
		return bosses.get(bIndex+1);
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public int getCondition() {
		return condition;
	}
	public void setCondition(int condition) {
		this.condition = condition;
	}
	public int getPlace() {
		return place;
	}
	public void setPlace(int place) {
		this.place = place;
	}
	public int getContinueT() {
		return continueT;
	}
	public void setContinueT(int continueT) {
		this.continueT = continueT;
	}
	public String getsBosses() {
		return sBosses;
	}
	public void setsBosses(String sBosses) {
		this.sBosses = sBosses;
	}
	public List<Integer> getBosses() {
		return bosses;
	}

	public String getsRewords() {
		return sRewords;
	}
	public void setsRewords(String sRewords) {
		this.sRewords = sRewords;
	}
	public Map<String, Integer> getRewords() {
		return rewords;
	}

	
	
	
	
	
}
