package com.hc.logic.creature;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hc.frame.Context;
import com.hc.frame.Scene;
import com.hc.logic.base.Session;
import com.hc.logic.basicService.BagService;
import com.hc.logic.basicService.GoodsService;
import com.hc.logic.chat.Email;
import com.hc.logic.config.LevelConfig;
import com.hc.logic.copys.Copys;
import com.hc.logic.dao.impl.UpdateTask;
import com.hc.logic.domain.CopyEntity;
import com.hc.logic.domain.Equip;
import com.hc.logic.domain.GoodsEntity;
import com.hc.logic.domain.PlayerEntity;

/**
 * ���
 * 
 * @author hc
 *
 */
public class Player extends LiveCreature{

	//�Ƿ񻹻���	
	private boolean isAlive;
	
	private Session session;
	
	//����id
	private List<Integer> skills = new ArrayList<>(); 
	//������ȴʱ��, ����Ӽ���ʱ��ӡ�key������id��value��cdʱ��
	private Map<Integer, Date> cdSkill = new HashMap<>();
	//ʹ�ü��ܣ���һ��ʱ������˺���key:����id��value������ʹ�õ�ʱ�䣻�����ж��Ƿ���Ч
	private Map<Integer, Date> reduceAtt = new HashMap<>();
	
	//���ʵ��
	private PlayerEntity playerEntity;
	
	//ʹ�ûָ�����Ʒ�󣬻���һ��ʱ���ڳ����ָ�hp/mp��key����Ʒid��value��ҩƷʹ��ʱ��
	private Map<Integer, Date> recoverHpMp = new HashMap<>();
	
	//����
	private BagService bagService;
	
	//�̵�ҳ��
	private int pageNumber;
	//����
	private Email email;
	//�Ƿ�����pk
	private boolean inPK = false;
	//����pkʱ���Է���ҵ�����
	private String pkTarget;
	
	
	
	@Override
	public void setDescribe() {
		this.describe = "����" + playerEntity.getName();
	}
	

