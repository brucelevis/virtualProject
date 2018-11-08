package com.hc.logic.order;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Login;
import com.hc.logic.base.Register;
import com.hc.logic.base.Session;
import com.hc.logic.base.Teleport;
import com.hc.logic.basicService.EnterWorld;
import com.hc.logic.basicService.MonsterService;
import com.hc.logic.basicService.NpcService;
import com.hc.logic.basicService.OrderVerifyService;
import com.hc.logic.basicService.SkillService;
import com.hc.logic.basicService.TransferService;
import com.hc.logic.creature.Player;

import io.netty.channel.Channel;

/**
 * �����ͨ��ö�ٵķ�ʽ����������������ַ�����ͬ��ҵ���߼���
 * @author hc
 *
 */
public enum Order {

	REGISTER("register", "ע��"){
		//���������ע������ַ���ע���߼���
		@Override
		public void doService(String[] args, Session session) {
			//�������������û���������
			if(!OrderVerifyService.twoString(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			String playerName = args[1];
			String password = args[2];
			new Register(playerName, password).register(session);;
		}
	},
	LOGIN("login", "��½"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoString(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			new Login(args[1], args[2]).login(session);
		}
	},
	ENTERWORLD("enterWorld", "��������"){
		@Override
		public void doService(String[] args, Session session) {
			new EnterWorld().enterWorld(session);;
		}
	},
	MAPINFORMATION("mapInfo", "��ǰ���ĸ���ͼ"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			Scene scene = session.getPlayer().getScene();
			session.sendMessage(scene.getDescribe());
		} 
	},
	EVERYTHINGINTHISMAP("allthing", "���ص�ǰ��ͼ�����ж���"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			Scene scene = session.getPlayer().getScene(); 
			scene.allThing(session);
		} 
	},
	TRANSFER("transfer", "���д���"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			int targetId = Integer.parseInt(args[1]);
			int sceneId = session.getPlayer().getSceneId();
			Context.getTransferService().allTransfer(targetId, sceneId, session);	
		} 
	},
	NPCTALK("npcTalk", "��npc�Ի�"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			NpcService npcS = Context.getNpcService();
			int nId = Integer.parseInt(args[1]);
			//��֤npc�Ƿ���ͬһ���������ڸ�����ʱҲû�����npc
			if((session.getPlayer().getSceneId() == 0) || !npcS.isOnScene(session, nId)) {
				session.sendMessage("û�����npc");
				return;
			}
			npcS.introduce(session, nId);
			npcS.task(session, nId);
		}
	},
	DMONST("dMonst", "��ù�����ϸ��Ϣ"){
		@Override
		public void doService(String[] args, Session session) {	
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			Context.getMonsterService().mDescribe(session, Integer.parseInt(args[1]));;
		}
	},
	ALLSKILL("allSkill", "���м���"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			SkillService skillSer = Context.getSkillService();
			skillSer.getAllSkill(session);
		}
	},
	ATTACKM("attackM", "��������"){
	    @Override
	    public void doService(String[] args, Session session) {
	    	if(!OrderVerifyService.twoInt(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
	    	SkillService skillSer = Context.getSkillService();
	    	skillSer.doAttack(session, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
	    }
	},
	PSTATE("pState", "���״̬"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			session.getPlayer().pState();
		}
	},
	BAG("bag", "�鿴����"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			session.getPlayer().getBagService().dispBag(session);
		}
	},
	ADDGOOD("addGood", "�����Ʒ"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			int gId = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			boolean inserted = session.getPlayer().addGoods(gId, amount);
			if(inserted) {
				session.sendMessage("�����Ʒ�ɹ�");
				return;
			}
			
		}
	},
	DELGOOD("delGood", "ɾ����Ʒ"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			int gId = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			boolean deleted = session.getPlayer().delGoods(gId, amount);
			if(deleted) session.sendMessage("ɾ���ɹ�");
			else session.sendMessage("ɾ��ʧ�ܣ������Ƿ�����ô����Ʒ");
		}
	},
	EQUIP("equip", "����װ��"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			session.getPlayer().addEquip(Integer.parseInt(args[1]));
		}
	},
	DEQUIP("dEquip", "ж��װ��"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			session.getPlayer().deletEquip(Integer.parseInt(args[1]));
		}
	},
	ALLEQUIP("allEquip", "�����Ѵ���װ��"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			String equips = Context.getGoodsService().allEquips(session.getPlayer().getPlayerEntity());
			session.sendMessage(equips);
		}
	},
	LSKILL("lSkill", "ѧϰ����"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			boolean learnIt = session.getPlayer().addSkill(Integer.parseInt(args[1]));
			if(learnIt) session.sendMessage("ѧϰ���ܳɹ�");
		}
	},
	USEGOOD("useGood", "ʹ�ûָ�����Ʒ"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			int gId = Integer.parseInt(args[1]);
			boolean used = session.getPlayer().addRecoverHpMp(gId);
			if(used) session.sendMessage("ʹ�óɹ�");
			else session.sendMessage("ʹ��ʧ�ܣ����ܲ�����ҩƷ�����ߴ�ҩƷ������");
		}
	},
	ECOPY("eCopy", "������븱��"){
		@Override 
		public void doService(String[] args, Session session) {
			//����Ĭ����ֻ��һ����ҽ��븱��
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			int copyId = Integer.parseInt(args[1]);
			//�ж��Ƿ���Խ��븱����
			if(!Context.getCopyService().canEnterCopy(session.getPlayer())) return;
			boolean entered = Context.getCopyService().enterCopy(copyId, session.getPlayer(), session, 0);		
		}
	},
	STORE("store", "��ѯ�̵���Ʒ"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			int page = Integer.parseInt(args[1]);
			Context.getStoreService().lookStore(session, page);
		}
	},
	BUY("buy", "������Ʒ"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			int gid = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			Context.getStoreService().validBuyGood(session, gid, amount);
		}
	},
	ALLCHAT("allchat", "ȫ������"){
		@Override 
		public void doService(String[] args, Session session) {
			Context.getChatService().decOrder(session, args);
		}
	},
	CHAT("chat", "˽��"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.threePara(args)) {
				session.sendMessage("������ʽ����ȷ������������");
				return;
			}
			//Ŀ����ҵ�id
			int tPlaId = Context.getWorld().getPlayerEntityByName(args[1]).getId();
			Context.getChatService().privateChat(session, tPlaId, args);
		}
	},
	EMAIL("email", "����ϵͳ"){
		@Override 
		public void doService(String[] args, Session session) {
			Context.getEmailService().descOrder(session, args);
		}
	},
	PK("pk", "����pk"){
		@Override 
		public void doService(String[] args, Session session) {
			Context.getPkService().desOrder(session, args);
		}
	},
	ATTACKP("attackP", "�������"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoString(args) || !OrderVerifyService.isDigit(args[1])){
				session.sendMessage("�����������ȷ");
				return;
			}
			int skillId = Integer.parseInt(args[1]);
			Context.getSkillService().attackPlayer(session, skillId, args[2]);
		}
	},
	PARTY("group", "�������"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.threePara(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			Context.getParty().desOrder(session, args);
		}
	};
	
	
	
	private String key;
	private String value;

	
	private Order(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * 
	 * @param st ��һ������������������ǲ�����������Ҳ����û��
	 */
	public static void getService(String[] st, Session session) {
		//System.out.println("--order--: " + session);
		//���û�е�½����ô��Ҿ�û����������б��Ͳ����ñ��ָ��
		//��Щ������session�洢״̬��ʵ�֡�
		System.out.println("getService");
		boolean isLoged = true;
		if(!st[0].equals("login") && !st[0].equals("register")) {	
		    Player pp = session.getPlayer();
		    if(pp == null) {
		    	session.sendMessage("���½");
		    	return;
		    }
		    isLoged = false;
		    
			for(Player p : Context.getOnlinPlayer().getOnlinePlayers()) {
				if(p.getName().equals(pp.getName())) {
					isLoged = true;
					break;
				}
				
			}

		}
		
		
		if(st[0].equals("register")) isLoged = true;
		
		if(!isLoged) {
			session.sendMessage("���½");
			return;
		}
		
		
		
		for(Order or : Order.values()) {	
			if(st[0].equals(or.getKey())) {
				or.doService(st, session);
				break;
			}
		}
		//session.sendMessage("û��������");
		//System.out.println("getService��");
	}
	
	public abstract void doService(String[] args, Session session);

	
	
	
	
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



}
