package com.hc.logic.deal;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.creature.Player;

@Component
public class DealService {

	private final String[] DEAL_INIT = {"deal", "nu","���������н��ף�������"};
	private final String[] DEAL_ACC = {"deal", "ac","���Ѿ�ͬ����Ľ������󣬿��Կ�ʼ���н����ˣ����ȷ������뽻�׵���Ʒ"};
	
	/**
	 * ��������
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length > 3 || args.length < 2) {
			session.sendMessage("�����������ȷ");
			return;
		}
		if(args.length == 3) {
			if(args[1].equals("b")) {
				//ͬ����н���
				acceptDeal(session, args);
				return;
			}else if(args[1].equals("gold") || OrderVerifyService.isDigit(args[1])) {
				//չʾ��Ʒ
				if(!OrderVerifyService.isDigit(args[2])) {
					session.sendMessage("�����������");
					return;
				}
				showGoods(session, args);
				return;
			}
			session.sendMessage("�����������ȷ");
			return;
		}
		if(args[1].equals("a")) {
			//ͬ�⽻��
			hopeDeal(session);
		}else if(args[1].equals("r")) {
			//�ܾ�����
			notHopeDeal(session);
		}else if(OrderVerifyService.isDigit(args[1]) && Integer.parseInt(args[1]) == 0) {
			//��������
			stopDeal(session);
		}else {
			//������
			initDeal(session, args);
		}
	}
	
	/**
	 * ������
	 * @param session
	 * @param args: deal Ŀ�������
	 */
	public void initDeal(Session session, String[] args) {
		Player player = session.getPlayer();
		String tName = args[1];  //Ŀ�������
		Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tName); //Ŀ�����
		if(player.getDeal() != null) {
			session.sendMessage("���Ѿ��ڽ���״̬�����Ƚ�������״̬���ٷ���������");
			return;
		}
		if(tPlayer == null) {
			session.sendMessage("Ŀ����Ҳ����ߣ����ܷ�����");
			return;
		}
		if(tPlayer.getSceneId() != player.getSceneId()) {
			session.sendMessage("������ʧ�ܣ�ֻ�ܺ�ͬһ�����е���ҽ��н���");
			return;
		}
		if(tPlayer.getDeal() != null) {
			session.sendMessage("�Է��ڽ����У����ܷ���������");
			return;
		}
		player.beginDeal(tName);
		Context.getChatService().privateChat(session, tPlayer.getId(), DEAL_INIT);
	}
	
	/**
	 * ͬ����н���
	 * @param session
	 * @param args��deal 1 ����������
	 */
	public void acceptDeal(Session session, String[] args) {
		Player player = session.getPlayer();
		String spName = args[2];  //����������
		Player sponsor = Context.getOnlinPlayer().getPlayerByName(spName);
		if(sponsor == null) {
			session.sendMessage("�����׵���ҿ��ܶ����ˣ����н���ʧ��");
			return;
		}
		if(player.getDeal() != null) {
			session.sendMessage("�����ڽ���״̬������ͬ�⽻������");
			return;
		}
		Deal ddel = sponsor.getDeal();
		if(ddel == null) {
			session.sendMessage("�Է���ȡ�����ף�����ֹͣ");
			return;
		}
		if(!isSponsor(session, sponsor)) return;
		player.accDeal(ddel);
		player.getDeal().setSpName(spName);
		session.sendMessage("������Գɹ������ǿ��Կ�ʼ�����ˣ����ھͿ���չʾ��Ʒ��");
		sponsor.getSession().sendMessage("�Է�ͬ�������н��ף����׿�ʼ�����Կ�ʼչʾ��Ʒ�ˣ�");
	}
	
	/**
	 * ������н��׵���Ʒ
	 * @param session
	 * @param args: deal ��Ʒid ����
	 *              deal gold   ����
	 */
	public void showGoods(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getDeal() == null) {
			session.sendMessage("��Ҫ�Ƚ��н�����ԣ����ܽ�����Ʒչʾ");
			return;
		}
		String counterp = player.getDeal().getCounter(player.getName());
		Player counterPlayer = Context.getOnlinPlayer().getPlayerByName(counterp);  //���׷�
		//�ж��Ƿ��ڽ���״̬
		if(player.getDeal().getSpName() == null || player.getDeal().getTpName() == null) {
			session.sendMessage("����δ���뽻��״̬�����ܷ��ý�����Ʒ��");
			return;
		}
		if(counterPlayer == null) {
			session.sendMessage("�Է��Ѿ������ߣ�����ֹͣ");
			stopDeal(player, counterPlayer);
			return;
		}
		if(!Context.getGoodsService().goodsEnough(player.getPlayerEntity(), args[1], Integer.parseInt(args[2]))) {
			session.sendMessage("û�������Ʒ��������������");
			return;
		}
		session.sendMessage("������Ʒ�ɹ�����ϣ�����׵���ƷΪ��");
		String msg = "";
		if(!OrderVerifyService.isDigit(args[1])) {
			msg = "���  " + args[2] + "��";
		}else { 
			String gName = Context.getGoodsParse().getGoodsConfigById(Integer.parseInt(args[1])).getName();
			msg = gName + " " +args[2] + "��";
		}
		session.sendMessage(msg);	
		counterPlayer.getSession().sendMessage("�Է�ϣ�����׵���ƷΪ: " + msg);
		player.getDeal().showGoods(player.getName(), args);
	}
	
	/**
	 * ��˫�����н��׵���Ʒ���⣬ͬ�⽻��
	 * @param session
	 */
	public void hopeDeal(Session session) {
		Player player = session.getPlayer();
		if(player.getDeal() == null) {
			session.sendMessage("��Ҫ�Ƚ��н�����ԣ����ܽ��н���");
			return;
		}
		String countName = player.getDeal().getCounter(player.getName());
		Player countPlayer = Context.getOnlinPlayer().getPlayerByName(countName);
		if(countPlayer == null) {
			session.sendMessage("�Է������ˣ�����ֹͣ");
			stopDeal(player, countPlayer);
			return;
		}
		if(!player.getDeal().isReadyVerify()) {
			session.sendMessage("���������Ʒչʾ���ٽ���ȷ��");
			return;
		}
		player.getDeal().hopeDeal(player.getName());
		session.sendMessage("���Ѿ�ͬ����н�����");
		countPlayer.getSession().sendMessage("���[" +player.getName()+"]�Ѿ�ͬ�⽻����");
		
		//��������Ҷ�ͬ����Զ����н�����
		if(player.getDeal().isReadyChange()) {
			player.getDeal().exchangeGoods();
			session.sendMessage("���׳ɹ�");
			countPlayer.getSession().sendMessage("���׳ɹ�");
		}
	}
	
	/**
	 * �����϶Է�����Ʒ���ܾ�����
	 * @param session
	 */
	public void notHopeDeal(Session session) {
		Player player = session.getPlayer();
		Deal deal = player.getDeal();
		if(deal == null) {
			session.sendMessage("���ȷ�����Ʒ������ȡ������");
			return;
		}
		if(!deal.isReadyVerify()) {
			session.sendMessage("���������Ʒչʾ���ſ��Ծܾ�����");
			return;
		}
		deal.notHopeDeal(player.getName());  //ֻҪһ���ܾ����ף��Զ���ս������ݺͽ���״̬
		session.sendMessage("���Ѿ��ܾ��˴˴ν���");
		String counName = deal.getCounter(player.getName());
		Player countp = Context.getOnlinPlayer().getPlayerByName(counName);
		if(countp == null) return;
		countp.getSession().sendMessage("�Է��Դ˴ν��ײ����⣬�Ѿ��ܾ��˽��ף���������չʾ��Ʒ");
	}
	
	/**
	 * ֹͣ����
	 * @param session
	 */
	public void stopDeal(Session session) {
		Player player = session.getPlayer();
		Deal deal = player.getDeal();
		if(deal == null) {
			session.sendMessage("��û�н��뽻�ף�Ҳ�Ͳ���ֹͣ����");
			return;
		}
		player.stopDeal();
		session.sendMessage("�˳����׳ɹ�");
		String countName = deal.getCounter(player.getName());
		if(countName != null) {
			Player cp = Context.getOnlinPlayer().getPlayerByName(countName);
			if(cp != null) {
				cp.stopDeal();
				cp.getSession().sendMessage("�Է��˳��˽���");
			}
		}
	}
	
	/**
	 * ֹͣ����
	 * @param p1
	 * @param p2
	 */
	public void stopDeal(Player p1, Player p2) {
		if(p1 != null) p1.stopDeal();
		if(p2 != null) p2.stopDeal();
	}
	

	
	
	/**
	 * �Ƿ��Ƿ�������
	 * @param spName
	 * @return
	 */
	private boolean isSponsor(Session session, Player sponsor) {
		Player pp = session.getPlayer();
		String counterName = sponsor.getDeal().getTpName();
		if(!pp.getName().equals(counterName)) {
			session.sendMessage("��Ҫ�ȷ��������󣬲��ܹ�ͬ�⽻��");
			return false;
		}
		return true;
	}
	
	
}
