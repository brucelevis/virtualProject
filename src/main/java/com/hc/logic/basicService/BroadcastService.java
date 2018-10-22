package com.hc.logic.basicService;

import com.hc.frame.Context;
import com.hc.frame.OnlinePlayer;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;

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
		for(Player player : oP.getOnlinePlayers()) {
			if(player.getSceneId() == sceneId) {
				player.getSession().sendMessage(mesg);
			}
		}
	}
}
