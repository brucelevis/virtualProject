package com.hc.logic.achieve;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

@Component
public class TaskService {

	/**
	 * �������
	 * @param session
	 * @param args
	 */
	public void desOrder(Session session, String[] args) {
		if(args.length != 2) {
			session.sendMessage("�����������");
			return;
		}
		if(args[1].equals("p")) {
			inProgressTasks(session);
		}
		if(args[1].equals("n")) {
			allProgressTask(session);
		}
	}
	
	/**
	 * չʾ����δ��������״̬
	 * @param session, task p
	 */
	public void inProgressTasks(Session session) {
		System.out.println("-------��������-------");
		Player player = session.getPlayer();
		List<Task> progressTask = new ArrayList<>(player.getPlayerTasks().getProgressTask());
		StringBuilder sb = new StringBuilder();
		sb.append("�������ڽ��е����� \n");
		for(Task task: progressTask) {
			sb.append(task.taskProgessDesc());
		}
		session.sendMessage(sb.toString());
	}
	
	/**
	 * �������������������
	 * @param session task n
	 */
	public void allProgressTask(Session session) {
		Player player = session.getPlayer();
		List<Task> progressTask = new ArrayList<>(player.getPlayerTasks().getProgressTask());
		List<Integer> completeTask = new ArrayList<>(player.getPlayerTasks().getCompleteTask());
		StringBuilder sb = new StringBuilder();
		sb.append("�������ڽ��е����� \n");
		for(Task task: progressTask) {
			sb.append( " " + task.getName() + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\n��������ɵ����� \n");
		for(int tid : completeTask) {
			String name = Context.getTaskParse().getTaskConfigByid(tid).getName();
			sb.append(" " + name + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		session.sendMessage(sb.toString());;
	}
	
}
