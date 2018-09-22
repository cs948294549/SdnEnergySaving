package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 设备信息类
 * 
 * @author 江荣展
 *
 */
public class Device implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 实体类名 一般为"DefaultEntityClass"
	 */
	public String entityClass;
	/**
	 * 物理地址 格式为"d6:ab:a9:f1:4c:04"
	 */
	public String[] macs;
	/**
	 * ipv4 格式为"10.0.0.8"
	 */
	public String[] ipv4s;
	/**
	 * vlan 格式为"0x0"
	 */
	public String[] vlans;
	public AttachmentPoint[] attachmentPoints;
	public Long lastSeen;

	public Device(JSONObject deviceJO) {
		try {
			entityClass = deviceJO.getString("entityClass");
			macs = fillData(deviceJO.getJSONArray("mac"));
			ipv4s = fillData(deviceJO.getJSONArray("ipv4"));
			vlans = fillData(deviceJO.getJSONArray("vlan"));
			attachmentPoints = fillAttachmentPointData(deviceJO.getJSONArray("attachmentPoint"));
			lastSeen = new Long(deviceJO.getLong("lastSeen"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将JSONArray中的数据填充到String数组中
	 * 
	 * @param in
	 *            数据源
	 * @return 目标数据
	 * @throws JSONException
	 */
	private String[] fillData(JSONArray in) throws JSONException {
		String[] out = null;
		if (in.length() > 0) {
			out = new String[in.length()];
			for (int i = 0; i < in.length(); i++) {
				out[i] = in.getString(i);
			}
		}
		return out;
	}

	/**
	 * 将JSONArray中的数据填充到AttachmentPoint数组中
	 * 
	 * @param attachmentPointsJA
	 *            数据源
	 * @return 目标数据
	 * @throws JSONException
	 */
	private AttachmentPoint[] fillAttachmentPointData(JSONArray attachmentPointsJA) throws JSONException {
		AttachmentPoint[] attachmentPoints = null;
		if (attachmentPointsJA.length() > 0) {
			attachmentPoints = new AttachmentPoint[attachmentPointsJA.length()];
			for (int i = 0; i < attachmentPointsJA.length(); i++) {
				attachmentPoints[i] = new AttachmentPoint(attachmentPointsJA.getJSONObject(i));
			}
		}
		return attachmentPoints;
	}

	public class AttachmentPoint implements Serializable{
		private static final long serialVersionUID = 1L;
		/**
		 * 交换机序列ID 格式为"00:00:00:00:00:00:00:0c"
		 */
		public String switchDPID;
		/**
		 * 交换机端口 格式为一个整数
		 */
		public Integer port;
		/**
		 * 交换机错误状态 正常状态下为null
		 */
		public Integer errorStatus;
		/**
		 * 带宽，计算值，默认值为0
		 */
		public Integer bandwidth=0;

		public AttachmentPoint(JSONObject attachmentPointJO) {
			try {
				switchDPID = attachmentPointJO.getString("switchDPID");
				port = attachmentPointJO.getInt("port");
				if (attachmentPointJO.isNull("errorStatus")) {
					errorStatus = null;
				} else {
					errorStatus = attachmentPointJO.getInt("errorStatus");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}