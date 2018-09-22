package cn.upc.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import cn.upc.res.FrameCfg.CardCfg;
import cn.upc.res.FrameCfg.ControlCfg;
import cn.upc.res.FrameCfg.MainCfg;
import cn.upc.res.R;
import cn.upc.res.Util;
import twaver.TDataBox;
import twaver.network.TNetwork;

/**
 * ����ؼ�����,�ؼ������ɽ��ڴ˴��� ʹ�õ������ģʽ����֤��������Ψһ�� ʹ��HashMap���л��棬��֤ÿ�λ�õĶ�����Ψһ�ģ�
 * �����ɵ�ʱ��ʹ�÷���Supplier����ʽ�ӿڣ��ô��Ǵ��������٣�����������Դ�٣�ֻ����ҪһЩlambdaʽ��֪ʶ
 * 
 * @author ����չ
 *
 */
public class UiFactory {
	/**
	 * ��Ϊ��ǰ���Ψһ����
	 */
	private static UiFactory instence;
	/**
	 * ���棬��������ɵĶ��󱣴棬�´λ�ȡʱ��֤��ͬ���Ķ���
	 */
	private static Cache cache;

	/**
	 * ˽�й���������֤�ⲿ�޷������˶���
	 */
	private UiFactory() {
		cache = new Cache();
	}

	/**
	 * ��̬���л�ö��󷽷�����֤�ⲿ��õ�����Ķ���
	 * 
	 * @return
	 */
	public static UiFactory newInstence() {
		// �ڵ�һ�λ�ö���������˶���
		if (instence == null) {
			instence = new UiFactory();
		}
		return instence;
	}

	private class Cache {
		private HashMap<String, Object> buffer;

		public Cache() {
			buffer = new HashMap<>();
		}

		/**
		 * ע�����
		 * 
		 * @param name������
		 * @param obj����
		 */
		public void register(String name, Object obj) {
			buffer.put(name, obj);
		}

		/**
		 * �ж϶����Ƿ��Ѿ�ע���
		 * 
		 * @param name������
		 * @return �����Ƿ�ע���
		 */
		public boolean isRegistered(String name) {
			return buffer.containsKey(name);
		}

		/**
		 * ���ݶ�������ö���
		 * 
		 * @param name������
		 * @return ����
		 */
		public Object get(String name) {
			return buffer.get(name);
		}
	}

	/**
	 * ���������
	 * 
	 * @return
	 */
	public JFrame getMainFrame() {
		return nullCreateGet("MainFrame", () -> {
			// ��������������ļ�����
			MainCfg cfg = R.FRAME_CFG.mainCfg;
			// �������ڵ�ͬʱ���ñ���
			JFrame frame = new JFrame(cfg.title);
			// ����ͼ��
			frame.setIconImage(new ImageIcon(cfg.iconPath).getImage());
			// ���ô��ڴ�С
			frame.setSize(cfg.width, cfg.height);
			// ���ô��ڲ��ɸı��С
			frame.setResizable(false);
			// ���ô��ڼ���λ���ǲ�������������λ������
			// mainFrame.setLocationRelativeTo(null);
			// ���������Ǽ����ã����Խ��������һЩ������
			Util.setLocationCenter(frame);
			// ���ô��ڹرշ�ʽ��3Ϊ�رմ���ʱ�رճ���
			frame.setDefaultCloseOperation(3);
			// ���ô������ݲ���Ϊ�����ϱ�
			frame.setLayout(new BorderLayout());
			return frame;
		});
	}

	/**
	 * ��ð�ť�������
	 * 
	 * @return
	 */
	public JPanel getControlPanel() {
		return nullCreateGet("ControlPanel", () -> {
			// ��ÿ���������ö���
			ControlCfg cfg = R.FRAME_CFG.controlCfg;
			// �������
			JPanel panel = new JPanel();
			// ��BorderLayer�����£����ù̶���С
			panel.setPreferredSize(new Dimension(cfg.width, cfg.height));
			// �������Ϊ���Բ���
			panel.setLayout(null);
			return panel;
		});
	}

