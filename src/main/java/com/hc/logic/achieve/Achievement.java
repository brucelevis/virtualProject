package com.hc.logic.achieve;

import java.util.List;

import com.hc.frame.Context;
import com.hc.logic.config.AchieveConfig;
import com.hc.logic.config.GoodsConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.AchieveEntity;

public enum Achievement {

	KILLM("KILLM", "ɱ��"){
		@Override
		public void achieve(Player player, int args) {
			int id = args;  //��Ҫ�������id
			player.getPlayerTasks().monstRecord(id);  //��¼��һ�ɱ�Ĺ���,��ɱ����
			List<AchieveConfig> achieves = Context.getAchieveParse()
					                       .getAchieveConfigByType(KILLM.ordinal()+1);
			for(AchieveConfig ac : achieves) {
				if(ac.getSid() == args) {
					//���³ɾͽ���
					int amount = player.getPlayerAchieves().achieveProgress(ac.getId());
					if(ac.getNum() <= amount) {  //��֤�Ƿ��ɳɾ�
						player.getPlayerAchieves().isComplete(ac.getId());
					}
				}
			}			
		}
	},
	LEVEL("level", "�ȼ�"){
		@Override
		public void achieve(Player player, int args) {
			
		}
	},
	NPC("npc", "npc���"){
		@Override
		public void achieve(Player player, int para) {
			if(para == 1) {//��Ҫ����npc id
				
			}
		}
	},
	EQUIP("equip", "װ�����"){
		@Override
		public void achieve(Player player, int para) {
			GoodsConfig goodsConfig = Context.getGoodsParse().getGoodsConfigById(para);//����װ��id			
			List<AchieveConfig> achieves = Context.getAchieveParse()
                    .getAchieveConfigByType(4);
			PlayerAchieves playerAchieve = player.getPlayerAchieves();
			for(AchieveConfig ac : achieves) {
				if(playerAchieve.isAchieveComplet(ac.getId())) return;
				if(ac.getDtype() == 1) {  //��Ʒװ������
					int num = -1;
					if(goodsConfig.getAttack() > ac.getSid()) {
						num = playerAchieve.achieveProgress(ac.getId());
					}
					if(ac.getNum() < num) {
						playerAchieve.isComplete(ac.getId());
					}
				}else if(ac.getDtype() == 2) { //װ���ȼ�
					
				}
			}							
		}
	},
	COPYS("copys", "�������"){
		@Override
		public void achieve(Player player, int para) {
			System.out.println("��ɵĸ���id��" + para);
			player.getPlayerTasks().copyRecord(para);  //��Ҫ������ɵĸ���id�� ����			
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
			
		}
	},
	PARTY("party", "�������"){
		@Override
		public void achieve(Player player, int para) {
			
		}
	},
	PK("pk", "pk���"){
		@Override
		public void achieve(Player player, int para) {
			
		}
	},
	GOLD("gold", "������"){
		@Override
		public void achieve(Player player, int para) {
			if(player.getGold() > 500) {  //��ҳ���500����ɳɾ�
				
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
