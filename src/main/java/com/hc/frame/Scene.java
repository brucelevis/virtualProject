package com.hc.frame;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.ApplicationContext;

import com.hc.frame.taskSchedule.TaskConsume;
import com.hc.logic.base.Session;
import com.hc.logic.config.LevelConfig;
import com.hc.logic.config.SceneConfig;
import com.hc.logic.creature.*;
import com.hc.logic.xmlParser.SceneParse;

public class Scene implements Runnable{

	//这个场景的描述
	protected String describe;
	//场景id
	protected int id;
	//
	protected String name;
	//当前场景内的所有npc, 不包括玩家
	protected List<LiveCreature> creatures = new ArrayList<>();
	//所有怪物
	protected List<Monster> monsters = new ArrayList<>();
	//当前场景内的所有玩家
	protected List<Player> players = new ArrayList<>();
	//当前场景的所有传送阵,列表中存放的是目标场景id的集合
	protected List<String> teleports = new ArrayList<>();
	//当前场景，所有的传送阵id。（场景id--传送阵id）
	protected Map<Integer, Integer> tSid2tid = new HashMap<>();
	
	//当前场景，被攻击玩家列表: key:怪物id, value:每个怪物可以攻击的玩家
	protected Map<Integer, List<Player>> attackPlayers = new HashMap<>();
	
	private Lock lock = new ReentrantLock();


	public Scene(ApplicationContext ctx) {
		//exe(5, "scene"+id); //启动一个周期性调度器，周期1秒
		ctx.getBean("taskConsume", TaskConsume.class).exe(1, "scene"+id, this);
		//doTask();
	}
	
	public Scene(int interval) {
		System.out.println("scene的构造方法" + interval);
	}
	
	
	//这个方法会被自动周期性调用
    @Override
    public void run() {
    	try {
    		//怪物攻击
        	attackPlayer();
        	letPlayerProgress();
    	}catch(Exception e){
    		System.out.println("-------scene 中的异常" + id + ", players.size=" + players.size());
    		e.printStackTrace();
    	}
    }

