package com.hc.logic.base;

import com.hc.logic.creature.Player;

import io.netty.channel.Channel;

public class Session {
	
	//��½��֤
	private int check;
	
	private Player player;

	private  Channel channel;
	
	public Session() {
		this.check = 1;
	}
	
	
	/**
	 * ������Ϣ
	 * ���з���˷��͵��ͻ��˵���Ϣ�����������
	 */
	public void sendMessage(String message) {
		channel.writeAndFlush(message);
	}
	
	
	
	
	
	
	
	
	
	public void addCheck() {
		check++;
	}
	
	public int getCheck() {
		return check;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}


	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
	}
	

	@Override
	public String toString() {
		return "session: " + channel.toString();
		
	}

}
