package com.hc.logic.basicService;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.NpcConfig;
import com.hc.logic.config.TaskConfig;
import com.hc.logic.creature.Player;

@Component
public class NpcService {
	
	/**
	 * ����npc��ص�����
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length < 2 || args.length > 4) {
			session.sendMessage("�����������");
			return;
		}
		if(!OrderVerifyService.isDigit(args[1])) {
			session.sendMessage("�����������");
			return;
		}
		//��֤npc�Ƿ���ͬһ���������ڸ�����ʱҲû�����npc
		if(!isOnScene(session, Integer.parseInt(args[1]))) {
			return;
		}
		if(args.length == 3) {
			if(OrderVerifyService.isDigit(args[2])) {
				buyGoodsFromNpc(session, args);
				return;
			}
			enterCopys(session, args);
			return;
		}
		if(args.length == 2) {
			introduce(session, Integer.parseInt(args[1]));
			return;
		}
		//��������Ϊ4������
		if(!OrderVerifyService.isDigit(args[3])) {
			session.sendMessage("�����������");
			return;
		}
		if(args[2].equals("r")) { //��ҽ�����
			receiveTask(session, args);
		}
		if(args[2].equals("c")) {  //����ύ����
			completeTask(session, args);
		}
	}
	
	/**
	 * npc��player�Ƿ���ͬһ����
	 * ���� id��npc��id
	 */
	public boolean isOnScene(Session session, int id) {
		Player player = session.getPlayer();
		if(player.getSceneId() == 0) {
			session.sendMessage("�����ڸ����У�û��npc");
			return false;
		}
		List<Integer> nSId = Context.getSceneParse().getSceneById(session.getPlayer().getSceneId()).getNpcs();
		for(int ii : nSId) {
			if(ii == id)
				return true;
		}
		session.sendMessage("��ǰ������û�и�npc");
		return false;
	}

	/**
	 * ͨ��npc���븱��
	 * @param session
	 * @param args: npc npcId c
	 */
	public void enterCopys(Session session, String[] args) {
		if(!Context.getCopyService().canEnterCopy(session.getPlayer())) return;
		int npcId = Integer.parseInt(args[1]);
		int copyId = Context.getSceneParse().getNpcs().getNpcConfigById(npcId).getCopyId();
		Context.getCopyService().enterCopy(copyId, session.getPlayer(), session, 0);
	}
	
	/**
	 * ��npc��������
	 * @param session
	 * @param args: npc npcId ��Ʒid��Ĭ��һ��ֻ����һ��
	 */
	public void buyGoodsFromNpc(Session session, String[] args) {
		int npcid = Integer.parseInt(args[1]);
		int goodsid = Integer.parseInt(args[2]);
		if(Context.getSceneParse().getNpcs().getNpcConfigById(npcid).getGoodId() != goodsid) {
			session.sendMessage("û����Ӧ����Ʒ������");
			return;
		}
		boolean secc = Context.getStore().buyGood(session.getPlayer(), goodsid, 1);  //��npc��������Ĭ��һ��ֻ����һ��
		if(secc) {
			session.sendMessage("����ɹ�");
		}else {
			session.sendMessage("����ʧ�ܣ������Ƿ����㹻�Ľ��");
		}
	}
	/**
	 * �����Ľ��ܵ�ǰnpc
	 * @param id npc��id
	 */
	public void introduce(Session session, int id) {
		NpcConfig nc = Context.getSceneParse().getNpcs().getNpcConfigById(id);
		TaskConfig rtc = Context.getTaskParse().getTaskConfigByid(nc.getReceive());
		TaskConfig ctc = Context.getTaskParse().getTaskConfigByid(nc.getCheckout());
		StringBuilder sb = new StringBuilder();
		sb.append(nc.getName() + ": " + nc.getDescription() + "\n");
		if(rtc != null) {
			sb.append("�ɷ��ŵ�����" + rtc.getName() + "\n");
		}else {
			sb.append("û�пɷ��ŵ�����" + "\n");
		}
		if(ctc != null) {
			sb.append("�����յ�����" + ctc.getName());
		}else {
			sb.append("û�п����յ�����");
		}
		if(nc.getGoodId() != 0) {
			String name = Context.getGoodsParse().getGoodsConfigById(nc.getGoodId()).getName();
			sb.append("\n��������Ʒ��" + name);
		}
		if(nc.getCopyId() > 0) {
			String name = Context.getCopysParse().getCopysConfById(nc.getCopyId()).getName();
			sb.append("\n�ɽ���ĸ�����" + name +"��" + nc.getCopyId() + "��");
		}
		session.sendMessage(sb.toString());
	}
	
	/**
	 * ��ȡ��ǰnpc������
	 * @param session
	 * @param args: npc npcId ����id
	 */
	public void receiveTask(Session session, String[] args) {
		Player player = session.getPlayer();
		NpcConfig npcC = Context.getSceneParse().getNpcs().getNpcConfigById(Integer.parseInt(args[1]));
		if(npcC.getReceive() != Integer.parseInt(args[3])) {
			session.sendMessage("��ǰnpcû���������");
			return;
		}
		boolean sec = player.getPlayerTasks().addTask(Integer.parseInt(args[3]));
		if(sec) session.sendMessage("��������ɹ�");
		else session.sendMessage("��������ʧ�ܣ���ǰ�Ѿ���ɹ���������������");
	}
	
	/**
	 * �ύ���񣬻�ý���
	 * ��Ҫ��������Ƿ����
	 * @param session
	 * @param args��npc npcId ����id
	 */
	public void completeTask(Session session, String[] args) {
		Player player = session.getPlayer();
		NpcConfig npcC = Context.getSceneParse().getNpcs().getNpcConfigById(Integer.parseInt(args[1]));
		if(npcC.getCheckout() != Integer.parseInt(args[3])) {
			session.sendMessage("��ǰnpc���ܽ����������");
			return;
		}
		boolean sec = player.getPlayerTasks().getTaskAward(player, Integer.parseInt(args[3]));
		if(sec) {
			session.sendMessage("\n ������ɣ�");			
		}else {
			session.sendMessage("����δ��ɣ�");
		}
	}
}
