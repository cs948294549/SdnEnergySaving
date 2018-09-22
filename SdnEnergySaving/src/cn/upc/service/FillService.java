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
 * 数据填充服务，主要处理将数据信息展示到控件上
 * 
 * @author 江荣展
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
	 * 向数据箱中定入物理连接数据
	 */
	public void fillDataInBox() {
		R.LOGGER.info("将交换机物理连接数据导入数据箱中");
		fillSwitchInBox();
		R.LOGGER.info("将主机与主机与交换机之间链路的物理连接数据导入数据箱中");
		fillDeviceLinkInBox();
		R.LOGGER.info("将交换机之间链路的物理连接数据导入数据箱中");
		fillLinkDataInBox();
		R.LOGGER.info("初始化拓扑显示的链路状态");
		clearFlowing();
	}

	/**
	 * 向数据箱中添加交换机的数据
	 * 
	 * @param features
	 */
	private void fillSwitchInBox() {
		HashMap<String, Feature> features = dataFactory.getFeaturesMap();
		// 遍历交换机信息，此Map集合中的key为交换机的DPID(datapathId)
		for (String datapathId : features.keySet()) {
			// 创建用于box的节点，switch（交换机）为JAVA关键字，所以使用sw(我们使用的是二层交换机)
			// new Node()中所需要的参数为节点的ID,以后可以用getElementByID方法来获得这个节点
			Node sw = new Node(datapathId);
			// 设置显示图片，字符串是url格式
			sw.setImage(R.FRAME_CFG.cardCfg.switchUrl);
			// 设置名称，此名称将用于拓扑显示，所以使用rename只保留有用部分
			sw.setName(Util.rename(datapathId));
			// 向box中添加此节点
			addElementToBox(sw);
			R.LOGGER.info("交换机节点数据 " + datapathId);
		}
	}

	/**
	 * 向数据箱中添加主机，主机与交换机之间链路的物理数据
	 */
	private void fillDeviceLinkInBox() {
		TDataBox box = uiFactory.getDataBox();
		ArrayList<Device> devices = dataFactory.getDevices();
		// 遍历主机信息
		for (Device device : devices) {
			// 在主机信息中极小几率包含一些孤立主机，需要过滤，具体原因不明
			Node host = new Node(device.ipv4s[0]);
			if (device.ipv4s == null) {
				if (device.macs != null) {// 有时IP为空，mininet中有数据，但floodlight中读不出来
					host = new Node(device.macs[0]);
				} else {
					continue;
				}
			} else {
				// 创建主机节点，同时将IPV4作为ID
				host = new Node(device.ipv4s[0]);
			}
			host.setImage(R.FRAME_CFG.cardCfg.hostUrl);
			host.setName(Util.rename(device.ipv4s[0]));
			addElementToBox(host);
			R.LOGGER.info("主机节点数据 " + device.ipv4s[0]);
			// 以下开始创建主机与交换机的链路，同时链路速度保存在主机的AttachmentPoints中
			String src = device.ipv4s[0];// 主机IPV4
			String dst = device.attachmentPoints[0].switchDPID;// 交换机DPID
			// new Link(link的ID，源节点，目标节点)
			Link link = new Link(src + "--" + dst, host, (Node) box.getElementByID(dst));
			link.setName(device.attachmentPoints[0].bandwidth + "Mbps");
			addElementToBox(link);
			R.LOGGER.info("主机绑定端口链路数据 " + src + "--" + dst + " 带宽 " + device.attachmentPoints[0].bandwidth + "Mbps");
		}
	}

	/**
	 * 向数据箱中添加交换机之间链路的物理数据
	 */
	private void fillLinkDataInBox() {
		TDataBox box = uiFactory.getDataBox();
		ArrayList<cn.upc.entity.Link> links = dataFactory.getLinks();
		for (cn.upc.entity.Link linkEntity : links) {
			// 构造函数Link(链路ID,源节点,目标节点);
			Link link = new Link(linkEntity.src_switch + "--" + linkEntity.dst_switch,
					(Node) box.getElementByID(linkEntity.src_switch), (Node) box.getElementByID(linkEntity.dst_switch));
			// 使用速度来代替名称
			link.setName(linkEntity.bandwidth + "Mbps");
			box.addElement(link);
			R.LOGGER.info("交换机之间链路数据 " + linkEntity.src_switch + "--" + linkEntity.dst_switch + " 带宽 "
					+ linkEntity.bandwidth + "Mbps");
		}
	}

	/**
	 * 如果数据箱中有目标对象，则更新对象；没有，则添加对象
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
	 * 初始化拓扑展示面板中节点的位置
	 */
	public void initElementLocation() {
		TDataBox box = uiFactory.getDataBox();
		@SuppressWarnings("unchecked")
		List<Element> eles = box.getAllElements();
		Iterator<Element> itr = eles.iterator();
		// 无论何种存储结构，迭代器总会提供一种尽可能效率高的遍历方式，而且还不会有删除导致的并发访问异常
		while (itr.hasNext()) {
			Element ele = itr.next();
			if (ele instanceof Node) {
				ele.setLocation(R.DATA_CFG.locationCfg.get(ele.getID().toString()));
			}
		}
	}

	/**
	 * 将整条链路改变颜色，并添加流动动画
	 * 
	 * @param paths
	 */
	public void flowingLing(String[] paths) {
		for (int i = 2; i < paths.length - 1; i++) {
			flowingLink(paths[i], paths[i + 1]);
		}
	}

	/**
	 * 将连接两点链路改变颜色，并添加流动动画
	 * 
	 * @param src
	 * @param dst
	 */
	private void flowingLink(String src, String dst) {
		TDataBox box = uiFactory.getDataBox();
		Link link;
		if (box.containsByID(src + "--" + dst)) {
			// 如果链路存在有源目标--目的目标的链路，直接获取
			link = (Link) box.getElementByID(src + "--" + dst);
		} else {
			// 如果不存在，那肯定是反向的，直接获取就可以
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
	 * 向文本框中输出信息
	 * 
	 * @param msg
	 */
	public void fieldSetMsg(String msg) {
		uiFactory.getPathField().setText(msg);
	}

	/**
	 * 清除所有彩色流动效果，即初始化链路显示状态
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
	 * 将流包的数据填充到折线图中
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
			dataset.addValue(packetNum * 1000 / (newTime - time), "节能路由", Util.rename(sw.dpid));
		});
		aggregatesMap = aggregatesMapNew;
		time = newTime;
	}

	/**
	 * 切换卡片面板的当前显示面板
	 * 
	 * @param index
	 *            卡片面板的位置
	 */
	public void showPanel(int index) {
		JPanel panel = uiFactory.getCenterPanel();
		CardLayout layout = uiFactory.getCardLayout();
		EventQueue.invokeLater(() -> layout.show(panel, R.FRAME_CFG.cardCfg.cardNames[index]));
	}

	/**
	 * 从路径文本框中获得文本数据，然后解析成主机IP末位返回
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
	 * 向路径展示列表中添加路径
	 * 
	 * @param paths
	 */
	public void setPathList(ArrayList<TempPath> paths) {
		DefaultListModel<String> model = uiFactory.getPathListModel();
		model.clear();
		paths.forEach(temp -> {
			StringBuilder sb = new StringBuilder();
			sb.append("RATE：").append(temp.getRateDescription()).append(" ");
			sb.append("INCE：").append(temp.getIncreaseDescription()).append("   ");
			for (int i = 2; i < temp.paths.length - 1; i++) {
				sb.append(Util.rename(temp.paths[i])).append(" --> ");
			}
			sb.append(Util.rename(temp.paths[temp.paths.length - 1]));
			model.addElement(sb.toString());
		});
	}

	/**
	 * 计算可达路径，并在路径展示框中展示
	 */
	public void listReachablePath() {
		try {
			// 从文本框中获得主机通讯对
			ArrayList<Integer> hostNums = getHostNums();
			if (hostNums.size() != 2) {
				R.LOGGER.warning("当前输入主机数量不为单对通信，拒绝计算");
				return;
			}
			ArrayList<TempPath> reachablePaths = new DataService().getAllPaths("H" + hostNums.get(0),
					"H" + hostNums.get(1));
			setPathList(reachablePaths);
		} catch (Exception e1) {
			R.LOGGER.error("所有可达路径计算异常，拒绝计算");
		}
	}

	/**
	 * 将选中的路径项展示到拓扑图中
	 */
	public void previewSelectedPath() {
		int index = uiFactory.getPathList().getSelectedIndex();
		ArrayList<TempPath> tempPaths = dataFactory.getTempPaths();
		if (index >= 0 && index < tempPaths.size()) {
			clearFlowing();
			flowingLing(tempPaths.get(index).paths);
			showPanel(0);
		} else {
			R.LOGGER.warning("预览失败，当前选定下标为：" + index);
		}
	}

	/**
	 * 将选中的路径部署
	 */
	public void pushSelectedPath() {
		int index = uiFactory.getPathList().getSelectedIndex();
		ArrayList<TempPath> tempPaths = dataFactory.getTempPaths();
		if (index >= 0 && index < tempPaths.size()) {
			DataService dataService = new DataService();
			dataService.pushPath(tempPaths.get(index).paths);
		} else {
			R.LOGGER.warning("布署失败，当前选定下标为：" + index);
		}
	}

	private static boolean flowViewOpen = false;

	/**
	 * 开关流表视图
	 * 
	 * @return
	 */
	public void switchFlowView() {
		if (flowViewOpen) {
			clearFlowing();
			flowViewOpen = false;
			R.LOGGER.info("流表视图关闭");
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
		R.LOGGER.info("流表视图开启");
	}

	/**
	 * 填充能耗对比图
	 */
	public void fillBarChart() {
		DefaultCategoryDataset dataset = uiFactory.getEnergyDataset();
		DataService service = new DataService();
		int tradition = service.getTraditionEnergy();
		int sdn = service.getSDNEnergy();
		dataset.addValue(tradition, "正规路由", "正规路由");
		dataset.addValue(sdn, "节能路由", "节能路由");
	}

	/**
	 * 填充单个交换机能耗对比图
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
	 * 自动控制时，向状态列表中添加信息,（不确定同步会不会有问题）
	 * 
	 * @param info
	 */
	public synchronized void addInfo(String info) {
		uiFactory.getAutoControlMessageListModel().addElement(info);
	}
}
