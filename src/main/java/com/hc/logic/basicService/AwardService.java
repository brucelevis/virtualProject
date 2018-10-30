package com.hc.logic.basicService;

import org.springframework.stereotype.Component;

import com.hc.logic.config.MonstConfig;
import com.hc.logic.creature.Player;

@Component
public class AwardService {

	/**
	 * ��ɱ����/boss��ý���
	 * �������飬��ң����������ϵȵ�
	 * @param player
	 * @param monstConfig
	 */
	public void obtainAward(Player player, MonstConfig monstConfig) {
		//���Ӿ���
		player.addExp(monstConfig.getExp());
		//TODO ��ý�ң������ȵȣ�������
		
		
	}
}
