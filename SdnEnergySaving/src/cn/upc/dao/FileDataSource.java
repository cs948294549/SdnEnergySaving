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
 * �ļ�����Դ������ר��
 * 
 * @author ����չ
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
	 * �鿴��̬·�ɵ�ַ
	 */
	private String switchsJAPath;
	/**
	 * �鿴����Ϣ��ַ
	 */
	private String packetsJOPath;
	/**
	 * �鿴������������̬·�ɵ�ַ
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
		R.LOGGER.info("�������ļ��ж�ȡ�ļ�·�� " + filePath);
		return filePath;
	}

	@Override
	public JSONArray getDevicesJA() {
		try {
			return new JSONArray(readFile(devicesJAPath));
		} catch (JSONException e) {
			System.out.println(devicesJAPath + " ת��JSONArrayʧ�ܡ�");
			return new JSONArray();
		}
	}

	@Override
	public JSONObject getFeaturesJO() {
		try {
			return new JSONObject(readFile(featuresJOPath));
		} catch (JSONException e) {
			System.out.println(featuresJOPath + " ת��JSONObjectʧ�ܡ�");
			return new JSONObject();
		}
	}

	@Override
	public JSONArray getLinksJA() {
		try {
			return new JSONArray(readFile(linksJAPath));
		} catch (JSONException e) {
			System.out.println(linksJAPath + " ת��JSONArrayʧ�ܡ�");
			return new JSONArray();
		}
	}

	private String readFile(String filePath) {
		File file = new File(filePath);
		Long fileLength = file.length();
		R.LOGGER.info("��ʼ��ȡ�ļ���" + filePath + " �ļ����ȣ�" + fileLength);
		byte[] fileContent = new byte[fileLength.intValue()];
		try {
			FileInputStream fis = new FileInputStream(file);
			fis.read(fileContent);
			fis.close();
		} catch (FileNotFoundException e) {
			R.LOGGER.error(filePath + "�ļ�δ�ҵ�");
		} catch (IOException e) {
			R.LOGGER.error(filePath + "��ȡ��������");
		}
		try {
			return new String(fileContent, ENCODING);
		} catch (UnsupportedEncodingException e) {
			R.LOGGER.error(filePath + "��֧��" + ENCODING + "�ļ���ʽת��");
			return "";
		}
	}

	@Override
	public void pushFlow(Flow flow) {
		File file = new File(pushFlowPath);
		String fileString = flow.toPushString();
		R.LOGGER.info("��ʼд������" + pushFlowPath + " �������ݣ�" + fileString);
		try {
			byte[] fileBytes = (fileString + "\n").getBytes(ENCODING);
			// �ļ���������ڶ�������������׷�ӵ���ʽ
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(fileBytes);
			fos.close();
		} catch (UnsupportedEncodingException e) {
			R.LOGGER.error("����֧��ת��Ϊ������ " + ENCODING);
		} catch (FileNotFoundException e) {
			R.LOGGER.error("Ŀ��λ��δ�ҵ� " + pushFlowPath);
		} catch (IOException e) {
			R.LOGGER.error("��д���쳣");
		}
	}

	@Override
	public String clearFlow() {
		try {
			File file = new File(pushFlowPath);
			file.deleteOnExit();
			file.createNewFile();
			R.LOGGER.info("��� " + pushFlowPath);
			return "Flow��Clear Success!";
		} catch (IOException e) {
			e.printStackTrace();
			return "Flow��Clear Error!";
		}
	}

	@Override
	public JSONArray getSwitchsJA() {
		try {
			return new JSONArray(readFile(switchsJAPath));
		} catch (JSONException e) {
			R.LOGGER.error(switchsJAPath + " ת��JSONObjectʧ�ܡ�");
			return new JSONArray();
		}
	}

	@Override
	public JSONObject getFlowJO() {
		try {
			JSONObject flows = new JSONObject(readFile(popFlowJO));
			return flows;
		} catch (JSONException e) {
			R.LOGGER.error("flow ���� JSONObject ��ʧ��");
			return new JSONObject();
		}
	}

	@Override
	public JSONObject getPacketsJO() {
		try {
			return new JSONObject(readFile(packetsJOPath));
		} catch (JSONException e) {
			R.LOGGER.error("packetsJOPath ���� JSONObject ��ʧ��");
			return new JSONObject();
		}
	}

	@Override
	public Socket getSocket() {
		return null;
	}
}
