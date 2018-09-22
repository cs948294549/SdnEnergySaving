package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * �豸��Ϣ��
 * 
 * @author ����չ
 *
 */
public class Device implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ʵ������ һ��Ϊ"DefaultEntityClass"
	 */
	public String entityClass;
	/**
	 * �����ַ ��ʽΪ"d6:ab:a9:f1:4c:04"
	 */
	public String[] macs;
	/**
	 * ipv4 ��ʽΪ"10.0.0.8"
	 */
	public String[] ipv4s;
	/**
	 * vlan ��ʽΪ"0x0"
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
	 * ��JSONArray�е�������䵽String������
	 * 
	 * @param in
	 *            ����Դ
	 * @return Ŀ������
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
	 * ��JSONArray�е�������䵽AttachmentPoint������
	 * 
	 * @param attachmentPointsJA
	 *            ����Դ
	 * @return Ŀ������
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
		 * ����������ID ��ʽΪ"00:00:00:00:00:00:00:0c"
		 */
		public String switchDPID;
		/**
		 * �������˿� ��ʽΪһ������
		 */
		public Integer port;
		/**
		 * ����������״̬ ����״̬��Ϊnull
		 */
		public Integer errorStatus;
		/**
		 * ��������ֵ��Ĭ��ֵΪ0
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