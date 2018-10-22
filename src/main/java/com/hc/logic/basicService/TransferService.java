package com.hc.logic.basicService;
import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Teleport;
import com.hc.logic.creature.Player;


public class TransferService implements Teleport{

	@Override
	public void transfer(Player player, int sId, int tId) {

		Scene target = Context.getWorld().getSceneById(tId);
		Scene source =  Context.getWorld().getSceneById(sId);
		//�����ִ��м������
		target.addPlayer(player);
		//ҲҪ�ڳ�ʼ��ɾ�����
		source.deletePlayer(player);
		
		//������ԭ�����еĹ��﹥��, Ҫ�ڸı�sceneIdǰ
		player.getScene().deleteAttackPlayer(player);

		
		//������ҵ�sceneid�ֶΡ�
		player.setSceneId(tId);
		
		//���³���ʱ�����ܵ��³�����������������Ĺ���(����)
		
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
