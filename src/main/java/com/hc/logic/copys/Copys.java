package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.List;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.TransferService;
import com.hc.logic.creature.Player;

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
    
    public Copys(int id, String name, String desc, List<Player> players, int bossInd) {
    	super(2);
    	this.id = id;  //����di
    	this.name = name;
    	this.describe = desc;
    	this.players = new ArrayList<>(players);   //���뵱ǰ����������б�
    	this.allBoss = Context.getCopysParse().getCopysConfById(id).getBosses();
    	this.bossIndex = bossInd;
    	//Ĭ������һֻboss
    	//if(bossIndex >= bosses.size()) complete();
    	if(bosses.size() == 0) createBoss(allBoss.get(bossIndex)); 
    	this.openTime = System.currentTimeMillis();  //��������ʱ����¼����ʱ��
    	exe(2, "copys"+id + players.get(0).getId()); //����һ�������Ե�����������2��
    }
    
    
	//��������ᱻ�Զ������Ե���
    @Override
    public void execute() {
    	//���ÿ��ָ���Ѫ���ͷ���
    	recoverHpMp();
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
    	System.out.println("bossNameLis---------------- " + bosses.size());
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
    	boss.exe(1, "boss"+bid+players.get(0).getId());
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
    	if(!Context.getSceneParse().getMonsters().getMonstConfgById(bossId).isAlive()) {
			//��boss�������� ���б���ɾ��
			delBoss(bossId);
			//�Ƿ��й����������
			boId = Context.getCopysParse().getCopysConfById(id).moreBoss(bossId);
			bossIndex++;  
			players.get(0).getCopEntity().setBossindex(bossIndex);  //����ʵ����
			System.out.println("-----------------haveAvailableBoss--boId=" + boId);
			if(boId != -1) {
				createBoss(boId);
				return;
			}
		}
    	//�Ѿ�û�й����ˣ���������˳�
    	if(boId == -1) {
    		System.out.println("-----------------haveAvailableBoss--û��boss��");
    		obtainAward();
        	complete();
    	}
    }
    
    /**
     *  ����ǳɹ���ɸ��������ý���
     *  ���ǳ�ʱ�뿪��������û�н���
     */
    public void obtainAward() {
    	//����
    	
    }
    
    /**
     * �������е�bossȫ�������ˣ���ô�����������
     */
    public void complete() {
    	System.out.println("------------------------complete ");
    	//�Զ����д���
    	int targetId = Context.getCopysParse().getCopysConfById(id).getPlace();
    	TransferService transferService = Context.getTransferService();
    	for(Player player : players) {
    		System.out.println("-----------------complete--���д���" + players.toString());
    		transferService.transferCopy(player, id); //�Ӹ����д��ͳ���
    	}
    }

	public int getBossIndex() {
		return bossIndex;
	}

	public void setBossIndex(int bossIndex) {
		this.bossIndex = bossIndex;
	}
    
	@Override
	public String toString() {
		return "copy {id=" + id
		      + ", name=" + name
		      +"}";
	}
    
	
}
