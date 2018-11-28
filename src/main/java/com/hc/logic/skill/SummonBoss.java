package com.hc.logic.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map.Entry;

import com.hc.frame.Context;
import com.hc.frame.taskSchedule.TaskConsume;
import com.hc.logic.basicService.BroadcastService;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;

/**
 * ���ٻ�ʦ�ٻ���boss
 * @author hc
 *
 */
public class SummonBoss implements Runnable{

	//boss id
	private int bid;
	private Monster summBoss;  //������ֻ
	//boss���ܶ�Ӧ�Ĺ�������key������id��value��������
	private Map<Integer, Integer> skill2attack = new HashMap<>();
	//boss����cd��key������id�� value���ϴ�ʹ��ʱ��
	private Map<Integer, Long> skillsCD = new HashMap<>();
	//��boss��Ӧ�ĳ����е����пɹ�������
	//private List<Player> players = new ArrayList<>();
	private List<Monster> monsters = new ArrayList<>();
	//��Ե�����ҵ��г���Ч���ļ��ܡ�key�����value��( key:����id, ʱ���յ� )��
	//���Զ������г�����Ч���ļ��ܣ�Ҳ�������������ֻ����Ҫÿ����Ҷ�����һЩ
	private Map<Monster, Map<Integer, Long>> attEnemy = new HashMap<>();
	//�Ƿ���������
	private boolean attackNow = false;
	//�ٻ����boss���ٻ�ʦ
	private Player player;
	//���ڵ�ʱ��
	private long terminate;
	private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
	
	
	/**
	 * 
	 * @param bid boss��id
	 * @param players
	 */
	public SummonBoss(Monster summboss, List<Monster> monsters, Player pp) {
        this.player = pp;
		this.bid = summboss.getMonstId();
		this.summBoss = summboss;
		this.monsters = new ArrayList<>(monsters);
		init();
		this.terminate = System.currentTimeMillis() + 13*1000;  //Ĭ�ϴ���7��
		System.out.println("summonboss��ʼ��" + terminate + ", ��ǰʱ��" + System.currentTimeMillis());
		System.out.println("�ٻ���ɹ����б�" + monsters.toString());
	}
	
