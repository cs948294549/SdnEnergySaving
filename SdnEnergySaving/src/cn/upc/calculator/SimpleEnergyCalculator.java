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
 * ���ܺ�ģ��
 * 
 * P(C)=F(C)+SUM(A(C)) (3-3)
 * 
 * P(C)=F(C)+A*X (3-4)
 * 
 * ���ܺ�ģ�͵�ȱ���ǽ��������ܺļ�����ʹ�ù�ʽ (3-3)
 * ����õ����ܺ�ֵ�����ڽ�������ʵ���ܺ�ֵ��������Ϊ���ܺ�ģ�ͺ��������������������豸�ܺĵ�Ӱ�졣
 * Ȼ���������豸�����ܺ���ȣ������豸�ڲ�ͬ�����µ��ܺĲ����С���� ��ʹ�ü��ܺ�ģ����������ܺļ��������Ժ��Բ��ơ� �ڹ�ʽ (3-3) �У�
 * ����������ӿڵ��ܺľ�Ϊ A �����ܺ�ģ�Ϳ��Խ�һ����Ϊ��ʽ (3-4)
 * 
 * @author ����չ
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
	 * ����������·ͼ���㴫ͳ·������Ҫ������
	 * 
	 * @param devices
	 *            �����豸
	 * @param features
	 *            �������豸
	 * @param links
	 *            ��·
	 * @return
	 */
	public int countEnergy(List<Device> devices, List<Feature> features, List<Link> links) {
		int energy = 0;
		// ��ͳ·�ɵĹ��к��ܼ���ֵ
		energy += tradition;
		// ����������*���������к��ܼ���ֵ
		energy += features.size() * switchCount;
		// ����·����*ÿ����·�������˿�+������������ͨ�ŵĶ˿�������*�˿ڹ��к��ܼ���ֵ
		energy += (links.size() * 2 + devices.size()) * portCount;
		return energy;
	}

	/**
	 * ����������·ͼ�ͽ�������ǰ����״̬ͼ����SDN·������Ҫ������
	 * 
	 * @param devices
	 *            �����豸
	 * @param flows
	 *            ������Ϣ
	 * @return
	 */
	public int countEnergy(List<Device> devices, List<FloodlightSwitchFlows> flows) {
		int energy = 0;
		// SDN·�ɵĹ��к��ܼ���ֵ
		energy += sdn;
		// ͳ��������ʹ�õĽ��������뽻�����ϵĶ˿���Ŀ
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
		// ����������*���������к��ܼ���ֵ
		energy += sws.size() * switchCount;
		// �˿�����*�˿ڹ��к��ܼ���ֵ
		energy += ports.size() * portCount;
		return energy;
	}

	/**
	 * ���㵥��SDN���������ܺ�
	 * 
	 * @param devices
	 *            �豸��Ϣ
	 * @param flowsMap
	 *            ������Ϣӳ��
	 * @param dpid
	 *            Ҫ����Ľ�������DPID
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
		// ���������к��ܼ���ֵ
		energy += switchCount;
		// �˿�����*�˿ڹ��к��ܼ���ֵ
		energy += ports.size() * portCount;
		return energy;
	}
}