	/**
	 * ��ÿ�����尴ť
	 * 
	 * @return
	 */
	public JButton[] getControlButtons() {
		return nullCreateGet("ControlButtons", () -> {
			// ��ÿ���������ö���
			ControlCfg cfg = R.FRAME_CFG.controlCfg;
			// �����ö����л�ð�ť��������������������ť
			JButton[] buttons = new JButton[cfg.buttonNames.length];
			// ���㰴ť���
			Dimension dim = Util.getPad(cfg.width, cfg.height, cfg.buttonWidth, cfg.buttonHeight,
					cfg.buttonNames.length, 1);
			for (int i = 0; i < buttons.length; i++) {
				// ��ť���ı���ͬʱ���ݴ��ļ��밴ť��������¼����й���
				buttons[i] = new JButton(cfg.buttonNames[i]);
				// ���ð�ť�Ĵ�С
				buttons[i].setSize(cfg.buttonWidth, cfg.buttonHeight);
				// ����λ�ã�ֻ�ھ��Բ�����������
				buttons[i].setLocation(dim.width, dim.height + (dim.height + cfg.buttonHeight) * i);
			}
			return buttons;
		});
	}

	/**
	 * ��ÿ�Ƭ���ֹ�����,������ӿ�Ƭ���͵�����չʾ�����ҳ��
	 * 
	 * @return
	 */
	public CardLayout getCardLayout() {
		return (CardLayout) nullCreateGet("CardLayout", () -> new CardLayout());
	}

	/**
	 * ���������壬Ҳ���������
	 * 
	 * @return
	 */
	public JPanel getCenterPanel() {
		return (JPanel) nullCreateGet("CenterPanel", () -> {
			// ���������
			JPanel panel = new JPanel();
			// ���������Ĳ���Ϊ��Ƭʽ����
			panel.setLayout(getCardLayout());
			return panel;
		});
	}

	/**
	 * �������չʾ���棨������е�һ����Ƭ��壩
	 * 
	 * @return
	 */
	public JPanel getNetworkPanel() {
		return nullCreateGet("NetworkPanel", () -> {
			// ��ÿ�Ƭ������ö���
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			// ��������չʾ���
			JPanel panel = new JPanel();
			panel.setSize(cfg.width, cfg.height);
			// �����������Ϊ���Բ��֣������Ϸ�������չʾ���·��ǲٿذ�ť
			panel.setLayout(null);
			// ���ñ�����ɫΪ����ɫ
			panel.setBackground(Color.LIGHT_GRAY);
			// ��������չʾ�ؼ�
			TNetwork network = new TNetwork();
			// ͨ���������ù�������ʵ����û�����������������ȡ����ʾ������
			network.setToolbarByName("�޹�����");
			// �������ݼ���Ҳ����������Դ��ͬʱ������Դ���ݸ���ʱ���������Ҳ��ͬ������
			network.setDataBox(getDataBox());
			// Ԥ�����·���ť�ؼ���λ��
			network.setSize(cfg.width, cfg.height - cfg.buttonPanelHeight);
			panel.add(network);
			// ����·��İ�ť�ؼ��������������
			for (JButton button : getTopoButtons()) {
				panel.add(button);
			}
			return panel;
		});
	}

	/**
	 * ��ð���������Ϣ��������
	 * 
	 * @return
	 */
	public TDataBox getDataBox() {
		return nullCreateGet("DataBox", TDataBox::new);
	}

	/**
	 * ����չʾ����İ�ť
	 * 
	 * @return
	 */
	public JButton[] getTopoButtons() {
		return nullCreateGet("TopoButtons", () -> {
			// ��ÿ�Ƭ������ö���
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			JButton[] buttons = new JButton[cfg.buttonNames.length];
			// ���㰴ť֮����
			Dimension dim = Util.getPad(cfg.width, cfg.buttonPanelHeight, cfg.buttonWidth, cfg.buttonHeight, 1,
					cfg.buttonNames.length);
			for (int i = 0; i < buttons.length; i++) {
				// ��ť���ı���ͬʱ���ݴ��ļ��밴ť��������¼����й���
				buttons[i] = new JButton(cfg.buttonNames[i]);
				// ���ð�ť�Ĵ�С
				buttons[i].setSize(cfg.buttonWidth, cfg.buttonHeight);
				// ���ð�ť��λ�ã�ֻ���ھ��Բ�����������
				buttons[i].setLocation(dim.width + (dim.width + cfg.buttonWidth) * i,
						cfg.height - cfg.buttonHeight - dim.height);
			}
			return buttons;
		});
	}