	private void init() {
		MonstConfig monstConfig = Context.getSceneParse().getMonsters().getMonstConfgById(bid);
		attackNow = (monstConfig.getAttackP() == 0 ? false : true);
		for(int i : monstConfig.getSkills()) {
			SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(i);
			skill2attack.put(i, skillConfig.getAttack());
		}
	}
	
	
	@Override
	public void run() {
		while(!tasks.isEmpty()) {
			try {
				tasks.take().run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("boss�߳�, bid=" + bid + ", ʱ�� " + System.currentTimeMillis());
		//���й���
		bossAttack();
		//���г�����Ч���ļ��ܣ�������Ӧ���Ѫ��
		reduceHp();
		if(System.currentTimeMillis() > terminate || summBoss.attack(1) == -1) {
			complete();
		}
	}
	
	public void bossAttack() {
		int skillId = nextSkill();
		//�����ÿ���μ��ܹ���ʱ�䲻��ȵ�Ч��
		if(!skill2attack.containsKey(new Integer(skillId))) return;
		//��֤�Ƿ�cd��
		if(!isCdOut(skillId)) return;
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		int conti = skillConfig.getContinueT()*1000;
		System.out.println("------------bossAttackPlayer---------continue=" + conti + " ���� " + skillConfig.getName());
		if(skillConfig.getScope() != 0) {
			//�˼���ֻ���һ�����
			if(nextAttackPlayer() < 0) return;
			Monster monster = monsters.get(nextAttackPlayer());
			//ѣ��
			//dizzi(monster, skillId);
			if(conti < 1) instantAttack(monster, skillId);
			else  continueAttack(monster, skillId);	
			//broadcastAttack(monster, skillId);  //���ż�����ɵ��˺�
		}else {
			//����������
			if(conti < 1) {
				instantAllAttack(skillId);
			}else {
				continueAllAttack(skillId);
			}
			//broadcastAttack(players.get(0), skillId);  //���ż�����ɵ��˺�
		}
		
	}
		
	/**
	 * ��֤�����Ƿ�cd�ꡣ��cd�꣬����true��������cdʱ��
	 * @param skillConfig
	 * @return
	 */
	public boolean isCdOut(int skillId) {
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		if(!skillsCD.containsKey(new Integer(skillId))) {
			skillsCD.put(skillId, System.currentTimeMillis());
			return true;
		}
		long past = skillsCD.get(new Integer(skillId));
		long nowT = System.currentTimeMillis();
		long dual = skillConfig.getCd() * 1000;
		if((nowT - past) > dual) {
			skillsCD.put(skillId,  System.currentTimeMillis());
			return true;
		}
		//System.out.println("--------����cdʱ��------------" + skillsCD.toString());
		return false;
	}
	
	/**
	 * ���г�����Ч���ļ��ܸ���Ҵ���������Ѫ
	 * ��ɾ���Ѿ������˵ļ��ܳ���Ч��
	 */
	public void reduceHp() {
		Map<Monster, Integer> monster2skiId = new HashMap<>();
		for(Entry<Monster, Map<Integer, Long>> enti : attEnemy.entrySet()) {
			Monster m = enti.getKey();
			for(Entry<Integer, Long> ent : enti.getValue().entrySet()) {
				int skillId = ent.getKey();
				long terminal = ent.getValue();
				if(terminal < System.currentTimeMillis()) {
					monster2skiId.put(m, skillId);  //��¼�Ѿ������˵ļ���Ч��
					continue;
				}
				int attack = skill2attack.get(new Integer(skillId));
				
				int attacked = m.attack(attack);
				broadCastMessage(m, attacked, attack);
			}	
		}
		delStaleDated(monster2skiId);
	}
	/**
	 * ɾ���Ѿ������˵ļ���Ч��
	 * @param p2s
	 */
	private void delStaleDated(Map<Monster, Integer> m2s) {
		for(Entry<Monster, Integer> enti : m2s.entrySet()) {
			attEnemy.get(enti.getKey()).remove(new Integer(enti.getValue()));
		}
	}
	
	/**
	 * ����г���Ч���ļ���
	 * @param player
	 * @param skillId ����id
	 */
	private void continueAttack(Monster monster, int skillId) {
		int conti = Context.getSkillParse().getSkillConfigById(skillId).getContinueT();
		long terminate = conti * 1000;
		if(!attEnemy.containsKey(monster)) attEnemy.put(monster, new HashMap<Integer, Long>());
		if(!attEnemy.get(monster).containsKey(new Integer(skillId))) {
			attEnemy.get(monster).put(skillId, System.currentTimeMillis()+ terminate);
		}else {
			//��һ�����ܵĳ���ʱ�仹û�н�����ֱ���ӳ�����ʱ��
			long term = attEnemy.get(monster).get(new Integer(skillId)) + terminate;
			attEnemy.get(monster).put(skillId, term);
		}
		//System.out.println("---------------continueAttack----------- " + attPlayer.toString());
	}
	/**
	 * �г���Ч����Ⱥ������
	 * @param skillId
	 */
	private void continueAllAttack(int skillId) {
		for(Monster m : monsters) {
			//if(!p.isAlive()) continue;
			continueAttack(m, skillId);
		}
	}
	
	
	/**
	 * ˲ʱ����
	 * @param player �����������
	 * @param skillId ����id
	 */
	private void instantAttack(Monster monster, int skillId) {
		int attack = Context.getSkillParse().getSkillConfigById(skillId).getAttack();
		//player.addHpMp(-attack, 0);
		int attacked = monster.attack(attack);
		broadCastMessage(monster, attacked, attack);
	}
	
	private void broadCastMessage(Monster monster, int attacked, int reduHp) {
		String summonbossName = Context.getSceneParse().getMonsters().getMonstConfgById(bid).getName();
		String mesg = "";
		if(attacked==1) {
			//�����ɹ�����ɱ������
			mesg = "�ٻ�ʦ[" + player.getName() + "]�ٻ���[" + summonbossName + "]��������[" +
					monster.getName() + "]��ɱ";
			//��ɱ����/boss�����Ӧ����
			Context.getAwardService().obtainAward(player, monster);
		}
		if(attacked == 0) {
			//�����ɹ�����û��ɱ��
			mesg = "�ٻ�ʦ[" + player.getName() + "]�ٻ���[" + summonbossName + "]���Թ���[" +
					monster.getName() + "]���[" + reduHp + "]���˺�, ʣ��Ѫ��[" + monster.getHp()
					+ "]";
		}
		if(attacked == -1) {
			//���ﱻ���˻�ɱ
			mesg = "�ٻ�ʦ[" + player.getName() + "]�ٻ���[" + summonbossName + "]���Թ���[" +
					monster.getName() + "���й���ʧ�ܣ�����������";
		}
		broadcastMesg(mesg);
		if(attacked != 0) {
			tasks.add(new Runnable() {
				public void run() {
					delEnermys(monster.getMonstId());
				}
			});
			player.getScene().deleteAttackMonst(monster);
		}
	}
	
	private void broadcastMesg(String mesg) {
		if(player.getCopEntity() != null && player.getCopEntity().getPlayers().size() > 1) {
			BroadcastService.broadInScene(player.getSession(), mesg);
		}else {
			player.getSession().sendMessage(mesg);
		}
	}
	
	private void dizzi(Player player, int skillId) {
		int dizziness = Context.getSkillParse().getSkillConfigById(skillId).getDizziness();
		System.out.println("----------------------����ѣ��ʱ��----------------------" + skillId + ", " + dizziness);
		if(dizziness != 0) player.setCanUSkill(dizziness);
	}
	/**
	 * ˲ʱȺ������
	 * @param skillId
	 */
	private void instantAllAttack(int skillId) {
		for(Monster m: monsters) {
			//if(!p.isAlive()) continue;
			instantAttack(m, skillId);
		}
	}
	
	/**
	 * ѡ��һ�����ŵ���ҳ��ܹ���
	 * @return -1����ȫ������Ѿ�����
	 */
	public int nextAttackPlayer() {
		Random random = new Random();
		int nextone = random.nextInt(monsters.size());
		//ȥ�����������
		if(!monsters.get(nextone).isAlive())
			return firstNotDead(nextone);
		return nextone;
	}
	private int firstNotDead(int index) {
		int bound = index + monsters.size();
		for(int i = index; i < bound; i++) {
			i = ((i >= monsters.size()) ? (i - monsters.size()) : i);
			if(monsters.get(i).isAlive()) return i;
		}
		return -1;   //��ʾ��Ҷ���ȫ������
	}
	
	/**
	 * bossѡ����һ�����ܣ�Ҳ�᷵�ز��Ǽ��ܵ���������
	 * @return
	 */
	public int nextSkill() {
		Random random = new Random();
		int nextone = random.nextInt(skill2attack.size()+6) + 100;  //���100 Ҫ�ó���
		return nextone;
	}
	
	
	

	public int getId() {
		return bid;
	}

	public void setId(int id) {
		this.bid = id;
	}
	
	
	public boolean isAttackNow() {
		return attackNow;
	}

	public void setAttackNow(boolean attackNow) {
		this.attackNow = attackNow;
	}

	/**
	 * ɾ��boss�����б�
	 * @param pid
	 */
	public void delEnermys(int pid) {
		//System.out.println("----------boss.delplayers, ǰ" + players.toString());
		for(Monster mm : monsters) {
			if(mm.getMonstId() == pid) {
				monsters.remove(mm);
				attEnemy.remove(mm);
				System.out.println("--------ɾ�������Ĺ���----" + monsters.toString());
				return;
			}
		}
	}
	

	@Override
	public String toString() {
		return "boss{id=" + bid
	          + ", name="+ Context.getCopysParse().getCopysConfById(bid).getName()
	          + ", players.size()=" + monsters.size()
	          + " }";
	}

	/**
	 * �ر��߳�
	 * ��ʱ��������
	 */
	public void complete() {
		System.out.println("---------��ʱ�˳�-----------" + terminate + ", ��ǰ"+ System.currentTimeMillis());
		Context.getWorld().delSummonsThread(player.getId(), bid);
	}
}
