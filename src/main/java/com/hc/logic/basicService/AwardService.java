package com.hc.logic.basicService;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
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
	
	/**
	 * ��ý�������ʽ����Ʒid��������
	 *               0����������ʾ���
	 * @param player
	 * @param award
	 */
	public void obtainAward(Player player, Map<Integer, Integer> award) {
		for(Map.Entry<Integer, Integer> ent : award.entrySet()) {
			if(ent.getKey() == 0) {
				player.addGold(ent.getValue());
				player.getSession().sendMessage("��ý�ң�" + ent.getValue() + " ��");
			}else {
				player.addGoods(ent.getKey(), ent.getValue());
				String name = Context.getGoodsParse().getGoodsConfigById(ent.getKey()).getName();
				player.getSession().sendMessage("���" +name+": " + ent.getValue() + " ��");
			}
		}
	}
}
