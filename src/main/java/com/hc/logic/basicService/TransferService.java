package com.hc.logic.basicService;
import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.base.Teleport;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
import com.hc.logic.domain.CopyEntity;

@Component
public class TransferService implements Teleport{
	
	
	/**
	 * �ֶ����д��͡�Ҫô����ͨ����ֱ�ӵĴ��ͣ�Ҫô��ǿ���˳�����(��������ͨ����)
	 * @param targetId ��ͨĿ�곡��
	 * @param sourceId ��ͨԭ����������Ϊ0
	 * @param session
	 */
	public void allTransfer(int targetId, int sourceId, Session session){
		if(sourceId !=0) {
			//��������ͨ�����д���
			if(!Context.getWorld().getSceneById(targetId).hasTelepId(sourceId)) {
				session.sendMessage("û����������󣬲��ܴ���");
				return;
			}
			transfer(session.getPlayer(), sourceId, targetId);
			return;
		}
		//�Ӹ�����ǿ���˳�
		int copyId = session.getPlayer().getCopEntity().getCopyId();
		if(targetId != Context.getCopysParse().getCopysConfById(copyId).getPlace()) {
			session.sendMessage("û����������󣬲��ܴ���");
			return;
		}
		transferCopy( session.getPlayer(), copyId);
		Player player = session.getPlayer();
		//�������ͳ���ʱ����Ҫɾ���������ݣ������������ݿ�
		player.getPlayerEntity().setNeedDel(true);
		Context.getTaskProducer().addTask(new CopyPersist(player.getPlayerEntity()));

	}

	/**
	 * ���ڳ����ͳ���֮��Ĵ���
	 */
	@Override
	public void transfer(Player player, int sId, int tId) {

		Scene target = Context.getWorld().getSceneById(tId);
		Scene source =  Context.getWorld().getSceneById(sId);
		//��Ŀ�곡���м������
		target.addPlayer(player);
		//ҲҪ��ԭ������ɾ�����
		source.deletePlayer(player);
		
		//������ԭ�����еĹ��﹥��, Ҫ�ڸı�sceneIdǰ
		player.getScene().deleteAttackPlayer(player);

		
		//������ҵ�sceneid�ֶΡ�
		player.setSceneId(tId);
		
		//���³���ʱ�����ܵ��³�����������������Ĺ���(����)
		
		player.getSession().sendMessage("��ӭ����" + Context.getWorld().getSceneById(tId).getName());
		
	}
	
	/**
	 * ��ͨ�����������Ĵ���
	 * @param player
	 * @param sId  ��ͨ����id
	 * @param tId  ����id
	 */
	public void copyTransfer(Player player, int tId) {
		if(player.getSceneId() == 0) return; //��ʾ�Ѿ��ڸ����У��Ƕ�������
		int sId = Context.getCopysParse().getCopysConfById(tId).getPlace();
		//��ͨ����
		Scene source =  Context.getWorld().getSceneById(sId);
		
		source.deleteAttackPlayer(player);
		source.deletePlayer(player);
		
		player.setSceneId(0);   //�ı����sceneid
		CopyEntity copyEntity = new CopyEntity(tId, System.currentTimeMillis(), player.getPlayerEntity(), 0);
		//player.setCopEntity(copyEntity);  
		player.getPlayerEntity().addCopyEntity(copyEntity); //�ı�copyid��ֵ
		
		player.getSession().sendMessage("��ӭ����������" + Context.getCopysParse().getCopysConfById(tId).getName());
		
	}
	
	/**
	 * �Ӹ����д��ͳ���������ԭ����
	 * ����Ҫ���ٸ���
	 * @param player
	 * @param sId  ����id
	 */
	public void transferCopy(Player player, int sId) {
		//��ý��븱��ǰ�ĳ���id
		int tId = Context.getCopysParse().getCopysConfById(sId).getPlace();
		Scene target = Context.getWorld().getSceneById(tId);
		//��Ŀ�곡���м������
		target.addPlayer(player);
		
		player.setSceneId(tId);
		Context.getWorld().delCopys(player.getCopEntity().getCopyId(), player);//���ͳ����ˣ������ٸ���
		Context.getWorld().delCopyThread(player);   //ֹͣ�����߳�

		System.out.println("�Ѿ������������Ҹ����߳���ֹͣ ");
		//player.setCopEntity(null); 
		
		player.getSession().sendMessage("��ӭ�ص���" + Context.getWorld().getSceneById(tId).getName());
	}
	
	@Override
	public String getDescribe() {
		return "";
	}

	
	public String toString(int id1, int id2) {
		StringBuilder sb = new StringBuilder();
		String start = Context.getWorld().getSceneById(id1).getName();
		String end = Context.getWorld().getSceneById(id2).getName();
		sb.append(start);
		sb.append(" -> ");
		sb.append(end);
		return sb.toString();
	}

	
}