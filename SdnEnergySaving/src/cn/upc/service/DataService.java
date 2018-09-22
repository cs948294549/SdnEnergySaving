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
 * 数据服务，主要处理数据信息的更新获取
 * 
 * @author 江荣展
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
	 * 初始化数据源数据
	 */
	public void deepInitData() {
		// 更新数据源
		factory.updateDataSource();
		// 从数据源获取数据并处理
		factory.loadData();
	}

	/**
	 * 初始化数据源数据
	 */
	public void initData() {
		// 从数据源获取数据并处理
		factory.loadData();
	}

	/**
	 * 获得所有可达路径,将其临时保存到工厂里，并返回副本
	 * 
	 * @param srcName
	 *            源主机IP
	 * @param dstName
	 *            目标主机IP
	 * @return
	 */
	public ArrayList<TempPath> getAllPaths(String srcName, String dstName) {
		// 使用路径计算器计算出路径，然后将其保存到数据，再将其返回
		return factory.createAndReturnTempPaths(pathCalculator.calculate(srcName, dstName, factory.getPathData()));
	}

	/**
	 * 打印全部计算出的路径
	 */
	public void printAllTempPaths() {
		factory.getTempPaths().stream().forEach(temp -> System.out.println(temp));
	}

	/**
	 * 对临时保存的路径进行排序，保存，并返回副本
	 * 
	 * @return
	 */
	public ArrayList<TempPath> sortTempPaths() {
		ArrayList<TempPath> tempPaths = factory.getTempPaths();
		Collections.sort(tempPaths);
		return factory.saveAndReturnTempPaths(tempPaths);
	}

	/**
	 * 计算临时路径的瞬时带宽占用率
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
	 * 获得源目标到目的目标的带宽,只是交换机间的，交换机与主机之间不计算
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
	 * 下发路径路由表
	 * 
	 * @param paths
	 */
	public void pushPath(String[] paths) {
		// 如果路径没有数据，直接跳出
		if (paths == null || paths.length <= 2) {
			return;
		}
		ArrayList<Flow> flows = new ArrayList<>();
		// 前两个是主机IP
		Device srcDevice = factory.getDevicesMap().get(paths[0]);
		Device dstDevice = factory.getDevicesMap().get(paths[1]);
		// 从主机信息中可以获得所连接的是交换机的哪个端口
		int ingressPort = srcDevice.attachmentPoints[0].port;
		int outPort;
		// 获得链路的映射集合（源交换机--目标交换机）->(链路）
		HashMap<String, Link> linksMap = factory.getLinksMap();
		// 提示信息，以下为paths的内容结构
		// paths：｛"源主机IP","目标主机IP","源主机IP","第二个节点","第三个节点"..."倒数第二个节点","目标主机IP"｝
		for (int i = 3; i < paths.length - 2; i++) {
			int newIngressPort;
			if (linksMap.containsKey(paths[i] + "--" + paths[i + 1])) {
				// 二三节点之间的链路，其源端口为第一个交换机内部转发的目标端口；其目的端口为第二个交换机内部转发的源端口
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
		// 下发流表
		factory.pushFlows(flows);
	}

	/**
	 * 设置Flowing模块开关状态
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
	 * 清空流表
	 * 
	 */
	public void clearFlows() {
		factory.clearFlows();
	}

	/**
	 * 获得传统网络交换机的总能耗
	 * 
	 * @return
	 */
	public int getTraditionEnergy() {
		return energyCalculator.countEnergy(factory.getDevices(), factory.getFeatures(), factory.getLinks());
	}

	/**
	 * 获得SDN网络交换机的总能耗
	 * 
	 * @return
	 */
	public int getSDNEnergy() {
		return energyCalculator.countEnergy(factory.getDevices(), factory.getFlows());
	}

	/**
	 * 获得SDN网络单个交换机的总能耗
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
	 * 计算如果使用当前路径，要新唤醒的交换机数
	 * 
	 * @return
	 */
	public ArrayList<TempPath> increaseCal() {
		ArrayList<TempPath> tempPaths = factory.getTempPaths();
		HashMap<String, FloodlightSwitchFlows> flowsMap = factory.getFlowsMap();
		tempPaths.forEach(temp -> {
			int num = 0;
			// 只遍历交换机，不遍历主机
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
