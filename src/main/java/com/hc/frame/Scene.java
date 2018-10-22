package com.hc.frame;
import java.util.*;
import java.util.Map.Entry;

import com.hc.frame.taskSchedule.TaskConsume;
import com.hc.frame.taskSchedule.TaskProducer;
import com.hc.logic.base.Teleport;
import com.hc.logic.base.World;
import com.hc.logic.config.LevelConfig;
import com.hc.logic.creature.*;

public class Scene extends TaskConsume{

	//�������������
	protected String describe;
	//����id
	protected int id;
	//
	protected String name;
	//��ǰ�����ڵ���������, �������
	protected List<LiveCreature> creatures = new ArrayList<>();
	//��ǰ�����ڵ��������
	protected List<Player> players = new ArrayList<>();
	//��ǰ���������д�����,�б��д�ŵ���Ŀ�곡��id�ļ���
	protected List<String> teleports = new ArrayList<>();
	//��ǰ���������еĴ�����id
	protected List<Integer> telepIds = new ArrayList<>();
	
	//��ǰ����������������б�: key:����id, value:ÿ��������Թ��������
	private Map<Integer, List<Player>> attackPlayers = new HashMap<>();
	

	public Scene() {
		exe(5); //����һ�������Ե�����������20��
	}
	
	
	//��������ᱻ�Զ������Ե���
    @Override
    public void execute() {
    	//���ÿ��ָ���Ѫ���ͷ���
    	recoverHpMp();
    	//���﹥��
    	attackPlayer();
    	
    }
	

/**	
	public static void main(String[] args) {
		Scene scene = new Scene();
		scene.prepareExecute();
		
	}
*/	
	/**
	 * ÿ��ָ�Ѫ��������
	 */
	public void recoverHpMp() {
		//LevelConfig lc = Context.getLevelParse().getLevelConfigById(level);
		for(Player p : players) {
			LevelConfig lc = Context.getLevelParse().getLevelConfigById(p.getLevel());
			int mhp = lc.getuHp();  //�������ļ��л��ÿ�����ӵ�Ѫ��
			int mmp = lc.getuMp(); //�������ļ��л��ÿ�����ӵķ���
			//p.addHpMp(mhp, mmp);
			
			//ʹ�ûָ��൤ҩ����һ��ʱ���ڻָ�Ѫ����������ÿ����Ҷ���ͬ
			int[] recorHpMp = p.allRecover();  //���س���Ϊ2�����飬��һ���ǻָ���Ѫ�����ڶ����ǻָ��ķ���

			mhp += recorHpMp[0];
			mmp += recorHpMp[1];
			
			p.addHpMp(mhp, mmp);
		}
		
		
	}

	/**
	 * �������, 
	 * ��ʵ���Ǽ�����ҵ�Ѫ��
	 * ÿ�����ÿ��ֻ��ѡһ����ҽ��й���������ѡ���һ�������������
	 */
	public void attackPlayer() {
		for(Entry<Integer, List<Player>> enti : attackPlayers.entrySet()) {
			int mId = enti.getKey();
			List<Player> attackP = enti.getValue();
			if(attackP.isEmpty()) return;
			Player pp = attackP.get(0); //ÿ��ֻ������һ�������������
			int dHp = Context.getSceneParse().getMonsters().getMonstConfgById(mId).getAttack();
			
			//��ҿ��Լ����˺���buff�����绤��
			int redu = pp.allReduce();
			dHp -= redu;
			if(dHp < 0) dHp = 0;   //��ֹ���ܵı��������ܵ����˺�
			
			pp.addHpMp(-dHp, 0); //�Ӹ����ţ��ͱ�ɼ���
			String name = Context.getSceneParse().getMonsters().getMonstConfgById(mId).getName();
			pp.getSession().sendMessage("���ڱ���" + name + " ����������Ѫ����" + dHp);

		}

	}
	
	//�������ܼ�����ң���Ҫɾ����ҡ�
	public void deletePlayer(Player player) {
		this.players.remove(player);
		//this.creatures.remove(player);
	}
	
