package cn.upc.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.upc.entity.Flow;
import cn.upc.res.R;

/**
 * 文件数据源，测试专用
 * 
 * @author 江荣展
 *
 */
public class FileDataSource implements DataSource {
	private static final long serialVersionUID = 1L;
	private static final String ENCODING = "GBK";
	private Properties prpt;
	private String devicesJAPath;
	private String featuresJOPath;
	private String linksJAPath;
	private String pushFlowPath;
	/**
	 * 查看静态路由地址
	 */
	private String switchsJAPath;
	/**
	 * 查看包信息地址
	 */
	private String packetsJOPath;
	/**
	 * 查看单个交换机静态路由地址
	 */
	private String popFlowJO;

	public FileDataSource(Properties prpt) {
		this.prpt = prpt;
		devicesJAPath = getValue("devicesJA");
		featuresJOPath = getValue("featuresJO");
		linksJAPath = getValue("linksJA");
		pushFlowPath = getValue("pushFlow");
		switchsJAPath = getValue("switchsJA");
		packetsJOPath = getValue("packetsJO");
		popFlowJO = getValue("popFlowJO");
	}

	private String getValue(String key) {
		String filePath = prpt.getProperty(key);
		filePath = "file/" + filePath.replace("/", ".") + ".txt";
		R.LOGGER.info("从配置文件中读取文件路径 " + filePath);
		return filePath;
	}

	@Override
	public JSONArray getDevicesJA() {
		try {
			return new JSONArray(readFile(devicesJAPath));
		} catch (JSONException e) {
			System.out.println(devicesJAPath + " 转换JSONArray失败。");
			return new JSONArray();
		}
	}

	@Override
	public JSONObject getFeaturesJO() {
		try {
			return new JSONObject(readFile(featuresJOPath));
		} catch (JSONException e) {
			System.out.println(featuresJOPath + " 转换JSONObject失败。");
			return new JSONObject();
		}
	}

	@Override
	public JSONArray getLinksJA() {
		try {
			return new JSONArray(readFile(linksJAPath));
		} catch (JSONException e) {
			System.out.println(linksJAPath + " 转换JSONArray失败。");
			return new JSONArray();
		}
	}

	private String readFile(String filePath) {
		File file = new File(filePath);
		Long fileLength = file.length();
		R.LOGGER.info("开始读取文件：" + filePath + " 文件长度：" + fileLength);
		byte[] fileContent = new byte[fileLength.intValue()];
		try {
			FileInputStream fis = new FileInputStream(file);
			fis.read(fileContent);
			fis.close();
		} catch (FileNotFoundException e) {
			R.LOGGER.error(filePath + "文件未找到");
		} catch (IOException e) {
			R.LOGGER.error(filePath + "读取出现问题");
		}
		try {
			return new String(fileContent, ENCODING);
		} catch (UnsupportedEncodingException e) {
			R.LOGGER.error(filePath + "不支持" + ENCODING + "文件格式转换");
			return "";
		}
	}

	@Override
	public void pushFlow(Flow flow) {
		File file = new File(pushFlowPath);
		String fileString = flow.toPushString();
		R.LOGGER.info("开始写出流表：" + pushFlowPath + " 流表内容：" + fileString);
		try {
			byte[] fileBytes = (fileString + "\n").getBytes(ENCODING);
			// 文件输出流，第二个参数代表以追加的形式
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(fileBytes);
			fos.close();
		} catch (UnsupportedEncodingException e) {
			R.LOGGER.error("流表不支持转换为流类型 " + ENCODING);
		} catch (FileNotFoundException e) {
			R.LOGGER.error("目标位置未找到 " + pushFlowPath);
		} catch (IOException e) {
			R.LOGGER.error("流写出异常");
		}
	}

	@Override
	public String clearFlow() {
		try {
			File file = new File(pushFlowPath);
			file.deleteOnExit();
			file.createNewFile();
			R.LOGGER.info("清空 " + pushFlowPath);
			return "Flow　Clear Success!";
		} catch (IOException e) {
			e.printStackTrace();
			return "Flow　Clear Error!";
		}
	}

	@Override
	public JSONArray getSwitchsJA() {
		try {
			return new JSONArray(readFile(switchsJAPath));
		} catch (JSONException e) {
			R.LOGGER.error(switchsJAPath + " 转换JSONObject失败。");
			return new JSONArray();
		}
	}

	@Override
	public JSONObject getFlowJO() {
		try {
			JSONObject flows = new JSONObject(readFile(popFlowJO));
			return flows;
		} catch (JSONException e) {
			R.LOGGER.error("flow 数据 JSONObject 化失败");
			return new JSONObject();
		}
	}

	@Override
	public JSONObject getPacketsJO() {
		try {
			return new JSONObject(readFile(packetsJOPath));
		} catch (JSONException e) {
			R.LOGGER.error("packetsJOPath 数据 JSONObject 化失败");
			return new JSONObject();
		}
	}

	@Override
	public Socket getSocket() {
		return null;
	}
}
