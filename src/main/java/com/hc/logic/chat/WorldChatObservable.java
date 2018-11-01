package com.hc.logic.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.creature.Player;

@Component
public class WorldChatObservable {

	//���н�������Ƶ�������
	private List<Player> players = new ArrayList<>();
	//���µ���Ϣ
	private String msg;
	
	synchronized public void registerPlayer(Player player) {
		players.add(player);
	}
	
	synchronized public void removePlayer(Player player) {
		players.remove(player);
	}
	
	synchronized public void notifyPlayers() {
		List<Player> notOnLine = new ArrayList<>();
		for(Player pp : players) {
			if(Context.getOnlinPlayer().getPlayerById(pp.getId()) == null)
				notOnLine.add(pp);
			pp.getSession().sendMessage(msg);
		}
	}
	
	synchronized public void delePlayer(List<Player> pls) {
		for(Player p : pls) {
			removePlayer(p);
		}
	}
	
	synchronized public void setMsg(String msg) {
		this.msg = msg;
		notifyPlayers();
	}
	
	public boolean containPlayer(Player p) {
		return players.contains(p);
	}
	
}
