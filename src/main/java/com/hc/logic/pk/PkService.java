package com.hc.logic.pk;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

@Component
public class PkService {

	private final String[] PK_INIT = {"pk", "nu","�������pk��������-)"};
	private final String[] PK_ACPT = {"pk", "nu", "�ҽ������pk�����ˣ�����"};
	
	/**
	 * ��������
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length < 2 || args.length > 3) {
			session.sendMessage("������ʽ����ȷ");
			return;
		}
		if(args.length == 2) {
			if(args[1].equals("g")) {
				//����
				giveUp(session);
			}else {
				//����pk����
				initPK(session, args);
			}
		}else if(args.length == 3) {
			//����pk����
			acceptPK(session, args);
		}
	}
	
	/**
	 * ����pk
	 * @param session
	 * @param args: pk Ŀ�������
	 */
	public void initPK(Session session, String[] args) {
		String tName = args[1];
		Player player = session.getPlayer();
		Player tPlayer = player.getScene().getPlayerByName(tName);
		if(tPlayer == null) {
			session.sendMessage("Ŀ����Ҳ��ڵ�ǰ��������pk��");
			return;
		}
		if(tPlayer.isInPK() || player.isInPK()) {
			session.sendMessage("����pk״̬�£���Է���pk״̬�£�����pk��");
			return;
		}
		Context.getChatService().privateChat(session, tPlayer.getId(), PK_INIT);
		player.setPkTarget(tName);
	}
	
	/**
	 * ����pk����
	 * @param session
	 * @param args�� pk 1  Ŀ�������
	 */
	public void acceptPK(Session session, String[] args) {
		Player player = session.getPlayer();
		Player tPlayer = player.getScene().getPlayerByName(args[2]);
		if(tPlayer == null) {
			session.sendMessage("�����Ҳ��ڵ�ǰ�����У�����������Ƿ�������ȷ");
			return;
		}
		if(tPlayer.isInPK()) {
			session.sendMessage("�Է��Ѿ���pk�У�����pkʧ��");
			return;
		}
		if((tPlayer.getPkTarget() == null) || (!tPlayer.getPkTarget().equals(player.getName()))) {
			session.sendMessage("Ҫ�����룬���ܽ���");
			return;
		}
		player.setInPK(true);
		tPlayer.setInPK(true);
		Context.getChatService().privateChat(session, tPlayer.getId(), PK_ACPT);
		player.setPkTarget(tPlayer.getName());
		tPlayer.setPkTarget(player.getName());
	}
	
	/**
	 * ���䡣
	 * @param session : һ�������䷽��session
	 */
	public void giveUp(Session session) {
		Player failP = session.getPlayer();
		Player winP = failP.getScene().getPlayerByName(failP.getPkTarget());
		session.sendMessage("��������ⳡpk");
		winP.getSession().sendMessage("�Է����䣬�����ⳡpk��ʤ����");
		Context.getTwoPlayerPK().geReward(winP, failP);
		winP.setInPK(false);
		winP.setPkTarget(null);
		failP.setInPK(false);
		failP.setPkTarget(null);
	}
	
	/**
	 * ��������󣬾�����
	 * @param player ������
	 */
	public void deadFailed(Player player) {
		giveUp(player.getSession());
	}
}
