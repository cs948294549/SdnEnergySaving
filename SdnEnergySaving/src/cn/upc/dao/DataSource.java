package cn.upc.dao;

import java.io.Serializable;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.upc.entity.Flow;

public interface DataSource extends Serializable {
	/**
	 * ����豸��Ϣ
	 * 
	 * @return
	 */
	public JSONArray getDevicesJA();

	/**
	 * ��ý�����״̬��Ϣ
	 * 
	 * @return
	 */
	public JSONObject getFeaturesJO();

	/**
	 * �����·��Ϣ
	 * 
	 * @return
	 */
	public JSONArray getLinksJA();

	/**
	 * �·�����
	 * 
	 * @param flow
	 */
	public void pushFlow(Flow flow);

	/**
	 * ���ע������״̬
	 * 
	 * @return
	 */
	public String clearFlow();

	/**
	 * ��ý�����DPID��Ϣ
	 * 
	 * @return
	 */
	public JSONArray getSwitchsJA();

	/**
	 * ���ָ��������������Ϣ
	 * 
	 * @param sw������
	 * @return
	 */
	public JSONObject getFlowJO();

	/**
	 * ���������Ϣ
	 * 
	 * @return
	 */
	public JSONObject getPacketsJO();

	/**
	 * �����ָ��ģ���Socket����
	 * 
	 * @return
	 */
	public Socket getSocket();
}
