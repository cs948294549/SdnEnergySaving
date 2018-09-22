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
	 * 深度复制 对象内部必须全部实现Serializable接口
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
			oos.writeObject(object);// 从流里读出来
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) new Object();
	}

	/**
	 * 提取名称ID中有用的信息，并加S（交换机），H（主机）
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
	 * 将窗口居中
	 * 
	 * @param frame
	 */
	public static void setLocationCenter(Frame frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation(screenSize.width - frameSize.width >> 1, (screenSize.height - frameSize.height >> 1) - 50);
	}

	/**
	 * 获得Properties文件
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
			System.err.println("加载Properties文件" + filePath + "失败！");
		}
		return prpt;
	}

	/**
	 * 根据画布大小，控件大小及数量计算间隔
	 * 
	 * @param bigW
	 *            画布宽度
	 * @param bigH
	 *            画布高度
	 * @param rectW
	 *            控件宽度
	 * @param rectH
	 *            控件高度
	 * @param row
	 *            控件行数
	 * @param col
	 *            控件列数
	 * @return (控件宽度间隔，控件高度间隔)
	 */
	public static Dimension getPad(int bigW, int bigH, int rectW, int rectH, int row, int col) {
		int padX = (bigW - rectW * col) / (col + 1);
		int padY = (bigH - rectH * row) / (row + 1);
		return new Dimension(padX, padY);
	}

}
