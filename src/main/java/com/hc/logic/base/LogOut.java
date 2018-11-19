package com.hc.logic.base;

import java.util.Map;

import com.hc.frame.Context;
import com.hc.logic.achieve.PlayerAchieves;
import com.hc.logic.achieve.PlayerTasks;
import com.hc.logic.achieve.Task;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.AchieveEntity;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.TaskEntity;

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
		//��������ʵ��
		updateTaskEntity(player);
		updateAchieveEntity(player);
		
		//���ڻ�����û�У�Ҳ�������ݿ���û�У������һ�����ݵ����ݿ⣬ͬʱҲ�ڻ����л���һ������
		if(pe == null) {
			System.out.println("logout���������ݿ����");	
			if(cPE.getProfession() == -1) return;
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
	 * ��������ʵ�壬�������ݿ�
	 * @param player
	 */
	public void updateTaskEntity(Player player) {
		//System.out.println("--------------ʵ��洢------------");
		PlayerTasks pts = player.getPlayerTasks();
		TaskEntity te = player.getTaskEntity();
		StringBuilder sb = new StringBuilder();
		for(Task task : pts.getProgressTask()) {
			int tid = task.getTid();
			for(Map.Entry<Integer, Integer> ent : task.getTaskTarget().getTaskComplete().entrySet()) {
				sb.append(tid +","+ ent.getKey() +","+ ent.getValue() + ";");
			}
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		te.setProgressTask(sb.toString());
		sb = new StringBuilder();
		for(int tid : pts.getCompleteTask()) {
			sb.append(tid + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		te.setNotAward(sb.toString());
		sb = new StringBuilder();
		for(int tid : pts.getAwardedTask()){
			sb.append(tid + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		te.setAwarded(sb.toString());
	}
	/**
	 * ���³ɾ�ʵ��
	 * @param player
	 */
	public void updateAchieveEntity(Player player) {
		PlayerAchieves playerAchieve = player.getPlayerAchieves();
		AchieveEntity achieveEntity = player.getAchieveEntity();
		StringBuilder sb = new StringBuilder();
		for(int id : playerAchieve.getAchieveCompletes()) {
			sb.append(id + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		achieveEntity.setCompleteAchieve(sb.toString());
		
		sb = new StringBuilder();
		for(Map.Entry<Integer, Integer> ent : playerAchieve.getContinueAch().entrySet()) {
			sb.append(ent.getKey() + ":" + ent.getValue() + ";");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		achieveEntity.setProgressAchieve(sb.toString());
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