	/**
	 * ���·���������
	 * 
	 * @return
	 */
	public JPanel getPathPanel() {
		return nullCreateGet("PathPanel", () -> {
			// ��ÿ�Ƭ������ö���
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			// ����·���������
			JPanel panel = new JPanel();
			panel.setSize(cfg.width, cfg.height);
			panel.setBackground(Color.LIGHT_GRAY);
			// Ϊ��ǰ������һ��������ı߿�
			panel.setBorder(BorderFactory.createTitledBorder("·������"));
			// ���þ��Բ���
			panel.setLayout(null);
			// ��Ӱ�ť
			JButton[] buttons = getPathButtons();
			for (JButton button : buttons) {
				panel.add(button);
			}
			// ���·���ı���
			JTextField field = getPathField();
			int w = buttons[buttons.length - 1].getX() - buttons[0].getX() + cfg.buttonWidth;
			int h = cfg.buttonHeight;
			int x = buttons[0].getX();
			int y = buttons[0].getY() - cfg.buttonHeight * 2;
			field.setSize(w, h);
			field.setLocation(x, y);
			panel.add(field);
			// ���·���б�
			JScrollPane pane = new JScrollPane(getPathList());
			h = cfg.height - buttons[0].getY() - cfg.buttonHeight * 3;
			y = buttons[0].getY() + cfg.buttonHeight * 2;
			pane.setSize(w, h);
			pane.setLocation(x, y);
			panel.add(pane);
			return panel;
		});
	}

	/**
	 * ���·���������İ�ť�ؼ�
	 * 
	 * @return
	 */
	public JButton[] getPathButtons() {
		return nullCreateGet("PathButtons", () -> {
			String[] names = { "·��Ԥ��", "·���ɴ�", "���ʼ���", "��������", "·������", "����ʵʩ" };
			// ��ÿ�Ƭ������ö���
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			Dimension dim = Util.getPad(cfg.width, cfg.height, cfg.buttonWidth, cfg.buttonHeight, 1, names.length);
			JButton buttons[] = new JButton[names.length];
			for (int i = 0; i < names.length; i++) {
				buttons[i] = new JButton(names[i]);
				buttons[i].setSize(cfg.buttonWidth, cfg.buttonHeight);
				buttons[i].setLocation(dim.width + (dim.width + cfg.buttonWidth) * i, cfg.buttonHeight * 3);
			}
			return buttons;
		});
	}

	/**
	 * ���·�������
	 * 
	 * @return
	 */
	public JTextField getPathField() {
		return nullCreateGet("PathField", JTextField::new);
	}

	/**
	 * ���·��չʾ�б�
	 * 
	 * @return
	 */
	public JList<String> getPathList() {
		return nullCreateGet("PathList", () -> {
			JList<String> list = new JList<>();
			DefaultListModel<String> model = getPathListModel();
			list.setModel(model);
			return list;
		});
	}

	/**
	 * ���·��ģ��
	 * 
	 * @return
	 */
	public DefaultListModel<String> getPathListModel() {
		return nullCreateGet("PathListModel", DefaultListModel::new);
	}

	/**
	 * ����ܺ����ݼ�
	 * 
	 * @return
	 */
	public DefaultCategoryDataset getEnergyDataset() {
		return nullCreateGet("EnergyDataset", () -> {
			// �����ܺ����ݼ�
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			return dataset;
		});
	}

	/**
	 * ����ܺ�չʾ���
	 * 
	 * @return
	 */
	public ChartPanel getEnergyPanel() {
		return nullCreateGet("EnergyPanel", () -> {
			// �����ܺ�3D����ͼ
			JFreeChart chart = ChartFactory.createBarChart3D("��ͳ������SDN�����ܺĶԱ�", "·�ɲ���", "�ܺĶ���", getEnergyDataset(),
					PlotOrientation.VERTICAL, true, true, true);
			BarRenderer3D barRenderer3D = (BarRenderer3D) ((CategoryPlot) chart.getPlot()).getRenderer();
			barRenderer3D.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			barRenderer3D.setBaseItemLabelsVisible(true);
			ChartPanel panel = new ChartPanel(chart);
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			panel.setSize(cfg.width, cfg.height);
			return panel;
		});
	}

	/**
	 * ���չʾÿ�������������ܺĵ����ݼ�
	 * 
	 * @return
	 */
	public DefaultCategoryDataset getSingleEnergyDataset() {
		return nullCreateGet("SingleEnergyDataset", () -> {
			// �����ܺ����ݼ�
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			return dataset;
		});
	}

	/**
	 * ���չʾÿ�������������ܺ�չʾ���
	 * 
	 * @return
	 */
	public ChartPanel getSingleEnergyPanel() {
		return nullCreateGet("SingleEnergyPanel", () -> {
			// �����ܺ�3D����ͼ
			JFreeChart chart = ChartFactory.createBarChart3D("��SDN�������ܺĶԱ�", "������ID", "�ܺĶ���", getSingleEnergyDataset(),
					PlotOrientation.VERTICAL, true, true, true);
			BarRenderer3D barRenderer3D = (BarRenderer3D) ((CategoryPlot) chart.getPlot()).getRenderer();
			barRenderer3D.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			barRenderer3D.setBaseItemLabelsVisible(true);
			ChartPanel panel = new ChartPanel(chart);
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			panel.setSize(cfg.width, cfg.height);
			return panel;
		});
	}

