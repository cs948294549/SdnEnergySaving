package cn.upc.calculator;

import java.util.ArrayList;

import cn.upc.entity.PathData;

/**
 * 路径计算器
 * 
 * @author 江荣展
 *
 */
public interface PathCalculator {
	/**
	 * 经过计算器计算，获得指定路径
	 * 
	 * @param srcName
	 *            源主机缩写代号例如：H1
	 * @param dstName
	 *            目标主机缩写代号
	 * @param pathData
	 *            路径计算辅助数据
	 * @return 多条路径详细结果
	 */
	public ArrayList<String[]> calculate(String srcName, String dstName, PathData pathData);
}
