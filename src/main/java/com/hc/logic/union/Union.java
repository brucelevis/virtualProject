package com.hc.logic.union;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.BagService;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.UnionEntity;

/**
 * ����
 * @author hc
 *
 */
public class Union {

	private Lock lock = new ReentrantLock();
	private UnionEntity unionEntity;
	private BagService bagService;
	//������빫��������
	private List<String> candidate = new ArrayList<>();
	//�������Ծ�����
	private Set<Player> activePlayers = new HashSet<>();
	
	public Union(UnionEntity ue) {
		this.unionEntity = ue;
		int size = Context.getUnionParse().getUCByid(unionEntity.getGrade()).getWarehouse();
		bagService = new BagService(initUnionWarehouse(), size);
	}
	
	private Map<Integer, Integer> initUnionWarehouse(){
		Map<Integer, Integer> map = new HashMap<>();
		for(GoodsEntity ge : unionEntity.getGoods()) {
			map.put(ge.geteId(), map.getOrDefault(ge.geteId(), 0) + 1);
		}
		return map;
	}
	
	/**
	 * ��ɢ����
	 * @param uname
	 * @param pname
	 * @return
	 */
	public boolean dissolveUnion(String uname, String pname) {
		lock.lock();
		try {
			if(!unionEntity.getOriginator().equals(pname)) return false;
			Context.getWorld().delUnionEntity(uname);
			new PlayerDaoImpl().delete(unionEntity);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * ������빤��
	 * @param uname
	 * @return
	 */
	public boolean enterUion(String uname, Player player) {
		lock.lock();
		try {
			if(unionEntity == null) {
				return false;
			}
			if(unionEntity.getPnum() == Context.getUnionParse().getUCByid(unionEntity.getGrade()).getNum()) {
				return false;
			}
			//unionEntity.getCandidate().add(player.getPlayerEntity());
			candidate.add(player.getName());
			addUnionExp(2);
			updateUnion();
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * �˳�����
	 * @param player
	 * @return
	 */
	public boolean quitUnion(Player player) {
		lock.lock();
		try{
			if(player.getName().equals(unionEntity.getOriginator())) {
				changeOriginate();
			}
			player.delUnion();
			int num = unionEntity.getPnum() - 1;
			if(num == 0) return false;
			if(player.getName().equals(unionEntity.getOriginator())) return false;
			unionEntity.setPnum(num);
			this.activePlayers.add(player);
			updateUnion();
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * ͬ����빤��
	 * @param player  ͬ����
	 * @param pname   �����ߵ�����
	 * @return
	 */
	public boolean agreeEnter(Player player, String pname) {
		lock.lock();
		try {
			PlayerEntity tpe = Context.getWorld().getPlayerEntityByName(pname);
			System.out.println("----������빤��-1-" + (tpe==null));
			if(tpe == null) return false;
			System.out.println("----������빤��-2-" + (tpe.getUnionName() !=null));
			if(tpe.getUnionName() != null) return false;
			if(unionEntity.getPnum() >= Context.getUnionParse().getUCByid(unionEntity.getGrade()).getNum()) {
				System.out.println("----������빤��-3-" + unionEntity.getPnum() +", "
						+ "--" + Context.getUnionParse().getUCByid(unionEntity.getGrade()).getNum() );
				return false;
			}
			if(!candidate.contains(pname))  return false;
			tpe.setUnionName(unionEntity.getName());
			tpe.setUnionTitle(1);
			unionEntity.setPnum(unionEntity.getPnum() + 1);
			//unionEntity.delCandidate(pname);
			this.activePlayers.add(player);
			addUnionExp(3);
			updateUnion();
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * �ܾ�ĳ��Ҽ��빤��
	 * @param player
	 * @param pname
	 * @return
	 */
	public boolean rejectEnter(Player player, String pname) {
		lock.lock();
		try {
			if(!candidate.contains(pname)) return false;
			candidate.remove(pname);
			this.activePlayers.add(player);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * ְλ����
	 * @param player
	 * @param pname
	 * @return
	 */
	public boolean titleUp(Player player, String pname, int d) {
		lock.lock();
		try {
			PlayerEntity pe = Context.getWorld().getPlayerEntityByName(pname);
			if(pe == null || pe.getUnionName() == null) return false;
			if(pe.getUnionTitle() >= player.getUnionTitle()) return false;
			if(pe.getUnionTitle() == 1 && d == -1) return false; 
			pe.setUnionTitle(pe.getUnionTitle() + d);
			addUnionExp(5);
			updateUnion();
			this.activePlayers.add(player);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * �����Ա������Ʒ�ͽ��
	 * @param player
	 * @param gname : ��Ʒ����gold
	 * @param amount �� ����������
	 * @return
	 */
	public boolean donateGoods(Player player, String gname, int amount) {
		lock.lock();
		try {
			if(gname.equals("gold")) {
				if(!player.minusGold(amount)) return false;
				unionEntity.setGold(unionEntity.getGold() + amount);
				return true;
			}
			int gid = Integer.parseInt(gname);	
			System.out.println("------����----1--" + player.hasEnoughGoods(gid, amount));
			if(!player.hasEnoughGoods(gid, amount)) return false; //��ʾ���û����ô����Ʒ
			Map<Integer, Integer> map = new HashMap<>();
			map.put(gid, amount);
			boolean inserted = bagService.insertBag(map); 
			System.out.println("------����---2---" + inserted);
			if(!inserted) return false; //��ʾ�ֿ�װ����
			
			List<GoodsEntity> glist = player.delGoods(gid, amount);
			for(GoodsEntity ge : glist) {  //������Ʒ(��Һ͹���֮��)
				GoodsEntity nge = Context.getGoodsService().changeGoods(ge);
				nge.setUnionEntity(unionEntity);
				unionEntity.getGoods().add(nge);
			}
			addUnionExp(10);
			updateUnion();
			this.activePlayers.add(player);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * �ӹ����л����Ʒ
	 * Ĭ�ϣ���ȡ���ǽ��ʱ���Զ� * 10
	 * @param player
	 * @param gname
	 * @param amount
	 * @return
	 */
	public boolean obtainGoods(Player player, String gname, int amount) {
		lock.lock();
		try {
			if(gname.equals("gold")) {
				int am = amount * 10;
				if(unionEntity.getGold() < am) return false;
				unionEntity.setGold(unionEntity.getGold() - am);
				player.addGold(am);
				return true;
			}
			int gid = Integer.parseInt(gname);	
			if(goodsNum(gid) < amount) return false;  //��ʾ�ֿ���û����ô����Ʒ
			
			Map<Integer, Integer> map = new HashMap<>();
			map.put(gid, amount);
			//System.out.println("addGoods " + map.size() + "to " + map.toString());
			boolean inserted = bagService.insertBag(map); //��ʾ		
			if(!inserted) return false;     //��ʾ��ұ�����û����ô������
			
			for(int i = 0; i < amount; i++) {
				GoodsEntity nge = Context.getGoodsService().changeGoods(unionEntity.delGoods(gid));
				nge.setPlayerEntity(player.getPlayerEntity());
				player.addGoods(nge);
			}
			updateUnion();
			this.activePlayers.add(player);
			return true;
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * ����״̬
	 * @param session
	 */
	public void unionState(Session session) {
		this.activePlayers.add(session.getPlayer());
		StringBuilder sb = new StringBuilder();
		sb.append("��ӭ�������᣺\n");
		sb.append("��ǰ��Ծ�ĳ�Ա��: \n");
		lock.lock();
		try {
			for(Player pp : activePlayers) {
				String tit = Context.getTitlParse().getTCByid(pp.getUnionTitle()).getName();
				sb.append("    " + pp.getName() + " " + tit + "\n");
			}
			sb.append("���ᵱǰ�ȼ���" + Context.getUnionParse().getUCByid(unionEntity.getGrade()).getName() + "\n");
			sb.append("����ֿ��е���Ʒ�У�\n");
			sb.append("���: " + unionEntity.getGold() + "\n");
			sb.append(bagService.bagGoodsdis());
			session.sendMessage(sb.toString());
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * ���᳤�˳�����ʱ
	 * ����ָ����ߵȼ��ĳ�ԱΪ�᳤
	 * �����û�г�Ա�����ɢ����
	 */
	private void changeOriginate() {
		Player player = null;
		int g = 0;
		for(Player pp : activePlayers) {
			if(pp.getUnionTitle() > g) {
				player = pp;
				g = pp.getUnionTitle();
			}
		}
		if(player != null) {
			player.getPlayerEntity().setUnionTitle(4);
			unionEntity.setOriginator(player.getName());
		}else {
			dissolveUnion(unionEntity.getName(), unionEntity.getOriginator());
		}
	}
	 
	
	public int goodsNum(int gid) {
		int si = 0;
		for(GoodsEntity ge : unionEntity.getGoods()) {
			if(ge.geteId() == gid) {
				si += 1;
			}
		}
		return si;
	}
	
	//���и���
	private void updateUnion() {
		System.out.println("union���и���");
		new PlayerDaoImpl().update(unionEntity);
	}

	public List<String> getCandidate() {
		return candidate;
	}

	/**
	 * ���ӹ��ᾭ�������
	 * @param am
	 */
	private void addUnionExp(int am) {
		int allexp = unionEntity.getExp();
		allexp += am;
		if(allexp >= Context.getUnionParse().getUCByid(unionEntity.getGrade()).getExp()) {
			int gradeu = unionEntity.getGrade() + 1;
			if(gradeu > 4) gradeu = 4;
			allexp -= Context.getUnionParse().getUCByid(unionEntity.getGrade()).getExp();
			unionEntity.setGrade(gradeu);
		}
		unionEntity.setExp(allexp);
	}

	public UnionEntity getUnionEntity() {
		return unionEntity;
	}

	public Set<Player> getActivePlayers() {
		return activePlayers;
	}

	
	
}