	public Player() {
		
	}
	//��playerEntity�е���
	public Player(Session session, int[] skill, PlayerEntity pe) {
		this.session = session;
		this.playerEntity = pe;
		this.isAlive = true;
		for(int i = 0; i < skill.length; i++) {
			addSkill(skill[i]);
		}
		bagService = new BagService(this);
		email = new Email(pe.getEmails());
	}
	//ע��ר��	
	public Player(int id, int level, String name, String pass, int sceneId, int hp, int mp,
			      int exp, int[] skil, Session session, boolean isAlive, List<Equip> equips) {
		//playerEntity = new PlayerEntity();
		StringBuilder sb = new StringBuilder();
		for(int ii : skills) {
			sb.append(ii + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		playerEntity = new PlayerEntity(id, level, name, pass, sceneId, hp, mp, exp, sb.toString(), new ArrayList<>(), null);
		
		
		this.session = session;
		this.bagService = new BagService(this);
		this.isAlive = true;
		this.email = new Email(playerEntity.getEmails());
	}
	


	
	/**
	 * ���ؿͻ�����ҵ�ǰ״̬
	 */
	public void pState() {
		LevelConfig lc = Context.getLevelParse().getLevelConfigById(playerEntity.getLevel());
		
		StringBuilder sb = new StringBuilder();
		sb.append("�Ƿ񻹻��ţ�" + isAlive + "\n");
		sb.append("�ȼ���" + playerEntity.getLevel()+ "\n");
		sb.append("���飺" + playerEntity.getExp() + "/" + lc.getExp() + "\n");
		sb.append("Ѫ��: " + playerEntity.getHp() + "/" + lc.getHp() + "\n");
		sb.append("ʣ�෨��: " + playerEntity.getMp() + "/" + lc.getMp() + "\n");
		sb.append("��ң�" + playerEntity.getGold() + "\n");
		sb.append("�����̵�ҳ�棺" + getPageNumber());
		session.sendMessage(sb.toString());
	}
	
	
	/**
	 * ͨ��sceneID�Ļ��scene
	 * ����ڸ����У��򷵻ظ���Copys
	 * @return
	 */
	public Scene getScene() {
		int sceId = playerEntity.getSceneId();
		System.out.println("sceId=0" + (sceId==0));
		if(sceId == 0) {
			return getCopys();
		}
		return Context.getWorld().getSceneById(sceId);
	}
	
	public Copys getCopys() {
		int copeId = playerEntity.getCopEntity().getCopyId();
		System.out.println("copeId" + copeId);
		return Context.getWorld().getCopysByAPlayer(copeId, playerEntity.getId());
	}
	
	public String getName() {
		return playerEntity.getName();
	}

	public void setName(String name) {
		this.playerEntity.setName(name);
	}

	public int getLevel() {
		return playerEntity.getLevel();
	}

	public void setLevel(int level) {
		playerEntity.setLevel(level);
	}
	
	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
		if(isAlive == false) {
			palyerDead();
		}
	}
	
	public void palyerDead() {
		session.sendMessage("���Ѿ�����");
		if(isInPK() == true) {
			//����֮���Զ�����
			Context.getPkService().deadFailed(this);
		}
	}


	public int getSceneId() {
		return playerEntity.getSceneId();
	}


	public void setSceneId(int sceneId) {
		//this.sceneId = sceneId;
		playerEntity.setSceneId(sceneId);
	}


	public int getId() {
		return playerEntity.getId();
	}


	public void setId(int id) {
		playerEntity.setId(id);
		setcId();
	}
	
	
	public Session getSession() {
		return session;
	}


	public void setSession(Session session) {
		this.session = session;
	}


	public String getPassword() {
		return playerEntity.getPassword();
	}

	public void setPassword(String password) {
		playerEntity.setPassword(password);
	}
	
	
	
	public int getHp() {
		return playerEntity.getHp();
	}

	public void setHp(int hp) {
		//this.hp = hp;
		playerEntity.setHp(hp);
	}
	
	

	public int getMp() {
		return playerEntity.getMp();
	}

	public void setMp(int mp) {
		//this.mp = mp;
		playerEntity.setMp(mp);
	}
	
	public int getGold() {
		return playerEntity.getGold();
	}
	/**
	 * ���ӽ��
	 * @param amount ����
	 */
	public void addGold(int amount) {
		playerEntity.setGold(playerEntity.getGold() + amount);
	}
	/**
	 * ���ٽ�ҡ�������ӵ�еĽ�Ҳ������򷵻�false
	 * @param amount
	 * @return
	 */
	public boolean minusGold(int amount) {
		int diff = playerEntity.getGold() - amount;
		if(diff < 0) return false;
		playerEntity.setGold(diff);
		return true;
	}
	
	/**
	 * ����(����)���Ѫ��������
	 * ������shp��Ҫ���ӵ�Ѫ��
	 *      smp��Ҫ���ӵķ���
	 */
	public void addHpMp(int shp, int smp) {
		//��֤�Ƿ񳬹������
		LevelConfig lc = Context.getLevelParse().getLevelConfigById(playerEntity.getLevel());
		int mHp = lc.getHp();
		int mMp = lc.getMp();
		shp += playerEntity.getHp();
		smp += playerEntity.getMp();
		if(shp > mHp) shp = mHp;
		if(smp > mMp) smp = mMp;
		
		//��֤�Ƿ�С��0
		if(shp < 0) {
			shp = 0;
			//�������
			setAlive(false);
		}
		if(smp < 0) smp = 0;
		
		setHp(shp);
		setMp(smp);
		//playerEntity.setMp(mp);
	}

	public boolean addSkill(int sk) {
		if(Context.getSkillParse().getSkillConfigById(sk) == null) {
			session.sendMessage("���ܲ�����");
			return false;
		}
		skills.add(sk);
		if(playerEntity != null) playerEntity.setSkills(skills);
		//��Ӽ���ʱ�������ӳ�ʼ��cdʱ��
		cdSkill.put(sk, new Date());
		return true;
	}
	
	/**
	 * �ж�����Ƿ�ӵ���������
	 * @param skId ����id
	 * @return
	 */
	public boolean hasSkill(int skId) {
		for(int ii : skills) {
			if(ii == skId) {
				return true;
			}
		}
		return false;
	}

	public List<Integer> getSkills() {
		return skills;
	}

	
	
	public int getExp() {
		return playerEntity.getExp();
	}

	public void setExp(int exp) {
		//this.exp = exp;
		playerEntity.setExp(exp);
	}
	/**
	 * ������Ҿ���
	 */
	public void addExp(int a) {
		int exp = playerEntity.getExp() + a;
		playerEntity.setExp(exp);
	}

	@Override
	public String toString() {
		return playerEntity.getName();
	}

	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}
	/**
	 * ��ʼ�����ʵ�壬ע��ʱ����
	 */
	public void initPlayerEntity() {
/**
		StringBuilder sb = new StringBuilder();
		for(int ii : skills) {
			sb.append(ii + ",");
		}
		if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
		//ע�⡣�������½�һ������˺ţ����򶼲����½�һ��playerEntity
		playerEntity = Context.getWorld().getPlayerEntityByName(name);
		if(playerEntity == null) {
			System.out.println("initPlayerEntity--------------");
			playerEntity = new PlayerEntity(id, level, name, password, sceneId, hp, mp, exp, sb.toString(), new ArrayList<>());
	*/	
	}
	

	
	
