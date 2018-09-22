package cn.upc.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import cn.upc.dao.DataFactory;
import cn.upc.res.Util;

/**
 * 辅助数据类，在计算路径时需要数据，同时使用timestamp来保证缓存
 * 生成于 {@link DataFactory#getPathData()}方法
 * 
 * @author 江荣展
 *
 */
public class PathData implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 时间戳，标记当前对象修改过的时间
	 */
	public long timestamp = -1;

	public PathData() {
		nameIdMap = new HashMap<>();
		idNodeMap = new HashMap<>();
	}

	/**
	 * 设备显示名称与实际ID号的映射
	 */
	public HashMap<String, String> nameIdMap;
	/**
	 * 实际ID号与计算节点数据的映射
	 */
	public HashMap<String, PathNode> idNodeMap;

	public void updateData(ArrayList<Device> devices, ArrayList<Link> links, long timestamp) {
		// 如果缓存内容一致，就不用更新数据了
		if (this.timestamp == timestamp) {
			return;
		}
		// 清空已经生成的辅助数据
		nameIdMap.clear();
		idNodeMap.clear();
		// 遍历主机数据
		for (Device device : devices) {
			if (device.ipv4s == null) {
				continue;
			}
			// 向辅助数据中加入路径节点数据以及可达性数据
			addData(device.ipv4s[0], device.attachmentPoints[0].switchDPID);
			addData(device.attachmentPoints[0].switchDPID, device.ipv4s[0]);
		}
		for (Link link : links) {
			// 向辅助数据中加入路径节点数据以及可达性数据
			addData(link.src_switch, link.dst_switch);
			addData(link.dst_switch, link.src_switch);
		}
		this.timestamp = timestamp;
	}

	/**
	 * 向映射表中添加信息,只加入意向数据
	 * 
	 * @param srcId
	 * @param dstId
	 */
	private void addData(String srcId, String dstId) {
		if (!nameIdMap.containsKey(Util.rename(srcId))) {
			// 如果名称ID映射不包含源id，则加入
			nameIdMap.put(Util.rename(srcId), srcId);
		}
		if (!idNodeMap.containsKey(srcId)) {
			// 如果不存在源id为键的PathNode对象，则创建
			PathNode node = new PathNode();
			node.id = srcId;
			node.name = Util.rename(srcId);
			node.distance = Integer.MAX_VALUE;
			node.reachNodeId = new ArrayList<>();
			idNodeMap.put(node.id, node);
		}
		if (!idNodeMap.get(srcId).reachNodeId.contains(dstId)) {
			// 如果源PathNode对象中不包含到目标对象的可达数据，则加入
			idNodeMap.get(srcId).reachNodeId.add(dstId);
		}
	}
	/**
	 * 用于计算路径的节点数据
	 * 
	 * @author 江荣展
	 *
	 */
	public class PathNode implements Serializable {
		private static final long serialVersionUID = 1L;
		public String id;// 节点ID
		public String name;// 节点rename后的名称
		public int distance;// 距离起始点的距离
		public ArrayList<String> reachNodeId;// 可以连接到的节点的ID
	}
}