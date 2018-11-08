package com.hc.logic.basicService;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

@Component
public class SkillService {
	
	
	/**
	 * ���͸��ͻ��ˣ���ǰ������м�����Ϣ
	 * @param session
	 */
	public void getAllSkill(Session session) {
		Player player = session.getPlayer();
		List<Integer> skills = player.getSkills();
		StringBuilder sb = new StringBuilder();
		sb.append("���м��ܣ�- - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
		for(int sId : skills) {
			SkillConfig sConfig = Context.getSkillParse().getSkillConfigById(sId);
			sb.append("��");
			sb.append(sConfig.getName() + ": ");
			sb.append(sConfig.getDescription());
			sb.append("; ��������" + sConfig.getAttack() );
			sb.append("; ��ȴʱ��: " + sConfig.getCd());
			sb.append("; ���ķ�����" + sConfig.getMp());
			sb.append("; ����ʱ�䣺" + sConfig.getContinueT() + "��");
			sb.append("��" + "\n");
		}
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - -" );
		session.sendMessage(sb.toString());
	}
	
	/**
	 * �������
	 * @param session
	 * @param skillId������id��mId������id
	 */
	public void doAttack(Session session, int skillId, int mId) {
		Player player = session.getPlayer();
		
		//��Ҽ�����֤
		if(!skillValid(session, skillId)) return;
		
		//�ж��Ƿ�͹�����ͬһ����
		if(!player.getScene().hasMonst(mId)) {
			session.sendMessage("û���������");
			return;
		}
		
		
		SkillConfig skillConf = Context.getSkillParse().getSkillConfigById(skillId);
		MonstConfig monstConf = Context.getSceneParse().getMonsters().getMonstConfgById(mId);
		Monster monst = player.getScene().getMonsteById(mId);
		
		boolean isalive = monst.canAttack( skillId, player);
		//�����Ѿ�����ʱ�����ܹ���
		//if(!monst.isAlive()) {
		if(!isalive) {
			session.sendMessage("�����Ѿ����������ܹ���");
			return;
		}
				
		//ʹ�ü��ܺ�------
		
		//���¼��ܺ�����
		updateWeapon(player, skillId);
		
		//���������Ҫ�����﹥����
		player.getScene().addAttackPlayer(mId, player);
		
		//������ҹ����󣬹���ʣ��Ѫ������ҵĹ�������Ҫ���ӣ�buff�����ܵȵ�
		//int restHp = monst.getHp() - player.AllAttack(skillId);
		session.sendMessage("����" + monst.getName());
		if(monst.getHp() <= 0) {
			//��������
			//monst.setHp(0);
			//monst.setAlive(false);
			player.getScene().deleteAttackMonst(); //���������󣬾Ͳ��ܹ������
			//��ɱ����/boss�����Ӧ����
			Context.getAwardService().obtainAward(player, monstConf);
			//��Ҫ�㲥����ǰ�������������
			String mesg = monst.getName() + "�����[" + player.getName() + "]��ɱ";
			BroadcastService.broadInScene(session, mesg);
			return;
		}
		//monst.setHp(restHp);
		//���Ź���Ѫ��
		String msg = monst.getName() + "�����[" + player.getName() +"]������ʣ��Ѫ��Ϊ��" + monst.getHp();
		BroadcastService.broadInScene(session, msg);
	}
	
	/**
	 * �������
	 * @param session �����ߵ�session
	 * @param skillId ʹ�õļ��ܵ�id
	 * @param tpName Ŀ�������
	 */
	public void attackPlayer(Session session, int skillId, String tpName) {
		Player player = session.getPlayer();
		Player tPlayer = player.getScene().getPlayerByName(tpName);
		//��֤���ֵ�pk�����Ƿ����Լ�
		if((player.getPkTarget()==null) || (tPlayer.getPkTarget()==null) ||!player.getPkTarget().equals(tpName) || !tPlayer.getPkTarget().equals(player.getName())) {
			//System.out.println(player.getPkTarget() + ", " + tPlayer.getPkTarget());
			session.sendMessage("����pk���󣬲��ܹ���");
			return;
		}
		
		if(!skillValid(session, skillId)) return;
		
		if(!tPlayer.isAlive()) {
			session.sendMessage("��������������ܹ���");
		}
		
		updateWeapon(player, skillId);
		
		//��Ŀ����˺�
		int hurt = player.AllAttack(skillId);
		//����Ŀ�����Ѫ����������ʵ�ʼ��ٵ�Ѫ��
		int reduce = tPlayer.attackPlayerReduce(hurt);  
		//Ŀ�����ʣ��Ѫ��
		int restHp = tPlayer.getHp();
		session.sendMessage("������ҡ�" + tpName + "������Ѫ��" + reduce +" ʣ��Ѫ��:" + restHp);
		
		if(restHp <= 0) {
			//�������
			tPlayer.setHp(0);
			session.sendMessage("��ҡ�" + tpName + "����������");
			//tPlayer.getSession().sendMessage("���ѱ���ҡ�" + player.getName() + "��ɱ����");
			tPlayer.setAlive(false);
			return;
		}
		tPlayer.setHp(restHp);
		tPlayer.getSession().sendMessage("������ҡ�" + player.getName() +"�����У�����Ѫ�� :" + reduce
		                                 + "ʣ��Ѫ��: " + restHp);
	}
	
	/**
	 * ʹ�ü��ܺ󣬸��������;ã����¼���cdʱ��
	 * @param player
	 * @param skillId ����id
	 */
	public void updateWeapon(Player player, int skillId) {
		SkillConfig skillConf = Context.getSkillParse().getSkillConfigById(skillId);
		//�ü��ܶ�Ӧ����������Ʒid
		int wId = Context.getSkillParse().getSkillConfigById(skillId).getWeapon();
		//���ٷ���
		int restMp = player.getMp() - skillConf.getMp();
		player.setMp(restMp);
		//��¼���г���Ч���ļ��ܵ�ʹ��ʱ��
		player.addReduceAtt(skillId);
		
		//���¼���cd
		player.updateCdById(skillId);
		System.out.println("-----=----����cdʱ��---" + player.getCdTimeByid(skillId));
		
		//���������;ö�
		player.minusContT(wId);

	}
	
	/**
	 * ʹ�ü���ǰ����֤��Ҽ����Ƿ����ʹ��
	 * @param session
	 * @param skillId  ����id
	 * @return
	 */
	public boolean skillValid(Session session, int skillId) {
		SkillConfig skillConf = Context.getSkillParse().getSkillConfigById(skillId);
		Player player = session.getPlayer();
        //�ж�����Ƿ���ʹ�ü���
		if(!player.canUseSkill()) {
			session.sendMessage("�ܵ����˵ļ���Ӱ�죬���ڻ�����ʹ�ü���");
			return false;
		}
		//�ж�����Ƿ�ӵ���������,������Ӧ������
		if(!player.hasSkill(skillId)) {
			session.sendMessage("û���������");
			return false;
		}
		
		//�ü��ܶ�Ӧ����������Ʒid
		int wId = Context.getSkillParse().getSkillConfigById(skillId).getWeapon();
		//�жϸü�����Ҫ�������Ƿ����
		if(!player.contEquip(wId)) {
			session.sendMessage("û��װ����Ӧ������������ʹ��");
			return false;
		}
		//�жϸü�������Ӧ���������;ö�
		if(player.restContiT(wId) < 1) {
			session.sendMessage("�������;öȹ��ͣ����������ʹ��");
			return false;
		}
		
		//�жϸü����Ƿ�cd��
		if(!cdOut(player, skillId)) {
			session.sendMessage("����û����ȴ�꣬����ʹ��");
			return false;
		}
		
		//�ж��Ƿ����㹻�ķ���
		if(player.getMp() < skillConf.getMp()) {
			session.sendMessage("��������");
			return false;
		}

		return true;
	}
	
	/**
	 * �����Ƿ���ȴ��
	 * sId������id
	 * @return
	 */
	public boolean cdOut(Player player, int sId) {
	    long pTime = player.getCdTimeByid(sId).getTime();
	    long nTime = new Date().getTime();
	    long diff = nTime - pTime;
	    long di = Context.getSkillParse().getSkillConfigById(sId).getCd() * 1000; //��Ҫ����1000������Ϊ����
	    //System.out.println(pTime + "=--" + nTime);
	    if(di > diff)
	    	return false;
	    return true;
	}
	


	
	
}
