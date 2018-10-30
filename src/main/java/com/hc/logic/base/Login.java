package com.hc.logic.base;

import java.util.ArrayList;
import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
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
		
		//���µ�½��Ҫ����Ҽ���ԭ���ĳ���/����
		enterScene(player);
		
		session.setPlayer(player);
		session.sendMessage("��½�ɹ�");
		//����½����ҵ�session��channel����
		Session pSession = player.getSession();
		pSession.setChannel(session.getChannel());
		//pSession.sendMessage("��½�ɹ�");
	    
	}
	
	/**
	 * ��½ʱ����Ҫ�ж�����ϴ��˳�ʱ�������ֳ����С�
	 * ��������ͨ�����У���������½�ͺ�
	 * �����ڸ����У���ô����Ҫ���ж��Ƿ�ʱ����û�г�ʱ��Ҫ�ָ�����
	 * ����ʱ���ͽ���ԭ����
	 */
	public void enterScene(Player player) {
		int sceneId = player.getSceneId();
		if(sceneId != 0) {
			//������ͨ����
			System.out.println("������ͨ����");
			enterNormalScene(player);
			return;
		}
		System.out.println("���븱��");
		int copyId = player.getCopEntity().getCopyId();
		long etC = player.getCopEntity().getFirstEnterTime();
		long cur = System.currentTimeMillis();
		long dual = Context.getCopysParse().getCopysConfById(copyId).getContinueT();
		dual = dual * 60 * 1000; 
		System.out.println("!!!!!!!!����ʱ��"+etC + " ����ʱ�� " + cur + " �� " + (cur-etC) + " ����ʱ�� " + dual );
		if(dual > (cur - etC)) {
			//���������븱��, �趨boss�� index
			int bossIndex = player.getCopEntity().getBossindex();
			Context.getCopyService().enterCopy(copyId, player, player.getSession(), bossIndex);
		}else {
			//��ʱ��������ͨ����
			System.out.println("��ʱ");
			player.setSceneId(Context.getCopysParse().getCopysConfById(copyId).getPlace());
			//ɾ���������ݿ���Ϣ
			player.getPlayerEntity().setNeedDel(true);
			Context.getTaskProducer().addTask(new CopyPersist(player.getPlayerEntity()));
			System.out.println("��ʱ����ɾ���ɹ�");
			enterNormalScene(player);
		}
	}
	
	private void enterNormalScene(Player player) {
		Scene sc = Context.getWorld().getSceneById(player.getSceneId());
		if(sc.getPlayerByName(player.getName()) == null) {
			sc.addPlayer(player);
		}
	}
	
	
}
