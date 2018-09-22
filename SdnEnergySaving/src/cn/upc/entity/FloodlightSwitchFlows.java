package cn.upc.entity;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Floodlight中读取出来的流表
 * 
 * @author 江荣展
 *
 */
public class FloodlightSwitchFlows implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 00:00:00:00:00:00:00:04
	 */
	public String datapathId;
	/**
	 * 交换机中的流表列表
	 */
	public FloodlightFlow[] floodlightFlows;

	public FloodlightSwitchFlows(String dpid,JSONArray floodlightSwitchFlowsJA) {
		try {
			floodlightFlows = new FloodlightFlow[floodlightSwitchFlowsJA.length()];
			for (int i = 0; i < floodlightSwitchFlowsJA.length(); i++) {
				floodlightFlows[i] = new FloodlightFlow(floodlightSwitchFlowsJA.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class FloodlightFlow {
		public int tableId;
		public Match match;
		public int durationSeconds;
		public int durationNanoseconds;
		public int priority;
		public int idleTimeout;
		public int hardTimeout;
		public long cookie;
		public int packetCount;
		public int byteCount;
		public Action[] actions;

		public FloodlightFlow(JSONObject floodlightFlowJO) {
			try {
				tableId = floodlightFlowJO.getInt("tableId");
				match = new Match(floodlightFlowJO.getJSONObject("match"));
				durationSeconds = floodlightFlowJO.getInt("durationSeconds");
				durationNanoseconds = floodlightFlowJO.getInt("durationNanoseconds");
				priority = floodlightFlowJO.getInt("priority");
				idleTimeout = floodlightFlowJO.getInt("idleTimeout");
				hardTimeout = floodlightFlowJO.getInt("hardTimeout");
				cookie = floodlightFlowJO.getLong("cookie");
				packetCount = floodlightFlowJO.getInt("packetCount");
				byteCount = floodlightFlowJO.getInt("byteCount");
				JSONArray actionsJA = floodlightFlowJO.getJSONArray("actions");
				actions = new Action[actionsJA.length()];
				for (int i = 0; i < actionsJA.length(); i++) {
					actions[i] = new Action(actionsJA.getJSONObject(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	

	public class Match {
		public String dataLayerDestination;
		public String dataLayerSource;
		public String dataLayerType;
		public int dataLayerVirtualLan;
		public int dataLayerVirtualLanPriorityCodePoint;
		public int inputPort;
		public String networkDestination;
		public int networkDestinationMaskLen;
		public int networkProtocol;
		public String networkSource;
		public int networkSourceMaskLen;
		public int networkTypeOfService;
		public int transportDestination;
		public int transportSource;
		public int wildcards;

		public Match(JSONObject matchJO) {
			try {
				dataLayerDestination = matchJO.getString("dataLayerDestination");
				dataLayerSource = matchJO.getString("dataLayerSource");
				dataLayerType = matchJO.getString("dataLayerType");
				dataLayerVirtualLan = matchJO.getInt("dataLayerVirtualLan");
				dataLayerVirtualLanPriorityCodePoint = matchJO.getInt("dataLayerVirtualLanPriorityCodePoint");
				inputPort = matchJO.getInt("inputPort");
				networkDestination = matchJO.getString("networkDestination");
				networkDestinationMaskLen = matchJO.getInt("networkDestinationMaskLen");
				networkProtocol = matchJO.getInt("networkProtocol");
				networkSource = matchJO.getString("networkSource");
				networkSourceMaskLen = matchJO.getInt("networkSourceMaskLen");
				networkTypeOfService = matchJO.getInt("networkTypeOfService");
				transportDestination = matchJO.getInt("transportDestination");
				transportSource = matchJO.getInt("transportSource");
				wildcards = matchJO.getInt("wildcards");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public class Action {
		public String type;
		public int length;
		public int port;
		public int maxLength;
		public int lengthU;

		public Action(JSONObject actionJO) {
			try {
				type = actionJO.getString("type");
				length = actionJO.getInt("length");
				port = actionJO.getInt("port");
				maxLength = actionJO.getInt("maxLength");
				lengthU = actionJO.getInt("lengthU");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
}
