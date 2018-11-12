package com.hc.logic.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hc.frame.Context;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

public class SkillAttackPlayer implements SkillAttack{

	//��Ե�����ҵ��г���Ч���ļ��ܡ�key����ң�value��( key:����id, ʱ���յ� )��
	//���Զ������г�����Ч���ļ��ܣ�Ҳ�������������ֻ����Ҫÿ����Ҷ�����һЩ
	private Map<Player, Map<Integer, Long>> attPlayer = new HashMap<>();


	public void addContiAttack(Player player, int skillId) {
		int conti = Context.getSkillParse().getSkillConfigById(skillId).getContinueT();
		long terminate = conti * 1000;
		if(!attPlayer.containsKey(player)) attPlayer.put(player, new HashMap<Integer, Long>());
		if(!attPlayer.get(player).containsKey(new Integer(skillId))) {
			attPlayer.get(player).put(skillId, System.currentTimeMillis()+ terminate);
		}else {
			//��һ�����ܵĳ���ʱ�仹û�н�����ֱ���ӳ�����ʱ��
			long term = attPlayer.get(player).get(new Integer(skillId)) + terminate;
			attPlayer.get(player).put(skillId, term);
		}
	}

	
	/**
	 * ���г�����Ч���ļ��ܸ���Ҵ���������Ѫ
	 * ��ɾ���Ѿ������˵ļ��ܳ���Ч��
	 */
	@Override
	public void doContiAttack(Player player) {
		if(attPlayer.size() == 0) return;
		Map<Player, Integer> player2skiId = new HashMap<>();
		List<Player> diedPlayer = new ArrayList<>();
		int sumAttack = 0;
		for(Entry<Player, Map<Integer, Long>> enti : attPlayer.entrySet()) {
			Player p = enti.getKey();
			for(Entry<Integer, Long> ent : enti.getValue().entrySet()) {
				int skillId = ent.getKey();
				long terminal = ent.getValue();
				if(terminal < System.currentTimeMillis()) {
					player2skiId.put(p, skillId);  //��¼�Ѿ������˵ļ���Ч��
					continue;
				}
				int attack = Context.getSkillParse().getSkillConfigById(skillId).getAttack();
				sumAttack += attack;
			}	
			//��ҿ��Լ����˺���buff�����绤��
			int redu = p.allReduce();
			sumAttack -= redu;
			if(sumAttack < 0) sumAttack = 0;   //��ֹ���ܵı��������ܵ����˺�				
			p.addHpMp(-sumAttack, 0); //�Ӹ����ţ��ͱ�ɼ���
			if(!p.isAlive()) {
				diedPlayer.add(p);
				p.getSession().sendMessage("���ѱ����["+ player.getName()+"]ɱ��");
				player.getSession().sendMessage("���ѽ����[" +p.getName() +"]ɱ��");
			}else{
				if(sumAttack > 0) {
					p.getSession().sendMessage("���["+player.getName() +
							"]�ĳ������ܶ�������˺��� ����Ѫ��" + sumAttack);
				}
			}

		}
		delStaleDated(player2skiId);
		deldiedPlayer(diedPlayer);
	}
	/**
	 * ɾ���Ѿ������˵ļ���Ч��
	 * @param p2s
	 */
	private void delStaleDated(Map<Player, Integer> p2s) {
		List<Player> timeoutps = new ArrayList<>();
		System.out.println("ɾ�����ڵļ���Ч��" + attPlayer.toString());
		for(Entry<Player, Integer> enti : p2s.entrySet()) {
			attPlayer.get(enti.getKey()).remove(new Integer(enti.getValue()));
			if(attPlayer.get(enti.getKey()).size() == 0) timeoutps.add(enti.getKey());
		}
		removetimeout(timeoutps);
	}
	private void removetimeout(List<Player> players) {
		for(Player p : players) {
			attPlayer.remove(p);
		}
		System.out.println("ɾ�����ڵļ���Ч��--��" + attPlayer.toString());
	}
	/**
	 * ɾ������������ҵĳ���Ч��
	 * @param players
	 */
	private void deldiedPlayer(List<Player> players) {
		System.out.println("ɾ������������ҵļ���Ч��" + attPlayer.toString());
		for(Player p : players) {
			attPlayer.remove(p);
		}
		System.out.println("ɾ������������ҵļ���Ч��--��" + attPlayer.toString());
	}
	
	@Override
	public void cleanup() {
		attPlayer.clear();
	}
	
	@Override
	public void addContiAttack(Monster monster, int skillId) {
		
	}

}
