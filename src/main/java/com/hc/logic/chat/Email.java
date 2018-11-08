package com.hc.logic.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.hc.frame.Context;
import com.hc.logic.base.Constants;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.EmailEntity;
import com.hc.logic.domain.PlayerEntity;


public class Email {
	//�������ݡ�list�а��������֣������ �ʼ���������
	private Stack<List<String>> emails = new Stack<>();
	//����ÿҳ��ʾ���ʼ�����
	private final int PAGENUM = 3;
	//����ҳ��
	private int emailPage = 0;
	
	//��player�е���
	public Email(List<EmailEntity> lists) {
		for(EmailEntity ee : lists) {
			addEmail(ee.getContent());
		}
	}
	
	/**
	 * ��ҳ��ʾ��������
	 */
	public String displayEmail(int page) {
		Map<Integer, String> emailPage = aPage(page);
		List<Integer> keys = sortedKeys(emailPage.keySet());
		StringBuilder sb = new StringBuilder();
		sb.append("�����䡿- - - - - - - - - - - - - - - - - - - - - - - -- - - - - - - -\n");
		sb.append("- - - - - - - - - -�ڡ�" + page + "��ҳ- - - - - - - - - - - -- -  -- - - - -\n");
		for(int i : keys) {
			sb.append("��"+ i + "��" + emailPage.get(i) + "\n"); //��Ҫ��1
		}
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -- - - - -");
		return sb.toString();
	}
	
	private List<Integer> sortedKeys(Set<Integer> keys){
		List<Integer> resu = new ArrayList<>();
		for(int i : keys)
			resu.add(i);
		Collections.sort(resu);
		return resu;
	}
	
	/**
	 * ��õ�nҳ���ʼ�
	 * @param page
	 * @return map: key: ������list�е�λ��+1�� value����Ӧ������
	 */
	public Map<Integer, String> aPage(int page){
		//key: ������list�е�λ��+1�� value����Ӧ������
		Map<Integer, String> result = new HashMap<>();
		int start = PAGENUM * (page-1);
		int stop = PAGENUM + start;System.out.println("------------ ����---- " + start +" " + stop);
		for(int i = start; i < stop; i++) {
			if(i >= emails.size()) break; //����һҳ
			result.put(i+1, emails.get(i).get(0)); //ֻ��ʾ�ʼ�����
		}
		return result;
	}
	
	/**
	 * ��ȡ�ʼ�����
	 * @param index �ʼ����
	 * @return
	 */
	public String readEmail(int index) {
		List<String> em = emails.get(index-1);
		String[] content= em.get(1).split(" ");
		if(Character.isDigit(content[0].charAt(0))) {
			return readGoodsEmail(content, index);
		}
		return readNormalEmail(content, index);
	}
	
	//��ȡ�����ʼ�,content��ʽ��1     Ŀ������� ���� ���ֵ���
	//���ֵ��߸�ʽ: gold:30;exp:20...
	private String readGoodsEmail(String[] content, int index) {
		int pId = Context.getWorld().getPlayerEntityByName(content[1]).getId();
		Player player = Context.getOnlinPlayer().getPlayerById(pId); //��ȡ�ʼ�����ҿ϶�����
		StringBuilder sb = new StringBuilder();
		sb.append("ϵͳ��������������: \n");
		String[] items = content[3].split(";");
		for(int i = 0; i < items.length; i++) {
			String[] nameAmount = items[i].split(":");
			Constants.doReword(player, nameAmount[0], nameAmount[1]);
			sb.append(nameAmount[0] + " " + nameAmount[1] + "��");
		}
		//ɾ�������ʼ�
		delEmail(player, index);
		return sb.toString();
	}
	//��ȡ��ͨ�ʼ���content��ʽ��email ����� Ŀ������� ����  ����
	private String readNormalEmail(String[] content, int index) {
		PlayerEntity tpe = Context.getWorld().getPlayerEntityByName(content[2]);
		if(tpe == null) {
			Player player = Context.getWorld().getPlayerByName(content[2]);
			tpe = player.getPlayerEntity();
		}
		int pId = tpe.getId(); 
		//int pId = Context.getWorld().getPlayerEntityByName(content[2]).getId();
		Player player = Context.getOnlinPlayer().getPlayerById(pId); //��ȡ�ʼ�����ҿ϶�����
		StringBuilder sb = new StringBuilder();
		sb.append("��ҡ�" + content[1] + "�����͵���Ϣ����: \n");
		for(int i = 4; i < content.length; i++) {
			sb.append(content[i] + " ");
		}
		return sb.toString();
	}
	
	/**
	 * ɾ���ʼ�, ����ɾ�����ݿ��е��ʼ�
	 * @param index �ʼ����
	 */
	public String delEmail(Player player, int index) {
		String content = emails.get(index-1).get(1);
		emails.remove(index-1);
		removeEmail(player, content);
		return content;
	}
	
	//ɾ���ʼ�
	private void removeEmail(Player player, String content) {
		PlayerEntity tpe = player.getPlayerEntity();
		for(EmailEntity ee : tpe.getEmails()) {
			if(ee.getContent().equals(content)) {
				tpe.delEmail(ee);
				return;
			}
		}
	}

	
	
	/**
	 * ҳ���Ƿ���Ч
	 * @param page 
	 * @return
	 */
	public boolean isValiedPage(int page) {
		if(page < 1) return false;
		int start = PAGENUM * (page-1) + 1;
		int volum = emails.size();
		if(volum == 0 && page == 1) return true;  //������Ϊ��ʱ��ֻ������ʾ��һҳ
		if(start > volum) return false;	
		//int playerPage = player.getPageNumber();
		if(page != emailPage && page != (emailPage + 1) && page != (emailPage - 1)) return false;
		return true;
	}
	
	/**
	 * �ж��ʼ��ı���Ƿ��ڵ�ǰҳ��
	 * @param index ���������ʼ����
	 * @return
	 */
	public boolean withinPage(int index) {
		for(int i : aPage(emailPage).keySet()) {
			if(i == index)
				return true;
		}
		return false;
	}

	public void setEmailPage(int emailPage) {
		this.emailPage = emailPage;
	}
	public int getEmailPage() {
		return emailPage;
	}

	/**
	 * ����ʼ�
	 * @param subj ����
	 * @param msg  ����
	 */
	public void addEmail(String subj, String msg) {
		List<String> list = new ArrayList<>();
		list.add(subj);
		list.add(msg);
		emails.push(list);
	}
	
	public Stack<List<String>> getEmail(){
		return emails;
	}
	
	/**
	 * ͨ���ʼ�������������ʼ�
	 * @param content
	 */
	public void addEmail(String content) {
		String[] con = content.split(" ");
		if(Character.isDigit(con[0].charAt(0))) {
			addEmail(con[2], content);
		}else {
			addEmail(con[3], content);
		}
	}
}
