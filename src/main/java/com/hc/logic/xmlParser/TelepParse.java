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

import com.hc.logic.config.TelepConfig;


@Component
public class TelepParse implements ParseXml{

	private List<TelepConfig> telepList = null;
	private TelepConfig telep = null;

	public TelepParse() {
		
		File file = new File("config/teleports.xml");
		parse(file);
	}
	

	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element teleps = document.getRootElement(); //���<scenes>
			Iterator sceneIt = teleps.elementIterator();
			
			telepList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				telep = new TelepConfig();
				Element sceneElement = (Element)sceneIt.next(); //���<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//����<scene>��ǩ������
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //���scene id
						telep.setTeleId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("description")) {
						telep.setDescription(child.getStringValue());
					}
				}
				
				telepList.add(telep);
				telep = null;
				
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}

	
	public TelepConfig getTelepConfigById(int id) {
		for(TelepConfig tc : telepList) {
			if(tc.getTeleId() == id) {
				return tc;
			}
		}
		return null;
	}
	
}
