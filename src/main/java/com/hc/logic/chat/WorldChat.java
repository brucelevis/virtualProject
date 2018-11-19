package com.hc.logic.chat;

import java.util.Vector;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;

@Component
public class WorldChat {

	//һ����С�������¼��ֻ��¼���µ���ʮ����¼��������ɾ����ɵļ�¼
	private Vector<String> records = new Vector<>(30);
	
	/**
	 * ��������¼�����֮��Ҫ���͸����н���������������
	 * @param msg
	 */
	synchronized public void addRecord(String msg) {
		if(records.size() < 30) {
			records.add(0, msg);
		}else {
			records.remove(records.size()-1);
			records.add(0, msg);
		}
		//֪ͨ���н�������Ƶ�������
		Context.getWorldChatObservable().setMsg(msg);
	}
	
	
	//�õ����µ�һ����¼
	public String getupdate() {
		return records.get(0);
	}
	
	/**
	 * ��õ�ǰ���������¼
	 * @return
	 */
	synchronized public Vector<String> getRecords(){
		return new Vector<>(records);
	}
	
}
