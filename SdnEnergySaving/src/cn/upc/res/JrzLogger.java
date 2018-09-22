package cn.upc.res;

public class JrzLogger {
	/**
	 * ��ʱ����ֻ���Debug��Ϣ�����ڵ���
	 */
	public static final String DEBUG = "Debug";
	/**
	 * ��Debug�⣬ȫ�����
	 */
	public static final String INFO = "Info";
	/**
	 * ֻ���Warning/Error��Ϣ
	 */
	public static final String WARNING = "Warning";
	/**
	 * ֻ���Error��Ϣ
	 */
	public static final String ERROR = "Error";
	private String level;
	private Handler handler;

	public JrzLogger(Handler handler) {
		this.handler = handler;
		this.level = INFO;
	}

	public JrzLogger() {
		this((level, msg) -> {
			System.out.println(level + ":" + msg);
		});
	}

	public void setFilterLevel(String level) {
		this.level = level;
	}

	public void debug(String msg) {
		if (level.equals(DEBUG)) {
			handler.handle(DEBUG, msg);
		}
	}

	public void info(String msg) {
		if (level.equals(INFO)) {
			handler.handle(INFO, msg);
		}
	}

	public void warning(String msg) {
		if (level.equals(INFO) || level.equals(DEBUG)) {
			handler.handle(DEBUG, msg);
		}
	}

	public void error(String msg) {
		if (level.equals(DEBUG)) {
			return;
		}
		handler.handle(ERROR, msg);
	}

	public static interface Handler {
		public void handle(String level, String msg);
	}
}
