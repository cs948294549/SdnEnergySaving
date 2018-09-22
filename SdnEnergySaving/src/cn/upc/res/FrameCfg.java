package cn.upc.res;

import java.awt.Font;
import java.io.Serializable;
import java.util.Properties;

public class FrameCfg implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * ���������ö���
	 */
	public final MainCfg mainCfg;
	/**
	 * ���ƴ������ö���
	 */
	public final ControlCfg controlCfg;
	/**
	 * ��Ƭ�������ö���
	 */
	public final CardCfg cardCfg;

	public FrameCfg(String filePath) {
		R.LOGGER.info("��ȡ���������ļ�" + filePath);
		Properties prpt = Util.getProperties(filePath);
		mainCfg = new MainCfg(prpt);
		controlCfg = new ControlCfg(prpt);
		cardCfg = new CardCfg(prpt);
	}

	public class MainCfg implements Serializable {
		private static final long serialVersionUID = 1L;
		/**
		 * �������������
		 */
		public final Font font;
		/**
		 * �����ڱ���
		 */
		public final String title;
		/**
		 * ������ͼ��·��
		 */
		public final String iconPath;
		/**
		 * �����ڿ��
		 */
		public final int width;
		/**
		 * �����ڸ߶�
		 */
		public final int height;

		public MainCfg(Properties prpt) {
			String name = "main.";
			String fontName = prpt.getProperty(name + "font_name");
			int fontSize = Integer.parseInt(prpt.getProperty(name + "font_size"));
			font = new Font(fontName, Font.CENTER_BASELINE, fontSize);
			title = prpt.getProperty(name + "title");
			iconPath = prpt.getProperty(name + "icon_path");
			width = Integer.parseInt(prpt.getProperty(name + "width"));
			height = Integer.parseInt(prpt.getProperty(name + "height"));
		}
	}

	public class ControlCfg implements Serializable {
		private static final long serialVersionUID = 1L;
		/**
		 * ���ƴ��ڿ��
		 */
		public final int width;
		/**
		 * ���ƴ��ڸ߶�
		 */
		public final int height;
		/**
		 * ���ƴ��ڰ�ť���б�
		 */
		public final String[] buttonNames;
		/**
		 * ���ƴ��ڵİ�ť�Ŀ��
		 */
		public final int buttonWidth;
		/**
		 * ���ƴ��ڵİ�ť�ĸ߶�
		 */
		public final int buttonHeight;

		public ControlCfg(Properties prpt) {
			String name = "control.";
			width = Integer.parseInt(prpt.getProperty(name + "width"));
			height = Integer.parseInt(prpt.getProperty(name + "height"));
			buttonNames = prpt.getProperty(name + "button_names").split("--");
			buttonWidth = Integer.parseInt(prpt.getProperty(name + "button_width"));
			buttonHeight = Integer.parseInt(prpt.getProperty(name + "button_height"));
		}
	}

	public class CardCfg implements Serializable {
		private static final long serialVersionUID = 1L;
		/**
		 * ��Ƭ���ڵĿ��
		 */
		public final int width;
		/**
		 * ��Ƭ���ڵĸ߶�
		 */
		public final int height;
		/**
		 * ��Ƭ��������а�ť���ֵĻ����䰴ť���ĸ߶�
		 */
		public final int buttonPanelHeight;
		/**
		 * ��Ƭ���ڰ�ť���б�
		 */
		public final String[] buttonNames;
		/**
		 * �Զ����ư�ť����
		 */
		public final String autoButtonName;
		/**
		 * ��Ƭ���ڰ�ť���
		 */
		public final int buttonWidth;
		/**
		 * ��Ƭ���ڰ�ť�߶�
		 */
		public final int buttonHeight;
		/**
		 * ��Ƭ���ڿ�Ƭ��������б��������
		 */
		public final String[] cardNames;
		/**
		 * ����ͼƬURL
		 */
		public final String hostUrl;
		/**
		 * ������ͼƬURL
		 */
		public final String switchUrl;

		public CardCfg(Properties prpt) {
			String name = "card.";
			width = Integer.parseInt(prpt.getProperty(name + "width"));
			height = Integer.parseInt(prpt.getProperty(name + "height"));
			buttonPanelHeight = Integer.parseInt(prpt.getProperty(name + "button_panel_height"));
			buttonNames = prpt.getProperty(name + "button_names").split("--");
			autoButtonName = prpt.getProperty(name + "button_auto_name");
			buttonWidth = Integer.parseInt(prpt.getProperty(name + "button_width"));
			buttonHeight = Integer.parseInt(prpt.getProperty(name + "button_height"));
			cardNames = prpt.getProperty(name + "card_names").split("--");
			hostUrl = prpt.getProperty(name + "host_url");
			switchUrl = prpt.getProperty(name + "switch_url");
		}
	}
}
