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
	 * �����˿�
	 */
	private static final int LISTENER_PORT = 6665;
	/**
	 * ·��������ʱ�� 30����
	 */
	private static final long LIVE_TIME = 30 * 60 * 1000;

	private static FillService SERVICE;

	private AutoControler() {
		try {
			socket = new DatagramSocket(LISTENER_PORT);
		} catch (SocketException e) {
			R.LOGGER.error("����ͨ��Socketʧ�ܣ���ȷ�϶˿�" + LISTENER_PORT + "û�б�ռ�ã��ر����ϴεĳ���δ��ȫ�ر�ʱ���ܷ���");
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
	 * ����UDP���ݰ�
	 */
	public void accecp() {
		while (true) {
			try {
				socket.receive(packet);
				String data = new String(packet.getData(), 0, packet.getLength());
				if (!workable) {
					SERVICE.addInfo("������Ϣ��" + data);
					continue;
				}
				SERVICE.addInfo("�յ���������У�" + data);
				queue.add(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	DataService service = new DataService();
	private ArrayList<TempPath> recode = new ArrayList<>();

	/**
	 * ��������е�����
	 */
	public void handle() {
		while (true) {
			try {
				String data = queue.take();
				// ���������������ȡ��������������������
				if (!data.equals("clear")) {
					SERVICE.addInfo("��ʼִ����·������" + data);
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
					SERVICE.addInfo("��ʼִ�������ʱ��·");
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
	 * �ж��Ƿ��Ѿ��·�����������·����ͽ���ǰʱ���趨Ϊ�·�ʱ��
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
	 * ���ڼ�鴦�������ʱ��Ϣ
	 */
	public void clear() {
		while (true) {
			try {
				// �������ڵ����������ʱ�ͷ���������Ϣ
				synchronized (recode) {
					for (int i = 0; i < recode.size(); i++) {
						TempPath tempPath = recode.get(i);
						if (System.currentTimeMillis() - tempPath.pushTime > LIVE_TIME) {
							SERVICE.addInfo("���ֳ�ʱ��·���Ѱ������");
							try {
								queue.put("clear");
								break;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				// �߳���Ϣ
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
			SERVICE.addInfo("��ʼ����������" + LISTENER_PORT + "�˿���Ϣ");
		} else {
			SERVICE.addInfo("����" + LISTENER_PORT + "�˿���Ϣ");
		}
		this.workable = b;
	}
}