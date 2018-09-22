package cn.upc.calculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import cn.upc.entity.PathData;
import cn.upc.entity.PathData.PathNode;

/**
 * 使用深度优先搜索实现的所有径计算器；不进行缓存
 * 
 * @author 江荣展
 *
 */
public class PathCalculatorDfsAll implements PathCalculator {

	private ArrayList<String[]> pathsResult;
	public PathCalculatorDfsAll() {
		pathsResult = new ArrayList<>();
	}

	@Override
	public ArrayList<String[]> calculate(String srcName, String dstName, PathData pathData) {
		pathsResult.clear();
		LinkedList<String> paths = new LinkedList<>();
		String srcId = pathData.nameIdMap.get(srcName);
		paths.add(srcId);
		String dstId = pathData.nameIdMap.get(dstName);
		paths.add(dstId);
		paths.add(srcId);
		dfs(paths, dstId, pathData);
		return pathsResult;
	}

	private void dfs(LinkedList<String> paths, String dstId, PathData pathData) {
		PathNode parent = pathData.idNodeMap.get(paths.getLast());
		parent.reachNodeId.forEach(nodeId->{
			if(nodeId.equals(dstId)){
				paths.addLast(dstId);
				writeToResult(paths);
				paths.removeLast();
			}else if(!paths.contains(nodeId)){
				paths.addLast(nodeId);
				dfs(paths,dstId,pathData);
				paths.removeLast();
			}
		});
	}

	private void writeToResult(LinkedList<String> paths) {
		String[] pathsNew = new String[paths.size()];
		Iterator<String> ite = paths.iterator();
		for (int i = 0; ite.hasNext(); i++) {
			pathsNew[i] = ite.next();
		}
		pathsResult.add(pathsNew);
	}
}
