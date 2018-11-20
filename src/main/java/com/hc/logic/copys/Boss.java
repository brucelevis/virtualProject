package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.hc.frame.Context;
import com.hc.frame.taskSchedule.TaskConsume;
import com.hc.logic.basicService.BroadcastService;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.SkillConfig;
import com.hc.logic.creature.Player;

/**
 * boss, ����һ��boss, �������Եĵ��á�
 * @author hc
 *
 */
public class Boss implements Runnable{

	//boss id
	private int bid;
	//boss���ܶ�Ӧ�Ĺ�������key������id��value��������
	private Map<Integer, Integer> skill2attack = new HashMap<>();
	//boss����cd��key������id�� value���ϴ�ʹ��ʱ��
	private Map<Integer, Long> skillsCD = new HashMap<>();
	//��boss��Ӧ�ĳ����е����пɹ������
	private List<Player> players = new ArrayList<>();
	//��Ե�����ҵ��г���Ч���ļ��ܡ�key����ң�value��( key:����id, ʱ���յ� )��
	//���Զ������г�����Ч���ļ��ܣ�Ҳ�������������ֻ����Ҫÿ����Ҷ�����һЩ
	private Map<Player, Map<Integer, Long>> attPlayer = new HashMap<>();
	//�Ƿ������������
	private boolean attackNow = false;
	
	
	
	/**
	 * 
	 * @param bid boss��id
	 * @param players
	 */
	public Boss(int bid, List<Player> players) {
		this.bid = bid;
		this.players = new ArrayList<>(players);
		init();
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
		//System.out.println("boss�߳�, bid=" + bid + ", ʱ�� " + System.currentTimeMillis());
		//�������
		bossAttackPlayer();
		//���г�����Ч���ļ��ܣ�������Ӧ���Ѫ��
		reduceHp();
	}
	
	public void bossAttackPlayer() {
		if(!attackNow) return;  //����Ǳ���������boss��attackNowҪ����ҹ�����bossʱ����	
		int skillId = nextSkill();
		//�����ÿ���μ��ܹ���ʱ�䲻��ȵ�Ч��
		if(!skill2attack.containsKey(new Integer(skillId))) return;
		//��֤�Ƿ�cd��
		if(!isCdOut(skillId)) return;
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		int conti = skillConfig.getContinueT()*1000;
		//System.out.println("------------bossAttackPlayer---------continue=" + conti + " ���� " + skillConfig.getName());
		if(skillConfig.getScope() != 0) {
			//�˼���ֻ���һ�����
			if(nextAttackPlayer() < 0) return;
			Player player = players.get(nextAttackPlayer());
			//ѣ��
			dizzi(player, skillId);
			if(conti < 1) instantAttack(player, skillId);
			else  continueAttack(player, skillId);	
			broadcastAttack(player, skillId);  //���ż�����ɵ��˺�
		}else {
			//����������
			if(conti < 1) {
				instantAllAttack(skillId);
			}else {
				continueAllAttack(skillId);
			}
			broadcastAttack(players.get(0), skillId);  //���ż�����ɵ��˺�
		}
		
	}
	
