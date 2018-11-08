package com.hc.logic.base;

import java.util.ArrayList;
import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
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
			enterNormalScene(player, sceneId);
			return;
		}
		System.out.println("���븱��");
		CopyEntity copyEntity = player.getCopEntity();		
		int copyId = copyEntity.getCopyId();
		sceneId = Context.getCopysParse().getCopysConfById(copyId).getPlace();
		
		long etC = player.getCopEntity().getFirstEnterTime();
		long cur = System.currentTimeMillis();
		long dual = Context.getCopysParse().getCopysConfById(copyId).getContinueT();
		dual = dual * 60 * 1000; 
		System.out.println("!!!!!!!!����ʱ��"+etC + " ����ʱ�� " + cur + " �� " + (cur-etC) + " ����ʱ�� " + dual );
		if(dual > (cur - etC)) {
			//���������븱��, �趨boss�� index
			if(Context.getWorld().getCopyEntityById(copyId) != null) {
				//�����л��ж��ѣ�ֱ�ӽ��븱��
				System.out.println("�����л��ж��ѣ�ֱ�ӽ��븱��" + (player.getCopEntity()==null));
				int spsId = Context.getWorld().getPlayerEntityByName(player.getCopEntity().getSponsor()).getId();				
				Context.getWorld().getCopysByAPlayer(copyId, spsId).playerComeback(player);				
				return;
			}
			//�����ѱ��������Ҫ�ٴδ���
			int bossIndex = player.getCopEntity().getBossindex();
			//��������ʱ��Ҫע�⸱���ķ�����
			List<Player> players = new ArrayList<>();
			players.add(player);
			int sponsId = Context.getWorld().getPlayerEntityByName(player.getCopEntity().getSponsor()).getId();
			Context.getWorld().creatCopy(copyId, players, bossIndex, sponsId);	
			Context.getWorld().addCopyEntity(player.getCopEntity());  //��һ�������������Ҫ����copyenityt
			//Context.getCopyService().enterCopy(copyId, player, player.getSession(), bossIndex);
		}else {
			//��ʱ��������ͨ����
			System.out.println("��ʱ");
			//ɾ���������ݿ���Ϣ
			player.getPlayerEntity().setNeedDel(true);

			enterNormalScene(player, sceneId);
			
			timeout(player);
			
			System.out.println("��ʱ����ɾ���ɹ�");
		}
	}
	
	
	/**
	 * ������ʱ�������ѳ�ʱ����Ҫ����������ݿ�
	 * @param player
	 */
	public void timeout(Player player) {
		CopyEntity ce = player.getPlayerEntity().getCopyEntity();
		System.out.println("----------timeout-------------" + ce.toString());
		String hql = "select ce.players from CopyEntity ce where sponsor "
				+ "like : name";
		List<PlayerEntity> list = new PlayerDaoImpl().find(hql, player.getSponserNmae());
		System.out.println("------------" + (list==null) + ", " + list.toString());
		for(PlayerEntity pe : list) {
			pe.setCopyEntity(null);
			pe.setSceneId(player.getSceneId());
			new PlayerDaoImpl().update(pe);	
			Context.getWorld().updatePlayerEntity(pe);
		}
		player.setSponserNmae(null);
		player.clearTeammate();
		new PlayerDaoImpl().delete(ce);
	}
	
	private void enterNormalScene(Player player, int sceneId) {
		System.out.println("---------------����normalscene" + player  + ", sceneId="+sceneId);
		player.setSceneId(sceneId);
		Scene sc = Context.getWorld().getSceneById(player.getSceneId());
		System.out.println("---------------����normalscene��-" + player.getSceneId());
		if(sc.getPlayerByName(player.getName()) == null) {
			sc.addPlayer(player);
		}
	}
	
	
}
