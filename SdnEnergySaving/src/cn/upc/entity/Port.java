package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 交换机端口
 * 
 * @author 江荣展
 *
 */
public class Port implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 示例 1
	 */
	public Integer portNumber;
	/**
	 * 示例 "8a:64:ec:9a:e0:70"
	 */
	public String hardwareAddress;
	/**
	 * 示例 "Switch4-eth1"
	 */
	public String name;
	/**
	 * 示例 0
	 */
	public Integer config;
	/**
	 * 示例 0
	 */
	public Integer state;
	/**
	 * 示例 192
	 */
	public Integer currentFeatures;
	/**
	 * 示例 0
	 */
	public Integer advertisedFeatures;
	/**
	 * 示例 0
	 */
	public Integer supportedFeatures;
	/**
	 * 示例 0
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