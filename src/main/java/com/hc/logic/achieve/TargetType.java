package com.hc.logic.achieve;

public enum TargetType {

	Null(null),
	/** ���*/
	AttackMonster(new AttackMonstTarget()),
	/** �ɼ� */
	Collect(new SerchGoodsTarget()),
	/** ͨ������ */
	PassCopys(new PassCopyTarget());
	
	private Target target;
	
	private TargetType(Target target) {
		this.target = target;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}
	
	/**
	 * ͨ����������id�����Ӧ������Ŀ��
	 * @param tid
	 * @return
	 */
	public static Target getTargetById(int tid) {
		TargetType[] values = TargetType.values();
		TargetType tarType = values[tid];
		return tarType.getTarget();
	}
	/**
	 * ͨ������id��������Ŀ������
	 * @param tid
	 * @return
	 */
	public static int getTargetTypeById(int tid) {
		TargetType[] values = TargetType.values();
		TargetType tarType = values[tid];
		return tarType.ordinal();
	}
}
