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
 * 监听事件工厂，界面上所有事件监听将在此处实现
 * 
 * @author 江荣展
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

		listeners.put("拓扑展示", showTopoPanel);
		listeners.put("路径计算", showPathPanel);
		listeners.put("能耗监控", showSingleEneryChartPanel);
		listeners.put("能耗对比", showEneryChartPanel);
		listeners.put("流量监控", showPacketChartPanel);
		listeners.put("自动控制", showAutoControlPanel);

		listeners.put("更新数据", topoRefresh);
		listeners.put("节点分散", nodeHomogenize);
		listeners.put("F-Mod:开", flowingSwitch);
		listeners.put("清空流表", clearFlows);
		listeners.put("流表视图", flowView);

		listeners.put("路径预览", perviewPath);
		listeners.put("路径可达", showReachablePath);
		listeners.put("速率计算", rateCal);
		listeners.put("新增计算", increaseCal);
		listeners.put("路径排序", sortTempPath);
		listeners.put("部署实施", pushPath);

		listeners.put("控制-关", autoSwitch);
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
		if (button.getText().endsWith("开")) {
			state = dataService.setFlowingModState(false);
		} else {
			state = dataService.setFlowingModState(true);
		}
		if (state) {
			EventQueue.invokeLater(() -> button.setText("F-Mod:开"));
		} else {
			EventQueue.invokeLater(() -> button.setText("F-Mod:关"));
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
		if (button.getText().equals("控制-关")) {
			button.setText("控制-开");
			AutoControler controler = AutoControler.newInstance();
			controler.setWorkable(true);
		} else {
			button.setText("控制-关");
			AutoControler controler = AutoControler.newInstance();
			controler.setWorkable(false);
		}

	};
}
