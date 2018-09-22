package cn.upc.res;

import cn.upc.service.FillService;

/**
 * �߳�Դ
 * 
 * @author ����չ
 *
 */
public class ThreadSource {
	private static Thread lineChartThread;
	/**
	 * ���β�ѯ֮��ĺ�����
	 */
	private static final int SLEEP_TIME = 200;

	/**
	 * ������ת�����ݲ�ѯ�̣߳����߳̽�ÿ��{@link ThreadSource#SLEEP_TIME}
	 * ��ʱ��ִ��һ�Σ�ͨ��URL����Floodlight REST�ӿڣ���õ�ǰÿ̨����������ת����������������������䵽����ͼ�����ݼ���
	 * 
	 */
	public static void lineChartThreadStart() {
		if (lineChartThread != null) {
			return;
		}
		lineChartThread = new Thread(() -> {
			FillService service = new FillService();
			R.LOGGER.info("��ת��������ѯ�߳̿���");
			while (!Thread.interrupted()) {
				service.fillLineChart();
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					break;
				}
			}
			R.LOGGER.info("��ת��������ѯ�̹߳ر�");
		});
		lineChartThread.start();
	}

	/**
	 * �رհ�ת�����ݲ�ѯ�̣߳��ڵ����ť�����ر�����ͼ����ʱ����
	 */
	public static void threadEnd() {
		if (lineChartThread != null) {
			lineChartThread.interrupt();
			lineChartThread = null;
		}
	}

	private static Thread barChartThread;

	public static void barChartThreadStart() {
		if (barChartThread == null) {
			barChartThread = new Thread(() -> {
				FillService service = new FillService();
				service.fillBarChart();
				barChartThread = null;
			});
			barChartThread.start();
		}
	}

	private static Thread singleBarChartThread;

	public static void singleBarCharThreadStart() {
		if (singleBarChartThread != null) {
			return;
		}
		singleBarChartThread = new Thread(() -> {
			FillService service = new FillService();
			service.fileSingleBarChart();
			singleBarChartThread = null;
		});
		singleBarChartThread.start();
	}
}
