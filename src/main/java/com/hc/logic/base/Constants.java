package com.hc.logic.base;

import com.hc.logic.creature.Player;

public enum Constants {

	GOLD("gold", "���"){
		@Override
		public void getReword(Player player, String goodName, String amount) {
			int am = Integer.parseInt(amount);
			player.addGold(am);
		}
	},
	EXP("exp", "����"){
		@Override
		public void getReword(Player player, String goodName, String amount) {
			player.addExp(Integer.parseInt(amount));
		}
	},
	GOOD("good", "��Ʒװ��"){
		@Override
		public void getReword(Player player, String goodId, String amount) {
			//��Ʒ�������еĸ�ʽ����Ʒid��������
			player.addGoods(Integer.parseInt(goodId), Integer.parseInt(amount));
		}
	};
	
	private String key;
	private String value;
	
	private Constants(String k, String v) {
		this.key = k;
		this.value = v;
	}
	
	/**
	 * ������䣬��ý���
	 */
	public static void doReword(Player player, String consName, String consVal) {
		//���е���Ʒװ�������ø�ʽ����Ʒid������
		if(Character.isDigit(consName.charAt(0))) {
			for(Constants constants : Constants.values()) {
				if(constants.getKey().equals("good")) {
					constants.getReword(player, consName, consVal);
					return;
				}
			}
		}
		
		for(Constants constants : Constants.values()) {
			if(constants.getKey().equals(consName)) {
				constants.getReword(player, consName, consVal);
				break;
			}
		}
	}
	
	public abstract void getReword(Player player, String goodName, String amount);
	
	
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
