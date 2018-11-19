package com.hc.logic.basicService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hc.frame.Context;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;

/**
 * ��������
 * ÿ����Ҷ���һ�������ֻ�ܴ���������ñ�����Ϣ
 * ��ʾ����û�д��ŵ���Ʒ
 * @author hc
 *
 */
public class BagService {

	//����Ĭ�ϴ�С
	private int size = 20;
	//�������洢������Ʒ��id������Ʒ����id����idС������ǰ�棻map�У�key: ��Ʒid��value: ��Ʒ����
	private Map<Integer, Integer>[] bags = new HashMap[size];
	//Ĭ��ÿ�����ӿɵ���ͬһ��Ʒ������
	//private int superposition = 2;
	private Player p;
	
	
	//idΪ���id, �����ݿ��еõ���Ʒ��Ϣ��������bag����ʾ
	public BagService(Player pe) {
		this.p = pe;
		//System.out.println("bagservide------------: " + (pe==null) + " && " );
		if(pe != null) {
			//���key����Ʒ����Ʒ id��value������
			Map<Integer, Integer> maop = pe.getAllOrtherEq();
			insertBag(maop);
		}
	}
	
	/**
	 * ��������ֿ⡣ÿ������һ��
	 * @param gid2amount�� key����Ʒ����Ʒ id��value������
	 * @param size�� �ֿ��С
	 */
	public BagService(Map<Integer, Integer> gid2amount, int size) {
		this.size = size;
		this.bags = new HashMap[size];
		insertBag(gid2amount);
	}
	
	/**
	 * ���ؿͻ��˱���״̬
	 * @param session
	 */
	public void dispBag(Session session) {
		StringBuilder sb = new StringBuilder();
		sb.append("�������У�- - - - - - - - - - - - - - - - - - - - \n");
		//System.out.println("bag: " + bags[0].toString());
		for(int i = 0; i < bags.length; i++) {
			if(bags[i] == null) break;  //��ĳ������Ϊnull�������ĸ��Ӷ�Ӧ��Ϊnull
			int goodId = bags[i].keySet().iterator().next();
			String name = Context.getGoodsParse().getGoodsConfigById(goodId).getName();
			sb.append(name + ", ������" + bags[i].get(goodId) + "\n");
		}
		sb.append("- - - - - - - -  - - - - - - - - - -  - - - - - - - - - - - - - - - - - -\n");
		session.sendMessage(sb.toString());
		
		//������
		//getGoods(1, 7);
		//System.out.println("***************************88888*************");
		//test(bags);
	}
	
