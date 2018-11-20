package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.TransferService;
import com.hc.logic.creature.Monster;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;

/**
 * ��������
 * @author hc
 *
 */
public class Copys extends Scene{
	//����boss��id
	private List<Integer> allBoss = new ArrayList<>();
	//�������У����������ɵĻ��ŵ�boss
	private List<Boss> bosses = new ArrayList<>();
	//�����˸�����ʱ��
	private long openTime;
	//���ĸ�boss��ʼ����ʼ����ʱ0��
	private int bossIndex = 0;
	private CopyEntity copyEntity;
	//���˸����У�ʣ������
	private List<Player> availablePlayer;
    
    public Copys(int id, String name, String desc, List<Player> players, int bossInd) {
    	super(2);
    	this.id = id;  //����di
    	this.name = name;
    	this.describe = desc;
    	this.players = new ArrayList<>(players);   //���뵱ǰ����������б�
    	this.availablePlayer = new ArrayList<>(players); //�������Ҷ��ǿɹ�������
    	System.out.println("------------copys�е��������---" + players.toString());
    	this.allBoss = Context.getCopysParse().getCopysConfById(id).getBosses();
    	this.bossIndex = bossInd;
    	//Ĭ������һֻboss
    	//if(bossIndex >= bosses.size()) complete();
    	if(bosses.size() == 0) createBoss(allBoss.get(bossIndex)); 
    	this.openTime = System.currentTimeMillis();  //��������ʱ����¼����ʱ��
    	//exe(2, "copys"+id + players.get(0).getId()); //����һ�������Ե�����������2��
    	Context.getTaskConsume().exe(2, "copys"+id + players.get(0).getId(), this);
    }
    
    
	//��������ᱻ�Զ������Ե���
    @Override
    public void run() {
    	//System.out.println("-------------------aaaaaaaaa");
    	//���ÿ��ָ���Ѫ���ͷ���
    	//recoverHpMp();
    	super.letPlayerProgress();
    	//ˢ��boss
    	//System.out.println("ˢ��boss�б�" + System.currentTimeMillis());
    	haveAvailableBoss();
    	//�ж��ڸ����д���ʱ��
    	if(isTimeOut()) {
    		//��ʱ����ǿ���뿪����
    		complete();
    	}
    }
    
    @Override
    public void attackPlayer() {
    	//ʲô��������ֻ����дScece�еĹ��﹥����ʹ��boss����������scene�н��е�
    }
    
    @Override
    public void allThing(Session session) {
		session.sendMessage("����boss: " + bossNameList());
		session.sendMessage("�������" + getPlayers() + "\n");
		int scecid = Context.getCopysParse().getCopysConfById(id).getPlace();
		String name = Context.getSceneParse().getSceneById(scecid).getName();
		session.sendMessage("���пɴ���Ŀ�꣺" + name + "��" + scecid + "��");
    }
    
    @Override
    public boolean hasMonst(int mId) {
    	for(int i : allBoss) {
    		if(i == mId)
    			return true;
    	}
    	return false;
    }
    
    @Override
    public void addAttackPlayer(int mId, Player p) {
    	//boss������ҵķ�ʽ�͹��ﲻһ������Boss��ʵ��
    	//���ڲ�����������ҵ�boss������״ι���bossʱ��boss�Ϳ�ʼ����
    	for(Boss boss : bosses) {
    		if(boss.getId() == mId) {
    			boss.setAttackNow(true);
    		}
    	}
    }
    
