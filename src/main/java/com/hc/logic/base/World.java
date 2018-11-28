package com.hc.logic.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hc.frame.Context;
import com.hc.frame.OnlinePlayer;
import com.hc.frame.Scene;
import com.hc.logic.config.CopysConfig;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.NpcConfig;
import com.hc.logic.config.SceneConfig;
import com.hc.logic.config.TelepConfig;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.EmailEntity;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
import com.hc.logic.domain.UnionEntity;
import com.hc.logic.xmlParser.MonstParse;
import com.hc.logic.xmlParser.NpcParse;
import com.hc.logic.xmlParser.SceneParse;
import com.hc.logic.xmlParser.TelepParse;

/**
 * ����
 * @author hc
 *
 */
@DependsOn(value="sceneParse")
@Component
public class World implements ApplicationContextAware{
  
	//���г�������sceneId, scene�������ڶ��Ǵ������ļ��м���
	private  Map<Integer, Scene> sceneResource = new HashMap<>();
	//���и�����(sceneId, (playerid, copys))��ͨ��ĳ�������е�player��id����Ψһȷ��һ��������
	private Map<Integer, Map<Integer, Copys>> allCopys = new HashMap<>();
	//ע�����ң�
	private List<Player> allRegisteredPlayer = new ArrayList<>();
	//���ʵ�建�档key���������value��ʵ��
	Cache<String, PlayerEntity> cache = CacheBuilder.newBuilder()
		                                             .maximumSize(1000)
		                                             .build();
	
	//��������̵߳ı�ʶ����ʽ��key��copys+����id+���id, value: ��Ӧ��future
	private ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<>();
	//���и���ʵ��
	private List<CopyEntity> copyEntitys = new ArrayList<>();
	//���й���ʵ��
	private List<UnionEntity> unionEntitys = new ArrayList<>();
	
	private Lock lock = new ReentrantLock();
	
	ApplicationContext context;
	
	public World() {
		
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}
	
	/**
	 * ��ʼ����������World bean����ʱ������
	 */
	@PostConstruct
	private void init() {
		//System.out.println("������world��init����");
		//String hql = "from PlayerEntity";
		//allPlayerEntity = new PlayerDaoImpl().find(hql);
		
		String hql1 = "from UnionEntity";
		unionEntitys = new PlayerDaoImpl().find(hql1);
		
		//�����ݿ��м���������ݺ��������id
		int maxId = getMaxId();
		Context.setpID(maxId);
		configAllScene();
	}
	
	
	/**
	 * ��ʼ�����г���,���������ļ��м��س�������
	 * ���صĳ�����������Wolrd���sceneResource�ֶ���
	 * ���س��������ļ�ʱ��ҲҪ���س����еĹ��npc�ʹ������ʵ�塣
	 * 
	 */
	public void configAllScene() {
	    SceneParse sceneP = context.getBean("sceneParse", SceneParse.class);
		List<SceneConfig> sParseList = sceneP.getAllSceneConfig();
		Scene scene = null;
		for(SceneConfig sConfig : sParseList) {
			scene = new Scene(context);
			scene.setId(sConfig.getSceneId());
			scene.setName(sConfig.getName());
			scene.setDescribe(sConfig.getDescription());
			//����creature
			
			for(int i: sConfig.getNpcs()) {  //npc
				NpcParse mp = sceneP.getNpcs();
				NpcConfig mc = mp.getNpcConfigById(i);  //��Ҫ��NpcParse�����ӷ���
				scene.addCreatures(mc);  
			}
			
			//���ô�����
			for(int tid : sConfig.getTeleports()) {
				TelepParse tp = sceneP.getTeleps();
				TelepConfig tc = tp.getTelepConfigById(tid);
				scene.addTeleport(tc.getDescription(), tid, tc.getSceneid()); //�����д�ŵ�ֻ�д����������
			}
			//�����г�����Ϣ����sceneResource�ֶ���
			addSceneResource(scene.getId(), scene);
		}
	}
	
	/**
	 *  ����һ���¸���, ����
	 * @param copyId  ����id
	 * @param players ��Ҫ����˸������������
	 */
	public void createCopy(int copyId, List<Player> players, int bossIndex) {
		CopysConfig copysConfig = Context.getCopysParse().getCopysConfById(copyId);
		Copys nCopys = new Copys(copyId, copysConfig.getName(), copysConfig.getDescription(), players, bossIndex);
	    addCopys(copyId, players.get(0).getId(), nCopys);  //Ĭ����players�ĵ�һ����id��Ϊ��ʶ
	}
	/**
	 * ������Ҷ����ߺ󣬵�һ����������ң���������
	 * @param copyId
	 * @param player
	 * @param bossIndex  
	 * @param sponsId   �����ߵ�id
	 */
	public void creatCopy(int copyId, List<Player> players, int bossIndex, int sponsId) {
		CopysConfig copysConfig = Context.getCopysParse().getCopysConfById(copyId);
		Copys nCopys = new Copys(copyId, copysConfig.getName(), copysConfig.getDescription(), players, bossIndex);
		addCopys(copyId, sponsId, nCopys);
	}
		
