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
 * ���������Ҫ������沼�֣��Ӳ��ֻ�����ݳ��������ؼ���ϣ��¼���ӵ�
 * 
 * @author ����չ
 *
 */
public class UiService {
	private UiFactory factory;

	public UiService() {
		factory = UiFactory.newInstence();
	}

	/**
	 * Ƥ����ע��,���ʹ��JAVA�Դ�������۸ı�
	 */
	public void initOutLook() {
		try {
			// �������
			Font font = R.FRAME_CFG.mainCfg.font;
			// ���������ʽ
			UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
			// ���ô��ڱ�������
			UIManager.put("InternalFrame.titleFont", font);
			// ������λ�����嶼����ΪĿ������
			FontUIResource fontRes = new FontUIResource(font);
			for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value instanceof FontUIResource) {
					UIManager.put(key, fontRes);
				}
			}
			R.LOGGER.info("ע��Ƥ�������");
		} catch (Exception e) {
			R.LOGGER.warning("����֧��UIManager.setLookAndFeel");
		}
	}

	/**
	 * JFreeChart�����趨��ͬʱ�趨����Ҳ�����ͼ���ϵ�����
	 */
	public void initChartFont() {
		// �������
		Font font = R.FRAME_CFG.mainCfg.font;
		// ����������ʽ
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// ���ñ�������
		standardChartTheme.setExtraLargeFont(font);
		// ����ͼ��������
		standardChartTheme.setRegularFont(font);
		// �������������
		standardChartTheme.setLargeFont(font);
		// Ӧ��������ʽ
		ChartFactory.setChartTheme(standardChartTheme);
		R.LOGGER.info("���ͼ���������");
	}

	/**
	 * �������ֵĿؼ���ϵ���������
	 */
	public void buildingWindow() {
		// ���������
		JFrame mainFrame = factory.getMainFrame();
		// ��ÿ�����壨��ť��
		JPanel controlPanel = factory.getControlPanel();
		// ����ť�ؼ���ӵ����������
		for (JButton button : factory.getControlButtons()) {
			controlPanel.add(button);
		}
		// �����������ӵ��������
		mainFrame.add(controlPanel, BorderLayout.WEST);
		// ��ÿ�Ƭ���������б����������ڿ�Ƭ������ת����������ʾ
		String[] cardNames = R.FRAME_CFG.cardCfg.cardNames;
		JPanel cardPanel = factory.getCenterPanel();
		cardPanel.add(factory.getNetworkPanel(), cardNames[0]);
		cardPanel.add(factory.getPathPanel(), cardNames[1]);
		cardPanel.add(factory.getSingleEnergyPanel(), cardNames[2]);
		cardPanel.add(factory.getEnergyPanel(), cardNames[3]);
		cardPanel.add(factory.getPacketPanel(), cardNames[4]);
		cardPanel.add(factory.getAutoControlPanel(), cardNames[5]);
		// ����Ƭ�����ӵ����ڵ��м䣬ʵ���ϲ����ô�Сʱ�����Զ����
		mainFrame.add(cardPanel, BorderLayout.CENTER);
		R.LOGGER.info("��������ϴ���������");
	}

	/**
	 * ���ݰ�ť�����ƣ���ť��Ӵ����¼�
	 */
	public void setListener() {
		// ��ÿ�������ϵİ�ť
		JButton[] buttons = factory.getControlButtons();
		ListenerFactory listenerFactory = ListenerFactory.newInstence();
		for (JButton button : buttons) {
			ActionListener listener = listenerFactory.getListener(button.getText());
			if (listener != null) {
				button.addActionListener(listener);
			}
		}
		// �������չʾ����ϵİ�ť
		buttons = factory.getTopoButtons();
		for (JButton button : buttons) {
			ActionListener listener = listenerFactory.getListener(button.getText());
			if (listener != null) {
				button.addActionListener(listener);
			}
		}
		// ���·������ϵİ�ť
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
		R.LOGGER.info("��ť����¼����");
	}
}
