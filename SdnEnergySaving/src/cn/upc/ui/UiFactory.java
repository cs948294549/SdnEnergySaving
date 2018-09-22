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
 * 界面控件工厂,控件的生成将在此处； 使用单例设计模式，保证工厂对象唯一； 使用HashMap进行缓存，保证每次获得的对象是唯一的；
 * 在生成的时候，使用泛型Supplier函数式接口，好处是代码量减少，而且消耗资源少，只是需要一些lambda式的知识
 * 
 * @author 江荣展
 *
 */
public class UiFactory {
	/**
	 * 此为当前类的唯一对象
	 */
	private static UiFactory instence;
	/**
	 * 缓存，将所有造成的对象保存，下次获取时保证是同样的对象
	 */
	private static Cache cache;

	/**
	 * 私有构造器，保证外部无法创建此对象
	 */
	private UiFactory() {
		cache = new Cache();
	}

	/**
	 * 静态公有获得对象方法，保证外部获得到此类的对象
	 * 
	 * @return
	 */
	public static UiFactory newInstence() {
		// 在第一次获得对象进创建此对象
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
		 * 注册对象
		 * 
		 * @param name对象名
		 * @param obj对象
		 */
		public void register(String name, Object obj) {
			buffer.put(name, obj);
		}

		/**
		 * 判断对象是否已经注册过
		 * 
		 * @param name对象名
		 * @return 对象是否注册过
		 */
		public boolean isRegistered(String name) {
			return buffer.containsKey(name);
		}

		/**
		 * 根据对象名获得对象
		 * 
		 * @param name对象名
		 * @return 对象
		 */
		public Object get(String name) {
			return buffer.get(name);
		}
	}

	/**
	 * 获得主窗口
	 * 
	 * @return
	 */
	public JFrame getMainFrame() {
		return nullCreateGet("MainFrame", () -> {
			// 获得主窗口配置文件对象
			MainCfg cfg = R.FRAME_CFG.mainCfg;
			// 创建窗口的同时设置标题
			JFrame frame = new JFrame(cfg.title);
			// 设置图标
			frame.setIconImage(new ImageIcon(cfg.iconPath).getImage());
			// 设置窗口大小
			frame.setSize(cfg.width, cfg.height);
			// 设置窗口不可改变大小
			frame.setResizable(false);
			// 设置窗口计算位置是不关心其他，即位于中心
			// mainFrame.setLocationRelativeTo(null);
			// 下面这种是计算获得，可以将窗口提高一些，美化
			Util.setLocationCenter(frame);
			// 设置窗口关闭方式，3为关闭窗口时关闭程序
			frame.setDefaultCloseOperation(3);
			// 设置窗口内容布局为东西南北
			frame.setLayout(new BorderLayout());
			return frame;
		});
	}

	/**
	 * 获得按钮控制面板
	 * 
	 * @return
	 */
	public JPanel getControlPanel() {
		return nullCreateGet("ControlPanel", () -> {
			// 获得控制面板配置对象
			ControlCfg cfg = R.FRAME_CFG.controlCfg;
			// 创建面板
			JPanel panel = new JPanel();
			// 在BorderLayer布局下，设置固定大小
			panel.setPreferredSize(new Dimension(cfg.width, cfg.height));
			// 设置面板为绝对布局
			panel.setLayout(null);
			return panel;
		});
	}

	/**
	 * 获得控制面板按钮
	 * 
	 * @return
	 */
	public JButton[] getControlButtons() {
		return nullCreateGet("ControlButtons", () -> {
			// 获得控制面板配置对象
			ControlCfg cfg = R.FRAME_CFG.controlCfg;
			// 从配置对象中获得按钮名单，根据数量创建按钮
			JButton[] buttons = new JButton[cfg.buttonNames.length];
			// 计算按钮间隔
			Dimension dim = Util.getPad(cfg.width, cfg.height, cfg.buttonWidth, cfg.buttonHeight,
					cfg.buttonNames.length, 1);
			for (int i = 0; i < buttons.length; i++) {
				// 按钮的文本，同时根据此文件与按钮所对象的事件进行关联
				buttons[i] = new JButton(cfg.buttonNames[i]);
				// 设置按钮的大小
				buttons[i].setSize(cfg.buttonWidth, cfg.buttonHeight);
				// 设置位置，只在绝对布局中有作用
				buttons[i].setLocation(dim.width, dim.height + (dim.height + cfg.buttonHeight) * i);
			}
			return buttons;
		});
	}

