package com.hc.logic.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hc.frame.Context;
import com.hc.logic.achieve.Achievement;
import com.hc.logic.basicService.BroadcastService;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

public class SkillAttackMonst implements SkillAttack{

	//��Ե���������г���Ч���ļ��ܡ�key�����value��( key:����id, ʱ���յ� )��
	//���Զ����������г�����Ч���ļ��ܣ�Ҳ�������������ֻ����Ҫÿ�����ﶼ����һЩ
	private Map<Monster, Map<Integer, Long>> attMonster = new HashMap<>();

	
	/**
	 * @param player : ������
	 */
	public void doContiAttack(Player player) {
		if(attMonster.size() == 0) return;
		Map<Monster, Integer> monst2skiId = new HashMap<>();  //����ɾ���Ĺ��ڳ���Ч��
		List<Monster> monsters = new ArrayList<>();          //����ɾ�������кʹ˹�����صĳ���Ч��
		int sumAttack = 0;  //��ʱ����δ���ڵļ��ܳ���Ч����ĳ��������ɵ����˺���
		//System.out.println("99999999999999999999999999999999");
		for(Entry<Monster, Map<Integer, Long>> enti : attMonster.entrySet()) {
			Monster m = enti.getKey();
			Map<Integer, Long> entiMap = enti.getValue();
			for(Entry<Integer, Long> ent : entiMap.entrySet()) {
				int skillId = ent.getKey();
				long terminal = ent.getValue();
				boolean timeout = System.currentTimeMillis() > terminal;
				System.out.println(System.currentTimeMillis() + ", " + terminal + ", " + timeout);
				if(timeout) {
					monst2skiId.put(m, skillId);  //��¼�Ѿ������˵ļ���Ч��
					continue;
				}
				int attack = Context.getSkillParse().getSkillConfigById(skillId).getAttack();
				sumAttack += attack;
			}	
			//if(sumAttack < 1) return;
			int attackSuccess = m.attack(sumAttack);
			//ֻҪattackSuccess������0���ͱ�ʾ�����Ѿ�����ɱ�ˣ���Ҫɾ���Դ˹�������й���
			if(attackSuccess != 0) {
				monsters.add(m);
			}
			if(attackSuccess == 1) {  //��ʾ���ﱻ�Լ���ɱ
				Context.getSkillService().doAttack(player.getSession(), m, 1);
				//ɱ���������Ҫ��֤�Ƿ���ĳ�ɾ�
				Achievement.getService(player, "KILLM", m.getMonstId());
			}
			if(attackSuccess == 0 && sumAttack > 0) {
				String msg = m.getName() + "�����[" + player.getName() +"]�ĳ������ܹ�����ʣ��Ѫ��Ϊ��" + m.getHp();
				BroadcastService.broadInScene(player.getSession(), msg);
			}
			sumAttack = 0;
		}
		delAMonstAttack(monsters);
		delStaleDated(monst2skiId);
	}
	
	/**
	 * ɾ�����к�ĳ��������صĹ��������������
	 * @param result
	 * @param tobeDrop
	 */
	private void delAMonstAttack(List<Monster> monsts) {
		System.out.println("��������" + monsts.toString());
		for(Monster m : monsts) {
			attMonster.remove(m);
		}
	}
	
	/**
	 * ɾ���Ѿ������˵ļ���Ч��
	 * @param p2s
	 */
	private void delStaleDated(Map<Monster, Integer> m2s) {
		System.out.println("���ڵļ���" + m2s.toString());
		List<Monster> mostdel = new ArrayList<>();
		for(Entry<Monster, Integer> enti : m2s.entrySet()) {
			attMonster.get(enti.getKey()).remove(new Integer(enti.getValue()));
			if(attMonster.get(enti.getKey()).size() == 0) {
				mostdel.add(enti.getKey());
			}
		}
		System.out.println("���ڵļ���---��" + m2s.toString());
		clearNull(mostdel);
	}
	private void clearNull(List<Monster> mostdel) {
		System.out.println("**���" + mostdel.toString());
		for(Monster monst : mostdel) {
			attMonster.remove(monst);
		}
		System.out.println("**���" + attMonster.toString());
	}

	
	/**
	 * ����г���Ч���ļ���
	 * @param player
	 * @param skillId ����id
	 */
	public void addContiAttack(Monster monster, int skillId) {
		System.out.println("-----����г���Ч���ļ���" + skillId + ", " + monster.getName());
		//System.out.println("----------" + attMonster.toString());
		int conti = Context.getSkillParse().getSkillConfigById(skillId).getContinueT();
		long terminate = conti * 1000;
		if(!attMonster.containsKey(monster)) attMonster.put(monster, new HashMap<Integer, Long>());
		if(!attMonster.get(monster).containsKey(new Integer(skillId))) {
			attMonster.get(monster).put(skillId, System.currentTimeMillis()+ terminate);
		}else {
			//��һ�����ܵĳ���ʱ�仹û�н�����ֱ���ӳ�����ʱ��
			long term = attMonster.get(monster).get(new Integer(skillId)) + terminate;
			attMonster.get(monster).put(skillId, term);
		}
		System.out.println("-----����г���Ч���ļ���--��---" + attMonster.toString());
	}
	
	/**
	 * ��ռ��ܳ���Ч��
	 */
	public void cleanup() {
		attMonster.clear();
	}

	public void addContiAttack(Player player, int skillId) {
		
	}
}
