package cn.upc.service;

import java.awt.EventQueue;

import cn.upc.ui.UiFactory;

public class MainControler {
	public void systemStart() {
		// �������������
		UiService uiControler = new UiService();
		Thread ui = new Thread(() -> {// ��ʼ���ڻ�Ƥ�����������
			uiControler.initOutLook();
		});
		ui.start();
		Thread chart = new Thread(() -> {// ��ʼ��ͼ������壨������룩
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
			// ��֯�������
			uiControler.buildingWindow();
			// ���ü����¼�
			uiControler.setListener();
		});
		wind.start();
		Thread data = new Thread(() -> {
			// �������ݿ�����
			DataService dataControler = new DataService();
			// ��ʼ������
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
			// �������
			new FillService().fillDataInBox();
			// ��ʾ������
			EventQueue.invokeLater(() -> UiFactory.newInstence().getMainFrame().setVisible(true));
		});
		frame.start();

		AutoControler controler = AutoControler.newInstance();
		// �����Զ������߳�
		new Thread(() -> controler.accecp()).start();
		new Thread(() -> controler.clear()).start();
		new Thread(() -> controler.handle()).start();
	}

	public static void main(String[] args) {
		new MainControler().systemStart();
	}
}
