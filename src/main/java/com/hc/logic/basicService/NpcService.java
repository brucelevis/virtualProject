package com.hc.logic.basicService;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.NpcConfig;

@Component
public class NpcService {
	
	/**
	 * npc��player�Ƿ���ͬһ����
	 * ���� id��npc��id
	 */
	public boolean isOnScene(Session session, int id) {
		List<Integer> nSId = Context.getSceneParse().getSceneById(session.getPlayer().getSceneId()).getNpcs();
		for(int ii : nSId) {
			if(ii == id)
				return true;
		}
		return false;
	}

	/**
	 * �����Ľ��ܵ�ǰnpc
	 * @param id npc��id
	 */
	public void introduce(Session session, int id) {
		NpcConfig npcC = Context.getSceneParse().getNpcs().getNpcConfigById(id);
		session.sendMessage(npcC.getDescription());
	}
	
	/**
	 * ��ǰnpc������
	 * @param session
	 * @param id npc��id
	 */
	public void task(Session session, int id) {
		NpcConfig npcC = Context.getSceneParse().getNpcs().getNpcConfigById(id);
		session.sendMessage(npcC.getTask());
	}
}
