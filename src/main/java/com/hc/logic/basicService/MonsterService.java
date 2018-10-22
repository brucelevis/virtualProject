package com.hc.logic.basicService;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.MonstConfig;

public class MonsterService {
	
	/**
	 * ��ǰ�������ϸ��Ϣ
	 */
	public void mDescribe(Session session, int mId) {
		MonstConfig mc = Context.getSceneParse().getMonsters().getMonstConfgById(mId);
		StringBuilder sb = new StringBuilder();
		sb.append(mc.getDescription() + "\n");
		sb.append("Ѫ����");
		sb.append(mc.getHp() + "\n");
		sb.append("��������");
		sb.append(mc.getAttack() + "\n");
		sb.append("�Ƿ���ţ�");
		sb.append(mc.isAlive());
		session.sendMessage(sb.toString());
	}

}
