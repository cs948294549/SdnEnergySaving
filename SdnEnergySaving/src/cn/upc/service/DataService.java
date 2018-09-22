package cn.upc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.upc.calculator.PathCalculator;
import cn.upc.calculator.PathCalculatorDfsAll;
import cn.upc.calculator.SimpleEnergyCalculator;
import cn.upc.dao.DataFactory;
import cn.upc.entity.*;
import cn.upc.res.R;

/**
 * ���ݷ�����Ҫ����������Ϣ�ĸ��»�ȡ
 * 
 * @author ����չ
 *
 */
public class DataService {
	private DataFactory factory;
	private PathCalculator pathCalculator;
	private SimpleEnergyCalculator energyCalculator;

	public DataService() {
		factory = DataFactory.newInstence();
		pathCalculator = new PathCalculatorDfsAll();
		energyCalculator = new SimpleEnergyCalculator(R.ENERGY_PRPT);
	}

	/**
	 * ��ʼ������Դ����
	 */
	public void deepInitData() {
		// ��������Դ
		factory.updateDataSource();
		// ������Դ��ȡ���ݲ�����
		factory.loadData();
	}

	/**
	 * ��ʼ������Դ����
	 */
	public void initData() {
		// ������Դ��ȡ���ݲ�����
		factory.loadData();
	}

	/**
	 * ������пɴ�·��,������ʱ���浽����������ظ���
	 * 
	 * @param srcName
	 *            Դ����IP
	 * @param dstName
	 *            Ŀ������IP
	 * @return
	 */
	public ArrayList<TempPath> getAllPaths(String srcName, String dstName) {
		// ʹ��·�������������·����Ȼ���䱣�浽���ݣ��ٽ��䷵��
		return factory.createAndReturnTempPaths(pathCalculator.calculate(srcName, dstName, factory.getPathData()));
	}

	/**
	 * ��ӡȫ���������·��
	 */
	public void printAllTempPaths() {
		factory.getTempPaths().stream().forEach(temp -> System.out.println(temp));
	}

	/**
	 * ����ʱ�����·���������򣬱��棬�����ظ���
	 * 
	 * @return
	 */
	public ArrayList<TempPath> sortTempPaths() {
		ArrayList<TempPath> tempPaths = factory.getTempPaths();
		Collections.sort(tempPaths);
		return factory.saveAndReturnTempPaths(tempPaths);
	}

	/**
	 * ������ʱ·����˲ʱ����ռ����
	 * 
	 * @return
	 */
	public ArrayList<TempPath> rateCalculate() {
		HashMap<String, Aggregate> aggregatesLast = factory.getAggregatesMap();
		long last = System.currentTimeMillis();
		ArrayList<TempPath> tempPaths = factory.getTempPaths();
		tempPaths.forEach(path -> {
			path.occupancyRate = 0;
		});
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		HashMap<String, Aggregate> aggregatesMapNew = factory.getAggregatesMap();
		final long time = System.currentTimeMillis() - last;
		tempPaths.forEach(temp -> {
			for (int i = 3; i < temp.paths.length - 2; i++) {
				int bandwidth = getBandwidth(temp.paths[i], temp.paths[i + 1]);
				long packetLeft = aggregatesMapNew.get(temp.paths[i]).packetCount
						- aggregatesLast.get(temp.paths[i]).packetCount;
				long packetRight = aggregatesMapNew.get(temp.paths[i + 1]).packetCount
						- aggregatesLast.get(temp.paths[i + 1]).packetCount;
				long packet = packetLeft > packetRight ? packetLeft : packetRight;
				if (((float) temp.occupancyRate) / temp.time / temp.bandwidth < ((float) packet) / time / bandwidth) {
					temp.occupancyRate = packet;
					temp.time = time;
					temp.bandwidth = bandwidth;
				}
			}
		});
		return factory.saveAndReturnTempPaths(tempPaths);
	}

	/**
	 * ���ԴĿ�굽Ŀ��Ŀ��Ĵ���,ֻ�ǽ�������ģ�������������֮�䲻����
	 * 
	 * @param srcId
	 * @param dstId
	 * @return
	 */
	private Integer getBandwidth(String srcId, String dstId) {
		HashMap<String, Link> links = factory.getLinksMap();
		if (links.containsKey(srcId + "--" + dstId)) {
			return links.get(srcId + "--" + dstId).bandwidth;
		} else {
			return links.get(dstId + "--" + srcId).bandwidth;
		}
	}

