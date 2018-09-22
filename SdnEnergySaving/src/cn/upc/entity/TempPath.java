package cn.upc.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * ���ڴ洢��������е�·������,��ռ������δʹ��ʱ����100
 * 
 * @author ����չ
 *
 */
public class TempPath implements Serializable, Comparable<TempPath> {
	private static final long serialVersionUID = 1L;
	/**
	 * �·���ǰ�����ʱ��
	 */
	public long pushTime;
	/**
	 * ·��
	 */
	public String[] paths;
	/**
	 * ƿ����������ת��������
	 */
	public long occupancyRate = 0;
	/**
	 * ������ʱ��
	 */
	public long time = Integer.MAX_VALUE;
	/**
	 * ƿ���������Ĵ���
	 */
	public int bandwidth = Integer.MAX_VALUE;
	/**
	 * �������������·����Ҫ���������Ľ���������
	 */
	public int switchIncreaseNum = -1;

	public TempPath() {
	}

	public TempPath(String[] paths) {
		this.paths = paths;
	}

	public String getRateDescription() {
		if (bandwidth == 10240) {
			return bandwidth == Integer.MAX_VALUE ? "X" : occupancyRate * 1000 / time + "/" + "10Gbps";
		}
		return bandwidth == Integer.MAX_VALUE ? "X" : occupancyRate * 1000 / time + "/" + bandwidth;
	}

	public String getIncreaseDescription() {
		return switchIncreaseNum == -1 ? "X" : switchIncreaseNum + "";
	}

	@Override
	public int compareTo(TempPath o) {
		int result = (int) (occupancyRate * o.bandwidth - o.occupancyRate * bandwidth);
		if (result != 0) {
			return result;
		}
		result = switchIncreaseNum - o.switchIncreaseNum;
		if (result != 0) {
			return result;
		}
		result = paths.length - o.paths.length;
		return result;
	}

	@Override
	public String toString() {
		return Arrays.toString(paths);
	}
}