	/**
	 * 获得卡片布局管理器,用来添加卡片面板和调整所展示的面板页数
	 * 
	 * @return
	 */
	public CardLayout getCardLayout() {
		return (CardLayout) nullCreateGet("CardLayout", () -> new CardLayout());
	}

	/**
	 * 获得中心面板，也就是主面板
	 * 
	 * @return
	 */
	public JPanel getCenterPanel() {
		return (JPanel) nullCreateGet("CenterPanel", () -> {
			// 创建主面板
			JPanel panel = new JPanel();
			// 设置主面板的布局为卡片式布局
			panel.setLayout(getCardLayout());
			return panel;
		});
	}

	/**
	 * 获得拓扑展示界面（主面板中第一个卡片面板）
	 * 
	 * @return
	 */
	public JPanel getNetworkPanel() {
		return nullCreateGet("NetworkPanel", () -> {
			// 获得卡片面板配置对象
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			// 创建拓扑展示面板
			JPanel panel = new JPanel();
			panel.setSize(cfg.width, cfg.height);
			// 设置拓扑面板为绝对布局，其中上方是拓扑展示，下方是操控按钮
			panel.setLayout(null);
			// 设置背景颜色为淡灰色
			panel.setBackground(Color.LIGHT_GRAY);
			// 创建拓扑展示控件
			TNetwork network = new TNetwork();
			// 通过名称设置工具栏，实际上没有这个东西，所以是取消显示工具栏
			network.setToolbarByName("无工具栏");
			// 设置数据集，也就是数据来源，同时数据来源数据更新时，面板数据也会同步更新
			network.setDataBox(getDataBox());
			// 预留出下方按钮控件的位置
			network.setSize(cfg.width, cfg.height - cfg.buttonPanelHeight);
			panel.add(network);
			// 获得下方的按钮控件，并加入面板中
			for (JButton button : getTopoButtons()) {
				panel.add(button);
			}
			return panel;
		});
	}

	/**
	 * 获得包含拓扑信息的数据箱
	 * 
	 * @return
	 */
	public TDataBox getDataBox() {
		return nullCreateGet("DataBox", TDataBox::new);
	}

	/**
	 * 拓扑展示界面的按钮
	 * 
	 * @return
	 */
	public JButton[] getTopoButtons() {
		return nullCreateGet("TopoButtons", () -> {
			// 获得卡片面板配置对象
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			JButton[] buttons = new JButton[cfg.buttonNames.length];
			// 计算按钮之间间距
			Dimension dim = Util.getPad(cfg.width, cfg.buttonPanelHeight, cfg.buttonWidth, cfg.buttonHeight, 1,
					cfg.buttonNames.length);
			for (int i = 0; i < buttons.length; i++) {
				// 按钮的文本，同时根据此文件与按钮所对象的事件进行关联
				buttons[i] = new JButton(cfg.buttonNames[i]);
				// 设置按钮的大小
				buttons[i].setSize(cfg.buttonWidth, cfg.buttonHeight);
				// 设置按钮的位置，只有在绝对布局中有作用
				buttons[i].setLocation(dim.width + (dim.width + cfg.buttonWidth) * i,
						cfg.height - cfg.buttonHeight - dim.height);
			}
			return buttons;
		});
	}

