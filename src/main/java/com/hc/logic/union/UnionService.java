package com.hc.logic.union;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.UnionEntity;

@Component
public class UnionService {

	/**
	 * ��������
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length < 2|| args.length > 4) {
			session.sendMessage("�����������");
			return;
		}
		if(args.length == 2) {  //�˳�����
			if(args[1].equals("d")) {
				quitUnion(session);
				return;
			}
			if(args[1].equals("f")) {
				allCandidate(session);
				return;
			}
			if(args[1].equals("s")) {
				unionState(session);
				return;
			}
			if(args[1].equals("e")) {
				allUnion(session);
				return;
			}
			session.sendMessage("�����������");
			return;
		}
		if(args.length == 4) { //�ӹ���ֿ�����Ʒ
			if(!args[2].equals("gold") && !OrderVerifyService.isDigit(args[2])) {
				session.sendMessage("�����������");
				return;
			}
			if(!OrderVerifyService.isDigit(args[3])) {
				session.sendMessage("�����������");
				return;
			}
			getGoodsFromUnion(session, args);
			return;
		}
		if(args.length != 3) {
			session.sendMessage("�����������");
		}
		
		if(args[1].equals("c")) {  //��������
			establishUnion(session, args);
			return;
		}
		if(args[1].equals("m")) {  //��ɢ����
			dissolveUnion(session, args);
			return;
		}
		if(args[1].equals("r")) {  //������빫��
			enterUnion(session, args);
			return;
		}
		if(args[1].equals("a")) {  //ͬ����빫������
			agreeEnter(session, args);
			return;
		}
		if(args[1].equals("j")) {  //�ܾ����빫������
			rejectEnter(session, args);
			return;
		}
		if(args[1].equals("t")) { // ������ҵĹ���ְλ
			upTitle(session, args);
			return;
		}
		if(args[1].equals("d")) {  //������ҵĹ���ְλ
			downTitle(session, args);
			return;
		}
		if((OrderVerifyService.isDigit(args[1]) || args[1].equals("gold")) && OrderVerifyService.isDigit(args[1])) {
			donateGoods(session, args);  //������Ʒ/��ҵ�����ֿ�
			return;
		}
		session.sendMessage("�����������");
	}
	
	/**
	 * ��������
	 * @param session
	 * @param args: union c ������
	 */
	public void establishUnion(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getUnion() != null) {
			session.sendMessage("��������ʧ�ܣ����Լ���һ��������");
			return;
		}
		boolean sess = Context.getWorld().createUnion(args[2], player.getName());
		if(sess) {
			player.enterUnion(args[2], 4);
			session.sendMessage("��������ɹ�");
		}else {
			session.sendMessage("��������ʧ�ܣ���������Ϊ����");
		}
	}
	/**
	 * ������й������Ϣ
	 * @param session
	 */
	public void allUnion(Session session) {
		List<UnionEntity> ulist = Context.getWorld().getUnionEntity();
		StringBuilder sb = new StringBuilder();
		sb.append("���еĹ������£�\n");
		for(UnionEntity ue : ulist) {
			String tn = Context.getUnionParse().getUCByid(ue.getGrade()).getName();
			sb.append(ue.getName() + " " + tn + "\n");
		}
		session.sendMessage(sb.toString());
	
	}
	
	/**
	 * ��ɢ����
	 * @param session
	 * @param args: union m ������
	 */
	public void dissolveUnion(Session session, String[] args){
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("�������������");
			return;
		}
		boolean secc = player.getUnion().dissolveUnion(player.getUnionName(), player.getName());
		if(secc) {
			session.sendMessage("��ɢ����ɹ�");
		}else {
			session.sendMessage("��ɢ����ʧ�ܣ��㲻���������Ļ᳤");
		}
	}
	
	/**
	 * ������빤��
	 * @param session
	 * @param args�� union r ������
	 */
	public void enterUnion(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getUnion() != null) {
			session.sendMessage("���Ѿ��ڹ����У����ܼ��빤��");
			return;
		}
		UnionEntity ue = Context.getWorld().getUnionEntityByName(args[2]);
		if(ue == null) {
			session.sendMessage("�������������");
			return;
		}
		boolean secc = ue.getUnion().enterUion(args[2], player);
		if(secc) {
			session.sendMessage("������빤��ɹ����ȴ��������");
		}else {
			session.sendMessage("���빤��ʧ��, ���᲻���ڣ����߹�������");
		}
	}
	
	/**
	 * �����˳�
	 * @param session
	 */
	public void quitUnion(Session session) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("�㲻�ڹ����У�����Ҫ�˳�");
			return;
		}
		boolean secc = player.getUnion().quitUnion(player);
		if(secc) {
			session.sendMessage("�˳�����ɹ�");
		}else {
			session.sendMessage("�˳�����ʧ�ܣ�ֻʣ��һ���˵Ĺ��᲻���˳������ǻ᳤Ҳ�����˳�");
		}
	}
	
	/**
	 * ������빤��
	 * @param session
	 * @param args: union a �����
	 */
	public void agreeEnter(Session session, String[] args) {
		Player player = session.getPlayer();
		if(!unionATitle(session, player)) return;
		boolean secc = player.getUnion().agreeEnter(player, args[2]);
		if(secc) {
			session.sendMessage("������빤��ɹ�");
			Player tp = Context.getOnlinPlayer().getPlayerByName(args[2]);
			if(tp != null ) tp.getSession().sendMessage("��ϲ���Ѽ��빤��[" + player.getUnionName() + "]");
		}else {
			session.sendMessage("���빤��ʧ�ܣ���������Ѿ������Ĺ��ᣬ���߹�������Ա");
		}
	}
	
	/**
	 * �ܾ����빤������
	 * @param session
	 * @param args: union j �����
	 */
	public void rejectEnter(Session session, String[] args) {
		Player player = session.getPlayer();
		if(!unionATitle(session, player)) return;
		boolean sec = player.getUnion().rejectEnter(player, args[2]);
		if(!sec) {
			session.sendMessage("û�����������");
			return;
		}
		session.sendMessage("�Ѿܾ����[" + args[2] +"]�ļ�������");
		Player tp = Context.getOnlinPlayer().getPlayerByName(args[2]);
		if(tp != null)
			tp.getSession().sendMessage("����[" + player.getUnionName() + "]�Ѿܾ���������");
	}
	
	/**
	 * �����¼���ְλ
	 * @param session
	 * @param args:  union t ����������
	 */
	public void upTitle(Session session, String[] args) {
		Player player = session.getPlayer();
		if(!unionATitle(session, player)) return;
		boolean secc = player.getUnion().titleUp(player, args[2], 1);
		if(secc) {
			PlayerEntity pe = Context.getWorld().getPlayerEntityByName(args[2]);
			int tit = pe.getUnionTitle();
			session.sendMessage("�����ɹ����������ߵ�ǰ�ȼ�Ϊ��" + Context.getTitlParse().getTCByid(tit).getName());
			Player tp = Context.getOnlinPlayer().getPlayerByName(args[2]);
			if(tp != null) {
				tp.getSession().sendMessage("���Ĺ���ְλ�����ˣ���������ְλ�ǣ�" + Context.getTitlParse().getTCByid(tit).getName());
			}
		}else {
			session.sendMessage("��������ȼ�ʧ�ܡ��϶Է��Ѳ��ڹ������������ְλ��������һ�������С");
		}
	}
	
	/**
	 * ������ҵĹ���ȼ�
	 * @param session
	 * @param args: union d ����������
	 */
	public void downTitle(Session session, String[] args) {
		Player player = session.getPlayer();
		if(!unionATitle(session, player)) return;
		boolean secc = player.getUnion().titleUp(player, args[2], -1);
		if(secc) {
			PlayerEntity pe = Context.getWorld().getPlayerEntityByName(args[2]);
			int tit = pe.getUnionTitle();
			session.sendMessage("��ְ�ɹ�������ְ�ߵ�ǰ�ȼ�Ϊ��" + Context.getTitlParse().getTCByid(tit).getName());
			Player tp = Context.getOnlinPlayer().getPlayerByName(args[2]);
			if(tp != null) {
				tp.getSession().sendMessage("���Ĺ���ְλ�����ˣ���������ְλ�ǣ�" + tp.getUnionTitle());
			}
		}
	}
	
	/**
	 * ������Ʒ/��ҵ�����ֿ�
	 * @param session
	 * @param args: union ��Ʒid ����
	 *                    gold   ����
	 */
	public void donateGoods(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("�������ڹ�����,���ȼ��빤��");
			return;
		}
		boolean secc = player.getUnion().donateGoods(player, args[1], Integer.parseInt(args[2]));
		if(secc) {
			session.sendMessage("�����ɹ�");
		}else {
			session.sendMessage("����ʧ�ܣ�Ҫô�ǲֿ�û����ô��յط���Ҫô����û����ô����Ʒ");
		}
	}
	
	/**
	 * �鿴�������������
	 * @param session
	 */
	public void allCandidate(Session session) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("�������ڹ�����,���ȼ��빤��");
			return;
		}
		List<String> names = player.getUnion().getCandidate();
		if(names.size() > 0) session.sendMessage("�������������£�" + names.toString());
		else session.sendMessage("���ڻ�û��������");
	}
	
	/**
	 * �ӹ���ֿ���ȡ��Ʒ
	 * @param session
	 * @param args�� union g ��Ʒid ����
	 *                       gold  ����
	 */
	public void getGoodsFromUnion(Session session, String[] args) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("�������ڹ�����,���ȼ��빤��");
			return;
		}
		boolean sec = player.getUnion().obtainGoods(player, args[2], Integer.parseInt(args[3]));
		if(sec) {
			session.sendMessage("�Ӳֿ��л����Ʒ�ɹ�");
		}else {
			session.sendMessage("��ȡ��Ʒʧ�ܣ������ǲֿ���û����ô����Ʒ��������ı�����������");
		}
	}
	
	/**
	 * �鿴����״̬
	 * @param session
	 */
	public void unionState(Session session) {
		Player player = session.getPlayer();
		if(player.getUnion() == null) {
			session.sendMessage("�������ڹ�����,���ȼ��빤��");
			return;
		}
		player.getUnion().unionState(session);
	}
	
	/**
	 * ��֤�Ƿ��ڹ����У��Լ�ְλ�Ƿ����Ҫ��
	 * @param session
	 * @param player
	 * @return
	 */
	private boolean unionATitle(Session session, Player player) {
		if(player.getUnion() == null) {
			session.sendMessage("�������ڹ�����,���ȼ��빤��");
			return false;
		}
		if(player.getUnionTitle() < 3) {
			session.sendMessage("����ְλ̫�ͣ�û�����������Ȩ��");
			return false;
		}
		return true;
	}
	
}
