package cn.upc.dao;

import java.io.Serializable;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.upc.entity.Flow;

public interface DataSource extends Serializable {
	/**
	 * 获得设备信息
	 * 
	 * @return
	 */
	public JSONArray getDevicesJA();

	/**
	 * 获得交换机状态信息
	 * 
	 * @return
	 */
	public JSONObject getFeaturesJO();

	/**
	 * 获得链路信息
	 * 
	 * @return
	 */
	public JSONArray getLinksJA();

	/**
	 * 下发流表
	 * 
	 * @param flow
	 */
	public void pushFlow(Flow flow);

	/**
	 * 清空注表，返回状态
	 * 
	 * @return
	 */
	public String clearFlow();

	/**
	 * 获得交换机DPID信息
	 * 
	 * @return
	 */
	public JSONArray getSwitchsJA();

	/**
	 * 获得指定交换机流表信息
	 * 
	 * @param sw交换机
	 * @return
	 */
	public JSONObject getFlowJO();

	/**
	 * 获得流包信息
	 * 
	 * @return
	 */
	public JSONObject getPacketsJO();

	/**
	 * 获得与指定模块的Socket连接
	 * 
	 * @return
	 */
	public Socket getSocket();
}
