package cn.upc.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.function.BiFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.upc.entity.Aggregate;
import cn.upc.entity.Device;
import cn.upc.entity.Feature;
import cn.upc.entity.FloodlightSwitchFlows;
import cn.upc.entity.Link;
import cn.upc.entity.Mark;
import cn.upc.entity.PathData;
import cn.upc.entity.Device.AttachmentPoint;
import cn.upc.entity.Port;
import cn.upc.entity.Switch;
import cn.upc.entity.TempPath;
import cn.upc.entity.Flow;
import cn.upc.res.DataCfg.BandwidthCfg;
import cn.upc.res.R;
import cn.upc.res.Util;
import upc.jrz.tools.Fun;

/**
 * 数据工厂类
 * 
 * @author 江荣展
 *
 */
public class DataFactory implements Serializable {
	private static final long serialVersionUID = 1L;
	private static DataFactory instence;

	private DataFactory() {
	}

	public static DataFactory newInstence() {
		if (instence == null) {
			instence = new DataFactory();
		}
		return instence;
	}

	/**
	 * 数据源
	 */
	private DataSource dataSource;

	/**
	 * 读取配置文件，组建数据源(配置文件修改后可以直接调用此方法刷新)
	 */
	public void updateDataSource() {
		String className = R.DATA_CFG.dataSourceCfg.getSourceClass();
		try {
			Class<?> c = Class.forName(className);
			Constructor<?> cst = c.getConstructor(Properties.class);
			dataSource = (DataSource) cst.newInstance(R.DATA_CFG.dataSourceCfg.getProperties());
			R.LOGGER.info("依据配置文件创建数据源成功");
		} catch (ClassNotFoundException e) {
			R.LOGGER.error("数据源类未找到" + className);
			System.exit(1);
		} catch (Exception e) {
			R.LOGGER.error("构造数据源对象失败，原因：");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 主机信息
	 */
	private ArrayList<Device> devices;
	private HashMap<String, Device> devicesMap;
	/**
	 * 交换机信息
	 */
	private ArrayList<Feature> features;
	private HashMap<String, Feature> featuresMap;
	/**
	 * 链路信息
	 */
	private ArrayList<Link> links;
	private HashMap<String, Link> linksMap;

	/**
	 * 从数据源获得数据，并进行格式化处理
	 */
	public void loadData() {
		loadDevicesJA();
		loadFeaturesJO();
		loadLinksJA();
		countBandwidth();
		R.LOGGER.info("数据处理全部完成");
		updateTimestamp();// 更新时间戳
	}

	private void loadDevicesJA() {
		R.LOGGER.info("处理主机设备数据(DevicesJA)开始");
		JSONArray devicesJA = dataSource.getDevicesJA();
		ArrayList<Device> devices = new ArrayList<>();
		HashMap<String, Device> devicesMap = new HashMap<>();
		for (int i = 0; i < devicesJA.length(); i++) {
			try {
				Device device = new Device(devicesJA.getJSONObject(i));
				if (device.ipv4s != null) {
					devices.add(device);
					devicesMap.put(device.ipv4s[0], device);
				}
			} catch (JSONException e) {
				R.LOGGER.error("devicesJA.getJSONObject失败\n" + devicesJA);
			}
		}
		this.devices = devices;
		this.devicesMap = devicesMap;
		R.LOGGER.info("处理主机设备数据(DevicesJA)完成");
	}

	private void loadFeaturesJO() {
		R.LOGGER.info("处理交换机设备数据(FeaturesJO)开始");
		JSONObject featuresJO = dataSource.getFeaturesJO();
		ArrayList<Feature> features = new ArrayList<>();
		HashMap<String, Feature> featuresMap = new HashMap<>();
		JSONArray names = featuresJO.names();
		for (int i = 0; i < names.length(); i++) {
			try {
				Feature feature = new Feature(featuresJO.getJSONObject(names.getString(i)));
				features.add(feature);
				featuresMap.put(names.getString(i), new Feature(featuresJO.getJSONObject(names.getString(i))));
			} catch (JSONException e) {
				R.LOGGER.error("featuresJO.getJSONObject失败\n" + featuresJO);
			}
		}
		this.features = features;
		this.featuresMap = featuresMap;
		R.LOGGER.info("处理交换机设备数据(FeaturesJO)完成");
	}

	private void loadLinksJA() {
		R.LOGGER.info("处理链路数据(LinksJA)开始");
		JSONArray linksJA = dataSource.getLinksJA();
		ArrayList<Link> links = new ArrayList<>();
		HashMap<String, Link> linksMap = new HashMap<>();
		for (int i = 0; i < linksJA.length(); i++) {
			try {
				Link link = new Link(linksJA.getJSONObject(i));
				links.add(link);
				linksMap.put(link.src_switch + "--" + link.dst_switch, link);
			} catch (JSONException e) {
				R.LOGGER.error("linksJA.getJSONObject失败\n" + linksJA);
			}
		}
		this.links = links;
		this.linksMap = linksMap;
		R.LOGGER.info("处理链路数据(LinksJA)完成");
	}

	/**
	 * 计算链路或主机商品的带宽
	 */
	private void countBandwidth() {
		R.LOGGER.info("计算链路带宽开始");
		for (int i = 0; i < links.size(); i++) {
			// 遍历每条链路
			Link link = links.get(i);
			// 获得链路源端口交换机
			Feature srcFeature = featuresMap.get(link.src_switch);
			// 获得链路源端口带宽
			int srcBandwidth = getBandwidth(srcFeature.ports, link.src_port);
			// 获得链路目的端口交换机
			Feature dstFeature = featuresMap.get(link.dst_switch);
			// 获得链路目的端口带宽
			int dstBandwidth = getBandwidth(dstFeature.ports, link.dst_port);
			link.bandwidth = Math.min(srcBandwidth, dstBandwidth);
			R.LOGGER.info("链路" + link.src_switch + "(" + link.src_port + ")-->" + link.dst_switch + "(" + link.dst_port
					+ ") 带宽：" + link.bandwidth);
		}
		for (int i = 0; i < devices.size(); i++) {
			Device device = devices.get(i);
			if (device.attachmentPoints == null) {
				continue;
			}
			AttachmentPoint attachmentPoint = device.attachmentPoints[0];
			Feature feature = featuresMap.get(attachmentPoint.switchDPID);
			attachmentPoint.bandwidth = getBandwidth(feature.ports, attachmentPoint.port);
			R.LOGGER.info("设备" + device.ipv4s[0] + "-->" + attachmentPoint.switchDPID + "(" + attachmentPoint.port
					+ ") 带宽：" + attachmentPoint.bandwidth);
		}
		R.LOGGER.info("计算链路带宽完成");
	}

	/**
	 * 通过获得端口列表中指定端口号的带宽
	 * 
	 * @param ports端口列表
	 * @param portNumber指定端口号
	 * @return 带宽
	 */
	private int getBandwidth(Port[] ports, int portNumber) {
		for (int j = 0; j < ports.length; j++) {
			// 遍历交换机的每个端口
			Port port = ports[j];
			// 找出端口号相同的交换机，决定了链路是与交换机的哪个端口连接
			BandwidthCfg cfg = R.DATA_CFG.bandwidthCfg;
			if (port.portNumber == portNumber) {
				// 此项属性决定了交换机的带宽
				return cfg.getRate(port.currentFeatures);
			}
		}
		return -1;
	}

	/**
	 * 时间戳，用来标记当前数据的最后更新时间
	 */
	private static long timestamp;

	private void updateTimestamp() {
		timestamp = System.currentTimeMillis();
	}

	/**
	 * 获得时间戳
	 * 
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public ArrayList<Device> getDevices() {
		if (devices == null) {
			return new ArrayList<Device>();
		}
		return devices;
	}

	public HashMap<String, Device> getDevicesMap() {
		if (devicesMap == null) {
			return new HashMap<>();
		}
		return devicesMap;
	}

	public ArrayList<Feature> getFeatures() {
		if (features == null) {
			return new ArrayList<>();
		}
		return features;
	}

	public HashMap<String, Feature> getFeaturesMap() {
		if (featuresMap == null) {
			return new HashMap<String, Feature>();
		}
		return featuresMap;
	}

	public ArrayList<Link> getLinks() {
		if (links == null) {
			return new ArrayList<Link>();
		}
		return links;
	}

	public HashMap<String, Link> getLinksMap() {
		if (linksMap == null) {
			return new HashMap<>();
		}
		return linksMap;
	}

	/**
	 * 路径计算辅助数据
	 */
	private PathData pathData;

	/**
	 * 获得路径计算辅助数据
	 * 
	 * @return
	 */
	public PathData getPathData() {
		if (pathData == null) {
			pathData = new PathData();
		}
		if (pathData.timestamp != getTimestamp()) {
			pathData.updateData(getDevices(), getLinks(), getTimestamp());
		}
		return Util.deepClone(pathData);
	}

	/**
	 * 下发路由表
	 * 
	 * @param flows
	 */
	public void pushFlows(ArrayList<Flow> flows) {
		for (Flow flow : flows) {
			dataSource.pushFlow(flow);
			R.LOGGER.info("下发流表：" + flow.toString());
		}
	}

	/**
	 * 清空流表
	 */
	public void clearFlows() {
		dataSource.clearFlow();
	}

	private ArrayList<Switch> switchs;
	private HashMap<String, Switch> switchsMap;
	private long switchsTimestamp;

	public ArrayList<Switch> getSwitchs() {
		if (switchsTimestamp < getTimestamp() || switchs == null) {
			loadSwitchsJA();
		}
		return switchs;
	}

	public HashMap<String, Switch> getSwitchsMap() {
		if (switchsTimestamp < getTimestamp() || switchsMap == null) {
			loadSwitchsJA();
		}
		return switchsMap;
	}

	private void loadSwitchsJA() {
		R.LOGGER.info("处理交换机数据(SwitchsJA)开始");
		JSONArray switchsJA = dataSource.getSwitchsJA();
		switchs = new ArrayList<>();
		switchsMap = new HashMap<>();
		for (int i = 0; i < switchsJA.length(); i++) {
			try {
				Switch s = new Switch(switchsJA.getJSONObject(i));
				switchs.add(s);
				switchsMap.put(s.dpid, s);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		switchsTimestamp = getTimestamp();
		R.LOGGER.info("处理交换机数据(SwitchsJA)结束");
	}

	private ArrayList<Aggregate> aggregates;
	private HashMap<String, Aggregate> aggregatesMap;

	public ArrayList<Aggregate> getAggregates() {
		loadPacketsJO();
		return aggregates;
	}

	public HashMap<String, Aggregate> getAggregatesMap() {
		loadPacketsJO();
		return aggregatesMap;
	}

	private void loadPacketsJO() {
		JSONObject aggregatesJO = dataSource.getPacketsJO();
		aggregates = new ArrayList<>();
		aggregatesMap = new HashMap<>();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = aggregatesJO.keys();
		while (iterator.hasNext()) {
			try {
				String key = iterator.next();
				Aggregate aggregate = new Aggregate(key, aggregatesJO.getJSONArray(key).getJSONObject(0));
				aggregates.add(aggregate);
				aggregatesMap.put(key, aggregate);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 临时存储路径计算结果
	 */
	private ArrayList<TempPath> tempPaths;

	public ArrayList<TempPath> createAndReturnTempPaths(ArrayList<String[]> tempPaths) {
		this.tempPaths = new ArrayList<>();
		tempPaths.forEach(e -> this.tempPaths.add(new TempPath(e)));
		return getTempPaths();
	}

	public ArrayList<TempPath> saveAndReturnTempPaths(ArrayList<TempPath> tempPaths) {
		this.tempPaths = tempPaths;
		return getTempPaths();
	}

	public ArrayList<TempPath> getTempPaths() {
		if (tempPaths == null) {
			return new ArrayList<>();
		} else {
			return Util.deepClone(tempPaths);
		}
	}
	
	private ArrayList<FloodlightSwitchFlows> floodlightSwitchFlows;

	public ArrayList<FloodlightSwitchFlows> getFlows() {
		loadFlows();
		return floodlightSwitchFlows;
	}

	private HashMap<String, FloodlightSwitchFlows> floodlightSwitchFlowsMap;

	public HashMap<String, FloodlightSwitchFlows> getFlowsMap() {
		loadFlows();
		return floodlightSwitchFlowsMap;
	}

	private void loadFlows() {
		JSONObject flowsJO = dataSource.getFlowJO();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = flowsJO.keys();
		floodlightSwitchFlows = new ArrayList<>();
		floodlightSwitchFlowsMap = new HashMap<>();
		try {
			while (iterator.hasNext()) {
				String name = iterator.next();
				JSONArray array = flowsJO.getJSONArray(name);
				FloodlightSwitchFlows flows = new FloodlightSwitchFlows(name, array);
				floodlightSwitchFlows.add(flows);
				floodlightSwitchFlowsMap.put(name, flows);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Forwarding模块是否开启
	 * 
	 * @return
	 */
	public boolean isForwardingOpen() {
		return handlerSocket(Fun.teBiFun((in, out) -> {
			out.println(Mark.FORWARD.toString() + Mark.SELECT);
			if (in.readLine().trim().equals(Mark.FORWARD.toString() + Mark.SELECT + Mark.YES)) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}));

	}

	@SuppressWarnings("unchecked")
	private <T> T handlerSocket(BiFunction<BufferedReader, PrintWriter, T> function) {
		try {
			Object o;
			Socket socket = dataSource.getSocket();
			if (socket == null) {
				throw new RuntimeException("socket获取失败！");
			}
			try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter pw = new PrintWriter(
							new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {
				do {
					pw.println(Mark.CLIENT.toString() + Mark.READY);
				} while (!br.readLine().trim().equals(Mark.SERVER.toString() + Mark.READY));
				o = function.apply(br, pw);
				do {
					pw.println(Mark.CLIENT.toString() + Mark.END);
				} while (!br.readLine().trim().equals(Mark.SERVER.toString() + Mark.END));
			}
			return (T) o;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 设置Forwarding模块的开关状态
	 * 
	 * @param state
	 * @return
	 */
	public boolean setForwardingSwitch(boolean state) {
		return handlerSocket(Fun.teBiFun((in, out) -> {
			if (state) {
				out.println(Mark.FORWARD.toString() + Mark.SET + Mark.YES);
			} else {
				out.println(Mark.FORWARD.toString() + Mark.SET + Mark.NO);
			}
			if (in.readLine().trim().equals(Mark.FORWARD.toString() + Mark.SELECT + Mark.YES)) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}));
	}

}
