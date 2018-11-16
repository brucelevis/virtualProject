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
			Element cop = document.getRootElement(); //获得<scenes>
			Iterator sceneIt = cop.elementIterator();
			
			achieveList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				achieve = new AchieveConfig();
				Element sceneElement = (Element)sceneIt.next(); //获得<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//遍历<scene>标签的属性
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //获得scene id
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
					}else if(nodeName.equals("charac")) {
						achieve.setCharac(child.getStringValue());
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
	 * 根据成就配置id获得相应的成就配置
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
	 * 根据成就配置的charac字段获得相应的成就配置
	 * 这个字段也是成就实体中的字段
	 * @param charac
	 * @return
	 */
	public AchieveConfig getAchieveConfigByCharac(String charac) {
		for(AchieveConfig ac : achieveList) {
			if(ac.getCharac().equals(charac)) {
				return ac;
			}
		}
		return null;
	}


}
