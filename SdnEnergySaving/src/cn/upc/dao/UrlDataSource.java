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
	 * 配置对象
	 */
	private Properties prpt;
	/**
	 * Floodlight地址
	 */
	private String ip;
	/**
	 * 端口
	 */
	private String port;
	/**
	 * Socket连接的端口号
	 */
	private int socketPort;
	/**
	 * 查看主机信息地址
	 */
	private String devicesJAPath;
	/**
	 * 查看交换机信息地址
	 */
	private String featuresJOPath;
	/**
	 * 查看链路连接信息地址
	 */
	private String linksJAPath;
	/**
	 * 设置静态路由地址
	 */
	private String pushFlowPath;
	/**
	 * 查看交换机DPID
	 */
	private String switchsJAPath;
	/**
	 * 查看包信息地址
	 */
	private String packetsJOPath;
	/**
	 * 查看交换机静态路由地址
	 */
	private String popFlowJO;
	/**
	 * 清空流表
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
		R.LOGGER.info("从配置文件中读取 " + filePath);
		return filePath;
	}

	@Override
	public JSONArray getDevicesJA() {
		try {
			return new JSONArray(restGet(devicesJAPath));
		} catch (JSONException e) {
			R.LOGGER.error("devices 数据 JSONArray 化失败");
		}
		return new JSONArray();
	}

	@Override
	public JSONObject getFeaturesJO() {
		try {
			return new JSONObject(restGet(featuresJOPath));
		} catch (JSONException e) {
			R.LOGGER.error("features 数据 JSONObject 化失败");
		}
		return new JSONObject();
	}

	@Override
	public JSONArray getLinksJA() {
		try {
			return new JSONArray(restGet(linksJAPath));
		} catch (JSONException e) {
			R.LOGGER.error("links 数据 JSONArray 化失败");
		}
		return new JSONArray();
	}

	/**
	 * 使用URL建立连接并读取网页数据
	 * 
	 * @param urlPath
	 * @return
	 */
	private String restGet(String urlPath) {
		try {
			// 建立连接
			HttpURLConnection connection = openConnection(urlPath);
			// 打开连接
			connection.connect();
			// 读取并返回数据
			return readData(connection);
		} catch (Exception e) {
			R.LOGGER.error("读取数据失败，URL地址=" + urlPath);
		}
		return "";
	}

	/**
	 * 建立连接
	 * 
	 * @param urlPath
	 * @return
	 * @throws Exception
	 */
	private HttpURLConnection openConnection(String urlPath) throws Exception {
		// 创建URL
		URL url = new URL(urlPath);
		// 建立连接
		return (HttpURLConnection) url.openConnection();
	}

	/**
	 * 从connection中读取数据
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
	 * 将数据写出到connection
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
			R.LOGGER.info("下发流表成功，result=" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String clearFlow() {
		try {
			HttpURLConnection conn = openConnection(clearFlow);
			readData(conn);
			R.LOGGER.info("流表清空完成");
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			R.LOGGER.error("流表清空失败");
			return "Flow Clear Error!";
		}
	}

	@Override
	public JSONArray getSwitchsJA() {
		try {
			return new JSONArray(restGet(switchsJAPath));
		} catch (JSONException e) {
			R.LOGGER.error("flows 数据 JSONArray 化失败");
		}
		return new JSONArray();
	}

	@Override
	public JSONObject getFlowJO() {
		try {
			return new JSONObject(restGet(popFlowJO));
		} catch (JSONException e) {
			R.LOGGER.error("指定flow 数据 JSONObject 化失败");
		}
		return new JSONObject();
	}

	@Override
	public JSONObject getPacketsJO() {
		try {
			return new JSONObject(restGet(packetsJOPath));
		} catch (JSONException e) {
			R.LOGGER.error("packeets 数据 JSONObject 化失败");
		}
		return new JSONObject();
	}

	@Override
	public Socket getSocket() {
		try {
			InetAddress address = InetAddress.getByName(ip);
			Socket socket = new Socket(address, socketPort);
			R.LOGGER.info("获得Socket连接");
			return socket;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
