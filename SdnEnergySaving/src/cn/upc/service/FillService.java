package cn.upc.service;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import org.jfree.data.category.DefaultCategoryDataset;

import cn.upc.dao.DataFactory;
import cn.upc.entity.Aggregate;
import cn.upc.entity.Device;
import cn.upc.entity.Feature;
import cn.upc.entity.FloodlightSwitchFlows;
import cn.upc.entity.FloodlightSwitchFlows.FloodlightFlow;
import cn.upc.entity.Switch;
import cn.upc.entity.TempPath;
import cn.upc.res.R;
import cn.upc.res.Util;
import cn.upc.ui.UiFactory;
import twaver.Element;
import twaver.Link;
import twaver.Node;
import twaver.TDataBox;

/**
 * ������������Ҫ����������Ϣչʾ���ؼ���
 * 
 * @author ����չ
 *
 */
public class FillService {
	private UiFactory uiFactory;
	private DataFactory dataFactory;

	public FillService() {
		uiFactory = UiFactory.newInstence();
		dataFactory = DataFactory.newInstence();
	}

	/**
	 * ���������ж���������������
	 */
	public void fillDataInBox() {
		R.LOGGER.info("�������������������ݵ�����������");
		fillSwitchInBox();
		R.LOGGER.info("�������������뽻����֮����·�������������ݵ�����������");
		fillDeviceLinkInBox();
		R.LOGGER.info("��������֮����·�������������ݵ�����������");
		fillLinkDataInBox();
		R.LOGGER.info("��ʼ��������ʾ����·״̬");
		clearFlowing();
	}

	/**
	 * ������������ӽ�����������
	 * 
	 * @param features
	 */
	private void fillSwitchInBox() {
		HashMap<String, Feature> features = dataFactory.getFeaturesMap();
		// ������������Ϣ����Map�����е�keyΪ��������DPID(datapathId)
		for (String datapathId : features.keySet()) {
			// ��������box�Ľڵ㣬switch����������ΪJAVA�ؼ��֣�����ʹ��sw(����ʹ�õ��Ƕ��㽻����)
			// new Node()������Ҫ�Ĳ���Ϊ�ڵ��ID,�Ժ������getElementByID�������������ڵ�
			Node sw = new Node(datapathId);
			// ������ʾͼƬ���ַ�����url��ʽ
			sw.setImage(R.FRAME_CFG.cardCfg.switchUrl);
			// �������ƣ������ƽ�����������ʾ������ʹ��renameֻ�������ò���
			sw.setName(Util.rename(datapathId));
			// ��box����Ӵ˽ڵ�
			addElementToBox(sw);
			R.LOGGER.info("�������ڵ����� " + datapathId);
		}
	}

	/**
	 * ������������������������뽻����֮����·����������
	 */
	private void fillDeviceLinkInBox() {
		TDataBox box = uiFactory.getDataBox();
		ArrayList<Device> devices = dataFactory.getDevices();
		// ����������Ϣ
		for (Device device : devices) {
			// ��������Ϣ�м�С���ʰ���һЩ������������Ҫ���ˣ�����ԭ����
			Node host = new Node(device.ipv4s[0]);
			if (device.ipv4s == null) {
				if (device.macs != null) {// ��ʱIPΪ�գ�mininet�������ݣ���floodlight�ж�������
					host = new Node(device.macs[0]);
				} else {
					continue;
				}
			} else {
				// ���������ڵ㣬ͬʱ��IPV4��ΪID
				host = new Node(device.ipv4s[0]);
			}
			host.setImage(R.FRAME_CFG.cardCfg.hostUrl);
			host.setName(Util.rename(device.ipv4s[0]));
			addElementToBox(host);
			R.LOGGER.info("�����ڵ����� " + device.ipv4s[0]);
			// ���¿�ʼ���������뽻��������·��ͬʱ��·�ٶȱ�����������AttachmentPoints��
			String src = device.ipv4s[0];// ����IPV4
			String dst = device.attachmentPoints[0].switchDPID;// ������DPID
			// new Link(link��ID��Դ�ڵ㣬Ŀ��ڵ�)
			Link link = new Link(src + "--" + dst, host, (Node) box.getElementByID(dst));
			link.setName(device.attachmentPoints[0].bandwidth + "Mbps");
			addElementToBox(link);
			R.LOGGER.info("�����󶨶˿���·���� " + src + "--" + dst + " ���� " + device.attachmentPoints[0].bandwidth + "Mbps");
		}
	}