	public Email getEmail() {
		return email;
	}
	public void setEmail(Email email) {
		this.email = email;
	}


	@Override
	public void setcId() {
		this.cId = playerEntity.getId();
	}


	public Map<Integer, Date> getCdSkill() {
		return cdSkill;
	}

	public void setCdSkill(Map<Integer, Date> cdSkill) {
		this.cdSkill = cdSkill;
	}
	/**
	 * ���ݼ���id�����¼���cd
	 * @param sId
	 */
	public void updateCdById(int sId) {
		this.cdSkill.put(sId, new Date());
	}
	
	/**
	 * ͨ������id�����Ӧ��cdʱ��
	 * @param sId
	 * @return
	 */
	public Date getCdTimeByid(int sId) {
		return cdSkill.get(sId);
	}
	
	
	public Map<Integer, Date> getReduceAtt() {
		return reduceAtt;
	}
	
	/**
	 * ���ݹ������������Ѫ��
	 * @param player  �ܵ��˺������
	 * @param hurt  �ܵ����˺�
	 * @return ���ʵ�ʼ��ٵ�Ѫ��
	 */
	public int attackPlayerReduce(int hurt) {
		int redu = allReduce();
		hurt -= redu;
		if(hurt < 0) hurt = 0;   //��ֹ���ܵı��������ܵ����˺�
		addHpMp(-hurt, 0); //�Ӹ����ţ��ͱ�ɼ���
		return hurt;
	}

	
	/**
	 * ��Ӽ����˺��ļ���
	 * @param skiId
	 */
	public void addReduceAtt(int skiId) {
		this.reduceAtt.put(skiId, new Date());
	}
	/**
	 * �������м��ܴ����ļ���Ч��
	 * ����ɾ�������˵ļ���
	 */
	public int skillReduce() {
		List<Integer> deleRed = new ArrayList<>(); //��¼�����˵ļ��ܳ���Ч��
		int allRe = 0;
		for( Entry<Integer, Date> ent : reduceAtt.entrySet()) {
			int sId = ent.getKey();
			Date d = ent.getValue();
			
			long dual = Context.getSkillParse().getSkillConfigById(sId).getContinueT() * 1000;
			long pTime = d.getTime();
			long nTime = new Date().getTime();
			//��֤�����Ƿ����
			if((nTime - pTime) > dual) {
				deleRed.add(sId);  
				continue;
			}
			allRe += Context.getSkillParse().getSkillConfigById(sId).getProtect();
		}
		//�ڷ���ǰ��ɾ�������˵ļ��ܳ���Ч��
		for(int i : deleRed) {
			reduceAtt.remove(i);
		}
		return allRe;
	}
	/**
	 * �����ܵ��˺�����
	 * Ӧ�ð������ܣ�װ���ȵȴ������ܵļ��ˡ�
	 * @return
	 */
	public int allReduce() {
		int allRed = 0;
		allRed += skillReduce();  //���м��ܴ����ļ���
		allRed += equipReduce();  //����װ�������ļ���
		
		
		return allRed;
	}
	
