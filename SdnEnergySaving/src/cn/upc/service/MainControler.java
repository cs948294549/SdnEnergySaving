package cn.upc.service;

import java.awt.EventQueue;

import cn.upc.ui.UiFactory;

public class MainControler {
	public void systemStart() {
		// 创建界面控制器
		UiService uiControler = new UiService();
		Thread ui = new Thread(() -> {// 初始窗口化皮肤包，字体等
			uiControler.initOutLook();
		});
		ui.start();
		Thread chart = new Thread(() -> {// 初始化图表的字体（解决乱码）
			uiControler.initChartFont();
		});
		chart.start();
		Thread wind = new Thread(() -> {
			try {
				ui.join();
				chart.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 组织窗口组件
			uiControler.buildingWindow();
			// 设置监听事件
			uiControler.setListener();
		});
		wind.start();
		Thread data = new Thread(() -> {
			// 创建数据控制器
			DataService dataControler = new DataService();
			// 初始化数据
			dataControler.deepInitData();
		});
		data.start();
		Thread frame = new Thread(() -> {
			try {
				wind.join();
				data.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 填充数据
			new FillService().fillDataInBox();
			// 显示主窗口
			EventQueue.invokeLater(() -> UiFactory.newInstence().getMainFrame().setVisible(true));
		});
		frame.start();

		AutoControler controler = AutoControler.newInstance();
		// 开启自动控制线程
		new Thread(() -> controler.accecp()).start();
		new Thread(() -> controler.clear()).start();
		new Thread(() -> controler.handle()).start();
	}

	public static void main(String[] args) {
		new MainControler().systemStart();
	}
}
