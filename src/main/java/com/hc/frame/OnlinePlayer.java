package com.hc.frame;
import java.util.*;
import com.hc.logic.creature.*;
/**
 * ���г����е��������
 * @author hc
 *
 */
public class OnlinePlayer {

	//�����������
	private List<Player> onlinePlayers = new ArrayList<>();

	
	
	
	
	
	public List<Player> getOnlinePlayers() {
		return onlinePlayers;
	}

	public void addPlayer(Player player) {
		onlinePlayers.add(player);
	}
	
	public void deletePlayer(Player player) {
		onlinePlayers.remove(player);
	}
	
	public Player getPlayerById(int id) {
		for(Player p : onlinePlayers) {
			if(p.getId() == id)
				return p;
		}
		return null;
	}
	
}
