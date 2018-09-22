package cn.upc.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.upc.entity.Flow;
import cn.upc.res.R;

public class UrlDataSource implements DataSource {
	private static final long serialVersionUID = 1L;
	/**
	 * ���ö���
	 */
	private Properties prpt;
	/**
	 * Floodlight��ַ
	 */
	private String ip;
	/**
	 * �˿�
	 */
	private String port;
	/**
	 * Socket���ӵĶ˿ں�
	 */
	private int socketPort;
	/**
	 * �鿴������Ϣ��ַ
	 */
	private String devicesJAPath;
	/**
	 * �鿴��������Ϣ��ַ
	 */
	private String featuresJOPath;
	/**
	 * �鿴��·������Ϣ��ַ
	 */
	private String linksJAPath;
	/**
	 * ���þ�̬·�ɵ�ַ
	 */
	private String pushFlowPath;
	/**
	 * �鿴������DPID
	 */
	private String switchsJAPath;
	/**
	 * �鿴����Ϣ��ַ
	 */
	private String packetsJOPath;
	/**
	 * �鿴��������̬·�ɵ�ַ
	 */
	private String popFlowJO;
	/**
	 * �������
	 */
	private String clearFlow;

	public UrlDataSource(Properties prpt) {
		this.prpt = prpt;
		ip = prpt.getProperty("ip");
		port = prpt.getProperty("port");
		socketPort = Integer.parseInt(prpt.getProperty("socketPort"));
		devicesJAPath = getValue("devicesJA");
		featuresJOPath = getValue("featuresJO");
		linksJAPath = getValue("linksJA");
		pushFlowPath = getValue("pushFlow");
		switchsJAPath = getValue("switchsJA");
		packetsJOPath = getValue("packetsJO");
		popFlowJO = getValue("popFlowJO");
		clearFlow = getValue("clearFlow");
	}

	private String getValue(String key) {
		String filePath = prpt.getProperty(key);
		filePath = "http://" + ip + ":" + port + "/" + filePath;
		R.LOGGER.info("�������ļ��ж�ȡ " + filePath);
		return filePath;
	}

	@Override
	public JSONArray getDevicesJA() {
		try {
			return new JSONArray(restGet(devicesJAPath));
		} catch (JSONException e) {
			R.LOGGER.error("devices ���� JSONArray ��ʧ��");
		}
		return new JSONArray();
	}

	@Override
	public JSONObject getFeaturesJO() {
		try {
			return new JSONObject(restGet(featuresJOPath));
		} catch (JSONException e) {
			R.LOGGER.error("features ���� JSONObject ��ʧ��");
		}
		return new JSONObject();
	}

	@Override
	public JSONArray getLinksJA() {
		try {
			return new JSONArray(restGet(linksJAPath));
		} catch (JSONException e) {
			R.LOGGER.error("links ���� JSONArray ��ʧ��");
		}
		return new JSONArray();
	}

	/**
	 * ʹ��URL�������Ӳ���ȡ��ҳ����
	 * 
	 * @param urlPath
	 * @return
	 */
	private String restGet(String urlPath) {
		try {
			// ��������
			HttpURLConnection connection = openConnection(urlPath);
			// ������
			connection.connect();
			// ��ȡ����������
			return readData(connection);
		} catch (Exception e) {
			R.LOGGER.error("��ȡ����ʧ�ܣ�URL��ַ=" + urlPath);
		}
		return "";
	}

	/**
	 * ��������
	 * 
	 * @param urlPath
	 * @return
	 * @throws Exception
	 */
	private HttpURLConnection openConnection(String urlPath) throws Exception {
		// ����URL
		URL url = new URL(urlPath);
		// ��������
		return (HttpURLConnection) url.openConnection();
	}

	/**
	 * ��connection�ж�ȡ����
	 * 
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	private String readData(HttpURLConnection connection) throws Exception {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			StringBuilder sb = new StringBuilder();
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				sb.append(line);
			}
			return sb.toString();
		}
	}

	/**
	 * ������д����connection
	 * 
	 * @param connection
	 * @param data
	 * @throws Exception
	 */
	private void writeData(HttpURLConnection connection, String data) throws Exception {
		connection.setDoOutput(true);
		try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
			br.write(data);
			br.flush();
		}
	}

	@Override
	public void pushFlow(Flow flow) {
		String result = "";
		try {
			HttpURLConnection connection = openConnection(pushFlowPath);
			writeData(connection, flow.toPushString());
			result = readData(connection);
			R.LOGGER.info("�·�����ɹ���result=" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String clearFlow() {
		try {
			HttpURLConnection conn = openConnection(clearFlow);
			readData(conn);
			R.LOGGER.info("����������");
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			R.LOGGER.error("�������ʧ��");
			return "Flow Clear Error!";
		}
	}

	@Override
	public JSONArray getSwitchsJA() {
		try {
			return new JSONArray(restGet(switchsJAPath));
		} catch (JSONException e) {
			R.LOGGER.error("flows ���� JSONArray ��ʧ��");
		}
		return new JSONArray();
	}

	@Override
	public JSONObject getFlowJO() {
		try {
			return new JSONObject(restGet(popFlowJO));
		} catch (JSONException e) {
			R.LOGGER.error("ָ��flow ���� JSONObject ��ʧ��");
		}
		return new JSONObject();
	}

	@Override
	public JSONObject getPacketsJO() {
		try {
			return new JSONObject(restGet(packetsJOPath));
		} catch (JSONException e) {
			R.LOGGER.error("packeets ���� JSONObject ��ʧ��");
		}
		return new JSONObject();
	}

	@Override
	public Socket getSocket() {
		try {
			InetAddress address = InetAddress.getByName(ip);
			Socket socket = new Socket(address, socketPort);
			R.LOGGER.info("���Socket����");
			return socket;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
