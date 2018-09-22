package cn.upc.ui;

import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;

import cn.upc.res.ThreadSource;
import cn.upc.service.AutoControler;
import cn.upc.service.DataService;
import cn.upc.service.FillService;

/**
 * �����¼������������������¼��������ڴ˴�ʵ��
 * 
 * @author ����չ
 *
 */
public class ListenerFactory {
	private static ListenerFactory instence;
	private HashMap<String, ActionListener> listeners;

	private DataService dataService;
	private FillService fillService;

	private ListenerFactory() {
		dataService = new DataService();
		fillService = new FillService();
		listeners = new HashMap<>();

		listeners.put("����չʾ", showTopoPanel);
		listeners.put("·������", showPathPanel);
		listeners.put("�ܺļ��", showSingleEneryChartPanel);
		listeners.put("�ܺĶԱ�", showEneryChartPanel);
		listeners.put("�������", showPacketChartPanel);
		listeners.put("�Զ�����", showAutoControlPanel);

		listeners.put("��������", topoRefresh);
		listeners.put("�ڵ��ɢ", nodeHomogenize);
		listeners.put("F-Mod:��", flowingSwitch);
		listeners.put("�������", clearFlows);
		listeners.put("������ͼ", flowView);

		listeners.put("·��Ԥ��", perviewPath);
		listeners.put("·���ɴ�", showReachablePath);
		listeners.put("���ʼ���", rateCal);
		listeners.put("��������", increaseCal);
		listeners.put("·������", sortTempPath);
		listeners.put("����ʵʩ", pushPath);

		listeners.put("����-��", autoSwitch);
	}

	public static ListenerFactory newInstence() {
		if (instence == null) {
			instence = new ListenerFactory();
		}
		return instence;
	}

	public ActionListener getListener(String name) {
		return listeners.getOrDefault(name, null);
	}

	private ActionListener showTopoPanel = event -> {
		fillService.showPanel(0);
		ThreadSource.threadEnd();
	};

	private ActionListener topoRefresh = event -> {
		dataService.initData();
		fillService.fillDataInBox();
	};

	private ActionListener nodeHomogenize = event -> fillService.initElementLocation();

	private ActionListener flowingSwitch = event -> {
		JButton button = (JButton) event.getSource();
		boolean state;
		if (button.getText().endsWith("��")) {
			state = dataService.setFlowingModState(false);
		} else {
			state = dataService.setFlowingModState(true);
		}
		if (state) {
			EventQueue.invokeLater(() -> button.setText("F-Mod:��"));
		} else {
			EventQueue.invokeLater(() -> button.setText("F-Mod:��"));
		}
	};

	private ActionListener showAutoControlPanel = event -> {
		fillService.showPanel(5);
		ThreadSource.threadEnd();
	};

	private ActionListener showPathPanel = event -> {
		fillService.showPanel(1);
		ThreadSource.threadEnd();
	};

	private ActionListener showSingleEneryChartPanel = event -> {
		ThreadSource.singleBarCharThreadStart();
		fillService.showPanel(2);
		ThreadSource.threadEnd();
	};

	private ActionListener showEneryChartPanel = event -> {
		ThreadSource.barChartThreadStart();
		fillService.showPanel(3);
		ThreadSource.threadEnd();
	};

	private ActionListener showPacketChartPanel = event -> {
		fillService.showPanel(4);
		ThreadSource.lineChartThreadStart();
	};

	private ActionListener showReachablePath = event -> fillService.listReachablePath();

	private ActionListener sortTempPath = event -> fillService.setPathList(dataService.sortTempPaths());

	private ActionListener perviewPath = event -> fillService.previewSelectedPath();

	private ActionListener rateCal = event -> {
		fillService.setPathList(dataService.rateCalculate());
	};

	private ActionListener increaseCal = event -> fillService.setPathList(dataService.increaseCal());

	private ActionListener pushPath = event -> fillService.pushSelectedPath();

	private ActionListener clearFlows = event -> dataService.clearFlows();

	private ActionListener flowView = event -> fillService.switchFlowView();

	private ActionListener autoSwitch = event -> {
		JButton button = (JButton) event.getSource();
		if (button.getText().equals("����-��")) {
			button.setText("����-��");
			AutoControler controler = AutoControler.newInstance();
			controler.setWorkable(true);
		} else {
			button.setText("����-��");
			AutoControler controler = AutoControler.newInstance();
			controler.setWorkable(false);
		}

	};
}
