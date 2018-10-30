package com.hc.logic.basicService;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
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
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
		session.sendMessage(sb.toString());
	}
	
	/**
	 * �������
	 * @param session
	 * @param skillId������id��mId������id
	 */
	public void doAttack(Session session, int skillId, int mId) {
		Player player = session.getPlayer();
		
		//�ж�����Ƿ�ӵ���������,������Ӧ������
		if(!player.hasSkill(skillId)) {
			session.sendMessage("û���������");
			return;
		}
		
		//�ü��ܶ�Ӧ����������Ʒid
		int wId = Context.getSkillParse().getSkillConfigById(skillId).getWeapon();
		//�жϸü�����Ҫ�������Ƿ����
		if(!player.contEquip(wId)) {
			session.sendMessage("û��װ����Ӧ������������ʹ��");
			return;
		}
		//�жϸü�������Ӧ���������;ö�
		if(player.restContiT(wId) < 1) {
			session.sendMessage("�������;öȹ��ͣ����������ʹ��");
			return;
		}
		
		//�жϸü����Ƿ�cd��
		if(!cdOut(player, skillId)) {
			session.sendMessage("����û����ȴ�꣬����ʹ��");
			return;
		}
		
		//�ж��Ƿ�͹�����ͬһ����
		if(!player.getScene().hasMonst(mId)) {
			session.sendMessage("û���������");
			return;
		}
		
		
		SkillConfig skillConf = Context.getSkillParse().getSkillConfigById(skillId);
		MonstConfig monstConf = Context.getSceneParse().getMonsters().getMonstConfgById(mId);
		
		//�����Ѿ�����ʱ�����ܹ���
		if(!monstConf.isAlive()) {
			session.sendMessage("�����Ѿ����������ܹ���");
			return;
		}
		
		//�ж��Ƿ����㹻�ķ���
		if(player.getMp() < skillConf.getMp()) {
			session.sendMessage("��������");
			return;
		}
		
		//ʹ�ü��ܺ�------
		
		//���ٷ���
		int restMp = player.getMp() - skillConf.getMp();
		player.setMp(restMp);
		//��¼���г���Ч���ļ��ܵ�ʹ��ʱ��
		player.addReduceAtt(skillId);
		
		//���¼���cd
		player.updateCdById(skillId);
		//���������Ҫ�����﹥����
		player.getScene().addAttackPlayer(mId, player);
		
		//���������;ö�
		player.minusContT(wId);
		
		//������ҹ����󣬹���ʣ��Ѫ������ҵĹ�������Ҫ���ӣ�buff�����ܵȵ�
		int restHp = monstConf.getHp() - player.AllAttack(skillId);
		session.sendMessage("����" + monstConf.getName());
		if(restHp < 0) {
			//��������
			monstConf.setHp(0);
			monstConf.setAlive(false);
			player.getScene().deleteAttackMonst(mId); //���������󣬾Ͳ��ܹ������
			//��ɱ����/boss�����Ӧ����
			Context.getAwardService().obtainAward(player, monstConf);
			//��Ҫ�㲥����ǰ�������������
			String mesg = monstConf.getName() + "����";
			BroadcastService.broadInScene(session, mesg);
			return;
		}
		monstConf.setHp(restHp);
		//���Ź���Ѫ��
		session.sendMessage(monstConf.getName() + "��Ѫ��Ϊ��" + monstConf.getHp());
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