	/**
	 * ����������ݼ�
	 * 
	 * @return
	 */
	public DefaultCategoryDataset getPacketDataset() {
		return nullCreateGet("PacketDataset", DefaultCategoryDataset::new);
	}

	/**
	 * �������չʾ���
	 * 
	 * @return
	 */
	public ChartPanel getPacketPanel() {
		return nullCreateGet("PacketPanel", () -> {
			JFreeChart chart = ChartFactory.createLineChart("��ͬ������ת�������ݰ�", "������ID", "ת�������ݰ�", getPacketDataset(),
					PlotOrientation.VERTICAL, true, true, true);
			// ���ͼ���������
			CategoryPlot categoryPlot = chart.getCategoryPlot();
			// ���Y�����
			NumberAxis numberAxis = (NumberAxis) categoryPlot.getRangeAxis();
			// ��Y������ʾ�̶�
			numberAxis.setAutoTickUnitSelection(false);
			// ��10��Ϊ1��
			numberAxis.setTickUnit(new NumberTickUnit(10));
			// ��ȡ��ͼ�������
			LineAndShapeRenderer lineAndShapeRenderer = (LineAndShapeRenderer) categoryPlot.getRenderer();
			// �ڹսǴ�����3���صĵ�
			Rectangle shape = new Rectangle(3, 3);
			lineAndShapeRenderer.setSeriesShape(0, shape);
			lineAndShapeRenderer.setSeriesShapesVisible(0, true);
			ChartPanel panel = new ChartPanel(chart);
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			panel.setSize(cfg.width, cfg.height);
			return panel;
		});
	}

	/**
	 * �Զ����ư�ť
	 * 
	 * @return
	 */
	public JButton getAutoControlButton() {
		return nullCreateGet("AutoControlButton", () -> {
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			JButton button = new JButton(cfg.autoButtonName);
			button.setSize(cfg.buttonWidth, cfg.buttonHeight);
			return button;
		});
	}

	/**
	 * �Զ�����״̬�ı���
	 * 
	 * @return
	 */
	public JTextField getAutoControlStateField() {
		return nullCreateGet("AutoControlStateField", () -> {
			JTextField field = new JTextField();
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			field.setSize(cfg.buttonWidth * 5, cfg.buttonHeight);
			field.setEditable(false);
			return field;
		});
	}

	/**
	 * �Զ�������Ϣ�б�ģ��
	 * 
	 * @return
	 */
	public DefaultListModel<String> getAutoControlMessageListModel() {
		return nullCreateGet("AutoControlListModel", () -> {
			return new DefaultListModel<String>();
		});
	}

	/**
	 * �Զ��������
	 * 
	 * @return
	 */
	public JPanel getAutoControlPanel() {
		return nullCreateGet("AutoControlPanel", () -> {
			JPanel panel = new JPanel();
			panel.setLayout(null);
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			panel.setSize(cfg.width, cfg.height);
			panel.setBorder(BorderFactory.createTitledBorder("�Զ�����"));
			panel.setBackground(Color.LIGHT_GRAY);
			JButton button = getAutoControlButton();
			JTextField field = getAutoControlStateField();
			JList<String> list = new JList<>(getAutoControlMessageListModel());
			int padx = (cfg.width - button.getWidth() - field.getWidth()) / 3;
			button.setLocation(padx, cfg.buttonHeight);
			panel.add(button);
			field.setLocation(padx * 2 + button.getWidth(), cfg.buttonHeight);
			panel.add(field);
			list.setSize(cfg.width - padx * 2, cfg.height - button.getHeight() * 4);
			list.setLocation(padx, button.getHeight() + cfg.buttonHeight * 2);
			panel.add(list);
			return panel;
		});
	}

	/**
	 * ����name���в��ң�������󲻴��ڣ��ʹ�������Ȼ�󷵻ض���
	 * 
	 * @param name
	 * @param supplier
	 *            ����ʽ��̽ӿ�
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T nullCreateGet(String name, Supplier<T> supplier) {
		if (!cache.isRegistered(name)) {// ���������û��ע��˶���
			// Supplier���󴴽�һ��Ŀ�����ͬʱ����ע�ᵽ������
			cache.register(name, supplier.get());
		}
		// ������ӻ�����ȡ��������
		return (T) cache.get(name);
	}
}
