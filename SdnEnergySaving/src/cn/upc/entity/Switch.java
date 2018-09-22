package cn.upc.entity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 交换机所有信息
 * 
 * @author 江荣展
 *
 */

public class Switch implements Serializable{
	private static final long serialVersionUID = 1L;
	public String harole;
	public String dpid;
	public ArrayList<Port> ports;
	public String buffers;
	public Description description;
	public Integer capabilities;
	public String inetAddress;
	public Long connectedSince;
	public Integer actions;
	public Attributes attributes;

	public Switch(JSONObject switchJO) {
		try {
			harole = switchJO.getString("harole");
			dpid = switchJO.getString("dpid");
			ports = getPorts(switchJO.getJSONArray("ports"));
			buffers = switchJO.getString("buffers");
			description = new Description(switchJO.getJSONObject("description"));
			capabilities = switchJO.getInt("capabilities");
			inetAddress = switchJO.getString("inetAddress");
			connectedSince = switchJO.getLong("connectedSince");
			actions = switchJO.getInt("actions");
			attributes = new Attributes(switchJO.getJSONObject("attributes"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Port> getPorts(JSONArray portsJA) throws JSONException {
		ArrayList<Port> ports = new ArrayList<>();
		for (int i = 0; i < portsJA.length(); i++) {
			ports.add(new Port(portsJA.getJSONObject(i)));
		}
		return ports;
	}

	public class Description {
		public String datapath;
		public String serialNum;
		public String software;
		public String manufacturer;
		public String hardware;

		public Description(JSONObject descriptionJO) {
			try {
				datapath = descriptionJO.getString("datapath");
				serialNum = descriptionJO.getString("serialNum");
				software = descriptionJO.getString("software");
				manufacturer = descriptionJO.getString("manufacturer");
				hardware = descriptionJO.getString("hardware");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public class Attributes {
		public boolean supportsOfppFlood;
		public boolean supportsOfppTable;
		public Integer FastWildcards;
		public boolean supportsNxRole;

		public Attributes(JSONObject attributesJO) {
			try {
				supportsOfppFlood = attributesJO.getBoolean("supportsOfppFlood");
				supportsOfppTable = attributesJO.getBoolean("supportsOfppTable");
				FastWildcards = attributesJO.getInt("FastWildcards");
				supportsNxRole = attributesJO.getBoolean("supportsNxRole");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
