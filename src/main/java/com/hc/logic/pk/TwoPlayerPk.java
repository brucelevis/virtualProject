package com.hc.logic.pk;

import org.springframework.stereotype.Component;

import com.hc.logic.creature.Player;
import com.hc.logic.domain.GoodsEntity;

@Component
public class TwoPlayerPk {

	/**
	 * pk������ʤ����һ����ý��������һ������óͷ�
	 * @param p1  ʤ����
	 * @param p2 ʧ�ܷ�
	 */
	public void geReward(Player p1, Player p2) {
		int gold = goldReword(p1, p2);
		p1.addGold(gold);
		int gId = goodReword(p1, p2);
		swapGood(p1, p2, gId);	
	}
	
	
	private void swapGood(Player p1, Player p2, int gId) {
		p1.addGoods(gId, 1);
		p2.delGoods(gId, 1);
	}
	
	/**
	 * ��Ҫ��������Ʒ
	 * @param p1 ʤ
	 * @param p2 ��
	 * @return ��Ʒid
	 */
	private int goodReword(Player p1, Player p2) {
		int size = p2.getPlayerEntity().getGoods().size();
		int index = (int)(1 + Math.random() * size);
		int i = 1;
		for(GoodsEntity ge : p2.getPlayerEntity().getGoods()) {
			if(i == index) {
				return ge.geteId();
			}
			i++;
		}
		return 1;
	}
	
	/**
	 * ��ҽ���
	 * @param p1 ��ʤ��
	 * @param p2 ʧ�ܷ�
	 * @return
	 */
	private int goldReword(Player p1, Player p2) {
		int levelGold = p1.getLevel() * 50;   //��ʤ�õ��Ľ�Һ͵ȼ��й�
		int difGold = (p1.getLevel() - p2.getLevel()) * p1.getLevel() * 10;  //��ʤ�õ��Ľ�Һ�˫���ȼ����й�
		int gold = levelGold - difGold;
		return gold;
	}
	
	
	
	
}