	/**
	 * ��Ҵ���װ��
	 * ���ڱ������кܶ���ͬ����Ʒʱ��ͨ����Ʒid����Ψһȷ������һ����Ʒ
	 * ���Ĭ�ϴ��ϵ�һ����id����Ʒ
	 * @param eId: ��Ʒid
	 */
	public void addEquip(int eId) {
		//ֻ����Ʒ��typeIdΪ2������װ����ͬһװ������װ������
		//System.out.println(equips.toString() + " cont " + equips.contains(eId));
		int tId = Context.getGoodsParse().getGoodsConfigById(eId).getTypeId();
		if( tId != 2 && tId != 3) {
			session.sendMessage("����Ʒ����װ��");
			return;
		}
		if(hasEquiped(eId)) {
			session.sendMessage("�����ظ�װ��");
			return;
		}
		//����Ʒid�ӱ�����ɾ��, Ĭ��һ��ֻװ��һ��
		boolean cont = getBagService().getGoods(eId, 1);
		if(!cont) {
			session.sendMessage("û�и���Ʒ");
		    return;
		}

		//playerEntity.getEquips().add(equ);
		//����goodlEntity�еı�����Ʒ�ֶ�
		//playerEntity.getOrtherEquipsById(eId).setState(1); //1��ʾ��װ��
		Context.getGoodsService().doEquip(eId, playerEntity);
		
		session.sendMessage("�������");		
	}
	/**
	 * �鿴�������͵�װ���Ƿ��Ѵ���.����ֻ�Ƕ���Ʒid�жϣ��������ж��ֽ���Ҳ��װ��
	 * @param eId ��Ʒid
	 * @return
	 */
	public boolean hasEquiped(int eId) {
		return new GoodsService().isEquiped(eId, playerEntity);
	}
	/**
	 * ж��װ��
	 * @param eId
	 */
	public void deletEquip(int eId) {
		if(!hasEquiped(eId)) {
			session.sendMessage("û�д��Ÿ�װ��");
			return;
		}
		
		//��װ��ж��, Ĭ��һ��ֻж��һ��
		getBagService().insertBag(eId, 1);
		

		Context.getGoodsService().deEquip(eId, playerEntity);
		
		session.sendMessage("ж��װ��");
	}
	/**
	 * ͨ����Ʒid�����ж�����Ƿ�ӵ�и�װ��/����
	 * @param gid
	 * @return
	 */
	public boolean contEquip(int gid) {
		return hasEquiped(gid);
		//return playerEntity.getEquipById(gid) != null;
		//return this.equips.contains(gid);
	}
	/**
	 * ������������Ѵ���װ�����ܷ���
	 * @return
	 */
	public int equipReduce() {
		int alRe = 0;
	    for(GoodsEntity e : playerEntity.getGoods()) {
	    	int tId = Context.getGoodsParse().getGoodsConfigById(e.geteId()).getTypeId();
	    	if(tId != 2 && tId != 3) continue;
	    	Equip eq = (Equip)e;
	    	if(eq.getState() == 0) continue;  //0��ʾδ���š��򲻼ӷ���
	    	int ii = eq.geteId();
	    	//�жϲ����ٷ�����װ�����;ö�, Ҳ�����������ӷ�����װ����Ҫ�����;ö�
	    	if(Context.getGoodsParse().getGoodsConfigById(ii).getProtect() > 0) {
		    	if(eq.getDuraion() < 1) {
		    		continue;
		    	}else {
		    		eq.setDuraion(eq.getDuraion() - 1);
		    	}
	    	}
	    	
	    	alRe += Context.getGoodsParse().getGoodsConfigById(ii).getProtect();
	    }
	    return alRe;
	}
	/**
	 * ������������Ѵ���װ�����ܹ�����
	 * @return
	 */
	public int equipAttack() {
		int eAtt = 0;
	    for(GoodsEntity ge : playerEntity.getGoods()) {
	    	int tId = Context.getGoodsParse().getGoodsConfigById(ge.geteId()).getTypeId();
	    	if(tId != 2 && tId != 3) continue;
	    	Equip e = (Equip)ge;
			if(e.getState() == 0) continue;
			int ii = e.geteId();
			eAtt += Context.getGoodsParse().getGoodsConfigById(ii).getAttack();
		}
		return eAtt;
	}
	/**
	 * ������ҵ��ܵĹ�����
	 * �����ȼ����ڹ����������ü��ܶ��ڹ�����������װ�����ڹ�����
	 * @param skiId ����id
	 * @return
	 */
	public int AllAttack(int skiId) {
		int allAt = 0;
		//�ȼ����ڹ���
		allAt += Context.getLevelParse().getLevelConfigById(getLevel()).getlAttack();
		//���ܶ��ڹ���
		allAt += Context.getSkillParse().getSkillConfigById(skiId).getAttack();
		//����װ�����ڹ���
		allAt += equipAttack();
		
		return allAt;
	}
	

	/**
	 * ������Ʒ
	 * @param gId ��Ʒid
	 * @param amount ����
	 */
	public boolean addGoods(int gId, int amount) {
		//��ʱ�����ｫ������Ʒ���Ž�������
		Map<Integer, Integer> map = new HashMap<>();
		map.put(gId, amount);
		//System.out.println("addGoods " + map.size() + "to " + map.toString());
		boolean inserted = bagService.insertBag(map); //��ʾ		
		if(!inserted) return false;
		GoodsService gs = Context.getGoodsService();
		for(int i = 0; i < amount; i++) {
			gs.addGoods(playerEntity, gId);
		}
		return true;
	}

