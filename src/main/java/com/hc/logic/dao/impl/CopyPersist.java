package com.hc.logic.dao.impl;

import com.hc.logic.creature.Player;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;

public class CopyPersist implements Runnable{

	Player player;
	public CopyPersist(Player player) {
		this.player = player;
	}
	
	/**
	 * ɾ���������е�����
	 * @param playerEntity
	 */
	public void delCopys() {
		if(!player.getPlayerEntity().isNeedDel()) return;
		CopyEntity copyEntity = player.getCopEntity();
		player.getPlayerEntity().setCopyEntity(null);
		if(player.getTeammate().size() > 0) {
			//�������ʱ��ֻ�з�������Ҫɾ���������ݿ�
			new PlayerDaoImpl().delete(copyEntity);
		}
		
	}

	/**
	 * �����������TaskProducer��ִ��.
	 */
	@Override
	public void run() {
		delCopys();
	}

}
