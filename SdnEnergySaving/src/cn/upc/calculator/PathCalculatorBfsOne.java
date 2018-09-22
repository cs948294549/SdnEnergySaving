package cn.upc.calculator;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.upc.entity.PathData;
import cn.upc.entity.PathData.PathNode;

/**
 * ʹ�ù����������ʵ�ֵ����·����������ֻ���ص�һ���ҵ������ݣ��˷����л��棬���ɵ�·�����ȶ�
 * 
 * @author ����չ
 *
 */
public class PathCalculatorBfsOne implements PathCalculator {

	/**
	 * ������������·����¼
	 */
	private ArrayList<String[]> pathRecord;
	/**
	 * ����ʱ���
	 */
	private long timestamp;

	public PathCalculatorBfsOne() {
		pathRecord = new ArrayList<>();
	}

	@Override
	public ArrayList<String[]> calculate(String srcName, String dstName, PathData pathData) {
		ArrayList<String[]> pathResult = new ArrayList<>();
		String srcId = pathData.nameIdMap.get(srcName);
		String dstId = pathData.nameIdMap.get(dstName);
		String[] paths = null;
		// ���ʱ�����ͬ�������ݿ����Ѿ����ҹ�һ�Σ����������棻�����ͬ������ջ��棬����ʱ���
		if (timestamp == pathData.timestamp) {
			paths = returnCache(srcId, dstId);
		} else {
			// ��ջ���
			pathRecord.clear();
			// ����ʱ���
			timestamp = pathData.timestamp;
		}
		// ���·��Ϊ�գ���ʼѰ��·��
		if (paths == null) {
			paths = findPathsById(srcId, dstId, pathData);
		}
		// ���뵽������
		pathRecord.add(paths);
		pathResult.add(paths);
		return pathResult;
	}

	private String[] returnCache(String srcId, String dstId) {
		for (String[] record : pathRecord) {
			// ��¼�д���Ҫ���ҵ�·��ʱ��ֱ�ӷ���
			if (record[0].equals(srcId) && record[1].equals(dstId)) {
				return record;
			}
			// ��¼�д���Ҫ���ҵ�·���ķ���·��ʱ���˴�����������·�����ȶ�
			if (record[1].equals(srcId) && record[0].equals(dstId)) {
				String[] paths = new String[record.length];
				paths[0] = record[1];
				paths[1] = record[0];
				// ��·������Ȼ�󷵻�
				for (int i = 2; i < record.length; i++) {
					paths[i] = record[(record.length - 1) - (i - 2)];
				}
				return paths;
			}
		}
		return null;
	}

	/**
	 * ͨ��ID����·��,�˴��������Ĺؼ�����
	 * 
	 * @param srcId
	 *            Դ����IP
	 * @param dstId
	 *            Ŀ������IP
	 * @param pathData
	 *            ���㸨������
	 * @return
	 */
	private String[] findPathsById(String srcId, String dstId, PathData pathData) {
		// ��ɾƵ����ʹ������������Ч��
		LinkedList<PathNode> list = new LinkedList<>();
		// ��һ�����ҵ���ʼ�ڵ㣬�����ڵ��������Ϊ0
		PathNode root = pathData.idNodeMap.get(srcId);
		root.distance = 0;
		// �ڶ���������ʼ�ڵ������
		list.add(root);
		// �����������в�Ϊ�գ���ʼ��������
		while (!list.isEmpty()) {
			// ���Ĳ���ȡ����һ���ڵ�
			PathNode parent = list.remove(0);
			// ���岽��������ǰ�ڵ�ĺ�̽ڵ�
			for (String sonId : parent.reachNodeId) {
				PathNode son = pathData.idNodeMap.get(sonId);
				if (son.id.equals(dstId)) {
					son.distance = parent.distance + 1;
					// ������(1)�������̽ڵ�����ֹ�ڵ㣬�㱣���¼����������
					String[] paths = new String[parent.distance + 4];// ������distance+2��+ǰ��λ��ʼ����
					paths[0] = srcId;
					paths[1] = dstId;
					fillConvertPaths(son, paths, pathData);
					return paths;
				} else if (son.distance > parent.distance + 1) {
					// ������(2)�������̽ڵ㵽��ʼ�ڵ�ľ�����ڴӴ�·������ľ��룬����¾��벢������У��ȴ���һ�ֱ���
					son.distance = parent.distance + 1;
					if (!list.contains(son)) {
						list.add(son);
					}
				}
			}
		}
		// ���ʵ��û�ҵ���ʵ���ϲ����ܣ�������ֻ����ͷβ��·��
		return new String[] { srcId, dstId };
	}

	/**
	 * ��������·������·����ӵ��ַ������У���������+�������=�������
	 * 
	 * @param start
	 *            Ŀ����������Ϊ�Ƿ���������ݣ�����ʹ����start����
	 * @param paths
	 *            Ҫ����Ŀ������
	 * @param pathData
	 *            ���㸨������
	 */
	private void fillConvertPaths(PathNode start, String[] paths, PathData pathData) {
		PathNode temp = start;
		paths[paths.length - 1] = temp.id;
		for (int i = 1; i < paths.length - 2; i++) {
			for (String name : temp.reachNodeId) {
				// �ڵ���Χ�ĵ�������ñȵ�ǰ�ڵ����С1����϶��Ƿ��ص���·
				if (pathData.idNodeMap.get(name).distance == temp.distance - 1) {
					paths[paths.length - 1 - i] = name;
					temp = pathData.idNodeMap.get(name);
					break;
				}
			}
		}
	}
}
