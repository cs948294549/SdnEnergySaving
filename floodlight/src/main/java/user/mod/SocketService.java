package user.mod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * 
 * @author ubantu
 *
 */
public class SocketService implements IFloodlightService, IFloodlightModule {

	private static final int PORT = 6666;
	private ServerSocket connect;
	private Runnable runnable;
	private HashMap<String, Function<PrintWriter, Boolean>> map;

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		return null;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		try {
			connect = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try (Socket socket = connect.accept()) {
						handler(socket);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		map = new HashMap<>();
		map.put(Mark.CLIENT.toString() + Mark.READY, pw -> {
			pw.println(Mark.SERVER.toString() + Mark.READY);
			return Boolean.valueOf(false);
		});
		map.put(Mark.CLIENT.toString() + Mark.END, pw -> {
			pw.println(Mark.SERVER.toString() + Mark.END);
			return Boolean.valueOf(true);
		});
		map.put(Mark.FORWARD.toString() + Mark.SELECT, pw -> {
			if (SwitchForwarding.canForwarding) {
				pw.println(Mark.FORWARD.toString() + Mark.SELECT + Mark.YES);
			} else {
				pw.println(Mark.FORWARD.toString() + Mark.SELECT + Mark.NO);
			}
			return Boolean.valueOf(false);
		});
		map.put(Mark.FORWARD.toString() + Mark.SET + Mark.YES, pw -> {
			SwitchForwarding.canForwarding = true;
			pw.println(Mark.FORWARD.toString() + Mark.SELECT + Mark.YES);
			return Boolean.valueOf(false);
		});
		map.put(Mark.FORWARD.toString() + Mark.SET + Mark.NO, pw -> {
			SwitchForwarding.canForwarding = false;
			pw.println(Mark.FORWARD.toString() + Mark.SELECT + Mark.NO);
			return Boolean.valueOf(false);
		});
	}

	private void handler(Socket socket) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
						true)) {
			boolean talk = true;
			while (talk) {
				String cmd = br.readLine();
				cmd = cmd.trim();
				System.out.println(cmd);
				if (map.containsKey(cmd) && map.get(cmd).apply(pw)) {
					talk = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		new Thread(runnable).start();
	}
}
