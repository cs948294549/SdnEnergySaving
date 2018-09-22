package cn.upc.res;

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

public class DataCfg implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 拓扑控件位置配置对象
	 */
	public final LocationCfg locationCfg;
	/**
	 * 带宽映射配置对象
	 */
	public final BandwidthCfg bandwidthCfg;
	/**
	 * 数据源配置对象
	 */
	public final DataSourceCfg dataSourceCfg;

	public DataCfg(String locationFile, String sourceFile) {
		R.LOGGER.info("读取拓扑图标位置文件" + locationFile);
		locationCfg = new LocationCfg(Util.getProperties(locationFile));
		R.LOGGER.info("创建带宽计算映射表");
		bandwidthCfg = new BandwidthCfg();
		R.LOGGER.info("读取数据源配置文件" + sourceFile);
		dataSourceCfg = new DataSourceCfg(Util.getProperties(sourceFile));
	}

	public class LocationCfg {
		private final Properties prpt;

		public LocationCfg(Properties prpt) {
			this.prpt = prpt;
		}

		/**
		 * 依据主机IP地址或交换机序列获得所处位置,如果没有配置，则返回（0，0）
		 * 
		 * @param key
		 * @return
		 */
		public Point get(String key) {
			String value = prpt.getProperty(key.replace(":", "."));
			if (value == null) {
				return new Point(0, 0);
			}
			String pos[] = value.split("--");
			return new Point(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
		}
	}

	public class BandwidthCfg {
		private final HashMap<Integer, Integer> rate;
		private final HashMap<Integer, String> type;

		public BandwidthCfg() {
			rate = new HashMap<>();
			type = new HashMap<>();
			rate.put(1, 10);
			type.put(1, "半双工");
			rate.put(2, 10);
			type.put(2, "全双工");
			rate.put(4, 100);
			type.put(4, "半双工");
			rate.put(8, 100);
			type.put(8, "全双工");
			rate.put(16, 1024);
			type.put(16, "半双工");
			rate.put(32, 1024);
			type.put(32, "全双工");
			rate.put(64, 10240);
			type.put(64, "全双工");
		}

		/**
		 * 根据带宽特征码来获得带宽速度
		 * 
		 * @param code
		 *            带宽特征码
		 * @return 速度单位(Mbps)
		 */
		public int getRate(int code) {
			return rate.get(code & 0x7f);
		}

		/**
		 * 根据带宽特征码来获得带宽通道类型
		 * 
		 * @param code
		 *            带宽特征码
		 * @return 全双工或半双工
		 */
		public String getType(int code) {
			return type.get(code & 0x7f);
		}
	}

	public class DataSourceCfg {
		private final Properties prpt;

		public DataSourceCfg(Properties prpt) {
			this.prpt = prpt;
		}

		/**
		 * 获得数据源类的命名
		 * 
		 * @return
		 */
		public String getSourceClass() {
			return prpt.getProperty(prpt.getProperty("DataSource"));
		}

		/**
		 * 获得配置参数集合
		 * 
		 * @return
		 */
		public Properties getProperties() {
			return prpt;
		}
	}
}
