package com.hc.logic.chat;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.dao.impl.UpdateTask;
import com.hc.logic.domain.EmailEntity;
import com.hc.logic.domain.PlayerEntity;

@Component
public class EmailService {
	
	/**
	 * ��������ϵͳ����
	 * @param session
	 * @param args
	 */
	public void descOrder(Session session, String[] args) {
		Email email = session.getPlayer().getEmail();
		if(args.length == 1){
			session.sendMessage("��ӭ��������");
			lookEmails(session, 1);
			return;
		}
		if((args.length > 1 && email.getEmailPage() == 0) ) {
			session.sendMessage("���Ƚ�������ϵͳ");
			return;
		}else if(args.length == 2) {
			if(!pisDigit(args[1])) {
				session.sendMessage("�������ʹ�������������");
				return;
			}
			if(Integer.parseInt(args[1]) == 0) {
				email.setEmailPage(0);
				session.sendMessage("�˳�����ϵͳ");
				return;
			}
			//�鿴�ʼ��б�
			lookEmails(session, Integer.parseInt(args[1]));
		}else if(args.length == 3 ) {
			if(!pisDigit(args[2])) {
				session.sendMessage("�������ʹ�������������");
				return;
			}
			if(args[1].equals("r")) {
				//�Ķ��ʼ�����
				readEmail(session, Integer.parseInt(args[2]));
			}else if(args[1].equals("d")){
				//ɾ���ʼ�
				delEmail(session, Integer.parseInt(args[2])); 
			}
		}else if(args.length > 3){
			//�����ʼ�
			sendNormalEmail(session, args[1], orderContent(args));
		}
		
	}
	
	/**
	 * �鿴�����ĳһҳ
	 * �����е��ʼ��ǵ����
	 * @param session
	 * @param page
	 */
	public void lookEmails(Session session, int page) {
		Email emails = session.getPlayer().getEmail();
		if(!emails.isValiedPage(page)) {
			session.sendMessage("��תʧ�ܣ�����û����ôҳ�棬������Ҳ�������ҳ��");
			return;
		}
		String dis = emails.displayEmail(page);
		emails.setEmailPage(page);
		session.sendMessage(dis);
	}
	
	/**
	 * �鿴��ǰҳ��ĵ�n���ʼ�
	 * ��������ʼ��ǵ����ʼ������õ��ߣ���ɾ���ʼ�
	 * @param session
	 * @param index �ʼ����
	 */
	public void readEmail(Session session, int index) {
		Email emails = session.getPlayer().getEmail();
		if(emails.getEmailPage() == 0) {
			session.sendMessage("��ǰ���������䣬���Ƚ������䣡");
			return;
		}
		if(!emails.withinPage(index)) {
			session.sendMessage("��Ҫ�鿴���ʼ����ڵ�ǰ�б��У����������");
			return;
		}

		String mesg = emails.readEmail(index);
		session.sendMessage(mesg);
	}
	
	/**
	 * �����ʼ�
	 * @param session
	 * @param targetPlayer  Ŀ�������
	 * @param subjcont ����+�ո�+����
	 */
	public void sendNormalEmail(Session session, String targetPlayer, String subjcont) {
		if(subjcont.equals("") || (subjcont.split(" ").length < 2)) {
			session.sendMessage("�ʼ���������ݲ���Ϊ��");
			return;
		}
		System.out.println("sendNormalEmail--------------" + subjcont);
		if((Context.getWorld().getPlayerEntityByName(targetPlayer) == null) 
				&& (Context.getWorld().getPlayerByName(targetPlayer) == null)) {
			session.sendMessage("����Ҫ���͵���Ҳ����ڣ�");
			return;
		}
		if(session.getPlayer().getName().equals(targetPlayer)) {
			session.sendMessage("���ܷ��ʼ����Լ�");
			return;
		}
		String content = "email" + " " + session.getPlayer().getName() + " " 
		              +  targetPlayer + " " + subjcont;
		System.out.println("sendNormalEmail--------------content: " + content);
		createEmail(targetPlayer, content);
		session.sendMessage("���ͳɹ�");
	}
	
	/**
	 * ���͵����ʼ���һ����ϵͳ��
	 * @param tPName Ŀ�������
	 * @param subjcont ���ݡ���ʽ��gold:30;exp:20;1:10
	 */
	public void sendGoodsEmail(String tPName, String subjcont) {
		String content = 1 + " " + tPName + " [award] " + subjcont; //�Զ��������
		createEmail(tPName, content);
	}
	
	
	//�����ʼ���ֻ��������ҵ����ݿ�
	private void createEmail(String tPName , String content) {
		PlayerEntity tpe = Context.getWorld().getPlayerEntityByName(tPName);
		if(tpe == null) {
			Player player = Context.getWorld().getPlayerByName(tPName);
			tpe = player.getPlayerEntity();
		}
		EmailEntity emiE = new EmailEntity(content, tpe);
		tpe.getEmails().add(emiE);
		Player tPlayer = Context.getOnlinPlayer().getPlayerById(tpe.getId());
		if(tPlayer != null) {
			//Ŀ���������, ��Ҫ���»���
			System.out.println("-------------createEmail---------Ŀ���������" );
			tPlayer.getEmail().addEmail(content);
		}
		System.out.println("-------------createEmail()---------" + tpe.getEmails().toString());
		//�������ݿ⣬ֻ�в����ߵ�Ŀ����Ҳ���Ҫ��������
		if(tPlayer == null) {
			//new UpdateTask(tpe);
			System.out.println("**********************************���и�������--ǰ");
			new PlayerDaoImpl().update(tpe);
			System.out.println("**********************************���и�������--��");
		}
	}
	
	/**
	 * ɾ���ʼ�
	 * @param session
	 * @param index �ʼ����
	 */
	public void delEmail(Session session, int index) {
		Email emails = session.getPlayer().getEmail();
		if(emails.getEmailPage() == 0) {
			session.sendMessage("��ǰ���������䣬���Ƚ������䣡");
			return;
		}
		if(!emails.withinPage(index)) {
			session.sendMessage("��Ҫ�鿴���ʼ����ڵ�ǰ�б��У����������");
			return;
		}
		String content = emails.delEmail(session.getPlayer(), index);
		//session.getPlayer().getEmail().delEmail(index);  //ɾ�������е�
		session.sendMessage("ɾ���ɹ�");
	}
	
	/**
	 * ��֤�Ƿ�������
	 * @param msg
	 * @return
	 */
	private boolean pisDigit(String msg) {
		for(int i = 0; i < msg.length(); i++) {
			if(!Character.isDigit(msg.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * �����ʼ�����
	 * @param args
	 * @return ���صĸ�ʽ������+�ո�+����
	 */
	private String orderContent(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append(args[2] + " ");
		for(int i = 3; i < args.length; i++) {
			sb.append(args[i] + " ");
		}
		System.out.println("------------orderContent " + sb.toString());
		return sb.toString();
	}
}
