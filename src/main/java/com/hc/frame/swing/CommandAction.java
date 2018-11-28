package com.hc.frame.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.hc.frame.handlers.ClientSender;
import com.hc.frame.MyClient;

import io.netty.channel.Channel;

/**
 * ���������þ��ǻ�����������ָ�
 * �����������ָ��
 * ��ָ����������
 * 
 * @author hc
 *
 */
public class CommandAction implements ActionListener{
	private static MyJPanel myPanel;
	private String playerOrder;  //�û����������
	private String backOrder; //�����������
	private static Channel channel;
	private String playerName = "*****"; 
	private boolean hasConnect = false;
	
	private static CommandAction instance;
	
	public CommandAction(MyJPanel mPanel) {
		this.instance = this;
		this.myPanel = mPanel;
	}
	
	//��������Զ�����
	@Override
	public void actionPerformed(ActionEvent event) {
		//��ȡ�����ָ��
		playerOrder = myPanel.getIn().getText();
		//���������
		myPanel.getIn().setText("");
		//��ָ��ֽ�һ��
		String[] splitOrder = playerOrder.split(" ");
		//���������ͻ���
		System.out.println("�ͻ���");
		
		if(!hasConnect && splitOrder[0].equals("ip")) {
			clientStart(splitOrder);
			return;
		}
		
		//������ʾ����ʾ�û���
		if(splitOrder[0].equals("login")) {
			playerName = splitOrder[1];
		}
		//ֻҪ������ָ���Ҫ��ָ��������
		sendOrderToServer();
		playerOrder = "-----------------" + playerName + "--����ָ��: " + playerOrder;
		//�������������ݼӵ������
		myPanel.getOut().append(playerOrder + "\n"); 
	}
	
	private void clientStart(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				MyClient.start(args[1], Integer.parseInt(args[2]));
			}
		}).start();
		myPanel.getOut().append("���ӷ������ɹ������½��ע��" + "\n"); 
		channel = MyClient.clientChannel;
		hasConnect = true;
	}
	
	/**
	 * ���û���ָ��͸�������
	 */
	public void sendOrderToServer() {
		System.out.println("��ʼ�������ݵ�������");
		channel.writeAndFlush(playerOrder);
	}

	/**
	 * ������˵�������ʾ
	 * Ҫ�ȵ���setBackOrder()�������ٵ������������
	 * @return
	 */
	public static void backDisplay(String msg) {
		myPanel.getOut().append(msg + "\n");
	}
	
	
	public String getPlayerOrder() {
		return playerOrder;
	}

	public void setPlayerOrder(String playerOrder) {
		this.playerOrder = playerOrder;
	}

	public String getBackOrder() {
		return backOrder;
	}

	public void setBackOrder(String backOrder) {
		this.backOrder = backOrder;
	}
	
	public static void setChannel(Channel ch) {
		channel = ch;
	}

	public static CommandAction getInstance() {
		return instance;
	}



}