    public void letPlayerProgress() {
    	lock.lock();
    	try {
    		//System.out.println("------------场景心跳----------" + id);
    		if(players == null || players.size() < 1) {
    			return;
    		}
    		//System.out.println("场景："+ id +" lePlayerProgress中，玩家队列的大小" + players.size());
    		for(int i = 0; i < players.size(); i++) {
    			if(players.get(i) != null) {
    				players.get(i).periodCall();
    			}
    		}
    	}finally {
    		lock.unlock();
    	}
    }
	/**
	 * 攻击玩家, 
	 * 其实就是减少玩家的血量
	 * 每个怪物，每次只能选一个玩家进行攻击，这里选择第一个攻击它的玩家
	 */
	public void attackPlayer() {
		//System.out.println("*****" + attackPlayers.size() + ", " + attackPlayers.toString());
		lock.lock();
		try {
			for(Entry<Integer, List<Player>> enti : attackPlayers.entrySet()) {
				System.out.println("啦啦啦" + attackPlayers.toString());
				int mId = enti.getKey();
				List<Player> attackP = enti.getValue();
				if(attackP.isEmpty()) return;
				Player pp = attackP.get(0); //每次只攻击第一个攻击它的玩家
				if(!pp.isAlive()) {
					deleteAttackPlayer(pp);
					return;
				}
				int dHp = Context.getSceneParse().getMonsters().getMonstConfgById(mId).getAttack();
				
				//玩家可以减少伤害的buff，比如护盾
				dHp = pp.attackPlayerReduce(dHp);
				
				String name = Context.getSceneParse().getMonsters().getMonstConfgById(mId).getName();
				pp.getSession().sendMessage("正在被 " + name + " 攻击，减少血量：" + dHp);

			}
		}finally {
			lock.unlock();
		}

	}
	
	
	//场景中能加入玩家，就要删除玩家。
	public void deletePlayer(Player player) {
		lock.lock();
		try {
			this.players.remove(player);
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 根据用户名获得玩家
	 */
	public Player getPlayerByName(String name) {
		lock.lock();
		try {
			for(Player p : players) {
				if(p.getName().equals(name)) {
					return p;
				}
			}
			return null;
		}finally {
			lock.unlock();
		}
	}

	
	/**
	 * 增加可以被某个怪物攻击的玩家
	 * 加入之后，就一只会遭受攻击，只有在怪物死亡、玩家离开当前场景，才不会被攻击
	 * @param mId : 怪物id 
	 * @param p ： 怪物可以攻击的玩家
	 */
	public void addAttackPlayer(int mId, Player p) {
		lock.lock();
		try {
			System.out.println("------------scene.addattackplayer----" + attackPlayers.toString());
			if(!attackPlayers.containsKey(mId)) {
				attackPlayers.put(mId, new ArrayList<Player>());
			}
			//不重复添加。一个怪物列表中，不能有重复的玩家
			if(attackPlayers.get(mId).contains(p)) return;
			attackPlayers.get(mId).add(p);
			System.out.println("------------scene.addattackplayer--后--" + attackPlayers.toString());
		}finally {
			lock.unlock();
		}
	}
	/**
	 * 当一个玩家去到别的场景时，怪物就攻击不到，从怪物的攻击列表中删除
	 * 同时也删除技能的持续效果
	 * @param p
	 */
	public void deleteAttackPlayer(Player p) {
		lock.lock();
		try {
			System.out.println(" 当一个玩家去到别的场景时，怪物就攻击不到，从怪物的攻击列表中删除");
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
						break;
					}
					
				}
			}
			//当玩家传送后，清空技能的持续效果
			if(p.getSkillAttack() != null) p.getSkillAttack().cleanup();
			//System.out.println("后 "+attackPlayers.toString() + ", " + monsIds.toString());
			delNullList(monsIds);
		}finally {
			lock.unlock();
		}
	}
	private void delNullList(List<Integer> delMId) {
		for(int i : delMId) {
			attackPlayers.remove(new Integer(i));
		}
	}
	/**
	 * 当怪物被击杀，就不能攻击玩家了，需要删除
	 * @param mId
	 */
	public void deleteAttackMonst(Monster monster) {
		lock.lock();
		try {
			attackPlayers.remove(new Integer(monster.getMonstId()));
		}finally {
			lock.unlock();
		}
	}

	

	/**
	 * 判断当前场景是否有这个目标场景id
	 * @param id
	 * @return
	 */
	public boolean hasTargetSceneId(int sid) {
		System.out.println("当前场景的传送阵：" + tSid2tid.toString() + ", 要传送的目标场景id：" + sid);
		for(int ii : tSid2tid.keySet()) {
			if(ii == sid) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 返回当前场景中的能进入目标场景的传送阵的id
	 * @param targetSId: 目标场景id
	 * @return
	 */
	public int getTelepIdsByTid(int targetSId){  //目标场景id
		return tSid2tid.get(new Integer(targetSId));
	}
	/**
	 * （场景id--传送阵id）
	 * @return
	 */
	public Map<Integer, Integer> getTelepIds(){ //返回
		return tSid2tid;
	}

	/**
	 * 所有当前场景可以传送到的场景
	 * @return
	 */
	public String allTransportableScene() {
		return teleports.toString();
	}
	
	/**
	 * 当前场景是否存在这个怪物
	 * @param mid 怪物id
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
	 * 返回客户端当前场景的所有信息
	 * @param session
	 */
	public void allThing(Session session) {
		StringBuilder sb = new StringBuilder();
		sb.append("所有npc" + getCreatures() + "\n");
		sb.append("所有怪物" + getMonsters() + "\n");
		sb.append("所有玩家" + getPlayers() + "\n");
		//更改了传送的方式
		sb.append("所有可传送目标：" + allTransportableScene());
		session.sendMessage(sb.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//************get,set方法**************
	
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
		lock.lock();
		try{
			return new ArrayList<>(players);
		}finally {
			lock.unlock();
		}
	}

	public void addPlayer(Player player) {
		lock.lock();
		try {
			this.players.add(player);
		}finally {
			lock.unlock();
		}
	}
	
	public List<String> getTeleport() {
		return teleports;
	}

	public void addTeleport(String te, int tid, int sourceId) {  //tid是传送阵id, sourceId是传送阵所在场景id
		teleports.add(te);
		tSid2tid.put(sourceId, tid);
	}


	public String getName() {
		return name;
	}
	public void setName(String n) {
		this.name = n;
	}

	

}
