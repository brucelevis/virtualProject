package com.hc.logic.config;

import java.util.HashMap;
import java.util.Map;

/**
 * ����
 * @author hc
 *
 */
public class TaskConfig {

	private int id;
	private int type;             //��������id
	private String name;          //��������
	private String need;          //�ַ�����ʽ�����������Ҫ������
	/**
	 * ��ʽ��
	 * ����type=1�Ļ�ɱ����key������id��value������
	 * ����type=2�Ĳɼ�����key����Ʒid��value������
	 * ����type=3�ĸ���ͨ������key������id��value��0
	 */
	private Map<Integer, Integer> needed; 
	private String award;         //�ַ�����ʽ��������ɺ�Ľ���
	/**
	 * ��ʽ��
	 * ��Ʒid�������������Ʒid=0��ʾ���
	 */
	private Map<Integer, Integer> awardit;
	
	public void convert() {
		convertNeed();
		convertAward();
	}
	private void convertNeed() {
		needed = new HashMap<>();
		String[] aw2nu = need.split(";");
		for(int i = 0; i < aw2nu.length; i++) {
			if(type == 3) {  //��ʾ����ͨ�����͵�����
				needed.put(Integer.parseInt(aw2nu[i]), 0);
			}else {
				String[] item = aw2nu[i].split(":");
				needed.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
			}
		}
		//System.out.println("�������ͣ�" + id + name +"��needed= " + needed.toString());
	}
	private void convertAward() {
		awardit = new HashMap<>();
		String[] aw2nu = award.split(";");
		for(int i = 0; i < aw2nu.length; i++) {
			String[] item = aw2nu[i].split(":");
			awardit.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setNeed(String need) {
		this.need = need;
	}
	public void setAward(String award) {
		this.award = award;
	}
	public Map<Integer, Integer> getNeeded() {
		return needed;
	}
	/**
	 * ͨ��id�����Ӧ��ֵ(������0/1)
	 * @param id
	 * @return
	 */
	public int getNeededByid(int id) {
		return needed.get(new Integer(id));
	}
	public Map<Integer, Integer> getAwardit() {
		return awardit;
	}
	/**
	 * ͨ��id�����Ӧ��ֵ(����)
	 * @param id
	 * @return
	 */
	public int getAwardByid(int id) {
		return awardit.get(new Integer(id));
	}

	
	
	
}
