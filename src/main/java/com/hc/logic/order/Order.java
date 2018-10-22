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
			//��½ʱ����������û���������
			//System.out.println("������login");
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
			session.sendMessage("��������" + scene.getCreatures());
			session.sendMessage("�������" + scene.getPlayers() + "\n");
			//�����˴��͵ķ�ʽ
			session.sendMessage("���пɴ���Ŀ�꣺" + scene.allTransportableScene());
		} 
	},
	TRANSFER("transfer", "���д���"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			int sceneId = session.getPlayer().getSceneId();
			Teleport t = new TransferService();
			int tSceneId = Integer.parseInt(args[1]);
			if(!Context.getWorld().getSceneById(sceneId).hasTelepId(tSceneId)) {
				session.sendMessage("û����������󣬲��ܴ���");
				return;
			}
			t.transfer(session.getPlayer(), sceneId, tSceneId);
			session.sendMessage("��ӭ����" + Context.getWorld().getSceneById(tSceneId).getName());
		} 
	},
	NPCTALK("npcTalk", "��npc�Ի�"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.ontInt(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			NpcService npcS = new NpcService();
			int nId = Integer.parseInt(args[1]);
			//��֤npc�Ƿ���ͬһ����
			if(!npcS.isOneScene(session, nId)) {
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
			new MonsterService().mDescribe(session, Integer.parseInt(args[1]));;
		}
	},
	ALLSKILL("allSkill", "���м���"){
		@Override
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.noPara(args)){
				session.sendMessage("�����������ȷ");
				return;
			}
			SkillService skillSer = new SkillService();
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
	    	SkillService skillSer = new SkillService();
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
	DELGOOD("delGood", "ɾ����ʹ����Ʒ"){
		@Override 
		public void doService(String[] args, Session session) {
			if(!OrderVerifyService.twoInt(args)) {
				session.sendMessage("�����������ȷ");
				return;
			}
			int gId = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			session.getPlayer().delGoods(gId, amount);
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
