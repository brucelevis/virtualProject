package com.hc.logic.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.config.MonstConfig;
import com.hc.logic.config.NpcConfig;
import com.hc.logic.config.SceneConfig;
import com.hc.logic.config.TelepConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
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
public class World {

	//���г�������sceneId, scene�������ڶ��Ǵ������ļ��м���
	private  Map<Integer, Scene> sceneResource = new HashMap<>();
	//����ע�����ң�
	private List<Player> allRegisteredPlayer = new ArrayList<>();
	//�������ʵ�壬������ʱ�������ݿ����
	private List<PlayerEntity> allPlayerEntity = new ArrayList<>();
	//���е���Ʒ������װ��
	//private List<GoodsEntity> allGoodsEntity = new ArrayList<>();
	//���е���ҵ�����װ�����������ڱ����У����Ǵ�����
	//private List<Equip> allEquip = new ArrayList<>();
	

	
	/**
	 * ֻ����һ��Worldʵ��
	 */
	private static World instance = new World();
	private World() {
		init();
	}
	public static World getInstance() {
		return instance;
	}
	
	/**
	 * ��ʼ���������ڷ���������ʱ���ã�
	 */
	private void init() {
		String hql = "from PlayerEntity";
		allPlayerEntity = new PlayerDaoImpl().find(hql);
		
		//allGoodsEntity = new PlayerDaoImpl().find("from GoodsEntity");
		
		
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
		SceneParse sceneP = Context.getSceneParse();
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
	

	
	
	
	public Scene getSceneById(int sceneId) {
		return sceneResource.get(sceneId);
	}

	public Map<Integer, Scene> getSceneResource() {
		return sceneResource;
	}

	public void addSceneResource(int sceneId, Scene scene) {
		sceneResource.put(sceneId, scene);
		//System.out.println("******" + scene.getName() + " && " + scene.getId() + " ** " + scene.getDescribe());
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
		System.out.println("getPlayerByid ---" + allRegisteredPlayer.toString());
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
	
}
