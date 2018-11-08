package com.hc.login.store;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;

@Component
public class StoreService {
	
	/**
	 * ����̵꣬����������̵�ҳ��
	 * @param session
	 * @param page
	 */
	public void lookStore(Session session, int page) {
		Store store = Context.getStore();
		if(!store.isValiedPage(session.getPlayer(), page)) {
			session.sendMessage("��תʧ�ܣ��̵�û����ôҳ�棬������Ҳ�������ҳ��");
			return;
		}
		String dis = store.displStore(page);
		//�����������̵�ҳ��
		session.getPlayer().setPageNumber(page); 
		if(dis.equals("")) session.sendMessage("��");
		else session.sendMessage(dis);
	}
	
	/**
	 * ������Ʒ������֤��ǰ�̵�ҳ���Ƿ��д���Ʒ
	 * @param session
	 * @param gid
	 * @param amount
	 */
	public void validBuyGood(Session session, int gid, int amount) {
		if(session.getPlayer().getPageNumber() == 0) {
			session.sendMessage("��ǰ�������̵�ҳ�棬���Ƚ����̵�");
			return;
		}
		Store store = Context.getStore();
		//��֤Ҫ�������Ʒ�Ƿ�������̵�ҳ��
		boolean within = store.withinPage(session.getPlayer(), gid);
		if(!within) {
			session.sendMessage("��Ҫ�������Ʒ���ڵ�ǰ�б��У���������Ƿ���ȷ��");
			return;
		}
		if(amount > 100) {
			session.sendMessage("����ʧ�ܣ�һ��ֻ�ܹ���100����Ӧ��Ʒ");
			return;
		}
		boolean hasbuy = store.buyGood(session.getPlayer(), gid, amount);
		if(!hasbuy) session.sendMessage("����ʧ�ܣ������Ƿ����㹻�Ľ��");
		else session.sendMessage("����ɹ�");
	}

}
