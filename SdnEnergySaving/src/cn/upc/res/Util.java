package cn.upc.res;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;

public class Util {
	/**
	 * ��ȸ��� �����ڲ�����ȫ��ʵ��Serializable�ӿ�
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deepClone(T object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);// �����������
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) new Object();
	}

	/**
	 * ��ȡ����ID�����õ���Ϣ������S������������H��������
	 * 
	 * @param name
	 * @return
	 */
	public static String rename(String name) {
		if (name.length() == 23) {
			return "S" + Integer.parseInt(name.replace(":", ""), 16);
		} else {
			return "H" + name.substring(name.lastIndexOf('.') + 1);
		}
	}

	/**
	 * �����ھ���
	 * 
	 * @param frame
	 */
	public static void setLocationCenter(Frame frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation(screenSize.width - frameSize.width >> 1, (screenSize.height - frameSize.height >> 1) - 50);
	}

	/**
	 * ���Properties�ļ�
	 * 
	 * @param filePath
	 * @return
	 */
	public static Properties getProperties(String filePath) {
		Properties prpt = new Properties();
		try {
			FileInputStream fis = new FileInputStream(filePath);
			prpt.load(fis);
			fis.close();
		} catch (Exception e) {
			System.err.println("����Properties�ļ�" + filePath + "ʧ�ܣ�");
		}
		return prpt;
	}

	/**
	 * ���ݻ�����С���ؼ���С������������
	 * 
	 * @param bigW
	 *            �������
	 * @param bigH
	 *            �����߶�
	 * @param rectW
	 *            �ؼ����
	 * @param rectH
	 *            �ؼ��߶�
	 * @param row
	 *            �ؼ�����
	 * @param col
	 *            �ؼ�����
	 * @return (�ؼ���ȼ�����ؼ��߶ȼ��)
	 */
	public static Dimension getPad(int bigW, int bigH, int rectW, int rectH, int row, int col) {
		int padX = (bigW - rectW * col) / (col + 1);
		int padY = (bigH - rectH * row) / (row + 1);
		return new Dimension(padX, padY);
	}

}
