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
import org.springframework.stereotype.Component;

import com.hc.logic.config.AchieveConfig;
import com.hc.logic.config.CopysConfig;

@Component
public class AchieveParse implements ParseXml{

	private List<AchieveConfig> achieveList = null;
	private AchieveConfig achieve = null;

	public AchieveParse() {
		File file = new File("config/achieve.xml");
		parse(file);
	}
	
	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element cop = document.getRootElement(); //���<scenes>
			Iterator sceneIt = cop.elementIterator();
			
			achieveList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				achieve = new AchieveConfig();
				Element sceneElement = (Element)sceneIt.next(); //���<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//����<scene>��ǩ������
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //���scene id
						achieve.setId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						achieve.setName(child.getStringValue());
					}else if(nodeName.equals("desc")) {
						achieve.setDesc(child.getStringValue());
					}else if(nodeName.equals("type")) {
						achieve.setType(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("dtype")) {
						achieve.setDtype(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("sid")) {
						achieve.setSid(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("num")) {
						achieve.setNum(Integer.parseInt(child.getStringValue()));
					}
				}
				
				achieveList.add(achieve);
				
				achieve = null;
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * ���ݳɾ�����id�����Ӧ�ĳɾ�����
	 * @param aid
	 * @return
	 */
	public AchieveConfig getAchieveConfigByid(int aid) {
		for(AchieveConfig ac : achieveList) {
			if(ac.getId() == aid)
				return ac;
		}
		return null;
	}
	
	/**
	 * ���ݳɾ����õ����ͻ��������Ӧ�ĳɾ�����
	 * @param charac
	 * @return
	 */
	public List<AchieveConfig> getAchieveConfigByType(int type) {
		List<AchieveConfig> typeAchie = new ArrayList<>();
		for(AchieveConfig ac : achieveList) {
			if(ac.getType() == type) {
				typeAchie.add(ac);
			}
		}
		return typeAchie;
	}


}
