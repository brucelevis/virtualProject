package com.hc.logic.base;

import com.hc.logic.creature.Player;

public interface Teleport {

	/**
	 * ���д���
	 * @param player
	 * @param sSceneId  Դ����id
	 * @param tSceneId  Ŀ�곡��id
	 */
	public void transfer(Player player, int sSceneId, int tSceneId);
	

	
	/**
	 * ���������Ľ���
	 * @return
	 */
	public String getDescribe();
	
	public String toString();
}