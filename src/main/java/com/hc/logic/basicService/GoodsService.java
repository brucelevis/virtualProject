package com.hc.logic.basicService;

import com.hc.frame.Context;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;

public class GoodsService {

	/**
	 * �����Ʒ
	 * @param pe
	 * @param gid ��Ʒid
	 */
	public void addGoods(PlayerEntity pe, int gid) {
		int tID = Context.getGoodsParse().getGoodsConfigById(gid).getTypeId();
		int cont = Context.getGoodsParse().getGoodsConfigById(gid).getContinueT();
		
		GoodsEntity ge = null;
		if(tID != 2 && tID != 3) { //�����Ʒ
			ge = new GoodsEntity(gid, pe);		
		}else {//���װ��
			ge = new Equip(gid, cont, pe);
		}
		pe.getGoods().add(ge);
	}
	
	/**
	 * ɾ����Ʒ
	 * @param pe
	 * @param gid ��Ʒid
	 */
	public void delGoods(PlayerEntity pe, int gid) {
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge.geteId() == gid) {
				if(delGood(ge, pe)) return;
			}
		}
	}
	
	private boolean delGood(GoodsEntity goodsEntity, PlayerEntity playerEntity) {
		if(goodsEntity instanceof Equip) {
			Equip equip = (Equip)goodsEntity;
			if(equip.getState() == 1)
				return false;
		}
		playerEntity.delGoods(goodsEntity);
		return true;
	}
	
	/**
	 * ����װ��
	 * @param gid ��Ʒid
	 */
	public void doEquip(int gid, PlayerEntity pe) {
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge.geteId() == gid) {
				if(ge instanceof Equip) {
					Equip eq = (Equip)ge;
					eq.setState(1);
					return;
				}
			}
		}
	}
	
	/**
	 * ж��װ��
	 * @param gid ��Ʒid
	 * @param pe
	 */
	public void deEquip(int gid, PlayerEntity pe) {
		int tID = Context.getGoodsParse().getGoodsConfigById(gid).getTypeId();
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.geteId() == gid && (eq.getState() == 1)) {
					eq.setState(0);
					return;
				}
			}
		}

	}
	/**
	 * �ж��Ƿ��Ѿ�װ��
	 * @param gid ��Ʒid
	 * @param pe
	 * @return
	 */
	public boolean isEquiped(int gid, PlayerEntity pe) {
		int tID = Context.getGoodsParse().getGoodsConfigById(gid).getTypeId();
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.geteId() == gid && eq.getState() == 1) {  //1��ʾ�Ѵ���
					return true;
				}
			}
		}
		return false;
	}
}