	/**
	 * 获得路径计算面板
	 * 
	 * @return
	 */
	public JPanel getPathPanel() {
		return nullCreateGet("PathPanel", () -> {
			// 获得卡片面板配置对象
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			// 创建路径计算面板
			JPanel panel = new JPanel();
			panel.setSize(cfg.width, cfg.height);
			panel.setBackground(Color.LIGHT_GRAY);
			// 为当前面板添加一个带标题的边框
			panel.setBorder(BorderFactory.createTitledBorder("路径计算"));
			// 设置绝对布局
			panel.setLayout(null);
			// 添加按钮
			JButton[] buttons = getPathButtons();
			for (JButton button : buttons) {
				panel.add(button);
			}
			// 添加路径文本框
			JTextField field = getPathField();
			int w = buttons[buttons.length - 1].getX() - buttons[0].getX() + cfg.buttonWidth;
			int h = cfg.buttonHeight;
			int x = buttons[0].getX();
			int y = buttons[0].getY() - cfg.buttonHeight * 2;
			field.setSize(w, h);
			field.setLocation(x, y);
			panel.add(field);
			// 添加路径列表
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
	 * 获得路径计算面板的按钮控件
	 * 
	 * @return
	 */
	public JButton[] getPathButtons() {
		return nullCreateGet("PathButtons", () -> {
			String[] names = { "路径预览", "路径可达", "速率计算", "新增计算", "路径排序", "部署实施" };
			// 获得卡片面板配置对象
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
	 * 获得路径输入框
	 * 
	 * @return
	 */
	public JTextField getPathField() {
		return nullCreateGet("PathField", JTextField::new);
	}

	/**
	 * 获得路径展示列表
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
	 * 获得路径模型
	 * 
	 * @return
	 */
	public DefaultListModel<String> getPathListModel() {
		return nullCreateGet("PathListModel", DefaultListModel::new);
	}

	/**
	 * 获得能耗数据集
	 * 
	 * @return
	 */
	public DefaultCategoryDataset getEnergyDataset() {
		return nullCreateGet("EnergyDataset", () -> {
			// 创建能耗数据集
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			return dataset;
		});
	}

	/**
	 * 获得能耗展示面板
	 * 
	 * @return
	 */
	public ChartPanel getEnergyPanel() {
		return nullCreateGet("EnergyPanel", () -> {
			// 创建能耗3D条形图
			JFreeChart chart = ChartFactory.createBarChart3D("传统网络与SDN网络能耗对比", "路由策略", "能耗度量", getEnergyDataset(),
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
	 * 获得展示每个交换机自身能耗的数据集
	 * 
	 * @return
	 */
	public DefaultCategoryDataset getSingleEnergyDataset() {
		return nullCreateGet("SingleEnergyDataset", () -> {
			// 创建能耗数据集
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			return dataset;
		});
	}

	/**
	 * 获得展示每个交换机自身能耗展示面板
	 * 
	 * @return
	 */
	public ChartPanel getSingleEnergyPanel() {
		return nullCreateGet("SingleEnergyPanel", () -> {
			// 创建能耗3D条形图
			JFreeChart chart = ChartFactory.createBarChart3D("各SDN交换机能耗对比", "交换机ID", "能耗度量", getSingleEnergyDataset(),
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
	 * 获得流包数据集
	 * 
	 * @return
	 */
	public DefaultCategoryDataset getPacketDataset() {
		return nullCreateGet("PacketDataset", DefaultCategoryDataset::new);
	}

	/**
	 * 获得流表展示面板
	 * 
	 * @return
	 */
	public ChartPanel getPacketPanel() {
		return nullCreateGet("PacketPanel", () -> {
			JFreeChart chart = ChartFactory.createLineChart("不同交换机转发的数据包", "交换机ID", "转发的数据包", getPacketDataset(),
					PlotOrientation.VERTICAL, true, true, true);
			// 获得图表区域对象
			CategoryPlot categoryPlot = chart.getCategoryPlot();
			// 获得Y轴对象
			NumberAxis numberAxis = (NumberAxis) categoryPlot.getRangeAxis();
			// 在Y轴上显示刻度
			numberAxis.setAutoTickUnitSelection(false);
			// 以10作为1格
			numberAxis.setTickUnit(new NumberTickUnit(10));
			// 获取绘图区域对象
			LineAndShapeRenderer lineAndShapeRenderer = (LineAndShapeRenderer) categoryPlot.getRenderer();
			// 在拐角处生成3像素的点
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
	 * 自动控制按钮
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
	 * 自动控制状态文本框
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
	 * 自动控制信息列表模型
	 * 
	 * @return
	 */
	public DefaultListModel<String> getAutoControlMessageListModel() {
		return nullCreateGet("AutoControlListModel", () -> {
			return new DefaultListModel<String>();
		});
	}

	/**
	 * 自动控制面板
	 * 
	 * @return
	 */
	public JPanel getAutoControlPanel() {
		return nullCreateGet("AutoControlPanel", () -> {
			JPanel panel = new JPanel();
			panel.setLayout(null);
			CardCfg cfg = R.FRAME_CFG.cardCfg;
			panel.setSize(cfg.width, cfg.height);
			panel.setBorder(BorderFactory.createTitledBorder("自动控制"));
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
	 * 根据name进行查找，如果对象不存在，就创建对象，然后返回对象
	 * 
	 * @param name
	 * @param supplier
	 *            函数式编程接口
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T nullCreateGet(String name, Supplier<T> supplier) {
		if (!cache.isRegistered(name)) {// 如果缓存中没有注册此对象
			// Supplier对象创建一个目标对象，同时将它注册到缓存中
			cache.register(name, supplier.get());
		}
		// 将对象从缓存中取出并返回
		return (T) cache.get(name);
	}
}