	/**
	 * �·�·��·�ɱ�
	 * 
	 * @param paths
	 */
	public void pushPath(String[] paths) {
		// ���·��û�����ݣ�ֱ������
		if (paths == null || paths.length <= 2) {
			return;
		}
		ArrayList<Flow> flows = new ArrayList<>();
		// ǰ����������IP
		Device srcDevice = factory.getDevicesMap().get(paths[0]);
		Device dstDevice = factory.getDevicesMap().get(paths[1]);
		// ��������Ϣ�п��Ի�������ӵ��ǽ��������ĸ��˿�
		int ingressPort = srcDevice.attachmentPoints[0].port;
		int outPort;
		// �����·��ӳ�伯�ϣ�Դ������--Ŀ�꽻������->(��·��
		HashMap<String, Link> linksMap = factory.getLinksMap();
		// ��ʾ��Ϣ������Ϊpaths�����ݽṹ
		// paths����"Դ����IP","Ŀ������IP","Դ����IP","�ڶ����ڵ�","�������ڵ�"..."�����ڶ����ڵ�","Ŀ������IP"��
		for (int i = 3; i < paths.length - 2; i++) {
			int newIngressPort;
			if (linksMap.containsKey(paths[i] + "--" + paths[i + 1])) {
				// �����ڵ�֮�����·����Դ�˿�Ϊ��һ���������ڲ�ת����Ŀ��˿ڣ���Ŀ�Ķ˿�Ϊ�ڶ����������ڲ�ת����Դ�˿�
				outPort = linksMap.get(paths[i] + "--" + paths[i + 1]).src_port;
				newIngressPort = linksMap.get(paths[i] + "--" + paths[i + 1]).dst_port;
			} else {
				outPort = linksMap.get(paths[i + 1] + "--" + paths[i]).dst_port;
				newIngressPort = linksMap.get(paths[i + 1] + "--" + paths[i]).src_port;
			}
			System.out.println("in:" + ingressPort + " sw:" + paths[i] + " out:" + outPort);
			flows.add(new Flow(i - 3, paths[i], ingressPort, outPort, srcDevice, dstDevice));
			flows.add(new Flow(i - 3, paths[i], outPort, ingressPort, dstDevice, srcDevice));
			ingressPort = newIngressPort;
		}
		outPort = dstDevice.attachmentPoints[0].port;
		flows.add(new Flow(paths.length - 5, paths[paths.length - 2], ingressPort, outPort, srcDevice, dstDevice));
		flows.add(new Flow(paths.length - 5, paths[paths.length - 2], outPort, ingressPort, dstDevice, srcDevice));
		// �·�����
		factory.pushFlows(flows);
	}

	/**
	 * ����Flowingģ�鿪��״̬
	 * 
	 * @param b
	 * @return
	 */
	public boolean setFlowingModState(boolean b) {
		boolean state = factory.isForwardingOpen();
		if (state == b) {
			return b;
		} else {
			return factory.setForwardingSwitch(b);
		}
	}

	/**
	 * �������
	 * 
	 */
	public void clearFlows() {
		factory.clearFlows();
	}

	/**
	 * ��ô�ͳ���罻���������ܺ�
	 * 
	 * @return
	 */
	public int getTraditionEnergy() {
		return energyCalculator.countEnergy(factory.getDevices(), factory.getFeatures(), factory.getLinks());
	}

	/**
	 * ���SDN���罻���������ܺ�
	 * 
	 * @return
	 */
	public int getSDNEnergy() {
		return energyCalculator.countEnergy(factory.getDevices(), factory.getFlows());
	}

	/**
	 * ���SDN���絥�������������ܺ�
	 * 
	 * @return
	 */
	public HashMap<String, Integer> getSingleSDNEnergy() {
		HashMap<String, Integer> energyMap = new HashMap<>();
		HashMap<String, FloodlightSwitchFlows> flowsMap = factory.getFlowsMap();
		List<Device> devices = factory.getDevices();
		List<Feature> features = factory.getFeatures();
		for (Feature feature : features) {
			String key = feature.datapathId;
			energyMap.put(key, energyCalculator.countEnergy(devices, flowsMap, key));
		}
		return energyMap;
	}

	/**
	 * �������ʹ�õ�ǰ·����Ҫ�»��ѵĽ�������
	 * 
	 * @return
	 */
	public ArrayList<TempPath> increaseCal() {
		ArrayList<TempPath> tempPaths = factory.getTempPaths();
		HashMap<String, FloodlightSwitchFlows> flowsMap = factory.getFlowsMap();
		tempPaths.forEach(temp -> {
			int num = 0;
			// ֻ����������������������
			for (int i = 3; i < temp.paths.length - 1; i++) {
				if (flowsMap.containsKey(temp.paths[i]) && flowsMap.get(temp.paths[i]).floodlightFlows.length == 0) {
					num++;
				}
			}
			temp.switchIncreaseNum = num;
		});
		return factory.saveAndReturnTempPaths(tempPaths);
	}
}