	/**
	 * ������������ӽ�����֮����·����������
	 */
	private void fillLinkDataInBox() {
		TDataBox box = uiFactory.getDataBox();
		ArrayList<cn.upc.entity.Link> links = dataFactory.getLinks();
		for (cn.upc.entity.Link linkEntity : links) {
			// ���캯��Link(��·ID,Դ�ڵ�,Ŀ��ڵ�);
			Link link = new Link(linkEntity.src_switch + "--" + linkEntity.dst_switch,
					(Node) box.getElementByID(linkEntity.src_switch), (Node) box.getElementByID(linkEntity.dst_switch));
			// ʹ���ٶ�����������
			link.setName(linkEntity.bandwidth + "Mbps");
			box.addElement(link);
			R.LOGGER.info("������֮����·���� " + linkEntity.src_switch + "--" + linkEntity.dst_switch + " ���� "
					+ linkEntity.bandwidth + "Mbps");
		}
	}

	/**
	 * �������������Ŀ���������¶���û�У�����Ӷ���
	 * 
	 * @param ele
	 */
	private void addElementToBox(Element ele) {
		TDataBox box = uiFactory.getDataBox();
		if (box.containsByID(ele.getID())) {
			Element old = box.removeElementByID(ele.getID());
			ele.setLocation(old.getLocation());
			box.addElement(ele);
		} else {
			box.addElement(ele);
		}
	}

	/**
	 * ��ʼ������չʾ����нڵ��λ��
	 */
	public void initElementLocation() {
		TDataBox box = uiFactory.getDataBox();
		@SuppressWarnings("unchecked")
		List<Element> eles = box.getAllElements();
		Iterator<Element> itr = eles.iterator();
		// ���ۺ��ִ洢�ṹ���������ܻ��ṩһ�־�����Ч�ʸߵı�����ʽ�����һ�������ɾ�����µĲ��������쳣
		while (itr.hasNext()) {
			Element ele = itr.next();
			if (ele instanceof Node) {
				ele.setLocation(R.DATA_CFG.locationCfg.get(ele.getID().toString()));
			}
		}
	}

	/**
	 * ��������·�ı���ɫ���������������
	 * 
	 * @param paths
	 */
	public void flowingLing(String[] paths) {
		for (int i = 2; i < paths.length - 1; i++) {
			flowingLink(paths[i], paths[i + 1]);
		}
	}

	/**
	 * ������������·�ı���ɫ���������������
	 * 
	 * @param src
	 * @param dst
	 */
	private void flowingLink(String src, String dst) {
		TDataBox box = uiFactory.getDataBox();
		Link link;
		if (box.containsByID(src + "--" + dst)) {
			// �����·������ԴĿ��--Ŀ��Ŀ�����·��ֱ�ӻ�ȡ
			link = (Link) box.getElementByID(src + "--" + dst);
		} else {
			// ��������ڣ��ǿ϶��Ƿ���ģ�ֱ�ӻ�ȡ�Ϳ���
			Link old = (Link) box.getElementByID(dst + "--" + src);
			box.removeElement(old);
			link = new Link(src + "--" + dst, (Node) box.getElementByID(src), (Node) box.getElementByID(dst));
			link.setName(old.getName());
			box.addElement(link);
		}
		link.putLinkFlowing(true);
		link.putLinkFlowingColor(Color.GREEN);
		link.putLinkOutlineColor(Color.BLACK);
		link.putLinkColor(Color.RED);
		link.putLabelColor(Color.RED);
	}

	/**
	 * ���ı����������Ϣ
	 * 
	 * @param msg
	 */
	public void fieldSetMsg(String msg) {
		uiFactory.getPathField().setText(msg);
	}

