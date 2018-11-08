package com.hc.logic.base;

import java.util.ArrayList;


import com.hc.frame.Context;
import com.hc.logic.creature.Player;
/**
 * ע��
 * 
 * ���ﴫ������뻹û���ô����Ժ�������ı���¼��ҵ�����
 * @author hc
 *
 */
public class Register {

	private Player player;
	
	private String name;
	private String password;
	
	public Register(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public void register(Session session) {
		//��ֹ�ظ�ע��
		if((Context.getWorld().getPlayerByName(name) != null)  || (Context.getWorld().getPlayerEntityByName(name) != null)) {
			session.sendMessage("��ǰ�û��Ѵ��ڣ�����������");
			return;
		}
		int id = Context.getpID();
		int hp = Context.getLevelParse().getLevelConfigById(1).getHp();
		int mp = Context.getLevelParse().getLevelConfigById(1).getMp();
		int[] skills = {}; //Ĭ��û�м���
		player = new Player(id, 1, name, password, 1, hp, mp, 0, skills, session, true, new ArrayList<>());

		//������ֱ������2���ܣ��Ժ��������Ӧ��ָ��
		//player.addSkill(2);
		//System.out.println("register: " + player.getSkills().toString() + " *** " + player.getPlayerEntity().getSkills());
		

		
		
		//player.setName(name);
		//player.setPassword(password);
		//player.setAlive(true);
		//player.setLevel(1);  //��ʼ�ȼ�
		//player.setHp(100);   //��ʼѪ��
		//player.setMp(20);    //��ʼ����
		//player.addSkill(1);  //��ʼ����
		//player.setExp(0);   //��ʼ����
	    
		//player.setSceneId(Context.getBornPlace().getId());   //���ע�᳡��id
		//Context.getBornPlace().addPlayer(player);            //�ڳ�����ע������ң�
		//player.setSceneId(1);  //���ע�ᣬĬ�Ͻ��볡��1����������
		Context.getWorld().getSceneById(1).addPlayer(player); //�ڳ���1��ע������ң�
		
		Context.getWorld().addAllRegisteredPlayer(player);  //�䵱���ݿ⣬����ͻ�����������
		//Context.getWorld().addPlayerEntity(player.getPlayerEntity());
		//player.setSession(session);  //ֻ����ע��׶Σ���ҲŻ�����session
		//player.setAttack(1);
		//player.setId(Context.getpID());   
		
		
		
		//��session��ע��player
		session.setPlayer(player);  
		
		//System.out.println("--register--: " + session + " channel: " + session.getChannel());
		session.sendMessage("ע��ɹ�");
	}

	public Player getPlayer() {
		return player;
	}

	
}
