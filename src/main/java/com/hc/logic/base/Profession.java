package com.hc.logic.base;

public enum Profession {

	ZHANSHI("zhanShi", "սʿ", "ӵ��ǿ��Ĺ����������ҷ��������ˣ�������ս������"),
	MUSHI("muShi", "��ʦ", "��ս�в���ȱ�ٵ���꣬ӵ����������������"),
	FASHI("faShi","��ʦ", "�ƿش���ߣ�ӵ��ͬʱ��һ�аٵ�����"),
	ZHAOHUANSHI("zhaoHuanShi","�ٻ�ʦ", "ӵ�п�Խ��������ͨ���Ҽ�����Ҳ��boss��");
	
	private String job;
	private String title;
	private String description;
	
	private Profession(String job, String na, String desc) {
		this.job = job;
		this.title = na;
		this.description = desc;
	}
	
	/**
	 * �����ַ�����������Ӧ����λ(��0��ʼ)��
	 * @param a
	 * @return  -1��ʾû�����ֵ
	 */
	public static int getJob(String a) {
		for(Profession pf : Profession.values()) {
			if(pf.getJob().equals(a)) {
				return pf.ordinal();
			}
		}
		return -1;
	}

	/**
	 * ����λ�û����Ӧ��ö�ٶ���
	 * @param index
	 * @return
	 */
	public static Profession getProfByIndex(int index) {
		for(Profession pf : Profession.values()) {
			if(pf.ordinal() == index) {
				return pf;
			}
		}
		return null;
	}
	
	public String getJob() {
		return job;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}


	
	
}
