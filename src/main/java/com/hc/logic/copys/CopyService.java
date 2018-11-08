package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.TransferService;
import com.hc.logic.config.CopysConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
import com.hc.logic.dao.impl.PlayerDaoImpl;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.PlayerEntity;


@Component
public class CopyService {

	/**
	 * ��֤�Ƿ���Խ��븱��������ʱ���Ѿ��ڸ����У��ȵ�
	 * @param player
	 * @return
	 */
	public boolean canEnterCopy(Player player) {
		if(player.getSponserNmae() != null && player.getTeammate().size() < 1 ) {
			player.getSession().sendMessage("��ӳ�Ա����������븱��");
			return false;
		}
		if(player.getCopEntity() == null) 
			return true;
		int copId = player.getCopEntity().getCopyId();
		CopysConfig copyConfig = Context.getCopysParse().getCopysConfById(copId);
		boolean hasComp = player.getCopEntity().getBossindex() >= copyConfig.getBosses().size();
		//ɾ����ʱ�������ݿ���Ϣ, ��index>boss������ʱҲɾ��������Ϣ
		if(isTimeOut(player) || hasComp) {
			player.getPlayerEntity().setNeedDel(true);
			new CopyPersist(player).delCopys();
			return true;
		}
		//�ж��Ƿ��Ѿ��ڸ�����
		Session session = player.getSession();
		if(player.getCopEntity() != null) {
			session.sendMessage("�Ѿ��ڸ����У�����ͬʱ����������");
			return false;
		}
		
		System.out.println("------�ܷ���븱����" +", " + player.getTeammate().size());
		return true;
	}
	
    /**
     *   ��֤�˸����Ƿ���ʣ�࿪��ʱ��
     *   @param id �˸���id
     * @return  ���r   true
     */
    public boolean isTimeOut(Player player) {
    	if(player.getCopEntity() == null) return false;
    	long openTime = player.getCopEntity().getFirstEnterTime();
    	int copId = player.getCopEntity().getCopyId();
    	long current = System.currentTimeMillis();
    	int continueT = Context.getCopysParse().getCopysConfById(copId).getContinueT();
        long allTime = continueT * 60 * 1000; //�˸����趨�Ŀ���ʱ�䡣�ӷ��ӻ�Ϊ����
        if((current - openTime) >= allTime) { 
        	return true;
        }
        return false;
    }
	
	
	/**
	 * ������븱��, ��������
	 * @param copyId ����id
	 * @return
	 */
	public boolean enterCopy(int copyId, Player player, Session session, int bossIndex) {
		CopysConfig copyConf = Context.getCopysParse().getCopysConfById(copyId);
		if(copyConf == null) {
			session.sendMessage("û���������");
			return false;
		}
		//��֤����ø����Ĵ�����������
		
		//��֤��ǰ�����Ƿ���Խ���˸���.����ҵ�sceneId=0,һ�����Խ��븱��
		boolean canEnter = (copyConf.getPlace() == player.getSceneId() || player.getSceneId()==0 );
		if(!canEnter) {
			session.sendMessage("��ǰ�������ܽ��븱��: " + copyConf.getName());
			return false;
		}
		//�ȼ�����
		boolean leveS = (player.getLevel() >= copyConf.getCondition());
		if(!leveS) {
			session.sendMessage("�ȼ����������ܽ��븱��: " + copyConf.getName());
			return false;
		}
		if(player.getTeammate().size() > 0 && !player.goupComplete()) {
			player.getSession().sendMessage("����������������ܽ��븱����");
			return false;
		}
		//��������
		Context.getWorld().createCopy(copyId, player, bossIndex);
		//���д��͡�
		TransferService transferService = Context.getTransferService();
		transferService.copyTransfer(player, copyId);  //�����߽��д���
		for(String pname : player.getTeammate()) {
			//�������������Ա���ͽ�����
			Player pp = Context.getOnlinPlayer().getPlayerByName(pname);
			transferService.copyTransfer(pp, copyId);
		}
		return true;
	}
	
	/**
	 * ���ݶ������������������ʵ��
	 * ���������ʵ��͸���ʵ��
	 * @param tId
	 * @param pnames
	 * @return
	 */
	public CopyEntity createCopyEntity(int cId, List<String> pnames, Player sponsor) {
		List<PlayerEntity> li = new ArrayList<>();
		CopyEntity ce = new CopyEntity(cId, System.currentTimeMillis(), li, 0, sponsor.getName());
		ce.getPlayers().add(sponsor.getPlayerEntity());  //ҲҪ�������߷��븱��ʵ����
		sponsor.getPlayerEntity().setCopyEntity(ce);
		System.out.println("------------createCopyentity,�������ʵ��" + pnames.toString());
		if(pnames.size() > 0) {
			for(String pn : pnames) {
				PlayerEntity pe = Context.getWorld().getPlayerEntityByName(pn);
				ce.getPlayers().add(pe);
				System.out.println("----------copyentity�е�players��" + ce.getPlayers().size());
			}
			//��ͨ��Ա��������ʵ��
			for(String pn : pnames) {
				PlayerEntity pe = Context.getWorld().getPlayerEntityByName(pn);
				pe.setCopyEntity(ce);
			}
		}
		if(sponsor.getSponserNmae() == null && sponsor.getTeammate().size()==0) {
			sponsor.setSponserNmae(sponsor.getName());
		}
		//�����ݿ��д洢����ʵ��
		new PlayerDaoImpl().insert(ce);
		Context.getWorld().addCopyEntity(ce);
		
		return ce;
	}
	

}
