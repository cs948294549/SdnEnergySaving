package cn.upc.calculator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.upc.entity.Device;
import cn.upc.entity.Feature;
import cn.upc.entity.FloodlightSwitchFlows;
import cn.upc.entity.FloodlightSwitchFlows.FloodlightFlow;
import cn.upc.entity.Link;

/**
 * 简化能耗模型
 * 
 * P(C)=F(C)+SUM(A(C)) (3-3)
 * 
 * P(C)=F(C)+A*X (3-4)
 * 
 * 简化能耗模型的缺点是将会引入能耗计算误差。使用公式 (3-3)
 * 计算得到的能耗值将大于交换机的实际能耗值，这是因为简化能耗模型忽略了网络流量对网络设备能耗的影响。
 * 然而与网络设备的总能耗相比，网络设备在不同流量下的能耗差异很小，因 此使用简化能耗模型所引入的能耗计算误差可以忽略不计。 在公式 (3-3) 中，
 * 如果交换机接口的能耗均为 A ，则能耗模型可以进一步简化为公式 (3-4)
 * 
 * @author 江荣展
 *
 */
public class SimpleEnergyCalculator {

	private final int tradition;
	private final int sdn;
	private final int switchCount;
	private final int portCount;

	public SimpleEnergyCalculator(Properties prpt) {
		this.tradition = Integer.parseInt((String) prpt.get("tradition"));
		this.sdn = Integer.parseInt((String) prpt.get("sdn"));
		this.switchCount = Integer.parseInt((String) prpt.get("switch"));
		this.portCount = Integer.parseInt((String) prpt.get("port"));
	}

	/**
	 * 根据物理链路图计算传统路由所需要的能量
	 * 
	 * @param devices
	 *            主机设备
	 * @param features
	 *            交换机设备
	 * @param links
	 *            链路
	 * @return
	 */
	public int countEnergy(List<Device> devices, List<Feature> features, List<Link> links) {
		int energy = 0;
		// 传统路由的固有耗能计量值
		energy += tradition;
		// 交换机数量*交换机固有耗能计量值
		energy += features.size() * switchCount;
		// （链路数量*每条链路有两个端口+交换机与主机通信的端口数量）*端口固有耗能计量值
		energy += (links.size() * 2 + devices.size()) * portCount;
		return energy;
	}

	/**
	 * 根据物理链路图和交换机当前流量状态图计算SDN路由所需要的能量
	 * 
	 * @param devices
	 *            主机设备
	 * @param flows
	 *            流表信息
	 * @return
	 */
	public int countEnergy(List<Device> devices, List<FloodlightSwitchFlows> flows) {
		int energy = 0;
		// SDN路由的固有耗能计量值
		energy += sdn;
		// 统计排重所使用的交换机数与交换机上的端口数目
		HashSet<String> sws = new HashSet<>();
		HashSet<String> ports = new HashSet<>();
		devices.forEach(device -> {
			String dpid = device.attachmentPoints[0].switchDPID;
			int port = device.attachmentPoints[0].port;
			sws.add(dpid);
			ports.add(dpid + "-" + port);
		});
		flows.forEach(flow -> {
			if (flow.floodlightFlows.length == 0) {
				return;
			}
			sws.add(flow.datapathId);
			for (int i = 0; i < flow.floodlightFlows.length; i++) {
				ports.add(flow.datapathId + "-" + flow.floodlightFlows[i].match.inputPort);
				ports.add(flow.datapathId + "-" + flow.floodlightFlows[i].actions[0].port);
			}
		});
		// 交换机数量*交换机固有耗能计量值
		energy += sws.size() * switchCount;
		// 端口数量*端口固有耗能计量值
		energy += ports.size() * portCount;
		return energy;
	}

	/**
	 * 计算单个SDN交换机的能耗
	 * 
	 * @param devices
	 *            设备信息
	 * @param flowsMap
	 *            流表信息映射
	 * @param dpid
	 *            要计算的交换机的DPID
	 * @return
	 */
	public int countEnergy(List<Device> devices, Map<String, FloodlightSwitchFlows> flowsMap, String dpid) {
		int energy = 0;
		HashSet<String> ports = new HashSet<>();
		devices.forEach(device -> {
			if (device.attachmentPoints[0].switchDPID.equals(dpid)) {
				ports.add(dpid + "-" + device.attachmentPoints[0].port);
			}
		});
		if (flowsMap.containsKey(dpid)) {
			for (int i = 0; i < flowsMap.get(dpid).floodlightFlows.length; i++) {
				FloodlightFlow floodlightFlow = flowsMap.get(dpid).floodlightFlows[i];
				ports.add(dpid + "-" + floodlightFlow.match.inputPort);
				ports.add(dpid + "-" + floodlightFlow.actions[0].port);
			}
		}
		if (ports.size() == 0) {
			return 0;
		}
		// 交换机固有耗能计量值
		energy += switchCount;
		// 端口数量*端口固有耗能计量值
		energy += ports.size() * portCount;
		return energy;
	}
}
