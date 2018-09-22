package cn.upc.res;

import cn.upc.service.FillService;

/**
 * 线程源
 * 
 * @author 江荣展
 *
 */
public class ThreadSource {
	private static Thread lineChartThread;
	/**
	 * 两次查询之间的毫秒间隔
	 */
	private static final int SLEEP_TIME = 200;

	/**
	 * 开启包转发数据查询线程，此线程将每隔{@link ThreadSource#SLEEP_TIME}
	 * 的时间执行一次，通过URL访问Floodlight REST接口，获得当前每台交换机正在转发的流包数量，并将其填充到折线图的数据集中
	 * 
	 */
	public static void lineChartThreadStart() {
		if (lineChartThread != null) {
			return;
		}
		lineChartThread = new Thread(() -> {
			FillService service = new FillService();
			R.LOGGER.info("包转发数量查询线程开启");
			while (!Thread.interrupted()) {
				service.fillLineChart();
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					break;
				}
			}
			R.LOGGER.info("包转发数量查询线程关闭");
		});
		lineChartThread.start();
	}

	/**
	 * 关闭包转发数据查询线程，在点击按钮主动关闭折线图窗口时调用
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
