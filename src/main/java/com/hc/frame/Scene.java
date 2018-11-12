package com.hc.frame;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.hc.frame.taskSchedule.TaskConsume;
import com.hc.logic.base.Session;
import com.hc.logic.config.LevelConfig;
import com.hc.logic.config.SceneConfig;
import com.hc.logic.creature.*;

public class Scene extends TaskConsume{

	//�������������
	protected String describe;
	//����id
	protected int id;
	//
	protected String name;
	//��ǰ�����ڵ�����npc, ���������
	protected List<LiveCreature> creatures = new ArrayList<>();
	//���й���
	protected List<Monster> monsters = new ArrayList<>();
	//��ǰ�����ڵ��������
	protected List<Player> players = new ArrayList<>();
	//��ǰ���������д�����,�б��д�ŵ���Ŀ�곡��id�ļ���
	protected List<String> teleports = new ArrayList<>();
	//��ǰ���������еĴ�����id
	protected List<Integer> telepIds = new ArrayList<>();
	
	//��ǰ����������������б�: key:����id, value:ÿ��������Թ��������
	protected Map<Integer, List<Player>> attackPlayers = new HashMap<>();
	
	//һ���������У����������������ͻᱻִ�С�
	BlockingQueue<Runnable> task = new LinkedBlockingQueue<>();


	public Scene() {
		exe(5, "scene"+id); //����һ�������Ե�����������20��
	}
	
	public Scene(int interval) {
		System.out.println("scene�Ĺ��췽��" + interval);
	}
	
	
	//��������ᱻ�Զ������Ե���
    @Override
    public void execute() {
    	while(!task.isEmpty()) {
    		task.poll().run();
    	}
    	//���ÿ��ָ���Ѫ���ͷ���
    	recoverHpMp();
    	//���﹥��
    	attackPlayer();
    	
    }
    
