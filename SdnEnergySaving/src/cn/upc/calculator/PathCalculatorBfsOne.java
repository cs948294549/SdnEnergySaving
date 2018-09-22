package cn.upc.calculator;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.upc.entity.PathData;
import cn.upc.entity.PathData.PathNode;

/**
 * 使用广度优先搜索实现的最短路径计算器，只返回第一条找到的数据；此方法有缓存，生成的路径不稳定
 * 
 * @author 江荣展
 *
 */
public class PathCalculatorBfsOne implements PathCalculator {

	/**
	 * 缓存搜索过的路径记录
	 */
	private ArrayList<String[]> pathRecord;
	/**
	 * 缓存时间戳
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
		// 如果时间戳相同，则数据可能已经查找过一次，先搜索缓存；如果不同，则清空缓存，更新时间戳
		if (timestamp == pathData.timestamp) {
			paths = returnCache(srcId, dstId);
		} else {
			// 清空缓存
			pathRecord.clear();
			// 更新时间戳
			timestamp = pathData.timestamp;
		}
		// 如果路径为空，则开始寻找路径
		if (paths == null) {
			paths = findPathsById(srcId, dstId, pathData);
		}
		// 加入到缓存中
		pathRecord.add(paths);
		pathResult.add(paths);
		return pathResult;
	}

	private String[] returnCache(String srcId, String dstId) {
		for (String[] record : pathRecord) {
			// 记录中存在要查找的路径时，直接返回
			if (record[0].equals(srcId) && record[1].equals(dstId)) {
				return record;
			}
			// 记录中存在要查找的路径的反向路径时，此处导致了搜索路径不稳定
			if (record[1].equals(srcId) && record[0].equals(dstId)) {
				String[] paths = new String[record.length];
				paths[0] = record[1];
				paths[1] = record[0];
				// 将路径反向，然后返回
				for (int i = 2; i < record.length; i++) {
					paths[i] = record[(record.length - 1) - (i - 2)];
				}
				return paths;
			}
		}
		return null;
	}

	/**
	 * 通过ID查找路径,此处是搜索的关键代码
	 * 
	 * @param srcId
	 *            源主机IP
	 * @param dstId
	 *            目标主机IP
	 * @param pathData
	 *            计算辅助数据
	 * @return
	 */
	private String[] findPathsById(String srcId, String dstId, PathData pathData) {
		// 增删频繁，使用链表可以提高效率
		LinkedList<PathNode> list = new LinkedList<>();
		// 第一步，找到初始节点，并将节点距离设置为0
		PathNode root = pathData.idNodeMap.get(srcId);
		root.distance = 0;
		// 第二步，将初始节点入队列
		list.add(root);
		// 第三步，队列不为空，开始遍历队列
		while (!list.isEmpty()) {
			// 第四步，取出第一个节点
			PathNode parent = list.remove(0);
			// 第五步，遍历当前节点的后继节点
			for (String sonId : parent.reachNodeId) {
				PathNode son = pathData.idNodeMap.get(sonId);
				if (son.id.equals(dstId)) {
					son.distance = parent.distance + 1;
					// 第六步(1)，如果后继节点是终止节点，便保存记录并返回数据
					String[] paths = new String[parent.distance + 4];// 总数（distance+2）+前两位开始结束
					paths[0] = srcId;
					paths[1] = dstId;
					fillConvertPaths(son, paths, pathData);
					return paths;
				} else if (son.distance > parent.distance + 1) {
					// 第六步(2)，如果后继节点到初始节点的距离大于从此路径计算的距离，则更新距离并加入队列，等待下一轮遍历
					son.distance = parent.distance + 1;
					if (!list.contains(son)) {
						list.add(son);
					}
				}
			}
		}
		// 如果实在没找到（实际上不可能），返回只含有头尾的路径
		return new String[] { srcId, dstId };
	}

	/**
	 * 反向搜索路径，将路径添加到字符数组中；反向搜索+反向填充=正向队列
	 * 
	 * @param start
	 *            目的主机，因为是反向填充数据，所以使用了start命名
	 * @param paths
	 *            要填充的目标数组
	 * @param pathData
	 *            计算辅助数据
	 */
	private void fillConvertPaths(PathNode start, String[] paths, PathData pathData) {
		PathNode temp = start;
		paths[paths.length - 1] = temp.id;
		for (int i = 1; i < paths.length - 2; i++) {
			for (String name : temp.reachNodeId) {
				// 节点周围的点如果正好比当前节点距离小1，则肯定是返回的线路
				if (pathData.idNodeMap.get(name).distance == temp.distance - 1) {
					paths[paths.length - 1 - i] = name;
					temp = pathData.idNodeMap.get(name);
					break;
				}
			}
		}
	}
}
