package cn.upc.service;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import cn.upc.res.R;
import cn.upc.ui.ListenerFactory;
import cn.upc.ui.UiFactory;

/**
 * 界面服务，主要处理界面布局，从布局获得数据初步处理，控件组合，事件添加等
 * 
 * @author 江荣展
 *
 */
public class UiService {
	private UiFactory factory;

	public UiService() {
		factory = UiFactory.newInstence();
	}

	/**
	 * 皮肤包注册,此项将使得JAVA自带窗口外观改变
	 */
	public void initOutLook() {
		try {
			// 获得字体
			Font font = R.FRAME_CFG.mainCfg.font;
			// 设置外观样式
			UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
			// 设置窗口标题字体
			UIManager.put("InternalFrame.titleFont", font);
			// 将所有位置字体都设置为目标字体
			FontUIResource fontRes = new FontUIResource(font);
			for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value instanceof FontUIResource) {
					UIManager.put(key, fontRes);
				}
			}
			R.LOGGER.info("注册皮肤包完成");
		} catch (Exception e) {
			R.LOGGER.warning("程序不支持UIManager.setLookAndFeel");
		}
	}

	/**
	 * JFreeChart字体设定，同时设定字体也解决了图表上的乱码
	 */
	public void initChartFont() {
		// 获得字体
		Font font = R.FRAME_CFG.mainCfg.font;
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(font);
		// 设置图例的字体
		standardChartTheme.setRegularFont(font);
		// 设置轴向的字体
		standardChartTheme.setLargeFont(font);
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
		R.LOGGER.info("解决图表乱码完成");
	}

	/**
	 * 将各部分的控件组合到主窗口上
	 */
	public void buildingWindow() {
		// 获得主窗口
		JFrame mainFrame = factory.getMainFrame();
		// 获得控制面板（按钮）
		JPanel controlPanel = factory.getControlPanel();
		// 将按钮控件添加到控制面板上
		for (JButton button : factory.getControlButtons()) {
			controlPanel.add(button);
		}
		// 将控制面板添加到窗口左边
		mainFrame.add(controlPanel, BorderLayout.WEST);
		// 获得卡片面板的名称列表，此名称用于卡片面板的跳转，不用于显示
		String[] cardNames = R.FRAME_CFG.cardCfg.cardNames;
		JPanel cardPanel = factory.getCenterPanel();
		cardPanel.add(factory.getNetworkPanel(), cardNames[0]);
		cardPanel.add(factory.getPathPanel(), cardNames[1]);
		cardPanel.add(factory.getSingleEnergyPanel(), cardNames[2]);
		cardPanel.add(factory.getEnergyPanel(), cardNames[3]);
		cardPanel.add(factory.getPacketPanel(), cardNames[4]);
		cardPanel.add(factory.getAutoControlPanel(), cardNames[5]);
		// 将卡片面板添加到窗口的中间，实际上不设置大小时，会自动填充
		mainFrame.add(cardPanel, BorderLayout.CENTER);
		R.LOGGER.info("创建并组合窗口组件完成");
	}

	/**
	 * 根据按钮的名称，向按钮添加处理事件
	 */
	public void setListener() {
		// 获得控制面板上的按钮
		JButton[] buttons = factory.getControlButtons();
		ListenerFactory listenerFactory = ListenerFactory.newInstence();
		for (JButton button : buttons) {
			ActionListener listener = listenerFactory.getListener(button.getText());
			if (listener != null) {
				button.addActionListener(listener);
			}
		}
		// 获得拓扑展示面板上的按钮
		buttons = factory.getTopoButtons();
		for (JButton button : buttons) {
			ActionListener listener = listenerFactory.getListener(button.getText());
			if (listener != null) {
				button.addActionListener(listener);
			}
		}
		// 获得路径面板上的按钮
		buttons = factory.getPathButtons();
		for (JButton button : buttons) {
			ActionListener listener = listenerFactory.getListener(button.getText());
			if (listener != null) {
				button.addActionListener(listener);
			}
		}
		JButton button = factory.getAutoControlButton();
		ActionListener listener = listenerFactory.getListener(button.getText());
		if (listener != null) {
			button.addActionListener(listener);
		}
		R.LOGGER.info("向按钮添加事件完成");
	}
}
