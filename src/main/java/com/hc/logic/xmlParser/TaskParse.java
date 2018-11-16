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

import com.hc.logic.config.TaskConfig;

@Component
public class TaskParse implements ParseXml{

	private List<TaskConfig> tasksList = null;
	private TaskConfig tasks = null;

	public TaskParse() {
		File file = new File("config/tasks.xml");
		parse(file);
	}
	
	@Override
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element cop = document.getRootElement(); //���<scenes>
			Iterator sceneIt = cop.elementIterator();
			
			tasksList = new ArrayList<>();
			while(sceneIt.hasNext()) {
				tasks = new TaskConfig();
				Element sceneElement = (Element)sceneIt.next(); //���<scene>
				List<Attribute> attributes = sceneElement.attributes();
				//����<scene>��ǩ������
				for(Attribute attribute: attributes) {
					if(attribute.getName().equals("id")) {
						String id = attribute.getValue(); //���scene id
						tasks.setId(Integer.parseInt(id));
					}
				}
				
				Iterator sIt = sceneElement.elementIterator();
				while(sIt.hasNext()) {
					Element child = (Element)sIt.next();
					String nodeName = child.getName();
					if(nodeName.equals("name")) {
						tasks.setName(child.getStringValue());
					}else if(nodeName.equals("type")) {
						tasks.setType(Integer.parseInt(child.getStringValue()));
					}else if(nodeName.equals("need")) {
						tasks.setNeed(child.getStringValue());
					}else if(nodeName.equals("award")) {
						tasks.setAward(child.getStringValue());
					}
				}
				
				tasksList.add(tasks);
				tasks.convert();
				
				tasks = null;
			}
		}catch(DocumentException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��������id�����Ӧ����������
	 * @param tid
	 * @return
	 */
	public TaskConfig getTaskConfigByid(int tid) {
		for(TaskConfig tc : tasksList) {
			if(tc.getId() == tid) {
				return tc;
			}
		}
		return null;
	}

}
