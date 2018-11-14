package com.hc.logic.basicService;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.UnionEntity;

@Component
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
			ge = new GoodsEntity(gid, pe, null);		
		}else {//���װ��
			ge = new Equip(gid, cont, pe, null);
		}
		pe.getGoods().add(ge);
	}
	
	/**
	 * ɾ����Ʒ
	 * @param pe
	 * @param gid ��Ʒid
	 */
	public GoodsEntity delGoods(PlayerEntity pe, int gid) {
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge.geteId() == gid) {
				if(delGood(ge, pe)) return ge;
			}
		}
		return null;
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
	
	/**
	 * ���������Ѵ��ŵ�װ��
	 * @param pe
	 * @return
	 */
	public String allEquips(PlayerEntity pe) {
		StringBuilder sb = new StringBuilder();
		sb.append("���ϵ�װ����- - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge instanceof Equip) {
				Equip equip = (Equip)ge;
				if(equip.getState() != 1) continue;
				int gid = equip.geteId();
				String name = Context.getGoodsParse().getGoodsConfigById(gid).getName();
				int dua = equip.getDuraion();
				sb.append(name + " ʣ���;öȣ�" + dua + "\n");
			}
		}
		sb.append("- - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
		return sb.toString();
	}
	
	/**
	 * �ж��Ƿ��������Ʒ�������Ƿ�
	 * @param gName: ��Ʒid��������"gold"
	 * @param amount
	 * @return
	 */
	public boolean goodsEnough(PlayerEntity pe, String gName, int amount) {
		if(!OrderVerifyService.isDigit(gName)) {
			return pe.getGold() >= amount;
		}
		int gid = Context.getGoodsParse().getGoodsConfigById(Integer.parseInt(gName)).getId();
		int numb = 0;
		for(GoodsEntity ge : pe.getGoods()) {
			if(ge.geteId() == gid)
				numb++;
		}
		return numb >= amount;
	}
	
	/**
	 * ������Ʒ
	 * @param ge
	 */
	public GoodsEntity changeGoods(GoodsEntity ge) {
		GoodsEntity res = null;
		if(ge instanceof Equip){
			Equip eq = (Equip)ge;
			res = new Equip(eq.geteId(), eq.getDuraion(), null, null);
			return res;
		}
		res = new GoodsEntity(ge.geteId(), null, null);
		return res;
	}
	
	
	
}