	/**
	 * ��������
	 * @param copyId
	 * @param player
	 * @param bossIndex
	 * @param a
	 */
	public void createCopy(int copyId, Player player, int bossIndex) {
		List<Player> players = new ArrayList<>();
		players.add(player);  //��ӷ����� 
		System.out.println("--------------------createCopy--team=" + player.getTeammate());
		if(player.getTeammate().size() > 0) {
			for(String pn : player.getTeammate()) {
				players.add(Context.getOnlinPlayer().getPlayerByName(pn));
			}
		}
		createCopy(copyId, players, bossIndex);
	}

	
	
	/**
	 * v�����������븱��ʱ������һ������
	 * @param copyId    ����id��Ҳ����sceneid
	 * @param playerId  ��Ҫ���븱����ĳ����ҵ���playerId
	 * @param copys
	 */
	private void addCopys(int copyId, int playerId, Copys copys) {
		if(allCopys.containsKey(new Integer(copyId))) {
			allCopys.get(new Integer(copyId)).put(playerId, copys); 
			return;
		}		
		Map<Integer, Copys> cop = new HashMap<>();
		cop.put(playerId, copys);
		allCopys.put(copyId, cop);	
	}
	/**
	 * ͨ������id������б�ɾ����Ӧ�ĸ���, ����
	 * @param copyId
	 * @param players Ҫɾ���ĸ����е��������
	 */
	public void delCopys(int copyId, List<Player> players) {
		for(Player p : players) {
			if(!delCopys(copyId, p.getName())) continue;
			return;
		}
	}
	/**
	 * ͨ������id����ң�ɾ����Ӧ�ĸ���,
	 * ����ɾ�����ʵ��
	 * @param copyId
	 * @param player
	 * @return
	 */
	public boolean delCopys(int copyId, String pname) {
		//System.out.println("------------world.delCopys����ɾ�����������ʵ��");
		int pid = getPlayerEntityByName(pname).getId();
		Map<Integer, Copys> pCopy = allCopys.get(new Integer(copyId));
		if(pCopy == null) return false;
		Copys copy = pCopy.remove(new Integer(pid));
		CopyEntity cpe = delCopyEntity(copy.getId());
		copy = null;
		return true;
	}
	/**
	 * ͨ������id������б������Ӧ�ĸ���
	 * @param copyId  ����id
	 * @param players ����б�
	 * @return
	 */
	public Copys getCopysBy(int copyId, List<Player> players) {
		for(Player p : players) {
			Map<Integer, Copys> pCopy = allCopys.get(new Integer(copyId));
			if(pCopy == null) continue;
			return pCopy.get(new Integer(p.getId()));
		}
		return null;
	}
	/**
	 * ���ݸ���id�����id�������Ӧ��Copys��ֻ�з����ߵ�id���ܵõ����ѵĸ���
	 * @param copyId ����id
	 * @param playerId ���id
	 * @return
	 */
	public Copys getCopysByAPlayer(int copyId, int playerId) {
		Map<Integer, Copys> pCopy = allCopys.get(new Integer(copyId));
		if(pCopy == null) return null;
		return pCopy.get(new Integer(playerId));
	}
	/**
	 * ֹͣ�����߳�
	 * @param player
	 * @return
	 */
	public void delCopyThread(Player player) {
		CopyEntity copyEntity = player.getCopEntity(); 
		if(copyEntity == null) return;
		int copyId = copyEntity.getCopyId();
		int pid = player.getId();
		delCopyThread(pid, copyId);
	}
	
	public void delCopyThread(int pid, int copyId) {
		System.out.println("---------ɾ�������߳�------");
		System.out.println("---------future----" + futureMap.size() + ", " + pid);
		String iden = "copys"+copyId + pid;
		Future future = futureMap.remove(iden);
		System.out.println("------futuremap=" + futureMap.toString());
		System.out.println("=------iden = " + iden);
		System.out.println("---------future-��---" + futureMap.size() + ", " + (future==null));
		if(future == null) return;	
		for(int bossId : Context.getCopysParse().getCopysConfById(copyId).getBosses()) {
			delBossThread(pid, bossId);
		}	
		future.cancel(true);
	}
	/**
	 * ֹͣboss�߳�
	 * @param playerId
	 * @param bossId
	 */
	public void delBossThread(int playerId, int bossId) {
		Future future = futureMap.remove("boss"+bossId+playerId);
		if(future == null) return;
		future.cancel(true);
		System.out.println("boss�߳� pid=" + playerId + ", bossId=" + bossId + " ��ֹͣ");
	}
	/**
	 * ֹͣ�ٻ����߳�
	 * @param playerId
	 * @param bossId
	 */
	public void delSummonsThread(int playerId, int bossId) {
		System.out.println("summon�߳� ǰ--" + futureMap.toString() );
		Future future = futureMap.remove("summon" + bossId + playerId);
		if(future == null) return;
		future.cancel(true);
		System.out.println("summon�߳� pid=" + playerId + ", bossId" + bossId + " ��ֹͣ");
		System.out.println("summon�߳� ֹͣ--" + futureMap.toString() );
	}

