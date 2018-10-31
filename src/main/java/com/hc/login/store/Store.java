package com.hc.login.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.config.GoodsConfig;
import com.hc.logic.creature.Player;

@Component
public class Store {
	//ÿҳ��ʾ����Ʒ�ĸ���
	private final int PAGENUM = 3;
	
	/**
	 * ������Ʒ
	 * @param gId ��Ʒid
	 * @return
	 */
	public boolean buyGood(Player player, int gId, int amount) {
		GoodsConfig goodsConfig = Context.getGoodsParse().getGoodsConfigById(gId);
		if(goodsConfig == null) return false;
		int price = goodsConfig.getPrice() * amount;
		if(!player.minusGold(price)) return false;
		player.addGoods(gId, amount);
		return true;
	}
	
	/**
	 * ��ʾ�̵�ĵڼ�ҳ����1��ʼ
	 * @param page ҳ��
	 */
	public String displStore(int page) {
		//��֤�Ƿ��������ҳ, ��order���Լ���֤����
		//if(!isValiedPage(page)) return "";
		List<GoodsConfig> goodPage = aPage(page);
		StringBuilder sb = new StringBuilder();
		sb.append("���̵꡿- - - - - - - - - - - - - - - - - - - - - - - -- - - - - - - -\n");
		sb.append("- - - - - - - - - -�ڡ�" + page + "��ҳ- - - - - - - - - - - -- -  -- - - - -\n");
		for(GoodsConfig gc : goodPage) {
			sb.append(gc.getName() + " " + gc.getDescription() + " " + gc.getPrice() + "���\n");
		}
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -- - - - -");
		return sb.toString();
	}
	
	/**
	 * �ж���������page�Ƿ���Ч
	 * 1����֤�̵��Ƿ�����ô��ҳ��
	 * 2����֤����Ƿ�������ҳ�档��������ҳ��Ҳ����Ч��
	 * 3����֤page�Ƿ���Ч������0��������
	 * @param page
	 * @return
	 */
	public boolean isValiedPage(Player player, int page) {
		if(page < 1) return false;
		int start = PAGENUM * (page-1) + 1;
		int volum = Context.getGoodsParse().getGoodsList().size();
		if(start > volum) return false;	
		int playerPage = player.getPageNumber();
		if(page != playerPage && page != (playerPage + 1) && page != (playerPage - 1)) return false;
		return true;
	}
	
	/**
	 * ��֤��Ʒid�Ƿ���������������̵�ҳ��
	 * @param player
	 * @param gId ��Ʒid
	 * @return
	 */
	public boolean withinPage(Player player, int gId) {
		for(GoodsConfig gc : aPage(player.getPageNumber())) {
			if(gc.getId() == gId) return true;
		}
		return false;
	}
	
	/**
	 * ��õ�nҳ����Ʒ�б�
	 * @param page �ڼ�ҳ
	 * @return
	 */
	private List<GoodsConfig> aPage(int page){
		List<GoodsConfig> goods = Context.getGoodsParse().getGoodsList();
		sortGoods(goods);
		List<GoodsConfig> result = new ArrayList<>();
		int start = PAGENUM * (page-1);
		int stop = PAGENUM + start;
		for(int i = start; i < stop; i++) {
			if(i >= goods.size()) break; //����һҳ
			result.add(goods.get(i));
		}
		return result;
	}
	
	/**
	 * ��������Ʒ��������Ʒ����id����
	 * @param goods
	 */
	private void sortGoods(List<GoodsConfig> goods) {
		Collections.sort(goods, new Comparator<GoodsConfig>() {
			@Override
			public int compare(GoodsConfig g1, GoodsConfig g2) {
				int t1 = g1.getTypeId();
				int t2 = g2.getTypeId();
				if(t1 > t2) return 1;
				else if(t1 < t2) return -1;
				return 0;
			}
		});
		//System.out.println("----------�����---------" + goods.toString());
	}
}
