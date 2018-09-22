package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 交换机特征类
 * @author 江荣展
 *
 */
public class Feature implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 默认4095
	 */
	public Integer actions;
	/**
	 * 示例256
	 */
	public Integer buffers;
	/**
	 * 示例199
	 */
	public Integer capabilities;
	/**
	 * "00:00:00:00:00:00:00:04"
	 */
	public String datapathId;
	/**
	 * 示例176
	 */
	public Integer length;

	public Port[] ports;
	/**
	 * 示例-2
	 */
	public Integer tables;
	/**
	 * 示例 "FEATURES_REPLY"
	 */
	public String type;
	/**
	 * 示例 1
	 */
	public Integer version;
	/**
	 * 示例 110
	 */
	public Integer xid;

	public Feature(JSONObject featureJO) {
		try {
			actions = featureJO.getInt("actions");
			buffers = featureJO.getInt("buffers");
			capabilities = featureJO.getInt("capabilities");
			datapathId = featureJO.getString("datapathId");
			length = featureJO.getInt("length");
			ports = fillPortsData(featureJO.getJSONArray("ports"));
			tables = featureJO.getInt("tables");
			type = featureJO.getString("type");
			version = featureJO.getInt("version");
			xid = featureJO.getInt("xid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将JSONArray中的数据填充到Port数组中
	 * 
	 * @param portsJA
	 *            数据源
	 * @return 目标数据
	 * @throws JSONException
	 */
	private Port[] fillPortsData(JSONArray portsJA) throws JSONException {
		Port[] ports = null;
		if (portsJA.length() > 0) {
			ports = new Port[portsJA.length()];
			for (int i = 0; i < portsJA.length(); i++) {
				ports[i] = new Port(portsJA.getJSONObject(i));
			}
		}
		return ports;
	}
}