	public Scene getSceneById(int sceneId) {
		return sceneResource.get(sceneId);
	}
	
	public void addSceneResource(int sceneId, Scene scene) {
		sceneResource.put(sceneId, scene);
	}
	public Player getPlayerByName(String name) {
		for(Player player : allRegisteredPlayer) {
			if(player.getName().equals(name)) {
				allRegisteredPlayer.remove(player);
				return player;
			}
		}
		return null;
	}
	public void addAllRegisteredPlayer(Player player) {
		this.allRegisteredPlayer.add(player);
	}

	public void addPlayerEntity(PlayerEntity playerEntity) {
		cache.put(playerEntity.getName(), playerEntity);
	}

	public PlayerEntity getPlayerEntityByName(String name) {
		if(cache.getIfPresent(name) != null) {
			return cache.getIfPresent(name);
		}
		String hql = "select pe from PlayerEntity pe where name "
				+ "like : name";
		List<PlayerEntity> pes = new PlayerDaoImpl().find(hql, name);
		if(pes.size() == 1) {
			PlayerEntity pe = pes.get(0);
			String hql1 = "from EmailEntity em where playerName like : name ";
			List<EmailEntity> list = new PlayerDaoImpl().find(hql1, pe.getName());
			pe.setEmails(list);
			cache.put(name, pe);
			return pe;
		}
		return null;
	}

	/**
	 * ���»���
	 * @param pe
	 */
	public void updatePlayerEntity(PlayerEntity pe) {
		cache.put(pe.getName(), pe);
	}
	/**
	 * ������ݿ��У�����id
	 * �Ӷ��ڷ��������رգ�������ʱ�򣬻��һ��Ŀǰ����id��������ע��������
	 */
	public int getMaxId() {
		String hql = "select max(pe.id) from PlayerEntity pe";
		List<Integer> pes = new PlayerDaoImpl().find(hql);
		int result = 0;
		if(pes != null && pes.get(0) != null) {
			result = pes.get(0);
		}
		return result + 1;
	}

	public ConcurrentHashMap<String, Future> getFutureMap() {
		return futureMap;
	}

	public List<CopyEntity> getCopyEntitys() {
		return copyEntitys;
	}
	public void addCopyEntity(CopyEntity ce) {
		this.copyEntitys.add(ce);
	}
	/**
	 * ɾ��copeEntity����
	 * @param cid
	 */
	public CopyEntity delCopyEntity(int cid) {
		for(CopyEntity cpe : copyEntitys) {
			if(cpe.getCopyId() == cid) {
				copyEntitys.remove(cpe);
				return cpe;
			}
		}
		return null;
	}
	public CopyEntity getCopyEntityById(int copyId) {
		for(CopyEntity cpe : copyEntitys) {
			if(cpe.getCopyId() == copyId) {
				return cpe;
			}
		}
		return null;
	}
	
	/**
	 * ���ݹ�������ù���ʵ��
	 * @param name
	 * @return
	 */
	public UnionEntity getUnionEntityByName(String name) {
		lock.lock();
		try {
			for(UnionEntity ue : unionEntitys) {
				if(ue.getName().equals(name)) {
					return ue;
				}
			}
			return null;
		}finally {
			lock.unlock();
		}
	}
	public List<UnionEntity> getUnionEntity(){
		lock.lock();
		try {
			return new ArrayList<>(unionEntitys);
		}finally {
			lock.unlock();
		}
	}
	/**
	 * ��������
	 * @param uname
	 * @param pname
	 * @return
	 */
	public boolean createUnion(String uname, String pname) {
		lock.lock();
		try {
			if(getUnionEntityByName(uname) != null) {
				return false;
			}
			UnionEntity ue = new UnionEntity(uname, pname);
			unionEntitys.add(ue);
			new PlayerDaoImpl().insert(ue);  //�������ݿ�
			return true;
		}finally {
			lock.unlock();
		}
	}
	/**
	 * ͨ������������ɢunion
	 * @param name
	 */
	public void delUnionEntity(String name) {
		lock.lock();
		try {
			for(UnionEntity ue : unionEntitys) {
				if(ue.getName().equals(name)) {
					unionEntitys.remove(ue);
					return;
				}
			}
		}finally {
			lock.unlock();
		}
	}
	
	
	
}
