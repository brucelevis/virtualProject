package com.hc.logic.base;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.creature.Player;
/**
 * ע��
 * 
 * ���ﴫ������뻹û���ô����Ժ�������ı���¼��ҵ�����
 * @author hc
 *
 */
@Component
public class Register {
	
	public void register(Session session, String name, String password) {
		//��ֹ�ظ�ע��
		if((Context.getWorld().getPlayerByName(name) != null)  || (Context.getWorld().getPlayerEntityByName(name) != null)) {
			session.sendMessage("��ǰ�û��Ѵ��ڣ�����������");
			return;
		}
		int id = Context.getpID();
		int hp = 0;
		int mp = 0;
		//Ѫ���ͷ�����ѡ��ְҵ������
		int[] skills = {0}; //Ĭ��û�м���
		Player player = new Player(id, 1, name, password, 1, hp, mp, 0, skills, session, true, new ArrayList<>());
		
		//Context.getWorld().getSceneById(1).addPlayer(player); //�ڳ���1��ע������ң�
		
		Context.getWorld().addAllRegisteredPlayer(player);  //�䵱���ݿ⣬����ͻ�����������
		session.setPlayer(player);  
		
		//System.out.println("--register--: " + session + " channel: " + session.getChannel());
		session.sendMessage("ע��ɹ�");
		choiceProf(session);
	}
	
	/**
	 * ע�����ʾѡ��ְҵ
	 * @param session
	 */
	private void choiceProf(Session session) {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for(Profession pf : Profession.values()) {
			sb.append( i + ", "+ pf.getTitle() + ": ");
			sb.append(pf.getDescription() + "\n");
			i++;
		}
		if(sb.length() > 1) sb.deleteCharAt(sb.length()-1);
		session.sendMessage("��ѡ��һ��ְҵ(���ú����޸�)�� \n" + sb.toString());
	}
	
	/**
	 * ����ѡ��ְҵ
	 * @param session
	 * @param index
	 */
	public void inChoiceProf(Session session, int index) {
		if(session.getPlayer().haveProf()) {
			session.sendMessage("�����ظ�����");
			return;
		}
		session.getPlayer().setProf(index);
		session.sendMessage("ְҵ���óɹ�������ְҵ�ǣ�" + Profession.getProfByIndex(index-1).getTitle());
		session.getPlayer().setHp(Context.getLevelParse().getLevelConfigById(1).getHpByProf(index-1));
		session.getPlayer().setMp(Context.getLevelParse().getLevelConfigById(1).getMpByProf(index-1));
	}



	
}