	/**
	 * ������в�ɫ����Ч��������ʼ����·��ʾ״̬
	 */
	public void clearFlowing() {
		TDataBox box = uiFactory.getDataBox();
		@SuppressWarnings("unchecked")
		List<Link> list = box.getElementsByType(Link.class);
		list.forEach(link -> {
			link.putLinkFlowing(false);
			link.putLinkFlowingColor(Color.GREEN);
			link.putLinkOutlineColor(Color.BLACK);
			link.putLinkColor(Color.LIGHT_GRAY);
			link.putLabelColor(Color.BLACK);
		});
	}

	private static long time;
	private static HashMap<String, Aggregate> aggregatesMap;

	/**
	 * ��������������䵽����ͼ��
	 */
	public void fillLineChart() {
		if (aggregatesMap == null) {
			aggregatesMap = dataFactory.getAggregatesMap();
			time = System.currentTimeMillis();
			return;
		}
		DefaultCategoryDataset dataset = uiFactory.getPacketDataset();
		ArrayList<Switch> switchsNew = dataFactory.getSwitchs();
		HashMap<String, Aggregate> aggregatesMapNew = dataFactory.getAggregatesMap();
		long newTime = System.currentTimeMillis();
		switchsNew.forEach(sw -> {
			long packetNum = aggregatesMapNew.get(sw.dpid).packetCount - aggregatesMap.get(sw.dpid).packetCount;
			dataset.addValue(packetNum * 1000 / (newTime - time), "����·��", Util.rename(sw.dpid));
		});
		aggregatesMap = aggregatesMapNew;
		time = newTime;
	}

	/**
	 * �л���Ƭ���ĵ�ǰ��ʾ���
	 * 
	 * @param index
	 *            ��Ƭ����λ��
	 */
	public void showPanel(int index) {
		JPanel panel = uiFactory.getCenterPanel();
		CardLayout layout = uiFactory.getCardLayout();
		EventQueue.invokeLater(() -> layout.show(panel, R.FRAME_CFG.cardCfg.cardNames[index]));
	}

	/**
	 * ��·���ı����л���ı����ݣ�Ȼ�����������IPĩλ����
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Integer> getHostNums() throws Exception {
		String text = uiFactory.getPathField().getText();
		ArrayList<Integer> nameNums = new ArrayList<>();
		for (String cell : text.split(" ")) {
			String[] nums = cell.split("-");
			if (nums[0].equals(nums[1])) {
				continue;
			}
			for (String num : nums) {
				int hostNum = Integer.parseInt(num.trim());
				if (hostNum < 1 || hostNum > 8) {
					throw new RuntimeException();
				}
				nameNums.add(hostNum);
			}

		}
		return nameNums;
	}

	/**
	 * ��·��չʾ�б������·��
	 * 
	 * @param paths
	 */
	public void setPathList(ArrayList<TempPath> paths) {
		DefaultListModel<String> model = uiFactory.getPathListModel();
		model.clear();
		paths.forEach(temp -> {
			StringBuilder sb = new StringBuilder();
			sb.append("RATE��").append(temp.getRateDescription()).append(" ");
			sb.append("INCE��").append(temp.getIncreaseDescription()).append("   ");
			for (int i = 2; i < temp.paths.length - 1; i++) {
				sb.append(Util.rename(temp.paths[i])).append(" --> ");
			}
			sb.append(Util.rename(temp.paths[temp.paths.length - 1]));
			model.addElement(sb.toString());
		});
	}

	/**
	 * ����ɴ�·��������·��չʾ����չʾ
	 */
	public void listReachablePath() {
		try {
			// ���ı����л������ͨѶ��
			ArrayList<Integer> hostNums = getHostNums();
			if (hostNums.size() != 2) {
				R.LOGGER.warning("��ǰ��������������Ϊ����ͨ�ţ��ܾ�����");
				return;
			}
			ArrayList<TempPath> reachablePaths = new DataService().getAllPaths("H" + hostNums.get(0),
					"H" + hostNums.get(1));
			setPathList(reachablePaths);
		} catch (Exception e1) {
			R.LOGGER.error("���пɴ�·�������쳣���ܾ�����");
		}
	}

