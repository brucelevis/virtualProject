package com.hc.logic.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.frame.OnlinePlayer;
import com.hc.frame.Scene;
import com.hc.logic.config.CopysConfig;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.NpcConfig;
import com.hc.logic.config.SceneConfig;
import com.hc.logic.config.TelepConfig;
import com.hc.logic.copys.Copys;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;
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
  //
	//���г�������sceneId, scene�������ڶ��Ǵ������ļ��м���
	private  Map<Integer, Scene> sceneResource = new HashMap<>();
	//���и�����(sceneId, (playerid, copys))��ͨ��ĳ�������е�player��id����Ψһȷ��һ��������
	private Map<Integer, Map<Integer, Copys>> allCopys = new HashMap<>();
	//����ע�����ң�
	private List<Player> allRegisteredPlayer = new ArrayList<>();
	//�������ʵ�壬������ʱ�������ݿ����
	private List<PlayerEntity> allPlayerEntity = new ArrayList<>();
	//��������̵߳ı�ʶ����ʽ��key��copys+����id+���id, value: ��Ӧ��future
	ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<>();
	
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
		String hql = "from PlayerEntity";
		allPlayerEntity = new PlayerDaoImpl().find(hql);
		
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
	 * bornPlace��VillageOfFrashman��Ӳ���볡������ɾ����
	 */
	public void configAllScene() {
	    SceneParse sceneP = context.getBean("sceneParse", SceneParse.class);
		List<SceneConfig> sParseList = sceneP.getAllSceneConfig();
		Scene scene = null;
		for(SceneConfig sConfig : sParseList) {
			scene = new Scene();
			scene.setId(sConfig.getSceneId());
			scene.setName(sConfig.getName());
			scene.setDescribe(sConfig.getDescription());
			//����creature
			for(int i : sConfig.getMonsts()) { //����
				MonstParse mp = sceneP.getMonsters();
				MonstConfig mc = mp.getMonstConfgById(i);
				scene.addCreatures(mc);
			}
			
			for(int i: sConfig.getNpcs()) {  //npc
				NpcParse mp = sceneP.getNpcs();
				NpcConfig mc = mp.getNpcConfigById(i);  //��Ҫ��NpcParse�����ӷ���
				scene.addCreatures(mc);  
			}
			
			//���ô�����
			for(int i : sConfig.getTeleports()) {
				TelepParse tp = sceneP.getTeleps();
				TelepConfig tc = tp.getTelepConfigById(i);
				scene.addTeleport(tc.getDescription()); //�����д�ŵ�ֻ�д����������
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
	 * ����һ������������
	 * @param copyId
	 * @param player
	 */
	public void createCopy(int copyId, Player player, int bossIndex) {
		List<Player> players = new ArrayList<>();
		players.add(player);
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
			boolean hasdel = delCopys(copyId, p);
			if(!hasdel) continue;
			return;
		}
	}
	/**
	 * ͨ������id����ң�ɾ����Ӧ�ĸ���, ����
	 * @param copyId
	 * @param player
	 * @return
	 */
	public boolean delCopys(int copyId, Player player) {
		Map<Integer, Copys> pCopy = allCopys.get(new Integer(copyId));
		if(pCopy == null) return false;
		Copys copy = pCopy.remove(new Integer(player.getId()));
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
	 * ���ݸ���id�����id�������Ӧ��Copys
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
		Future future = futureMap.remove("copys"+copyId+player.getId());
		if(future == null) return;	
		for(int bossId : Context.getCopysParse().getCopysConfById(copyEntity.getCopyId()).getBosses()) {
			delBossThread(player.getId(), bossId);
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

	public Scene getSceneById(int sceneId) {
		return sceneResource.get(sceneId);
	}
	
	public void addSceneResource(int sceneId, Scene scene) {
		sceneResource.put(sceneId, scene);
	}
	public Player getPlayerByName(String name) {
		for(Player player : allRegisteredPlayer) {
			if(player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	/**
	 * ͨ�����id���player
	 * @param id
	 * @return
	 */
	public Player getPlayerById(int id) {  
		for(Player player : allRegisteredPlayer) {
			if(player.getId() == id){
				return player;
			}
		}
		return null;
	}
	public void addAllRegisteredPlayer(Player player) {
		this.allRegisteredPlayer.add(player);
	}
	public List<Player> getPlayers(){
		return allRegisteredPlayer;
	}

	public void addPlayerEntity(PlayerEntity playerEntity) {
		this.allPlayerEntity.add(playerEntity);
	}

	public PlayerEntity getPlayerEntityByName(String name) {
		for(PlayerEntity p : allPlayerEntity) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	
	/**
	 * ������ݿ��У�����id
	 * �Ӷ��ڷ��������رգ�������ʱ�򣬻��һ��Ŀǰ����id��������ע��������
	 */
	public int getMaxId() {
		int result = 0;
		for(PlayerEntity p : allPlayerEntity) {
			if(p.getId() > result)
				result = p.getId();
		}
		return result + 1;
	}

	public ConcurrentHashMap<String, Future> getFutureMap() {
		return futureMap;
	}

	
	
	
}