    /**
     * ���ص�ǰ���е�boss�������б�
     * @return
     */
    public String bossNameList() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("��");
    	//System.out.println("bossNameLis---------------- " + bosses.size());
    	for(Boss boss : bosses) {
    		String name = Context.getSceneParse().getMonsters().getMonstConfgById(boss.getId()).getName();
    		sb.append(name);
    		sb.append(", ");
    	}
    	if(sb.length() > 1) sb.deleteCharAt(sb.length()-1);
    	sb.append("��");
    	return sb.toString();
    }
    
    /**
     *    ͨ��boss id������һ��boss, ���ɵ�boss���Զ������Լ������ý�����Ӧ�Ĺ����
     *    Ҳ����˵��boss�Ĺ����������Լ������ģ������ڳ��������õġ�
     * @param id
     */
    public Boss createBoss(int bid) {
    	Boss boss = new Boss(bid, players);
    	bosses.add(boss);
    	//����Boss�������Ե����̣߳�1��
    	//boss.exe(1, "boss"+bid+players.get(0).getId());
    	Context.getTaskConsume().exe(1, "boss"+bid+players.get(0).getId(), boss);
    	return boss;
    }
    
    /**
     *    ����������ʱ����boss�б���ɾ��
     * @param id  boss��id
     * @return  
     */
    public boolean delBoss(int id) {
    	for(Boss boss : bosses) {
    		if(boss.getId() == id) {
    			bosses.remove(boss);
    			//һ��boss��������Ҫֹͣ��boss���߳�
    			Context.getWorld().delBossThread(players.get(0).getId(), id);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     *   ww��֤�˸����Ƿ���ʣ�࿪��ʱ��
     *   @param id �˸���id
     * @return  ���r   true
     */
    public boolean isTimeOut() {
    	long current = System.currentTimeMillis();
    	int continueT = Context.getCopysParse().getCopysConfById(id).getContinueT();
        long allTime = continueT * 60 * 1000; //�˸����趨�Ŀ���ʱ�䡣�ӷ��ӻ�Ϊ����
        if((current - openTime) >= allTime) { 
        	return true;
        }
        return false;
    }
    
    /**
     * ��֤��ǰboss�Ƿ��������������Ƿ���boss���ɣ�����������ɹ��
     * 
     */
    public void haveAvailableBoss() {
    	int boId = 1000;
    	int bossId = allBoss.get(bossIndex);
    	//System.out.println("-------haveavailableBosos------" + bossId + ", " + bossIndex);
    	//System.out.println("------getMonstById=null " + getMonsteById(bossId));
    	if(!getMonsteById(bossId).isAlive()) {
			//��boss�������� ���б���ɾ��
			delBoss(bossId);
			//�Ƿ��й����������
			boId = Context.getCopysParse().getCopysConfById(id).moreBoss(bossId);
			bossIndex++;  
			players.get(0).getCopEntity().setBossindex(bossIndex);  //����ʵ����
			//System.out.println("-----------------haveAvailableBoss--boId=" + boId);
			if(boId != -1) {
				createBoss(boId);
				return;
			}
		}
    	//�Ѿ�û�й����ˣ���������˳�
    	if(boId == -1) {
    		//System.out.println("-----------------haveAvailableBoss--û��boss��");
    		obtainAward();
        	complete();
    	}
    }
    
    /**
     *  ����ǳɹ���ɸ��������ý���
     *  ���ǳ�ʱ�뿪��������û�н���
     */
    public void obtainAward() {
    	String award = Context.getCopysParse().getCopysConfById(id).getsRewords();
    	//���ͽ����ʼ�
    	for(Player p : players) {
    		Context.getEmailService().sendGoodsEmail(p.getName(), award);
    	}
    }
    
    /**
     * �������е�bossȫ�������ˣ���ô�����������
     */
    public void complete() {
    	System.out.println("------------------------complete ");
    	//�Զ����д���
    	int targetId = Context.getCopysParse().getCopysConfById(id).getPlace();
    	TransferService transferService = Context.getTransferService();
    	//��������ڸ�����ɺ�û�����������
    	offLinePla(players.get(0));
    	for(int i = (players.size()-1); i >=0 ; i--) {
    		Player player = players.get(i);
    		System.out.println("-----------------complete--���д���" + players.toString());
    		transferService.transferCopy(player, id); //�Ӹ����д��ͳ���   		
    	}
    }
    
    /**
     * �������ʱ�����¶���δ������ҵ����ݿ�
     * ���в��������뿪��������Ҷ���õ�����
     * @param player ����һ�����ڸ����е����
     */
    public void offLinePla(Player player) {
    	System.out.println("--------����δ����---����--ǰ " + player.getName());
    	String award = Context.getCopysParse().getCopysConfById(id).getsRewords();
    	//List<PlayerEntity> pes = player.getCopEntity().getPlayers();
		String hql = "select ce.players from CopyEntity ce where sponsor "
				+ "like : name";
		List<PlayerEntity> pes = new PlayerDaoImpl().find(hql, player.getSponserNmae());
		
    	System.out.println("--------����δ����---����--�� " + pes.toString());
    	int place = Context.getCopysParse().getCopysConfById(id).getPlace();
    	for(PlayerEntity pe : pes) {
    		if(getPlayerByName(pe.getName()) == null) {
    			if(pe.getCopyEntity() != null) {
    				System.out.println("--------����δ����---���� " + pe.getName());
    				//��ʾ�Ƕ���δ����
    				pe.setSceneId(place);
    				pe.setCopyEntity(null);
    				new PlayerDaoImpl().update(pe);
    				Context.getWorld().updatePlayerEntity(pe); //���»���
    				//Context.getEmailService().sendGoodsEmail(pe.getName(), award);
    				System.out.println("-----------����δ���������pe=" + pe.toString());
    			}
    		}
    	}
    	
    }

	public int getBossIndex() {
		return bossIndex;
	}

	public void setBossIndex(int bossIndex) {
		this.bossIndex = bossIndex;
	}
	
	@Override
	public Monster getMonsteById(int bid) {
		if(monsters.size() == 0) initMonst();
		for(Monster mon : monsters) {
			if(mon.getMonstId() == bid) {
				return mon;
			}
		}
		return null;
	}
	
	public void initMonst() {
		for(int bid : allBoss) {
			Monster boss = new Monster(bid);
			monsters.add(boss);
		}
		//System.out.println("---copys�е�intitMonst--------------------------" + monsters.toString());
	}
    
	@Override
	public String toString() {
		return "copy {id=" + id
		      + ", name=" + name
		      +"}";
	}


	public CopyEntity getCopyEntity() {
		return copyEntity;
	}


	public void setCopyEntity(CopyEntity copyEntity) {
		this.copyEntity = copyEntity;
	}


	/**
	 * �������Ƿ�Ҫ���
	 * @return
	 */
	public boolean haveAvailablePlayer() {
		return availablePlayer.size() > 0;
	}
	/**
	 * ����뿪����ʱ����ʣ�������ɾ��
	 * @param id
	 */
	public void delPlayer(int id) {
		for(Player p : availablePlayer) {
			if(p.getId() == id) {
				availablePlayer.remove(p);
				players.remove(p);  //�������ͳ�ȥ��Ҫ���ٳ������������
				break;
			}
		}
		for(Boss bs : bosses) {
			bs.delPlayers(id);
		}
	}
	public void playerComeback(Player player) {
		System.out.println("-----players--" + players.toString());
		System.out.println("-----availablePlayer--" + availablePlayer.toString());

		this.availablePlayer.add(player);
		this.players.add(player);
		for(Boss bs : bosses) {
			bs.addPlayer(player);
		}
		System.out.println("-----players-��-" + players.toString());
		System.out.println("-----availablePlayer-��-" + availablePlayer.toString());
		
	}

    
	
}
