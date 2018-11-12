package com.hc.logic.basicService;

import org.springframework.stereotype.Component;

import com.hc.logic.config.MonstConfig;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

@Component
public class AwardService {

	/**
	 * ��ɱ����/boss��ý���
	 * �������飬��ң����������ϵȵ�
	 * @param player
	 * @param monstConfig
	 */
	public void obtainAward(Player player, Monster monst) {
		//���Ӿ���
		System.out.println("---------��ɱ�����ý���--"+ player.getExp() + ", ���" + monst.getExp());
		player.addExp(monst.getExp());
		System.out.println("---------��ɱ�����ý���----��-"+ player.getExp());
		//TODO ��ý�ң������ȵȣ�������
		
		
	}
}
