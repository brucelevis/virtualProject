package com.hc.logic.skill;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.achieve.Achievement;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.BroadcastService;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

@Component
public class SkillService {
	
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
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			//��������: attackM ����id ����id
			int skillId = Integer.parseInt(args[1]);
			int mid = Integer.parseInt(args[2]);
			attackAMonster( session, skillId, mid);
		}else {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			//Ⱥ�幥��: attackM ����
			int skillId = Integer.parseInt(args[1]);
			attackAllMonster( session,  skillId);
		}
	}
	
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
			sb.append("; ��������" + sConfig.getProtect());
			sb.append("; ���ƣ�" + sConfig.getCure());
			sb.append("; ��ȴʱ��: " + sConfig.getCd());
			sb.append("; ���ķ�����" + sConfig.getMp());
			sb.append("; ����ʱ�䣺" + sConfig.getContinueT() + "��");
			sb.append("��" + "\n");
		}
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - -" );
		session.sendMessage(sb.toString());
	}
	
	/**
	 * ��������
	 * @param session
	 * @param skillId
	 * @param mid
	 */
	public void attackAMonster(Session session, int skillId, int mid) {
		System.out.println("------------���е�������---------");
		Player player = session.getPlayer();		
		//��Ҽ�����֤
		if(!skillValid(session, skillId)) return;
		if(Context.getSkillParse().getSkillConfigById(skillId).getScope() == 0) {
			session.sendMessage("�˼�����ȫ��Χ���������������е���");
			attackAllMonster( session, skillId);
			return;
		}
		//�ж��Ƿ�͹�����ͬһ����
		if(!player.getScene().hasMonst(mid)) {
			session.sendMessage("û���������");
			return;
		}
		Monster monst = player.getScene().getMonsteById(mid);
		int isalive = monst.canAttack(skillId, player);
		//�����Ѿ�����ʱ�����ܹ���
		if(isalive == -1) {
			session.sendMessage("�����Ѿ����������ܹ���");
			return;
		}
		//��¼���г���Ч���ļ��ܵ�ʹ��ʱ��
		player.addContinueAtttib(skillId, monst);			
		//���������Ҫ�����﹥����
		player.getScene().addAttackPlayer(mid, player);			
		doAttack(session, monst, isalive);
		//���¼��ܺ�����
		updateWeapon(player, skillId);

	}
	/**
	 * Ⱥ�幥��
	 * @param session
	 * @param skillId
	 */
	public void attackAllMonster(Session session, int skillId) {
		System.out.println("------------����Ⱥ�幥��---------");
		Player player = session.getPlayer();		
		//��Ҽ�����֤
		if(!skillValid(session, skillId)) return;
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		if(skillConfig.getSummonBoss() != 0) {
			summonBoss(session, skillId);
			return;
		}
		if(skillConfig.getScope() != 0) {
			session.sendMessage("�˼���Ϊ������������ָ��Ҫ�����ĵ���");
			return;
		}
		boolean attacked = false;
		List<Monster> monsters = player.getScene().getMonsters();
		for(Monster monster : monsters) {
			int isalive = monster.canAttack( skillId, player);
			//�����Ѿ�����ʱ�����ܹ���
			//if(!monst.isAlive()) {
			if(isalive == -1) {
				continue;
			}
			if(skillConfig.getAttack() < 1 && skillConfig.getAttack()>0) continue;  //�ͷż���ʱ��ֻ�й�������0�ſ���
			//��¼���г���Ч���Ĺ������ܵ�ʹ��ʱ��
			player.addContinueAtttib(skillId, monster);		
			doAttack(session, monster, isalive);
			//���������Ҫ�����﹥����ֻ�й�������0�Ż��յ����﹥��
			if(monster.getHp() > 0) {
				player.getScene().addAttackPlayer(monster.getMonstId(), player);			
			}
			attacked = true;
		}
		//��¼���г����ָ�Ѫ���ͷ����ļ���ʹ��ʱ��
		player.addContinueRecov(skillId);
		if(!attacked) {
			session.sendMessage("���й��ﶼ���������ܹ���");
			return;
		}
		//���¼��ܺ�����
		updateWeapon(player, skillId);

	}
	
	/**
	 * �ٻ�ʦ�ٻ�boss
	 * �����
	 * @param session
	 * @param skillId
	 */
	public void summonBoss(Session session, int skillId) {
		//�ٻ�ʦҲҪ�����﹥��
	    Player player = session.getPlayer();
	    for(Monster m : player.getScene().getMonsters()) {
			player.getScene().addAttackPlayer(m.getMonstId(), player);	
		}		
		System.out.println("�����ٻ�");
		SkillConfig skConfig = Context.getSkillParse().getSkillConfigById(skillId);
		Monster m = new Monster(skConfig.getSummonBoss());
		SummonBoss summonBoss = new SummonBoss(m, player.getScene().getMonsters(), player);
		summonBoss.exe(1, "summon" + m.getMonstId() + player.getId());
		session.sendMessage("�ٻ��ɹ�");
		//���¼��ܺ�����
		updateWeapon(player, skillId);
	}
	
	/**
	 * �������
	 * @param session
	 * @param skillId������id��mId������id
	 */
	public void doAttack(Session session, Monster monst, int isalive) {
		Player player = session.getPlayer();
		
		if(isalive == 1) {
			player.getScene().deleteAttackMonst(monst);
			//��ɱ����/boss�����Ӧ����
			Context.getAwardService().obtainAward(player, monst);
			//ɱ���������Ҫ��֤�Ƿ���ĳ�ɾ�
			Achievement.getService(player, "KILLM", monst.getMonstId());
			//��Ҫ�㲥����ǰ�������������
			String mesg = monst.getName() + "�����[" + player.getName() + "]��ɱ";
			BroadcastService.broadInScene(session, mesg);
			return;
		}
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
		
		//ʹ�ü��ܺ�
		
		updateWeapon(player, skillId);
		
		//��¼���г���Ч���ļ��ܵ�ʹ��ʱ��
		player.addContinueAttib(skillId, tPlayer);

		
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
		System.out.println("---------updatewapon���¼��ܺ�����");
		SkillConfig skillConf = Context.getSkillParse().getSkillConfigById(skillId);
		//�ü��ܶ�Ӧ����������Ʒid
		int wId = Context.getSkillParse().getSkillConfigById(skillId).getWeapon();
		//���ٷ���
		int restMp = player.getMp() - skillConf.getMp();
		player.setMp(restMp);
		
		//���¼���cd
		player.updateCdById(skillId);
		System.out.println("-----=----����cdʱ��---" + player.getCdTimeByid(skillId));
		
		//���������;ö�. wid==0��ʾ�˼��ܲ���Ҫ����
		if(wId != 0) player.minusContT(wId);

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
		if(wId!=0 &&!player.contEquip(wId)) {
			session.sendMessage("û��װ����Ӧ������������ʹ��");
			return false;
		}
		//�жϸü�������Ӧ���������;ö�
		if(wId!=0 && player.restContiT(wId) < 1) {
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
	    long nTime = System.currentTimeMillis();
	    long diff = nTime - pTime;
	    long di = Context.getSkillParse().getSkillConfigById(sId).getCd() * 1000; //��Ҫ����1000������Ϊ����
	    System.out.println(pTime + "=--" + nTime +", "+ diff+ ", cd=" + di);
	    if(di > diff)
	    	return false;
	    return true;
	}
	


	
	
}
