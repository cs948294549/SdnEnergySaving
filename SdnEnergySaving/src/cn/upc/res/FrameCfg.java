package cn.upc.res;

import java.awt.Font;
import java.io.Serializable;
import java.util.Properties;

public class FrameCfg implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主窗口配置对象
	 */
	public final MainCfg mainCfg;
	/**
	 * 控制窗口配置对象
	 */
	public final ControlCfg controlCfg;
	/**
	 * 卡片窗口配置对象
	 */
	public final CardCfg cardCfg;

	public FrameCfg(String filePath) {
		R.LOGGER.info("读取窗口配置文件" + filePath);
		Properties prpt = Util.getProperties(filePath);
		mainCfg = new MainCfg(prpt);
		controlCfg = new ControlCfg(prpt);
		cardCfg = new CardCfg(prpt);
	}

	public class MainCfg implements Serializable {
		private static final long serialVersionUID = 1L;
		/**
		 * 主窗口字体对象
		 */
		public final Font font;
		/**
		 * 主窗口标题
		 */
		public final String title;
		/**
		 * 主窗口图标路径
		 */
		public final String iconPath;
		/**
		 * 主窗口宽度
		 */
		public final int width;
		/**
		 * 主窗口高度
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
		 * 控制窗口宽度
		 */
		public final int width;
		/**
		 * 控制窗口高度
		 */
		public final int height;
		/**
		 * 控制窗口按钮名列表
		 */
		public final String[] buttonNames;
		/**
		 * 控制窗口的按钮的宽度
		 */
		public final int buttonWidth;
		/**
		 * 控制窗口的按钮的高度
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
		 * 卡片窗口的宽度
		 */
		public final int width;
		/**
		 * 卡片窗口的高度
		 */
		public final int height;
		/**
		 * 卡片窗口如果有按钮部分的话，其按钮面板的高度
		 */
		public final int buttonPanelHeight;
		/**
		 * 卡片窗口按钮名列表
		 */
		public final String[] buttonNames;
		/**
		 * 自动控制按钮名称
		 */
		public final String autoButtonName;
		/**
		 * 卡片窗口按钮宽度
		 */
		public final int buttonWidth;
		/**
		 * 卡片窗口按钮高度
		 */
		public final int buttonHeight;
		/**
		 * 卡片窗口卡片面板名称列表，仅标记用
		 */
		public final String[] cardNames;
		/**
		 * 主机图片URL
		 */
		public final String hostUrl;
		/**
		 * 交换机图片URL
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