	/**
	 * ��ѡ�е�·����չʾ������ͼ��
	 */
	public void previewSelectedPath() {
		int index = uiFactory.getPathList().getSelectedIndex();
		ArrayList<TempPath> tempPaths = dataFactory.getTempPaths();
		if (index >= 0 && index < tempPaths.size()) {
			clearFlowing();
			flowingLing(tempPaths.get(index).paths);
			showPanel(0);
		} else {
			R.LOGGER.warning("Ԥ��ʧ�ܣ���ǰѡ���±�Ϊ��" + index);
		}
	}

	/**
	 * ��ѡ�е�·������
	 */
	public void pushSelectedPath() {
		int index = uiFactory.getPathList().getSelectedIndex();
		ArrayList<TempPath> tempPaths = dataFactory.getTempPaths();
		if (index >= 0 && index < tempPaths.size()) {
			DataService dataService = new DataService();
			dataService.pushPath(tempPaths.get(index).paths);
		} else {
			R.LOGGER.warning("����ʧ�ܣ���ǰѡ���±�Ϊ��" + index);
		}
	}

	private static boolean flowViewOpen = false;

	/**
	 * ����������ͼ
	 * 
	 * @return
	 */
	public void switchFlowView() {
		if (flowViewOpen) {
			clearFlowing();
			flowViewOpen = false;
			R.LOGGER.info("������ͼ�ر�");
			return;
		}
		clearFlowing();
		Map<String, FloodlightSwitchFlows> flows = dataFactory.getFlowsMap();
		List<Device> devices = dataFactory.getDevices();
		List<cn.upc.entity.Link> links = dataFactory.getLinks();
		for (int i = 0; i < devices.size(); i++) {
			String dpid = devices.get(i).attachmentPoints[0].switchDPID;
			int port = devices.get(i).attachmentPoints[0].port;
			if (flows.containsKey(dpid)) {
				FloodlightFlow[] floodlightFlows = flows.get(dpid).floodlightFlows;
				for (FloodlightFlow flow : floodlightFlows) {
					if (flow.match.inputPort == port || flow.actions[0].port == port) {
						flowingLink(devices.get(i).ipv4s[0], dpid);
					}
				}
			}
		}
		for (int i = 0; i < links.size(); i++) {
			String dpid = links.get(i).src_switch;
			int port = links.get(i).src_port;
			if (flows.containsKey(dpid)) {
				FloodlightFlow[] floodlightFlows = flows.get(dpid).floodlightFlows;
				for (FloodlightFlow flow : floodlightFlows) {
					if (flow.match.inputPort == port || flow.actions[0].port == port) {
						flowingLink(dpid, links.get(i).dst_switch);
					}
				}
			}
		}
		flowViewOpen = true;
		R.LOGGER.info("������ͼ����");
	}

	/**
	 * ����ܺĶԱ�ͼ
	 */
	public void fillBarChart() {
		DefaultCategoryDataset dataset = uiFactory.getEnergyDataset();
		DataService service = new DataService();
		int tradition = service.getTraditionEnergy();
		int sdn = service.getSDNEnergy();
		dataset.addValue(tradition, "����·��", "����·��");
		dataset.addValue(sdn, "����·��", "����·��");
	}

	/**
	 * ��䵥���������ܺĶԱ�ͼ
	 */
	public void fileSingleBarChart() {
		DefaultCategoryDataset dataset = uiFactory.getSingleEnergyDataset();
		HashMap<String, Integer> energyMap = new DataService().getSingleSDNEnergy();
		List<Map.Entry<String, Integer>> list = new ArrayList<>(energyMap.entrySet());
		Collections.sort(list, (a, b) -> a.getKey().compareTo(b.getKey()));
		for (Map.Entry<String, Integer> entry : list) {
			dataset.addValue(entry.getValue(), Util.rename(entry.getKey()), Util.rename(entry.getKey()));
		}
	}

	/**
	 * �Զ�����ʱ����״̬�б��������Ϣ,����ȷ��ͬ���᲻�������⣩
	 * 
	 * @param info
	 */
	public synchronized void addInfo(String info) {
		uiFactory.getAutoControlMessageListModel().addElement(info);
	}
}
