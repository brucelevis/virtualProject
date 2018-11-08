package com.hc.logic.basicService;

import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.OnlinePlayer;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.PlayerEntity;

/**
 * �㲥
 * @author hc
 *
 */
public class BroadcastService {

	/**
	 * �㲥����ǰ�����������������
	 * @param session 
	 * @param mesg ��Ҫ�㲥����Ϣ
	 */
	public static void broadInScene(Session session, String mesg) {
		OnlinePlayer oP = Context.getOnlinPlayer();
		int sceneId = session.getPlayer().getSceneId();
		if(sceneId == 0) {
			//�ڸ�����
			System.out.println("--------�ڸ����й㲥����Ѫ��----" + sceneId + ", " );
			//List<PlayerEntity> teammate = session.getPlayer().getPlayerEntity().getCopyEntity().getPlayers(); 
			int spoid = Context.getWorld().getPlayerEntityByName(session.getPlayer().getCopEntity().getSponsor()).getId();
			Copys copys = Context.getWorld().getCopysByAPlayer(session.getPlayer().getCopEntity().getCopyId(), spoid);
			List<Player> teammate = copys.getPlayers();
			//List<Player> teammate = session.getPlayer().getCopys().getPlayers();
			System.out.println("--------�ڸ����й㲥����Ѫ��----" + sceneId + ", " + teammate.toString());
			for(Player pe : teammate) {
				Context.getOnlinPlayer().getPlayerById(pe.getId()).getSession().sendMessage(mesg);
			}		
			return;
		}
		//��ͨ������
		for(Player player : oP.getOnlinePlayers()) {
			if(player.getSceneId() == sceneId) {
				player.getSession().sendMessage(mesg);
			}
		}
	}
	
	/**
	 * ����ҷ�����Ϣ
	 * @param player
	 * @param mesg
	 */
	public static void broadToPlayer(List<Player> players, String mesg) {
		for(Player p : players) {
			p.getSession().sendMessage(mesg);
		}
	}
	
	
}