	/**
	 * ����boss�ͷŵļ��ܣ��Լ���ɵ��˺�
	 * @param player
	 * @param skillId
	 */
	public void broadcastAttack(Player player, int skillId) {
		MonstConfig bossConfig = Context.getSceneParse().getMonsters().getMonstConfgById(bid);
		SkillConfig skillConfig = Context.getSkillParse().getSkillConfigById(skillId);
		String allPlayer = ((skillConfig.getScope() == 0) ? "�������" : ("���"+player.getName())); 
		String hert = (skillConfig.getContinueT() < 1 ? (skillConfig.getAttack()+"��") : ("ÿ��"+skillConfig.getAttack()+"��"));
		StringBuilder sb = new StringBuilder();
		sb.append("boss " + bossConfig.getName());
		sb.append(" ʹ�ü���  " + skillConfig.getName());
		sb.append(" �� " + allPlayer + " ��� ");
		sb.append( hert + " ���˺���");
		//System.out.println("-----------------------broadcast " + sb.toString());
		BroadcastService.broadToPlayer(players, sb.toString());
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
		//System.out.println("-&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&reduceHp---");
		Map<Player, Integer> player2skiId = new HashMap<>();
		for(Entry<Player, Map<Integer, Long>> enti : attPlayer.entrySet()) {
			Player p = enti.getKey();
			for(Entry<Integer, Long> ent : enti.getValue().entrySet()) {
				int skillId = ent.getKey();
				long terminal = ent.getValue();
				if(terminal < System.currentTimeMillis()) {
					player2skiId.put(p, skillId);  //��¼�Ѿ������˵ļ���Ч��
					continue;
				}
				int attack = skill2attack.get(new Integer(skillId));
				//��ҿ��Լ����˺���buff�����绤��
				int redu = p.allReduce();
				attack -= redu;
				if(attack < 0) attack = 0;   //��ֹ���ܵı��������ܵ����˺�				
				p.addHpMp(-attack, 0); //�Ӹ����ţ��ͱ�ɼ���
				//System.out.println("boss�ĳ������ܶ�������˺��� ����Ѫ��" + attack);
				p.getSession().sendMessage("boss�ĳ������ܶ�������˺��� ����Ѫ��" + attack);
			}	
		}
		delStaleDated(player2skiId);
	}
	/**
	 * ɾ���Ѿ������˵ļ���Ч��
	 * @param p2s
	 */
	private void delStaleDated(Map<Player, Integer> p2s) {
		for(Entry<Player, Integer> enti : p2s.entrySet()) {
			attPlayer.get(enti.getKey()).remove(new Integer(enti.getValue()));
		}
	}
	
	/**
	 * ����г���Ч���ļ���
	 * @param player
	 * @param skillId ����id
	 */
	private void continueAttack(Player player, int skillId) {
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
		//System.out.println("---------------continueAttack----------- " + attPlayer.toString());
	}
	/**
	 * �г���Ч����Ⱥ������
	 * @param skillId
	 */
	private void continueAllAttack(int skillId) {
		for(Player p : players) {
			if(!p.isAlive()) continue;
			continueAttack(p, skillId);
		}
	}
	
	
	/**
	 * ˲ʱ����
	 * @param player �����������
	 * @param skillId ����id
	 */
	private void instantAttack(Player player, int skillId) {
		int attack = Context.getSkillParse().getSkillConfigById(skillId).getAttack();
		player.addHpMp(-attack, 0);
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
		for(Player p : players) {
			if(!p.isAlive()) continue;
			instantAttack(p, skillId);
		}
	}
	
	/**
	 * ѡ��һ�����ŵ���ҳ��ܹ���
	 * @return -1����ȫ������Ѿ�����
	 */
	public int nextAttackPlayer() {
		Random random = new Random();
		int nextone = random.nextInt(players.size());
		//ȥ�����������
		if(!players.get(nextone).isAlive())
			return firstNotDead(nextone);
		return nextone;
	}
	private int firstNotDead(int index) {
		int bound = index + players.size();
		for(int i = index; i < bound; i++) {
			i = ((i >= players.size()) ? (i - players.size()) : i);
			if(players.get(i).isAlive()) return i;
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
	 * ������ҹ����б�
	 * @param p
	 */
	public void addPlayer(Player p) {
		 players.add(p);
	}

	/**
	 * ɾ��boss�����б�
	 * @param pid
	 */
	public void delPlayers(int pid) {
		//System.out.println("----------boss.delplayers, ǰ" + players.toString());
		for(Player pp : players) {
			if(pp.getId() == pid) {
				players.remove(pp);
				return;
			}
		}
	}

	@Override
	public String toString() {
		return "boss{id=" + bid
	          + ", name="+ Context.getCopysParse().getCopysConfById(bid).getName()
	          + ", players.size()=" + players.size()
	          + " }";
	}
	
}
