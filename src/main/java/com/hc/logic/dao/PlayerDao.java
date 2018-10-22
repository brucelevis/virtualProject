package com.hc.logic.dao;

import java.util.List;

import com.hc.logic.domain.PlayerEntity;

/**
 * ��������÷���
 * @author hc
 *
 */
public interface PlayerDao {

	public void insert(PlayerEntity p);
	public void delete(PlayerEntity p);
	public void update(PlayerEntity p);
	public List<PlayerEntity> find();
	
}
