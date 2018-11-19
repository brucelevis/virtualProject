package com.hc.logic.basicService;
import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.base.Teleport;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
import com.hc.logic.dao.impl.PlayerDaoImpl;
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
			if(session.getPlayer().isInPK()) Context.getPkService().giveUp(session);
			//��������ͨ�����д���
			if(!Context.getWorld().getSceneById(sourceId).hasTelepId(targetId)) {
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

	}

	/**
	 * ���ڳ����ͳ���֮��Ĵ���
	 */
	@Override
	public void transfer(Player player, int sId, int tId) {
		if(player.getTeammate().size() != 0) {
			player.getSession().sendMessage("����в��ܴ���");
			return;
		}
		Scene target = Context.getWorld().getSceneById(tId);
		Scene source =  Context.getWorld().getSceneById(sId);		
		//������ԭ�����еĹ��﹥��, Ҫ�ڸı�sceneIdǰ
		source.deleteAttackPlayer(player);
		//��Ŀ�곡���м������
		target.addPlayer(player);
		//ҲҪ��ԭ������ɾ�����
		source.deletePlayer(player);
		
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
	public void copyTransfer(Player player, int cId) {
		if(player.getSceneId() == 0) return; //��ʾ�Ѿ��ڸ����У��Ƕ�������
		int sId = Context.getCopysParse().getCopysConfById(cId).getPlace();
		//��ͨ����
		Scene source =  Context.getWorld().getSceneById(sId);
		
		//source.deleteAttackPlayer(player);
		source.deleteAttackPlayer(player);
		source.deletePlayer(player);
		
		player.setSceneId(0);   //�ı����sceneid
		//CopyEntity copyEntity = new CopyEntity(tId, System.currentTimeMillis(), player.getPlayerEntity(), 0);
		//���˽��븱����������ӽ��룬ֻ�з������ܲ������븱��
		if(player.getSponserNmae() != null && player.getTeammate().size() > 0 || (player.getSponserNmae()==null && player.getTeammate().size() < 1)) {
			CopyEntity copyEntity = Context.getCopyService().createCopyEntity(cId, player.getTeammate(), player);
		    //������ʵ���������ʵ��
			System.out.println("------------------transferservice�еĹ���copyentity, " + (copyEntity==null));
			//player.getPlayerEntity().setCopyEntity(copyEntity);
		}
		
		player.getSession().sendMessage("��ӭ����������" + Context.getCopysParse().getCopysConfById(cId).getName());
		
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
		
		Copys copy = player.getCopys();
		//�����������˳�������ɸ����˳�������Ҫ���ٸ����е����
		copy.delPlayer(player.getId());
		//player.getCopEntity().getPlayers().remove(player.getPlayerEntity());
	
		if(!copy.haveAvailablePlayer()) {
			//������������ˣ�team.size=0,spons=name; ����: team.size=0,spons!=name; team.size>0, spons==nam
			if(player.getTeammate().size() < 1 && player.getSponserNmae() != player.getName()) {
				int copyid = player.getCopEntity().getCopyId();
				System.out.println("�رո����߳�" + player.getId() + ", copyid=" + copyid);
				int spoid = Context.getWorld().getPlayerEntityByName(player.getPlayerEntity().getCopyEntity().getSponsor()).getId();
				System.out.println("�رո����߳�--��" + spoid);
				Context.getWorld().delCopyThread(spoid, copyid);   //ֹͣ�����߳�
			}else {
				//û������ˣ������ٸ���
				Context.getWorld().delCopyThread(player);   //ֹͣ�����߳�
			}
			Context.getWorld().delCopys(player.getCopEntity().getCopyId(), player.getSponserNmae());
			//��������ɸ�����������������������ͳ����������ڷ�����������Ҫ�����ݿ�ɾ������
			//CopyEntity cpe = Context.getWorld().getPlayerEntityByName(player.getSponserNmae()).getCopyEntity();
			CopyEntity cpe = player.getCopEntity();
			player.getPlayerEntity().setCopyEntity(null);
			new PlayerDaoImpl().update(player.getPlayerEntity());
			//������е��ߵ����
		
			new PlayerDaoImpl().delete(cpe);
			System.out.println("�Ѿ������������Ҹ����߳���ֹͣ " + (cpe==null) + ", " );
		}else {
			player.getPlayerEntity().setCopyEntity(null);
			new PlayerDaoImpl().update(player.getPlayerEntity()); //ͬʱ�������ݿ��ֵ
		}
		System.out.println("---------�ĸ���һص���ͨ����----" + player.getName());
		
		player.setSponserNmae(null);  //������״̬
		player.clearTeammate();
		
		//��Ŀ�곡���м������
		target.addPlayer(player);		
		player.setSceneId(tId);
		
		System.out.println("-------------��ӭ�ص�=---");
		System.out.println("-----------" + player.getPlayerEntity().toString());
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
