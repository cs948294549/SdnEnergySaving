package cn.upc.res;

import java.util.Properties;

public class R {
	private static final String DATA_SOURCE_CONFIG_FILE_PATH = "cfg/dataSource.properties";
	private static final String FRAME_CONFIG_FILE_PATH = "cfg/frame.properties";
	private static final String TOPO_CONFGI_FILE_PATH = "cfg/topo.properties";
	private static final String ENERGY_PRPT_FILE_PATH = "cfg/energy.properties";

	public static final DataCfg DATA_CFG;
	public static final FrameCfg FRAME_CFG;
	public static final Properties ENERGY_PRPT;

	// 日志操作对象
	public static final JrzLogger LOGGER;

	static {
		LOGGER = new JrzLogger();
		DATA_CFG = new DataCfg(TOPO_CONFGI_FILE_PATH, DATA_SOURCE_CONFIG_FILE_PATH);
		FRAME_CFG = new FrameCfg(FRAME_CONFIG_FILE_PATH);
		ENERGY_PRPT = Util.getProperties(ENERGY_PRPT_FILE_PATH);
	}
}
