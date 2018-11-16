package com.hc.logic.achieve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hc.frame.Context;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.TaskEntity;

/**
 * ��ҵ���������
 * ����: �������ڽ��е�����
 *     �����Ѿ���ɵ���û���ύ������
 * @author hc
 *
 */
public class PlayerTasks {
	
	//��ɱ�Ĺ���.key:����id;value:��ɱ������
	private Map<Integer, Integer> mid2amount = new HashMap<>();
	//��ɵĸ�����key������id��value:��ɵĴ���
	private Map<Integer, Integer> cid2amount = new HashMap<>();


	//���ڽ��е�����
	private List<Task> progressTask = new ArrayList<>();
	//�Ѿ���ɵ�û���ύ������.�ŵ�������id
	private List<Integer> completeTask = new ArrayList<>();
	//����ȡ����������.�ŵ�������id
	private List<Integer> awardedTask = new ArrayList<>();
	
	public PlayerTasks(Player player) {
		//�����ݿ��лָ�����
		System.out.println("------�����ݿ��лָ�����------");
		TaskEntity taskEntity = player.getTaskEntity();		
		if(taskEntity.getProgressTask() != null && !taskEntity.getProgressTask().equals("")) {
			String[] pgs = taskEntity.getProgressTask().split(";");
			Map<Integer, Map<Integer, Integer>> mpgs = new HashMap<>();
			for(int i = 0; i < pgs.length; i++) {
				String[] stask = pgs[i].split(",");
				int tid = Integer.parseInt(stask[0]);
				int id = Integer.parseInt(stask[1]);
				int num = Integer.parseInt(stask[2]);
				if(mpgs.get(new Integer(tid)) == null) {
					Map<Integer, Integer> tem = new HashMap<>();
					tem.put(id, num);
					mpgs.put(tid, tem);
				}else {
					mpgs.get(new Integer(tid)).put(id, num);
				}
			}
			for(Entry<Integer, Map<Integer, Integer>> ent : mpgs.entrySet()) {
				progressTask.add(new Task(ent.getKey(), ent.getValue()));
			}
		}
		
		if(taskEntity.getNotAward() != null && !taskEntity.getNotAward().equals("")) {
			String[] ste = taskEntity.getNotAward().split(",");
			for(int i = 0; i < ste.length; i++) {
				int te = Integer.parseInt(ste[i]);
				completeTask.add(te);
			}
		}
		if(taskEntity.getAwarded() != null && !taskEntity.getAwarded().equals("")) {
			String[] saw = taskEntity.getAwarded().split(",");
			for(int i = 0; i < saw.length; i++) {
				int aw = Integer.parseInt(saw[i]);
				awardedTask.add(aw);
			}	
		}
	}
	public PlayerTasks() {
		
	}
	
	/**
	 * �������
	 * @param tid������id
	 */
	public boolean addTask(int tid) {
		if(hadDoTask(tid)) return false;
		Task task = new Task(tid);
		progressTask.add(task);
		System.out.println("---------�������------" + progressTask.size() + ", "
				 + completeTask.size());
		return true;
	}
	
	private boolean hadDoTask(int tid) {  //��֤���������ǰ�Ƿ�����
		if(completeTask.contains(new Integer(tid)) ||
				awardedTask.contains(new Integer(tid))) {
			return true;
		};
		return false;
	}
	
	/**
	 * ��֤�����Ƿ����
	 * @param tid
	 */
	public void isTaskComplete(Task task) {
		if(task.checkTaskComplete()) {
			completeTask.add(task.getTid());
			progressTask.remove(task);
		}	
	}
	public int isTaskComplete(int tid) {
		for(int id : completeTask) {
			if(id == tid) {
				return id;
			}
		}
		return -1;
	}
	/**
	 * ��֤�����Ƿ���ɣ���ɵ���Ҫ�ӽ���������ɾ��
	 * ���������������
	 * @param tid
	 * @return
	 */
	public void isTaskComplete(List<Task> tasks) {
		List<Task> completed = new ArrayList<>();
		for(Task task : tasks) {
			if(task.checkTaskComplete()) {
				completed.add(task);
			}
		}
		taskComplete(completed);
	}
	private void taskComplete(List<Task> tasks) {
		for(Task task : tasks) {
			completeTask.add(task.getTid());
			progressTask.remove(task);
		}
	}
	
	
	/**
	 * ��¼��һ�ɱ���������
	 * @param mid
	 */
	public void monstRecord(int mid) {
		mid2amount.put(mid, mid2amount.getOrDefault(new Integer(mid), 0) + 1);		
		updateTask(1, mid);  
	}
	/**
	 * ��¼��Ҹ����������
	 * @param cid
	 */
	public void copyRecord(int cid) {
		cid2amount.put(cid, cid2amount.getOrDefault(new Integer(cid), 0) + 1);
		updateTask(3, cid);
	}
	
	/**
	 * �����������
	 * @param taskType����������͡�1����ɱ���2���ɼ���Ʒ��3����ɸ���
	 * @param id
	 */
	private void updateTask(int taskType, int id) {
		System.out.println("--�����������--" + taskType + ", " + id);
		List<Task> updatedtasks = new ArrayList<>();
		for(Task task : progressTask) {
			if(task.isSameTaskType(taskType)) {
				task.addComplete(id);
				updatedtasks.add(task);
			}
		}
		isTaskComplete(updatedtasks);  //��֤�����˽��ȵ������Ƿ����		
	}
	
	/**
	 * npc�������񣬴Ӷ���ý���
	 * @param tid: ����id
	 */
	public boolean getTaskAward(Player player, int tid) {
		int isCom = isTaskComplete(tid);
		if(isCom == -1) return false;
		awardedTask.add(tid);
		completeTask.remove(new Integer(tid));
		getAward(player, tid);  //������
		return true;
	}
	private void getAward(Player player, int tid) {
		Map<Integer, Integer> award = Context.getTaskParse().getTaskConfigByid(tid).getAwardit();
		Context.getAwardService().obtainAward(player, award);
	}
	
	

	public List<Task> getProgressTask() {
		return progressTask;
	}

	
	public List<Integer> getCompleteTask() {
		return completeTask;
	}
	public List<Integer> getAwardedTask() {
		return awardedTask;
	}


	
	
}
