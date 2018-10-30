package com.hc.logic.dao.impl;

import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;

public class CopyPersist implements Runnable{

	PlayerEntity playerEntity;
	public CopyPersist(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	
	/**
	 * ɾ���������е�����
	 * @param playerEntity
	 */
	public void delCopys(PlayerEntity playerEntity) {
		if(!playerEntity.isNeedDel()) return;
		CopyEntity copyEntity = playerEntity.getCopEntity();
		playerEntity.setCopEntity(null);
		new PlayerDaoImpl().delete(copyEntity);
	}

	/**
	 * �����������TaskProducer��ִ��.
	 */
	@Override
	public void run() {
		delCopys(playerEntity);
	}

}
