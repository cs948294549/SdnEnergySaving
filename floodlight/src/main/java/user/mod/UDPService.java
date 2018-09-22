package user.mod;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPService {
	private static final String IP = "172.24.255.254";
	private static final int PORT = 6665;
	private DatagramSocket s;
	private InetAddress hostAdderss;

	public UDPService() {
		try {
			s = new DatagramSocket();
			hostAdderss = InetAddress.getByName(IP);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String msg) {
		byte[] buf = msg.getBytes();
		try {
			s.send(new DatagramPacket(buf, buf.length, hostAdderss, PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
