package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 拓扑链路类
 * 
 * @author 江荣展
 *
 */
public class Link implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * "00:00:00:00:00:00:00:06"
	 */
	public String src_switch;
	/**
	 * 3
	 */
	public Integer src_port;
	/**
	 * "00:00:00:00:00:00:00:09"
	 */
	public String dst_switch;
	/**
	 * 2
	 */
	public Integer dst_port;
	/**
	 * "internal"
	 */
	public String type;
	/**
	 * "bidirectional"
	 */
	public String direction;
	/**
	 * 带宽，计算值，默认值为0
	 */
	public Integer bandwidth = 0;

	public Link(JSONObject linkJO) {
		try {
			src_switch = linkJO.getString("src-switch");
			src_port = linkJO.getInt("src-port");
			dst_switch = linkJO.getString("dst-switch");
			dst_port = linkJO.getInt("dst-port");
			type = linkJO.getString("type");
			direction = linkJO.getString("direction");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
