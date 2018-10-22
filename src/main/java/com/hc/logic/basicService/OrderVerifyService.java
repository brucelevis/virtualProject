package com.hc.logic.basicService;

public class OrderVerifyService {

	/**
	 * �����������ַ��������������ע�ᡢ��½
	 * @param args
	 * @return
	 */
	public static boolean twoString(String[] args) {
		if(args.length < 3 || args.length > 3) return false;
		return true;
	}
	
	/**
	 * �������������������ֵ�����
	 * @param args
	 * @return
	 */
	public static boolean twoInt(String[] args) {
		if(args.length < 3 || args.length > 3) return false;
		if(!isDigit(args[1])) return false;
		if(!isDigit(args[2])) return false;
		return true;
	}
	
	/**
	 * ����ֻ��һ�����ֲ���������
	 * @param args
	 * @return
	 */
	public static boolean ontInt(String[] args) {
		if(args.length < 2 || args.length > 2) return false;
		if(!isDigit(args[1])) return false;
		return true;
	}

	/**
	 * ����û�в���������
	 * @param args
	 * @return
	 */
	public static boolean noPara(String[] args) {
		if(args.length > 1) return false;
		return true;
	}
	
	/**
	 * �ж�һ���ַ����Ƿ����������
	 * @param s
	 * @return
	 */
	public static boolean isDigit(String s) {
		for(int i = s.length(); --i >=0;) {
			if(!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
