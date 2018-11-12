package com.hc.logic.skill;

import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

public interface SkillAttack {

	/**
	 * ���ܳ���Ч�����й���
	 * @param player ������
	 */
	void doContiAttack(Player player);   
	/**
	 * ��Ӽ��ܳ���Ч��
	 * @param monster
	 * @param skillId
	 */
	void addContiAttack(Monster monster, int skillId);
	/**
	 * ��Ӽ��ܳ���Ч��
	 * @param player  �����������
	 * @param skillId
	 */
	void addContiAttack(Player player, int skillId);
	
	/**
	 * ��ռ��ܵĳ���Ч��
	 */
	void cleanup();
}