	/**
	 * �����û���������
	 */
	public Player getPlayerByName(String name) {
		for(Player p : players) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	
	
	//************get,set����**************
	
	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<LiveCreature> getCreatures() {
		return creatures;
	}

	public void addCreatures(LiveCreature creature) {
		this.creatures.add(creature);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		this.players.add(player);
		//this.creatures.add(player);
	}

	public List<String> getTeleport() {
		return teleports;
	}

	public void addTeleport(String te) {
		teleports.add(te);
		String sid = te.substring(te.length()-2, te.length()-1);
		telepIds.add(Integer.parseInt(sid));
	}


	public String getName() {
		return name;
	}
	public void setName(String n) {
		this.name = n;
	}

	public List<Integer> getTelepIds() {
		return telepIds;
	}
	

	public Map<Integer, List<Player>> getAttackPlayers() {
		return attackPlayers;
	}


	public void setAttackPlayers(Map<Integer, List<Player>> attackPlayers) {
		this.attackPlayers = attackPlayers;
	}
	/**
	 * ���ӿ��Ա�ĳ�����﹥�������
	 * ����֮�󣬾�һֻ�����ܹ�����ֻ���ڹ�������������뿪��ǰ�������Ų��ᱻ����
	 * @param mId : ����id 
	 * @param p �� ������Թ��������
	 */
	public void addAttackPlayer(int mId, Player p) {
		if(!attackPlayers.containsKey(mId)) {
			attackPlayers.put(mId, new ArrayList<Player>());
		}
		//���ظ���ӡ�һ�������б��У��������ظ������
		if(attackPlayers.get(mId).contains(p)) return;
		attackPlayers.get(mId).add(p);
		System.out.println("addAttackPlayer " + attackPlayers.toString());
	}
	/**
	 * ��һ�����ȥ����ĳ���ʱ������͹����������ӹ���Ĺ����б���ɾ��
	 * @param p
	 */
	public void deleteAttackPlayer(Player p) {
		System.out.println(" ��һ�����ȥ����ĳ���ʱ������͹����������ӹ���Ĺ����б���ɾ��");
		//boolean find = false;
		for(Entry<Integer, List<Player>> enti : attackPlayers.entrySet()) {
			int mId = enti.getKey();
			List<Player> attackP = enti.getValue();
			for(int j = 0; j < attackP.size(); j++) {
				if(attackP.get(j).getName().equals(p.getName())) {
					attackP.remove(p);
				}
				if(attackP.isEmpty()) {
					attackPlayers.remove(mId);
				}
				break;
			}
		}
		/**
		for(int i = 1; i <= attackPlayers.size(); i++) {
			for(int j = 0; j < attackPlayers.get(i).size(); j++) {
				if(attackPlayers.get(i).get(j).getName().equals(p.getName())) {
					attackPlayers.get(i).remove(p);
					//find = true;
					//������б�Ϊ�գ���ɾ��
					if(attackPlayers.get(i).isEmpty()) {
						attackPlayers.remove(i);
					}
					break;
				}
			}
			//if(find == true) break;
		}
		*/
		
	}
	/**
	 * �����ﱻ��ɱ���Ͳ��ܹ�������ˣ���Ҫɾ��
	 * @param mId
	 */
	public void deleteAttackMonst(int mId) {
		System.out.println("�����ﱻ��ɱ���Ͳ��ܹ�������ˣ���Ҫɾ��");
		attackPlayers.remove(mId);
	}


	/**
	 * �жϵ�ǰ�����Ƿ������������id
	 * @param id
	 * @return
	 */
	public boolean hasTelepId(int id) {
		for(int ii : telepIds) {
			if(ii == id) {
				return true;
			}
		}
		return false;
	}


	/**
	 * ���е�ǰ�������Դ��͵��ĳ���
	 * @return
	 */
	public String allTransportableScene() {
		return teleports.toString();
	}
	
	/**
	 * ��ǰ�����Ƿ�����������
	 */
	public boolean hasMonst(int mId) {
		for(LiveCreature ii : creatures) {
			if(ii.getcId() == mId) {
				return true;
			}
		}
		return false;
	}
}
