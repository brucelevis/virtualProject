package com.hc.logic.deal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hc.frame.Context;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.GoodsEntity;

public class Deal {

	private String tpName;  //交易另一方的名字
	private String spName;  //发起者名字
	//交易双方交易的物品和数量。格式， key：物品id/gold ; value: 数量
	private Map<String, Integer> tpDeal = new HashMap<>();
	private Map<String, Integer> spDeal = new HashMap<>();  //发起者希望交易的物品和数量
	private boolean tpAccep;   //是否同意进行交换
	private boolean spAccep;
	
	//需要交换的物品
	private List<GoodsEntity> exchange;

	public Deal(String p) {
		this.tpName = p;
	}
	
	/**
	 * 当双方都同意交换后，开始交换物品
	 */
	public void exchangeGoods() {
		System.out.println("-----开始交换-----" + tpDeal.toString() +", " + spDeal.toString());
		System.out.println("---交易双方-----" + tpName + ", " + spName);
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tpName);
			Player sPlayer = Context.getOnlinPlayer().getPlayerByName(spName);
			playerDelGoods(tPlayer, tpDeal, exchange);
			playerAddGoods(tPlayer, spDeal);
			playerAddGoods(sPlayer, tpDeal, exchange);
			playerDelGoods(sPlayer, spDeal, exchange);
			notHopeDeal(tpName);
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 
	 * @param player 减少物品的玩家
	 * @param goods  减少的物品
	 * @param changex 如果减少的是物品，则放入这个里面，否则保持null
	 */
	private void playerDelGoods(Player player, Map<String, Integer> goods,  List<GoodsEntity> changex) {
		for(Entry<String, Integer> ent : goods.entrySet()) {
			if(!OrderVerifyService.isDigit(ent.getKey())) { //金币
				player.minusGold(ent.getValue());
			}else {  //物品
			    int gid = Integer.parseInt(ent.getKey());
			    this.exchange = new ArrayList<>(player.delGoods(gid, ent.getValue()));
			}
		}
		if(exchange != null) System.out.println("-----------exchange.tostrp----" + exchange.toString());
	}
	/**
	 * 获得交换的物品
	 * @param player 获得的玩家
	 * @param goods  获得的物品
	 * @param changex  如果获得的是物品，则再这里面，否则这个为null
	 */
	private void playerAddGoods(Player player, Map<String, Integer> goods, List<GoodsEntity> exchanges) {
		if(exchange == null) {
			player.addGold(goods.get("gold"));
			return;
		}
		for(GoodsEntity ge : exchanges) {
			player.addGoods(ge);
		}
		exchange = null;
	}
	private void playerAddGoods(Player player, Map<String, Integer> goods) {
		for(Entry<String, Integer> ent : goods.entrySet()) {
			if(ent.getKey().equals("gold")){
				player.addGold(ent.getValue());
			}else {
				player.addGoods(Integer.parseInt(ent.getKey()), ent.getValue());
			}
		}
	}
	
	/**
	 * 进入交易状态后，可以放置物品
	 * @param pName : 放置物品的玩家名
	 * @param args: deal 物品名 数量
	 */
	public void showGoods(String pName, String[] args) {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			if(pName.equals(tpName)) {
				tpDeal.put(args[1], Integer.parseInt(args[2]));
			}else {
				spDeal.put(args[1], Integer.parseInt(args[2]));
			}
		}finally {
			lock.unlock();
		}
	}
	
	
	
	public String getTpName() {
		return tpName;
	}
	public String getSpName() {
		return spName;
	}
	public void setSpName(String spName) {
		this.spName = spName;
	}
	/**
	 * 获得交易对方的名字
	 * @param name
	 * @return
	 */
	public String getCounter(String name) {
		if(name.equals(spName)) return tpName;
		return spName;
	}

	/**
	 * 同意进行交换
	 * @param pName 同意交换的玩家名
	 */
	public void hopeDeal(String pName) {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			if(pName.equals(spName)) {
				this.spAccep = true;
			}else if(pName.equals(tpName)) {
				this.tpAccep = true;
			}
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 不同意交换
	 * @param pName
	 */
	public void notHopeDeal(String pName) {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			tpAccep = false;
			spAccep = false;
			tpDeal.clear();
			spDeal.clear();
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 是否已经可以开始进行交换确认
	 * @return
	 */
	public boolean isReadyVerify() {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			if(tpDeal.size() == 0 || spDeal.size() == 0) {
				return false;
			}
			return true;
		}finally {
			lock.unlock();
		}
	}
	/**
	 * 是否可以开始交换了
	 * @return
	 */
	public boolean isReadyChange() {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			if(!tpAccep || !spAccep) {
				return false;
			}
			return true;
		}finally {
			lock.unlock();
		}
	}

	
}
