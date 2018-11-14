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

	private String tpName;  //������һ��������
	private String spName;  //����������
	//����˫�����׵���Ʒ����������ʽ�� key����Ʒid/gold ; value: ����
	private Map<String, Integer> tpDeal = new HashMap<>();
	private Map<String, Integer> spDeal = new HashMap<>();  //������ϣ�����׵���Ʒ������
	private boolean tpAccep;   //�Ƿ�ͬ����н���
	private boolean spAccep;
	private Lock lock = new ReentrantLock();
	//��Ҫ��������Ʒ
	private List<GoodsEntity> exchange = new ArrayList<>();
	private List<GoodsEntity> aexchange = new ArrayList<>();

	public Deal(String p) {
		this.tpName = p;
	}
	
	public void acceptDeal(Player tPlayer, String sName) {
		lock.lock();
		try {
			if(spName != null || !tPlayer.getName().equals(tpName)) {
				tPlayer.getSession().sendMessage("�Է��Ѿ��ڽ����У����ʧ��");
				return;
			}
			this.spName = sName;
			tPlayer.accDeal(this);
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * ��˫����ͬ�⽻���󣬿�ʼ������Ʒ
	 */
	public void exchangeGoods() {
		System.out.println("-----��ʼ����-----" + tpDeal.toString() +", " + spDeal.toString());
		System.out.println("---����˫��-----" + tpName + ", " + spName);
		lock.lock();
		try {
			Player tPlayer = Context.getOnlinPlayer().getPlayerByName(tpName);
			Player sPlayer = Context.getOnlinPlayer().getPlayerByName(spName);
			//playerDelGoods(tPlayer, tpDeal, exchange);
			//playerAddGoods(tPlayer, spDeal);
			//playerAddGoods(sPlayer, tpDeal, exchange);
			//playerDelGoods(sPlayer, spDeal, exchange);
			playerDelGoods(tPlayer, tpDeal, exchange);
			playerDelGoods(sPlayer, spDeal, aexchange);
			System.out.println("---------ǰ--exchange---" + exchange.size());
			System.out.println("-----------ǰ---" + aexchange.size());
			playerAddGoods(tPlayer, spDeal, aexchange);
			playerAddGoods(sPlayer, tpDeal, exchange);
			System.out.println("----------��--exchange--" + exchange.size());
			System.out.println("--------------" + aexchange.size());
			notHopeDeal(tpName);
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 
	 * @param player ������Ʒ�����
	 * @param goods  ���ٵ���Ʒ
	 * @param changex ������ٵ�����Ʒ�������������棬���򱣳�null
	 */
	private void playerDelGoods(Player player, Map<String, Integer> goods,  List<GoodsEntity> changex) {
		for(Entry<String, Integer> ent : goods.entrySet()) {
			if(!OrderVerifyService.isDigit(ent.getKey())) { //���
				player.minusGold(ent.getValue());
			}else {  //��Ʒ
				//this.exchange = new ArrayList<>();
			    int gid = Integer.parseInt(ent.getKey());
			    List<GoodsEntity> glist = player.delGoods(gid, ent.getValue());
			    for(GoodsEntity ge : glist) {  //������Ʒ(��Һ͹���֮��)
					GoodsEntity nge = Context.getGoodsService().changeGoods(ge);
					changex.add(nge);
				}
			}
		}
	}
	/**
	 * ��ý�������Ʒ
	 * @param player ��õ����
	 * @param goods  ��õ���Ʒ
	 * @param changex  �����õ�����Ʒ�����������棬�������Ϊnull
	 */
	private void playerAddGoods(Player player, Map<String, Integer> goods, List<GoodsEntity> changes) {
		if(changes.size() == 0) {
			player.addGold(goods.get("gold"));
			return;
		}
		for(GoodsEntity ge : changes) {
			ge.setPlayerEntity(player.getPlayerEntity());
			player.addGoods(ge);
		}
		changes = null;
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
	 * ���뽻��״̬�󣬿��Է�����Ʒ
	 * @param pName : ������Ʒ�������
	 * @param args: deal ��Ʒ�� ����
	 */
	public void showGoods(String pName, String[] args) {
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
	 * ��ý��׶Է�������
	 * @param name
	 * @return
	 */
	public String getCounter(String name) {
		if(name.equals(spName)) return tpName;
		return spName;
	}

	/**
	 * ͬ����н���
	 * @param pName ͬ�⽻���������
	 */
	public void hopeDeal(String pName) {
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
	 * ��ͬ�⽻��
	 * @param pName
	 */
	public void notHopeDeal(String pName) {
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
	 * �Ƿ��Ѿ����Կ�ʼ���н���ȷ��
	 * @return
	 */
	public boolean isReadyVerify() {
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
	 * �Ƿ���Կ�ʼ������
	 * @return
	 */
	public boolean isReadyChange() {
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
