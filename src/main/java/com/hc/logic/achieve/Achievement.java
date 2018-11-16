package com.hc.logic.achieve;

import com.hc.frame.Context;
import com.hc.logic.config.GoodsConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.AchieveEntity;

public enum Achievement {

	KILLM("KILLM", "ɱ��"){
		@Override
		public void achieve(Player player, int args) {
			int id = args;  //��Ҫ�������id
			player.getPlayerTasks().monstRecord(id);  //��¼��һ�ɱ�Ĺ���
			if(id == 2) {
				int num = player.getPlayerEntity().getAchieveEntity().getAtm2_5();
				player.getPlayerEntity().getAchieveEntity().setAtm2_5(num+1);
				if(num != -1 && (num+1) == 5) {
					//�ɾʹ��
					player.getPlayerEntity().getAchieveEntity().setAtm2_5(-1);
				}
			}
		}
	},
	LEVEL("level", "�ȼ�"){
		@Override
		public void achieve(Player player, int args) {
			if(player.getLevel() == 2) {
				AchieveEntity ae = player.getPlayerEntity().getAchieveEntity();
				if(ae.getLevl2() != -1) {
					ae.setLevl2(-1);
				}
			}
		}
	},
	NPC("npc", "npc���"){
		@Override
		public void achieve(Player player, int para) {
			if(para == 1) {//��Ҫ����npc id
				player.getAchieveEntity().setNpc1(-1);
			}
		}
	},
	EQUIP("equip", "װ�����"){
		@Override
		public void achieve(Player player, int para) {
			GoodsConfig gcf = Context.getGoodsParse().getGoodsConfigById(para);//����װ��id
			if(gcf.getAttack() > 100) {//������������100�ĳ�Ϊ��Ʒװ��
				int nume = player.getAchieveEntity().gettEquip5();
				player.getAchieveEntity().settEquip5(nume+1);
				if(nume+1 == 5) {  //��5����Ʒװ�����ɾʹ��
					player.getAchieveEntity().settEquip5(-1);
				}
				return;
			}
			
				
		}
	},
	COPYS("copys", "�������"){
		@Override
		public void achieve(Player player, int para) {
			player.getPlayerTasks().copyRecord(para);  //��Ҫ������ɵĸ���id
			if(para == 1) { //��Ҫ���븱��id
				player.getAchieveEntity().setCopy1(-1);
			}
		}
	},
	SOCIAL("social", "�罻���"){
		@Override
		public void achieve(Player player, int para) {
			
		}
	},
	GROUP("group", "������"){
		@Override
		public void achieve(Player player, int para) {
			player.getAchieveEntity().setGroup1(-1);
		}
	},
	PARTY("party", "�������"){
		@Override
		public void achieve(Player player, int para) {
			player.getAchieveEntity().setParty1(-1);
		}
	},
	PK("pk", "pk���"){
		@Override
		public void achieve(Player player, int para) {
			player.getAchieveEntity().setPk1(-1);
		}
	},
	GOLD("gold", "������"){
		@Override
		public void achieve(Player player, int para) {
			if(player.getGold() > 500) {  //��ҳ���500����ɳɾ�
				player.getAchieveEntity().setGold500(-1);
			}
		}
	};
	
	private String key;
	private String desc;
	
	private Achievement(String k, String v) {
		this.key = k;
		this.desc = v;
	}
	
	/**
	 * ��֤�Ƿ��ɳɾ�
	 * @param player
	 * @param type
	 * @param aid, 
	 */
	public static void getService(Player player, String type, int aid) {
		for(Achievement ac : Achievement.values()) {
			if(ac.getKey().equals(type)) {
				ac.achieve(player, aid);
			}
		}
		
	}
	
	public abstract void achieve(Player player, int args);
	
	public String getKey() {
		return key;
	}
	
	public String getDesc() {
		return desc;
	}
	
	
}