	public String bagGoodsdis() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < bags.length; i++) {
			if(bags[i] == null) break;  //��ĳ������Ϊnull�������ĸ��Ӷ�Ӧ��Ϊnull
			int goodId = bags[i].keySet().iterator().next();
			String name = Context.getGoodsParse().getGoodsConfigById(goodId).getName();
			sb.append(name + ", ������" + bags[i].get(goodId) + "\n");
		}
		return sb.toString();
	}
	
	
	/**
	 * �ӱ�����ȡ��һ��������ĳ����Ʒ
	 * 
	 * ��ĳ�������е���Ʒ��ȡ��ȥ�ˣ���ô�ͽ����������Ϊnull
	 * @param gid
	 * @param amount
	 * @return �������������Ʒ�����������㹻ʱ������true�����򷵻�false
	 */
	public boolean getGoods(int gid, int amount) {
		//System.out.println("getGoods&$$$$$$$$$$$" + gid + ", " );
		int pos = 10000;
		//��Ӧ��Equip��id
		//int equId = -1;
		//��֤�������Ƿ��������Ʒ
		for(int i = 0; i < bags.length; i++) {
			if(bags[i] == null) return false;  //û�������Ʒ
			if(bags[i].keySet().iterator().next() == gid) {
				pos = i;
				break;
			}
		}
		
		if(pos >= bags.length) return false;  //û�������Ʒ
		//��֤�����Ƿ�
		int all = 0;
		for(int i = pos; i < bags.length; i ++) {
			if(bags[i] == null) break;
			if(bags[i].keySet().iterator().next() != gid) break;
			all += bags[i].get(gid);
			if(all >= amount) break;  
		}
		if(all < amount) return false; //��������
		
		//System.out.println("************************* pos " + pos + " amount " + amount);
		
		//�ӱ�����ɾ��������������Ʒ
		for(int i = pos; i < bags.length; i ++) {
			
			if(amount <= 0) break;
			
			boolean dif = (amount - bags[i].get(gid)) >= 0;
			//System.out.println("i " + i + " amount " + amount + " dif " + dif);
			
			if(dif )  {   //��ǰ�����е���Ʒ��������
				amount -= bags[i].get(gid);
				bags[i] = null;
			}else {      //��ǰ�����е���Ʒ�����㹻
				bags[i].put(gid, bags[i].get(gid) - amount);
				amount -= bags[i].get(gid);
			}
			//System.out.println("i " + i + " amount " + amount);
			
		}
		//System.out.println("**************88ǰ------- " );
		//test(bags);
		//ȡ����Ʒ��Ҫ�Ա������е���
		adjustBag( gid, pos);
		
		return true;
		
	}
	
	/**
	 * �Ա������е���
	 * �����м�Ŀո��ӣ�ʹ���������ͬ��Ʒ���ӣ���������Ҷ˲���
	 * @param gid ��Ҫ��������Ʒ��id
	 * @param pos ��Ҫ��������Ʒ��λ��
	 */
	private void adjustBag( int gid, int pos) {
		//�����ո���
		remEmpty(pos);
		//�������������ͬ��Ʒ����
		int rPos = leftFull(gid, pos);

		if(bags[rPos] == null) {
			remEmpty(rPos);
		}else if((rPos + 1 < size) && bags[rPos+1] == null) {
			remEmpty(rPos+1);
		}
		
	}
	
	/**
	 * ���չ������������ֹ��ͬ��Ʒ�ĸ��ӣ������Ҫ�����Ҳ���Բ���
	 * ��adjustBag����
	 * @param gid
	 * @param pos ��Ҫ��������Ʒ��λ��
	 * @return
	 */
	private int leftFull(int gid, int pos) {
		int rPos = pos;  //װ����Ʒ�����Ҳ�ĸ���
		int superposition = Context.getGoodsParse().getGoodsConfigById(gid).getSuperposi();
		for(int i = pos+1; i < bags.length; i++) {
			if(bags[i] == null) break;
			if(bags[i].keySet().iterator().next() != gid) break;
			rPos = i;
		}
		if(rPos == pos) return rPos; //ֻ��һ������װ�и���Ʒ

		//if(need == 0) return;  //�����������
		while(rPos > pos) {
			int lm = bags[pos].get(gid);
			int need = superposition - lm;
			int rm = bags[rPos].get(gid);		
			if(need >= rm) { //��û�����������
				bags[pos].put(gid, lm + rm);
				bags[rPos] = null;
			}else {
				bags[pos].put(gid, superposition);
				bags[rPos].put(gid, rm - need );
				break;
			}
			need -= rm;
			rPos--;
		}
		
		return rPos;

	}
	
	/**
	 * �����ո���
	 * @param pos ��Ҫ�����ո��ӵĿ�ʼλ��
	 */
	private void remEmpty(int pos) {
		int isNull = -1;
		for(int i = pos; i < bags.length; i++) {
			if(bags[i] != null) break;
			isNull = i;
		}
		if(isNull == (bags.length - 1)) isNull = -1; //Ҳ����˵pos����ȫ��null��Ҳ�Ͳ���Ҫ�����ո�����
		if(isNull != -1) {  //�пո���
			setForward(isNull + 1, (isNull - pos + 1) );
		}

	}
	

	/**
	 * ��һϵ����Ʒ���뱳��  (�ܶ���Ʒ��һ��������)
	 * @param goods key:��Ʒid�� value����Ʒ����
	 */
	public boolean insertBag(Map<Integer, Integer> goods) {
		System.out.println("insertBag: " + goods.toString());
		for(Entry<Integer, Integer> ent : goods.entrySet()) {
			boolean a = insertBag(ent.getKey(), ent.getValue());
			if(!a) return false;
		}
		return true;
	}
	
	
	/**
	 * ����Ʒ���뱳����(һ����Ʒ��һ��������)
	 * @param gid ��Ʒ��id
	 * @param amount ��Ʒ������
	 * @param superposition �ɵ�������
	 */
	public boolean insertBag(int gid, int amount) {
		//System.out.println("����insertBag " );
		int superposition = Context.getGoodsParse().getGoodsConfigById(gid).getSuperposi();
		int hasInsert = 0;  //��¼�Ѿ������˶��ٸ���Ʒ�����㵱�������˻ع�
		while(true) {
			if(amount <= 0) break;
			int insertAm = amount;
			if(amount > superposition) {
				insertAm = superposition;
			}
			amount -= superposition;
			int posi = firstEmpty();
			System.out.println("posi-------------: " + posi);
			if(posi == -1) {
				p.getSession().sendMessage("������������");
				getGoods(gid, hasInsert);  //�ع�������
				return false;
			}
			bags[posi] = new HashMap<>();
			bags[posi].put(gid, insertAm);

			//System.out.println("-----ǰ*****************8");
			//test(bags);
			doSort(gid, posi);
		   // test(bags);
			//System.out.println("-----��************8*****");
			hasInsert++;
		}
		return true;
	}
	
	/**
	 * �Ա�����������
	 * @param pos �¼�����Ҫ�������Ʒ�ķ���ı�������λ��
	 *        gid : ��Ҫ�������Ʒid
	 */
	private void doSort(int gid, int pos) {
		//System.out.println("doSort***gid: " + gid +" pos "+ pos );
		int amount = bags[pos].get(gid);
		int type = Context.getGoodsParse().getGoodsConfigById(gid).getTypeId(); //�����Ʒ������id
		int insertPo = getInsetPos(gid, type);  //Ҫ�����λ��
		//��Ҫ����λ�ü�֮���Ԫ�أ��������һλ
		setBack(pos, insertPo);
		bags[insertPo] = new HashMap<>();
		bags[insertPo].put(gid, amount);
		
		//System.out.println("((((((&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + gid + " insertPo " + insertPo);
		//test(bags);
		
		//������������Ҳ�ɲ�������
		adjustBag(gid, insertPo);
		//System.out.println("doSort**---- " + bags[0].toString() );
	}
	
	/**
	 * ����begin��end֮�������(����begin)�����һ��
	 * @param begin
	 * @param end
	 */
	private void setBack(int end, int begin) {
		//System.out.println("begin " + begin + " end " + end);
		for(int i = end; i > begin; i--) {
			//System.out.println("i " + i);
			bags[i] = bags[i-1];
			bags[i-1] = null;
		}

	}
	
	/**
	 * ����begin���������(����begin)��ǰ��step��
	 * ��������Ҫ�ƶ�����������null
	 * @param begin
	 * @param step ��Ҫǰ���ĸ���
	 */
	private void setForward(int begin, int step) {
		for(int i = begin; i < bags.length; i++) {
			if(bags[i] == null) break;
			bags[i-step] = bags[i];
			bags[i] = null;
		}
	}
	
	/**
	 * ��ò����λ��
	 * @param typeId Ҫ������Ʒ������id
	 *        gid   Ҫ������Ʒ��id
	 * @return
	 */
	private int getInsetPos(int gid, int typeId) {
		//System.out.println("getInsetPos " + typeId );
		int insertPo = 10000;
		for(int i = 0; i < bags.length; i++) {
			int iid = bags[i].keySet().iterator().next(); //λ��i�ϵ���Ʒ��id
			int gTy = Context.getGoodsParse().getGoodsConfigById(iid).getTypeId(); //λ��i�ϵ���Ʒ������id
			//��������id��ͬ�ģ���������֤��Ʒ���Ƶġ��Ӷ������������Ʒ��һ������Ʒ����ᱻ����ͬ����id�����
			//                                          ���������Ʒ�������У���ᱻ����ͬ����Ʒ֮ǰ��
			if(gTy == typeId) {
				String insertName = Context.getGoodsParse().getGoodsConfigById(gid).getName();
				String name = Context.getGoodsParse().getGoodsConfigById(iid).getName();
				if(insertName.equals(name)) {
					insertPo = i;
					break;
				}
				continue;
			}
			if(gTy > typeId) {  //Ѱ�ҵ�һ������id����Ҫ�������Ʒ������id
				insertPo = i;
				break;
			}
		}
		//System.out.println("getInsetPos***** " + insertPo );
		return insertPo;

	}
	
	/**
	 * Ѱ�ҵ�һ���ո���
	 * @return -1: ��������
	 */
	private int firstEmpty() {
		for(int i=0; i<bags.length; i++) {
			if(bags[i] == null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * ������
	 * @param map
	 */
	public void test(Map<Integer, Integer>[] map) {
		StringBuilder sb = new StringBuilder();
		sb.append("�������У�- - - - - - - - - - - - - - - - - - - - \n");
		for(int i = 0; i < map.length; i++) {
			if(map[i] == null) {
				sb.append("null" + "\n");
				continue;  //��ĳ������Ϊnull�������ĸ��Ӷ�Ӧ��Ϊnull
			}
			int goodId = map[i].keySet().iterator().next();
			String name = Context.getGoodsParse().getGoodsConfigById(goodId).getName();
			sb.append(name + ", ������" + map[i].get(goodId) + "\n");
		}
		sb.append("- - - - - - - -  - - - - - - - - - -  - - - - - - - - - - - -\n");
		System.out.println(sb.toString());

	}
	
	/**
	 * �ѱ��������ݣ�ת��Ϊ���ݿ��д洢����ʽ
	 * @return
	 */
	private String retBag(){
		StringBuilder sb = new StringBuilder();
		if(bags == null) return sb.toString();
		for(int i = 0; i < bags.length; i++) {
			if(bags[i] == null) break;
			int gid = bags[i].keySet().iterator().next();
			int amo = bags[i].get(new Integer(gid));
			sb.append(gid + ":" + amo + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
}
