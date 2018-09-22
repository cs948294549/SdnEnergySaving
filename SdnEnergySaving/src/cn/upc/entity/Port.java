package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * �������˿�
 * 
 * @author ����չ
 *
 */
public class Port implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * ʾ�� 1
	 */
	public Integer portNumber;
	/**
	 * ʾ�� "8a:64:ec:9a:e0:70"
	 */
	public String hardwareAddress;
	/**
	 * ʾ�� "Switch4-eth1"
	 */
	public String name;
	/**
	 * ʾ�� 0
	 */
	public Integer config;
	/**
	 * ʾ�� 0
	 */
	public Integer state;
	/**
	 * ʾ�� 192
	 */
	public Integer currentFeatures;
	/**
	 * ʾ�� 0
	 */
	public Integer advertisedFeatures;
	/**
	 * ʾ�� 0
	 */
	public Integer supportedFeatures;
	/**
	 * ʾ�� 0
	 */
	public Integer peerFeatures;

	public Port(JSONObject portJO) {
		try {
			portNumber = portJO.getInt("portNumber");
			hardwareAddress = portJO.getString("hardwareAddress");
			name = portJO.getString("name");
			config = portJO.getInt("config");
			state = portJO.getInt("state");
			currentFeatures = portJO.getInt("currentFeatures");
			advertisedFeatures = portJO.getInt("advertisedFeatures");
			supportedFeatures = portJO.getInt("supportedFeatures");
			peerFeatures = portJO.getInt("peerFeatures");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}