package com.hc.logic.basicService;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.creature.Monster;

@Component
public class MonsterService {
	
	/**
	 * ��ǰ�������ϸ��Ϣ
	 */
	public void mDescribe(Session session, int mId) {
		//MonstConfig mc = Context.getSceneParse().getMonsters().getMonstConfgById(mId);
		Monster mc = session.getPlayer().getScene().getMonsteById(mId);
		if(mc == null) {
			session.sendMessage("�ù��ﲻ�ڵ�ǰ����");
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(mc.getDescription() + "\n");
		sb.append("Ѫ����");
		sb.append(mc.getHp() + "\n");
		sb.append("��������");
		sb.append(mc.getAttack() + "\n");
		sb.append("�Ƿ���ţ�");
		sb.append(mc.isAlive());
		if(mc.getGold() != 0) {
			//������boss
			sb.append("\n" + "��ɱ��ý�ң�" + mc.getGold());
			sb.append("\n" + "���м���" + mc.bossSkillList());
		}
		
		session.sendMessage(sb.toString());
	}

}
