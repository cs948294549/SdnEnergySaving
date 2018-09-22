package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * ������������
 * @author ����չ
 *
 */
public class Feature implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * Ĭ��4095
	 */
	public Integer actions;
	/**
	 * ʾ��256
	 */
	public Integer buffers;
	/**
	 * ʾ��199
	 */
	public Integer capabilities;
	/**
	 * "00:00:00:00:00:00:00:04"
	 */
	public String datapathId;
	/**
	 * ʾ��176
	 */
	public Integer length;

	public Port[] ports;
	/**
	 * ʾ��-2
	 */
	public Integer tables;
	/**
	 * ʾ�� "FEATURES_REPLY"
	 */
	public String type;
	/**
	 * ʾ�� 1
	 */
	public Integer version;
	/**
	 * ʾ�� 110
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
	 * ��JSONArray�е�������䵽Port������
	 * 
	 * @param portsJA
	 *            ����Դ
	 * @return Ŀ������
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
