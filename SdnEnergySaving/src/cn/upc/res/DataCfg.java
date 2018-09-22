package cn.upc.res;

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

public class DataCfg implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * ���˿ؼ�λ�����ö���
	 */
	public final LocationCfg locationCfg;
	/**
	 * ����ӳ�����ö���
	 */
	public final BandwidthCfg bandwidthCfg;
	/**
	 * ����Դ���ö���
	 */
	public final DataSourceCfg dataSourceCfg;

	public DataCfg(String locationFile, String sourceFile) {
		R.LOGGER.info("��ȡ����ͼ��λ���ļ�" + locationFile);
		locationCfg = new LocationCfg(Util.getProperties(locationFile));
		R.LOGGER.info("�����������ӳ���");
		bandwidthCfg = new BandwidthCfg();
		R.LOGGER.info("��ȡ����Դ�����ļ�" + sourceFile);
		dataSourceCfg = new DataSourceCfg(Util.getProperties(sourceFile));
	}

	public class LocationCfg {
		private final Properties prpt;

		public LocationCfg(Properties prpt) {
			this.prpt = prpt;
		}

		/**
		 * ��������IP��ַ�򽻻������л������λ��,���û�����ã��򷵻أ�0��0��
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
			type.put(1, "��˫��");
			rate.put(2, 10);
			type.put(2, "ȫ˫��");
			rate.put(4, 100);
			type.put(4, "��˫��");
			rate.put(8, 100);
			type.put(8, "ȫ˫��");
			rate.put(16, 1024);
			type.put(16, "��˫��");
			rate.put(32, 1024);
			type.put(32, "ȫ˫��");
			rate.put(64, 10240);
			type.put(64, "ȫ˫��");
		}

		/**
		 * ���ݴ�������������ô����ٶ�
		 * 
		 * @param code
		 *            ����������
		 * @return �ٶȵ�λ(Mbps)
		 */
		public int getRate(int code) {
			return rate.get(code & 0x7f);
		}

		/**
		 * ���ݴ�������������ô���ͨ������
		 * 
		 * @param code
		 *            ����������
		 * @return ȫ˫�����˫��
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
		 * �������Դ�������
		 * 
		 * @return
		 */
		public String getSourceClass() {
			return prpt.getProperty(prpt.getProperty("DataSource"));
		}

		/**
		 * ������ò�������
		 * 
		 * @return
		 */
		public Properties getProperties() {
			return prpt;
		}
	}
}
