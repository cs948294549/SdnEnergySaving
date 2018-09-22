package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 流量数据
 * @author 江荣展
 *
 */
public class Aggregate implements Serializable {
	private static final long serialVersionUID = 1L;
	public String dpid;
	public long packetCount;
	public long byteCount;
	public long flowCount;

	public Aggregate(String dpid, JSONObject aggregateJO) {
		this.dpid = dpid;
		try {
			packetCount = aggregateJO.getLong("packetCount");
			byteCount = aggregateJO.getLong("byteCount");
			flowCount = aggregateJO.getLong("flowCount");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
