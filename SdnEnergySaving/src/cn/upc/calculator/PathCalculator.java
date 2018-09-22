package cn.upc.calculator;

import java.util.ArrayList;

import cn.upc.entity.PathData;

/**
 * ·��������
 * 
 * @author ����չ
 *
 */
public interface PathCalculator {
	/**
	 * �������������㣬���ָ��·��
	 * 
	 * @param srcName
	 *            Դ������д�������磺H1
	 * @param dstName
	 *            Ŀ��������д����
	 * @param pathData
	 *            ·�����㸨������
	 * @return ����·����ϸ���
	 */
	public ArrayList<String[]> calculate(String srcName, String dstName, PathData pathData);
}
