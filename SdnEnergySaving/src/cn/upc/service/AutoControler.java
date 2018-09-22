package cn.upc.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import cn.upc.entity.TempPath;
import cn.upc.res.R;
import cn.upc.res.Util;

public class AutoControler {
	private static AutoControler controler = new AutoControler();
	/**
	 * 监听端口
	 */
	private static final int LISTENER_PORT = 6665;
	/**
	 * 路径的生存时间 30分钟
	 */
	private static final long LIVE_TIME = 30 * 60 * 1000;

	private static FillService SERVICE;

	private AutoControler() {
		try {
			socket = new DatagramSocket(LISTENER_PORT);
		} catch (SocketException e) {
			R.LOGGER.error("构造通信Socket失败，请确认端口" + LISTENER_PORT + "没有被占用，特别是上次的程序未完全关闭时可能发生");
			System.exit(1);
		}
		SERVICE = new FillService();
	}

	private byte[] buf = new byte[1000];
	private DatagramPacket packet = new DatagramPacket(buf, buf.length);
	private DatagramSocket socket;
	private volatile boolean workable = false;
	private BlockingQueue<String> queue = new ArrayBlockingQueue<>(30);

	public static AutoControler newInstance() {
		return controler;
	}

	/**
	 * 接收UDP数据包
	 */
	public void accecp() {
		while (true) {
			try {
				socket.receive(packet);
				String data = new String(packet.getData(), 0, packet.getLength());
				if (!workable) {
					SERVICE.addInfo("丢弃信息：" + data);
					continue;
				}
				SERVICE.addInfo("收到并加入队列：" + data);
				queue.add(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	DataService service = new DataService();
	private ArrayList<TempPath> recode = new ArrayList<>();

	/**
	 * 处理队列中的数据
	 */
	public void handle() {
		while (true) {
			try {
				String data = queue.take();
				// 如果从阻塞队列中取到的是新来的流表数据
				if (!data.equals("clear")) {
					SERVICE.addInfo("开始执行链路创建：" + data);
					String[] path = data.split("--");
					if (contentAndFreshTime(Util.rename(path[0]), Util.rename(path[1]))) {
						continue;
					}
					service.getAllPaths(Util.rename(path[0]), Util.rename(path[1]));
					service.rateCalculate();
					service.increaseCal();
					ArrayList<TempPath> paths = service.sortTempPaths();
					if (paths.size() > 0) {
						TempPath tempPath = paths.get(0);
						tempPath.pushTime = System.currentTimeMillis();
						service.pushPath(tempPath.paths);
						recode.add(tempPath);
					}
				} else if (data.equals("clear")) {
					SERVICE.addInfo("开始执行清除过时链路");
					service.clearFlows();
					ArrayList<TempPath> newPaths = new ArrayList<>();
					synchronized (recode) {
						for (int i = 0; i < recode.size(); i++) {
							TempPath tempPath = recode.get(i);
							if (System.currentTimeMillis() - tempPath.pushTime > LIVE_TIME) {
								continue;
							}
							service.pushPath(tempPath.paths);
							newPaths.add(tempPath);
						}
						recode = newPaths;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断是否已经下发过流表，如果下发过就将当前时间设定为下发时间
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	private boolean contentAndFreshTime(String src, String dst) {
		for (int i = 0; i < recode.size(); i++) {
			synchronized (recode) {
				String[] paths = recode.get(i).paths;
				if (paths[0].equals(src) && paths[1].equals(dst)) {
					recode.get(i).pushTime = System.currentTimeMillis();
					return true;
				}
				if (paths[1].equals(src) && paths[0].equals(dst)) {
					recode.get(i).pushTime = System.currentTimeMillis();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 定期检查处理流表过时信息
	 */
	public void clear() {
		while (true) {
			try {
				// 遍历存在的流表，如果超时就发出清理信息
				synchronized (recode) {
					for (int i = 0; i < recode.size(); i++) {
						TempPath tempPath = recode.get(i);
						if (System.currentTimeMillis() - tempPath.pushTime > LIVE_TIME) {
							SERVICE.addInfo("发现超时链路，已安排清除");
							try {
								queue.put("clear");
								break;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				// 线程休息
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setWorkable(boolean b) {
		if (b) {
			SERVICE.addInfo("开始监听并处理" + LISTENER_PORT + "端口信息");
		} else {
			SERVICE.addInfo("忽略" + LISTENER_PORT + "端口信息");
		}
		this.workable = b;
	}
}