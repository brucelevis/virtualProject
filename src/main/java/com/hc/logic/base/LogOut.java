package com.hc.logic.base;

import java.util.List;
import java.util.concurrent.Future;

import com.hc.frame.Context;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
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
		
		//��ҶϿ�����ʱ����Ҫ���������������߳�
		Context.getWorld().delCopyThread(p);
		
		//����Ҵ���һ�����ʵ��,�����������ݿ�
		PlayerEntity cPE = player.getPlayerEntity();  
		PlayerEntity pe = Context.getWorld().getPlayerEntityByName(name);
		
		//���ڻ�����û�У�Ҳ�������ݿ���û�У������һ�����ݵ����ݿ⣬ͬʱҲ�ڻ����л���һ������
		if(pe == null) {
			System.out.println("logout���������ݿ����");			
			new PlayerDaoImpl().insert(cPE);
			Context.getWorld().addPlayerEntity(cPE);  //���뻺��
		}else {
			//�������ڻ����У���ֻҪ�������ݿ����
			System.out.println("logout���������ݿ����");
			new PlayerDaoImpl().update(cPE);  
			//delCopys(cPE);
		}
		
	}
	
	
	/**
	 * �����������TaskProducer��ִ��.
	 */
	@Override
	public void run() {
		updateDB(p);
	}
}
