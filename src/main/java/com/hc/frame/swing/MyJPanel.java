package com.hc.frame.swing;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class MyJPanel extends JPanel{
	private JButton button;
	private JPanel panel;
	private JTextField in;
	private JTextArea out;
	private JScrollPane scro;
	
	//������Ӧ��action����Ҳ����ʾ�Ĺؼ�
	CommandAction action;

	public MyJPanel() {
		setLayout(new BorderLayout());
		
		//�����
		out = new JTextArea();
		out.setEditable(false);
		//out.append("�����������IP��ַ���˿ںţ��Կո���");
		scro = new JScrollPane(out);  //ֻ���ṩһ�����Գ��ֹ��������
		
		
		add(scro, BorderLayout.CENTER); //������������м�
		
		//��ť
		button = new JButton("����");
		action = new CommandAction(this);
		button.addActionListener(action); //Ϊ��ť��Ӽ�����
		
		//������
		in = new JTextField();
		in.setEditable(true);
		
		//�ڶ���panel�������������Ͱ�ť
		panel = new JPanel();
		panel.setLayout(new GridLayout(1,2));
		panel.add(in);
		panel.add(button);
		
		add(panel, BorderLayout.SOUTH);  //���ڶ���panel���ڵ���
		
	}
	

	public JTextField getIn() {
		return in;
	}

	public void setIn(JTextField in) {
		this.in = in;
	}

	public JTextArea getOut() {
		return out;
	}

	public void setOut(JTextArea out) {
		this.out = out;
	}


	public CommandAction getAction() {
		return action;
	}



}
