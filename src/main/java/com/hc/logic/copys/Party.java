package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

/**
 * ���
 * @author hc
 *
 */
@Component
public class Party {
	
	private final String[] TEAM_INIT = {"team", "nu","������������ӣ��뾡��ͬ��"};
	private final String[] TEAM_ACC = {"team", "nu","���Ѿ�ͬ��������������"};

	/**
	 * �����������
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length != 3) {
			session.sendMessage("��������");
			return;
		}
		if(args[1].equals("1")) {
			//ͬ�����
			acceptParty(session, args);
			return;
		}else if(args[1].equals("n")) {
			//�ܾ����
			rejectParty(session, args[2]);
		}else {
			//�������
			creatParty(session, args);
		}
	}
	
	/**
	 * �������
	 * @param session
	 * @param args: group name1 name2
	 */
	public void creatParty(Session session, String[] args) {
		List<Player> players = new ArrayList<>();
		Player player = session.getPlayer();  //������
		for(int i = 1; i < args.length ; i++) {
			Player p = Context.getOnlinPlayer().getPlayerByName(args[i]);
			if(p == null) {
				rejectParty(session, session.getPlayer().getName());
				session.sendMessage("���ʧ�ܣ��������벻���ߵ���ҡ�" + args[i] + "����ӣ�");
				return;
			}
			if(p.getSceneId() != player.getSceneId()) {
				rejectParty(session, session.getPlayer().getName());
				session.sendMessage("���ʧ�ܣ�ֻ������ͬһ�����е���ҽ������");
				return;
			}
			if(player.getName().equals(args[i])) {
				session.sendMessage("���ܺ��Լ���ӣ�");
				return;
			}
			players.add(p);
		}
		for(Player pp : players) {
			Context.getChatService().privateChat(session, pp	.getId(), TEAM_INIT);
		}
		player.clearTeammate();
		player.addTeammate(players);
		System.out.println("------�������------" + players);
		//session.getPlayer().setInParty(true);		
		player.setSponserNmae(player.getName());
	}
	
	/**
	 * ͬ�����
	 * @param session
	 * @param args: group 1 name��name�Ƿ����ߵ�����
	 */
	public void acceptParty(Session session, String[] args) {
		String tName = args[2];
		Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tName);
		if(tPlayer == null) {
			session.sendMessage("Ŀ����Ҳ����ߣ��������");
			return;
		}
		if(!tPlayer.teamContain(session.getPlayer().getName())) {
			session.sendMessage("��Ҫ�������ͬ�⣬���߶�����ȡ��");
			return;
		}
		if(!isSponser(session, tName)) {
			return;
		}
		Context.getChatService().privateChat(session, tPlayer.getId(), TEAM_ACC);
		session.getPlayer().setSponserNmae(tName);		
		tPlayer.acTeam(session.getPlayer().getName());
	}
	
	/**
	 * �ܾ����
	 * @param session
	 * @param args��group r ����������
	 */
	public void rejectParty(Session session, String tName) {
		Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tName);
		if(!isSponser(session, tName)) return;
		List<String> pNames = tPlayer.getTeammate();
		for(String pn : pNames) {
			//֪ͨ�����Ѿ�ͬ����ӵ����ȡ�����
			Player p = Context.getOnlinPlayer().getPlayerByName(pn);
			p.getSession().sendMessage("���ȡ����");
			p.setSponserNmae(null);
		}
		//��շ����ߵ����״̬
		tPlayer.getSession().sendMessage("��������Ҿܾ������������������Ҳ����ߣ��������ʧ�ܣ�");
		tPlayer.clearTeammate();
		tPlayer.setSponserNmae(null);
	}
	

	/*
	 * ��֤tName�Ƿ��Ƿ����ߵ�����
	 */
	private boolean isSponser(Session session, String tName) {
		//��֤Ŀ������Ƿ��Ƿ�����
		Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tName);  //������ӵ���һ������
		if(tPlayer == null) {
			session.sendMessage("��Ҳ����ߣ���������Ƿ���ȷ��Ҳ����ȡ�������");
			return false;
		}
		if(!tPlayer.contPlaName(session.getPlayer().getName())) {
			session.sendMessage("��Ҫ�Է��������ͬ����ӣ�������������������");
			return false;
		}
		return true;
	}
}
