package com.hc.logic.base;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.PlayerEntity;

/**
 * ��½
 * @author hc
 *
 */
public class Login {
	
	
	private String name;
	private String password;

	public Login(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public void login(Session session) {
		//��ע�����ң���������û�еġ�
		Player player = Context.getWorld().getPlayerByName(name);
		//��������������ô��Ҫ�����ݿ�Ļ����У�Ѱ���Ƿ��Ѿ�ע���
		PlayerEntity playerEnt = Context.getWorld().getPlayerEntityByName(name);
		
		//ֻ�е����ݿ⻺�����ע�Ỻ���ж�û��ʱ������ȷ���������û��ע��
		if(playerEnt == null && player == null) {
		    session.sendMessage("�û��������ڣ�����ע��");
		    return;
		}
		if(playerEnt != null) {
			player = playerEnt.createPlayer(session);
		}
		
		
		if(!player.getPassword().equals(password)) {
			session.sendMessage("�������");
			return;
		}
		
		//�ж������½���˺��Ƿ��Ѿ���½�ˡ�
		for(Player p : Context.getOnlinPlayer().getOnlinePlayers()) {
			if(p.getName().equals(player.getName())) {
				session.sendMessage("����˺��Ѿ���½");
				return;
			}
		}
		
		
		//��½ʱ��Ҫ����Ҽ�����������б�OnlinePlayer��������û�е�½���Ͳ��������������б�
		Context.getOnlinPlayer().addPlayer(player);
		
		//���µ�½��Ҫ����Ҽ���ԭ���ĳ���
		Scene sc = Context.getWorld().getSceneById(player.getSceneId());
		if(sc.getPlayerByName(player.getName()) == null) {
			sc.addPlayer(player);
		}
		
		session.setPlayer(player);
		session.sendMessage("��½�ɹ�");
		//����½����ҵ�session��channel����
		Session pSession = player.getSession();
		pSession.setChannel(session.getChannel());
		//pSession.sendMessage("��½�ɹ�");
	    
	}
}
