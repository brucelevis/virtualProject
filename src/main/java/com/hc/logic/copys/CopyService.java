package com.hc.logic.copys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.config.CopysConfig;
import com.hc.logic.creature.Player;
import com.hc.logic.dao.impl.CopyPersist;
import com.hc.logic.domain.PlayerEntity;


@Component
public class CopyService {

	/**
	 * ��֤�Ƿ���Խ��븱��
	 * @param player
	 * @return
	 */
	public boolean canEnterCopy(Player player) {
		int copId = player.getCopEntity().getCopyId();
		CopysConfig copyConfig = Context.getCopysParse().getCopysConfById(copId);
		boolean hasComp = player.getCopEntity().getBossindex() >= copyConfig.getBosses().size();
		//ɾ����ʱ�������ݿ���Ϣ
		if(isTimeOut(player) || hasComp) {
			player.getPlayerEntity().setNeedDel(true);
			new CopyPersist(player.getPlayerEntity()).delCopys(player.getPlayerEntity());
			player.getPlayerEntity().setNeedDel(true);
			return true;
		}
		//�ж��Ƿ��Ѿ��ڸ�����
		Session session = player.getSession();
		if(player.getCopEntity() != null) {
			session.sendMessage("�Ѿ��ڸ����У�����ͬʱ����������");
			return false;
		}
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
		//��������
		Context.getWorld().createCopy(copyId, player, bossIndex);
		//���д��͡�
		Context.getTransferService().copyTransfer(player, copyId);
		return true;
	}
	
	
	
	

}