    /**
     * �������
     * @param t
     */
    public void addTask(Runnable t) {
    	try {
			task.put(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
		
	/**
	 * ÿ��ָ�Ѫ��������
	 */
	public void recoverHpMp() {
		for(Player p : players) {
			LevelConfig lc = Context.getLevelParse().getLevelConfigById(p.getLevel());
			int mhp = lc.getuHp();  //�������ļ��л��ÿ�����ӵ�Ѫ��
			int mmp = lc.getuMp(); //�������ļ��л��ÿ�����ӵķ���
			
			//ʹ�ûָ��൤ҩ����һ��ʱ���ڻָ�Ѫ����������ÿ����Ҷ���ͬ
			int[] recorHpMp = p.allRecover();  //���س���Ϊ2�����飬��һ���ǻָ���Ѫ�����ڶ����ǻָ��ķ���	 
			mhp += recorHpMp[0];
			mmp += recorHpMp[1];	
			p.addHpMp(mhp, mmp);
			p.skillRecHp();   //���ܵ��µĳ�����Ѫ		
			p.skillContiAttack(); //���ܵĳ�������Ч��
		}
	}

	/**
	 * �������, 
	 * ��ʵ���Ǽ�����ҵ�Ѫ��
	 * ÿ�����ÿ��ֻ��ѡһ����ҽ��й���������ѡ���һ�������������
	 */
	public void attackPlayer() {
		//System.out.println("*****" + attackPlayers.size() + ", " + attackPlayers.toString());
		for(Entry<Integer, List<Player>> enti : attackPlayers.entrySet()) {
			System.out.println("������" + attackPlayers.toString());
			int mId = enti.getKey();
			List<Player> attackP = enti.getValue();
			if(attackP.isEmpty()) return;
			Player pp = attackP.get(0); //ÿ��ֻ������һ�������������
			int dHp = Context.getSceneParse().getMonsters().getMonstConfgById(mId).getAttack();
			
			//��ҿ��Լ����˺���buff�����绤��
			pp.attackPlayerReduce(dHp);
			
			String name = Context.getSceneParse().getMonsters().getMonstConfgById(mId).getName();
			pp.getSession().sendMessage("���ڱ� " + name + " ����������Ѫ����" + dHp);

		}

	}
	
	
	//�������ܼ�����ң���Ҫɾ����ҡ�
	public void deletePlayer(Player player) {
		this.players.remove(player);
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

	
	/**
	 * ���ӿ��Ա�ĳ�����﹥�������
	 * ����֮�󣬾�һֻ�����ܹ�����ֻ���ڹ�������������뿪��ǰ�������Ų��ᱻ����
	 * @param mId : ����id 
	 * @param p �� ������Թ��������
	 */
	public void addAttackPlayer(int mId, Player p) {
		System.out.println("------------scene.addattackplayer----" + attackPlayers.toString());
		if(!attackPlayers.containsKey(mId)) {
			attackPlayers.put(mId, new ArrayList<Player>());
		}
		//���ظ���ӡ�һ�������б��У��������ظ������
		if(attackPlayers.get(mId).contains(p)) return;
		attackPlayers.get(mId).add(p);
		System.out.println("------------scene.addattackplayer--��--" + attackPlayers.toString());
	}
	/**
	 * ��һ�����ȥ����ĳ���ʱ������͹����������ӹ���Ĺ����б���ɾ��
	 * ͬʱҲɾ�����ܵĳ���Ч��
	 * @param p
	 */
	public void deleteAttackPlayer(Player p) {
		System.out.println(" ��һ�����ȥ����ĳ���ʱ������͹����������ӹ���Ĺ����б���ɾ��");
		//boolean find = false;
		System.out.println("ǰ "+attackPlayers.toString());
		List<Integer> monsIds = new ArrayList<>();
		for(Entry<Integer, List<Player>> enti : attackPlayers.entrySet()) {
			int mId = enti.getKey();
			List<Player> attackP = enti.getValue();
			for(int j = 0; j < attackP.size(); j++) {
				if(attackP.get(j).getName().equals(p.getName())) {
					attackP.remove(p);
				}
				if(attackP.isEmpty()) {
					//attackPlayers.remove(mId);
					monsIds.add(mId);
				}
				break;
			}
		}
		//����Ҵ��ͺ���ռ��ܵĳ���Ч��
		p.getSkillAttack().cleanup();
		System.out.println("�� "+attackPlayers.toString() + ", " + monsIds.toString());
		delNullList(monsIds);
	}
	private void delNullList(List<Integer> delMId) {
		for(int i : delMId) {
			attackPlayers.remove(new Integer(i));
		}
	}
	/**
	 * �����ﱻ��ɱ���Ͳ��ܹ�������ˣ���Ҫɾ��
	 * @param mId
	 */
	public void deleteAttackMonst(Monster monster) {
		System.out.println("================���ﱻ��ɱ��");
		attackPlayers.remove(new Integer(monster.getMonstId()));
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
	 * @param mid ����id
	 */
	public boolean hasMonst(int mId) {
		for(Monster ii : monsters) {
			if(ii.getMonstId() == mId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ���ؿͻ��˵�ǰ������������Ϣ
	 * @param session
	 */
	public void allThing(Session session) {
		session.sendMessage("����npc" + getCreatures() + "");
		session.sendMessage("���й���" + getMonsters() + "\n");
		session.sendMessage("�������" + getPlayers() + "\n");
		//�����˴��͵ķ�ʽ
		session.sendMessage("���пɴ���Ŀ�꣺" + allTransportableScene());
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

	public void addMonste(Monster monst) {
		this.monsters.add(monst);
	}
	public Monster getMonsteById(int id) {
		for(Monster mon : monsters) {
			if(mon.getMonstId() == id) {
				return mon;
			}
		}
		return null;
	}
	public void initMonster() {
		SceneConfig sConfig = Context.getSceneParse().getSceneById(id);
		for(int i : sConfig.getMonsts()) {
			Monster monst = new Monster(i);
			addMonste(monst);
		}
	}
	public List<Monster> getMonsters() {
		if(monsters.size() == 0) initMonster();
		return monsters;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		this.players.add(player);
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
	

}
