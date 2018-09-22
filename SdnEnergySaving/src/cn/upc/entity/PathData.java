package cn.upc.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import cn.upc.dao.DataFactory;
import cn.upc.res.Util;

/**
 * ���������࣬�ڼ���·��ʱ��Ҫ���ݣ�ͬʱʹ��timestamp����֤����
 * ������ {@link DataFactory#getPathData()}����
 * 
 * @author ����չ
 *
 */
public class PathData implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * ʱ�������ǵ�ǰ�����޸Ĺ���ʱ��
	 */
	public long timestamp = -1;

	public PathData() {
		nameIdMap = new HashMap<>();
		idNodeMap = new HashMap<>();
	}

	/**
	 * �豸��ʾ������ʵ��ID�ŵ�ӳ��
	 */
	public HashMap<String, String> nameIdMap;
	/**
	 * ʵ��ID�������ڵ����ݵ�ӳ��
	 */
	public HashMap<String, PathNode> idNodeMap;

	public void updateData(ArrayList<Device> devices, ArrayList<Link> links, long timestamp) {
		// �����������һ�£��Ͳ��ø���������
		if (this.timestamp == timestamp) {
			return;
		}
		// ����Ѿ����ɵĸ�������
		nameIdMap.clear();
		idNodeMap.clear();
		// ������������
		for (Device device : devices) {
			if (device.ipv4s == null) {
				continue;
			}
			// ���������м���·���ڵ������Լ��ɴ�������
			addData(device.ipv4s[0], device.attachmentPoints[0].switchDPID);
			addData(device.attachmentPoints[0].switchDPID, device.ipv4s[0]);
		}
		for (Link link : links) {
			// ���������м���·���ڵ������Լ��ɴ�������
			addData(link.src_switch, link.dst_switch);
			addData(link.dst_switch, link.src_switch);
		}
		this.timestamp = timestamp;
	}

	/**
	 * ��ӳ����������Ϣ,ֻ������������
	 * 
	 * @param srcId
	 * @param dstId
	 */
	private void addData(String srcId, String dstId) {
		if (!nameIdMap.containsKey(Util.rename(srcId))) {
			// �������IDӳ�䲻����Դid�������
			nameIdMap.put(Util.rename(srcId), srcId);
		}
		if (!idNodeMap.containsKey(srcId)) {
			// ���������ԴidΪ����PathNode�����򴴽�
			PathNode node = new PathNode();
			node.id = srcId;
			node.name = Util.rename(srcId);
			node.distance = Integer.MAX_VALUE;
			node.reachNodeId = new ArrayList<>();
			idNodeMap.put(node.id, node);
		}
		if (!idNodeMap.get(srcId).reachNodeId.contains(dstId)) {
			// ���ԴPathNode�����в�������Ŀ�����Ŀɴ����ݣ������
			idNodeMap.get(srcId).reachNodeId.add(dstId);
		}
	}
	/**
	 * ���ڼ���·���Ľڵ�����
	 * 
	 * @author ����չ
	 *
	 */
	public class PathNode implements Serializable {
		private static final long serialVersionUID = 1L;
		public String id;// �ڵ�ID
		public String name;// �ڵ�rename�������
		public int distance;// ������ʼ��ľ���
		public ArrayList<String> reachNodeId;// �������ӵ��Ľڵ��ID
	}
}