	/**
	 * ɾ����Ʒ
	 * @param gId
	 * @param amount
	 */
	public boolean delGoods(int gId, int amount) {
		boolean hasDel = bagService.getGoods(gId, amount);
		if(!hasDel) return false;
		GoodsService gs = Context.getGoodsService();
		for(int i = 0; i < amount; i++) {
			gs.delGoods(playerEntity, gId);
		}		
		return true;		
	}
	
	public BagService getBagService() {
		return bagService;
	}
	
	/**
	 * ʹ�ûָ���ҩƷ
	 * @param gId  ��Ʒid
	 */
	public boolean addRecoverHpMp(int gId) {  
		//��֤�Ƿ����ڻָ�����Ʒ
		int typeId = Context.getGoodsParse().getGoodsConfigById(gId).getTypeId();
		if(typeId != 1) return false;
		//��֤����Ʒ�����Ƿ��㹻, ����ɾ��
		if(!delGoods(gId, 1)) return false;
		this.recoverHpMp.put(gId, new Date());
		return true;
	}
	/**
	 * ��ô�ʱhp/mp�ܵĻָ���, ʹ�ûָ���ҩƷ��Ч������
	 * @return
	 */
	public int[] allRecover() {
		int allHp = 0;
		int allMp = 0;
		List<Integer> deleRed = new ArrayList<>(); //��¼�����˵�ҩƷ����Ч��
		for( Entry<Integer, Date> ent : recoverHpMp.entrySet()) {
			int gId = ent.getKey();
			Date d = ent.getValue();
			
			long dual = Context.getGoodsParse().getGoodsConfigById(gId).getContinueT() * 1000;
			long pTime = d.getTime();
			long nTime = new Date().getTime();
			//��֤ҩƷ�Ƿ����
			if((nTime - pTime) > dual) {
				deleRed.add(gId);  
				continue;
			}
			allHp += Context.getGoodsParse().getGoodsConfigById(gId).getHp();
			allMp += Context.getGoodsParse().getGoodsConfigById(gId).getMp();
		}
		//�ڷ���ǰ��ɾ�������˵ļ��ܳ���Ч��
		for(int i : deleRed) {
			reduceAtt.remove(i);
		}
		return new int[] {allHp, allMp};
	}

	/**
	 * ͨ��װ��/����id�������Ӧ��ʣ���;ö�
	 * ����ͨ����Ʒid������Ψһȷ��һ����Ʒ����������ֻ��һ��ͬ���͵���Ʒ��
	 * �Ӷ�ֻ��һ�������ϣ����Կ���ͨ�������ȷ�����ĸ���Ʒ
	 * @param eId ��Ʒid
	 * @return
	 */
	public int restContiT(int eId) {
		for(GoodsEntity ge : playerEntity.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.geteId() == eId && hasEquiped(eId)) {
					return eq.getDuraion();
				}
			}
		}
		return 0;
	}
	/**
	 * ͨ��װ��/����id��������Ӧ���;öȣ�Ĭ����1
	 * @param wId ��Ʒid
	 */
	public void minusContT(int wId) {
		for(GoodsEntity ge : playerEntity.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.geteId() == wId && hasEquiped(wId)) {
					int res = eq.getDuraion();
					res -= 1;
					if(res < 0) res = 0;
					eq.setDuraion(res);
					return;
				}
			}
		}

	}
	
	/**
	 * ��map����ʽ�����������δ���ŵ���Ʒ��װ��
	 * key:  ��Ʒid��value������
	 * @return
	 */
	public Map<Integer, Integer> getAllOrtherEq(){
		Map<Integer, Integer> map = new HashMap<>();
		for(GoodsEntity ge : playerEntity.getGoods()) {
			if(ge instanceof Equip) {
				Equip eq = (Equip)ge;
				if(eq.getState() == 1) continue;
			}
			map.put(ge.geteId(), map.getOrDefault(ge.geteId(), 0) + 1);
		}
		return map;
	}
	
	public void setCopEntity(CopyEntity copy) {
		//playerEntity.setCopEntity(copy);
		playerEntity.addCopyEntity(copy);
	}
	public CopyEntity getCopEntity() {
		return playerEntity.getCopEntity();
	}

	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}


	public boolean isInPK() {
		return inPK;
	}
	public void setInPK(boolean inPK) {
		this.inPK = inPK;
	}

	public String getPkTarget() {
		return pkTarget;
	}
	public void setPkTarget(String pkTarget) {
		this.pkTarget = pkTarget;
	}


	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Player oP = (Player)o;
		return oP.getName().equals(playerEntity.getName());
	}
}
