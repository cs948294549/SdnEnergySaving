package cn.upc.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 用于存储计算过程中的路径数据,包占用率在未使用时超过100
 * 
 * @author 江荣展
 *
 */
public class TempPath implements Serializable, Comparable<TempPath> {
	private static final long serialVersionUID = 1L;
	/**
	 * 下发当前流表的时间
	 */
	public long pushTime;
	/**
	 * 路径
	 */
	public String[] paths;
	/**
	 * 瓶颈交换机的转发包数量
	 */
	public long occupancyRate = 0;
	/**
	 * 经过的时间
	 */
	public long time = Integer.MAX_VALUE;
	/**
	 * 瓶颈交换机的带宽
	 */
	public int bandwidth = Integer.MAX_VALUE;
	/**
	 * 如果开启这条线路，需要增加启动的交换机数量
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
