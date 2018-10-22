package com.hc.logic.xmlParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hc.frame.Scene;
import com.hc.logic.config.SceneConfig;



/**
 * �������ó��������ļ���
 * �����������ص������ļ�������������Ӧ�Զ���
 * @author hc
 *
 */
public class SceneParse implements ParseXml{
	//���������ļ��еĳ�������
	private List<SceneConfig> sceneList = null;
	private SceneConfig scene = null;
	//���г����������Ѿ���ʼ��
	//private List<Scene> allScene = new ArrayList<>();
	
	MonstParse monsters;
	NpcParse npcs;
	TelepParse teleps;


	public SceneParse() {
		File file = new File("config/scenes.xml");
		parse(file);
		init();
	}
	
	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element scenes = document.getRootElement(); //���<scenes>
			Iterator sceneIt = scenes.elementIterator();
			
			sceneList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				scene = new SceneConfig();
				Element sceneElement = (Element)sceneIt.next(); //���<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//����<scene>��ǩ������
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //���scene id
						scene.setSceneId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						scene.setName(child.getStringValue());
					}else if(nodeName.equals("description")) {
						scene.setDescription(child.getStringValue());
					}else if(nodeName.equals("monst")) {
						scene.setMonst(child.getStringValue());
					}else if(nodeName.equals("npc")) {
						scene.setNpc(child.getStringValue());
					}else if(nodeName.equals("teleport")) {
						scene.setTeleport(child.getStringValue());
					}
				}
				
				sceneList.add(scene);
				
				//��Ҫ��String���͵�id����ת��Ϊint����
				scene.parseString();
				scene = null;
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * ���ع��npc��������������ļ�
	 */
	public void init() {
		monsters = new MonstParse();
		npcs = new NpcParse();
		teleps = new TelepParse();
		
		monsters.parse(new File("config/monst.xml"));
		npcs.parse(new File("config/npc.xml"));
		teleps.parse(new File("config/teleports.xml"));
		
		
	}
	
	
	
	
	
	/**
	 * ͨ������id��ó���
	 */
	public SceneConfig getSceneById(int sceneId) {
		for(SceneConfig sConfig : sceneList) {
			if(sConfig.getSceneId() == sceneId) {
				return sConfig;
			}
		}
		return null;
	}
	
	/**
	 * ������г�������
	 */
	public List<SceneConfig> getAllSceneConfig(){
		return sceneList;
	}

	
	
	public MonstParse getMonsters() {
		return monsters;
	}

	public NpcParse getNpcs() {
		return npcs;
	}

	public TelepParse getTeleps() {
		return teleps;
	}

	
	
	
}
