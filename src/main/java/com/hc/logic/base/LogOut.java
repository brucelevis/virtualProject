package com.hc.logic.base;

import java.util.List;
import java.util.concurrent.Future;

import com.hc.frame.Context;
import com.hc.logic.copys.Copys;
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
		System.out.println("logout --------------------" + player==null);
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
		//�Ӹ�����ɾ�����
		manageCopy(player);
		
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
	 *����ʱ����Ҫ�Ӹ�����ɾ����ҡ�
	 *���һ����Ҷ��ߣ����������
	 * @param player
	 */
	private void manageCopy(Player player) {
		System.out.println("------------manageCopy " + player.getCopys());
		//��ҶϿ�����ʱ����Ҫ���������������߳�
		Copys copy = player.getCopys();
		if(copy == null) return;
		copy.delPlayer(player.getId());
		//ÿ������Ҷ��߶�Ҫ���¸���ʵ��
		CopyEntity cpe = Context.getWorld().getCopyEntityById(copy.getId());
		cpe.setBossindex(copy.getBossIndex());
		new PlayerDaoImpl().update(cpe);
		if(!copy.haveAvailablePlayer()) {
			System.out.println("------------manageCopy ");
			//��ҶϿ�����,���������һ���뿪�����ģ���Ҫ���������������߳�
			Context.getWorld().delCopys(copy.getId(), player.getSponserNmae());
			Context.getWorld().delCopyThread(player);
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
