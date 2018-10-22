package com.hc.logic.base;

import java.util.List;

import com.hc.frame.Context;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;

/**
 * ��ҵǳ�
 * ��Ӧ�������߳�����
 * ����౾����һ��runnable���࣬�ᱻ����TaskProduce�б�����һ������ִ�С�
 * @author hc
 *
 */
public class LogOut implements Runnable{
	private Player p;

	public LogOut(Player player) {
		//��ҵǳ�ʱ��ҲҪ�������б���ɾ��
		this.p = player;
		Context.getOnlinPlayer().deletePlayer(player);
	}
	
	/**
	 * ˢ�����ݿ⣬
	 */
	public void updateDB(Player player) {
		System.out.println("logout --------------------");
		if(player == null)
			return;
		//�������Ƿ��������ң���Ϊ�ڷ���������ʱ���ͻὫ���ݿ��е���Ϣ��������
		//������World��allplayerEntiy�ֶ�
		String name = player.getName();
		
		if(name.equals(null)) 
			return;
		
		//����Ҵ���һ�����ʵ��,�����������ݿ�
		PlayerEntity cPE = player.getPlayerEntity();  
		PlayerEntity pe = Context.getWorld().getPlayerEntityByName(name);
		
		//�����Ʒʵ��
		//GoodsEntity cGE = player.getGoodsEntity();
		
		//���ڻ�����û�У�Ҳ�������ݿ���û�У������һ�����ݵ����ݿ⣬ͬʱҲ�ڻ����л���һ������
		if(pe == null) {
			System.out.println("logout���������ݿ����");			
			//playerEntSave(cPE);
			new PlayerDaoImpl().insert(cPE);
			//new PlayerDaoImpl().insert(cGE);
			Context.getWorld().addPlayerEntity(cPE);  //���뻺��
			//Context.getWorld().addGoodsEntity(cGE);
		}else {
			//�������ڻ����У���ֻҪ�������ݿ����
			System.out.println("logout���������ݿ����");
			//System.out.println(cPE.toString());
			//updatePE(cPE);
			new PlayerDaoImpl().update(cPE);  //��pe�����������
			//new PlayerDaoImpl().update(cGE);
			//Context.getWorld().updCache(cPE);  //���ø��»��棬��Ϊ����player���playerEntity������
		}
		
	}

	public void updatePE(PlayerEntity pp) {
		//Context.getWorld().updatePE(pp);
		new PlayerDaoImpl().update(pp);
	}
	
	
	/**
	 * �����������TaskProducer��ִ��.
	 */
	@Override
	public void run() {
		updateDB(p);
	}
}
