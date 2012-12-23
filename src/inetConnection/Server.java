package inetConnection;

import java.io.*;
import java.net.*;
import java.util.TimerTask;

import message.Message;
import inetConnection.GetIp;

public class Server {
	// �������� ���� ��� �������� 1-1024:
	public static final int PORT = 8080;
	static BufferedReader in;
	static PrintWriter out;
	static boolean timeOut;

	public static Socket createServer() throws IOException {
		final ServerSocket s = new ServerSocket(PORT);

		// ������ ��� �������� �������� ��������
		final java.util.Timer timerClose = new java.util.Timer();
		TimerTask closeWaiting = new TimerTask() {
			public void run() {
				try {
					s.close();
					timerClose.cancel();
					timeOut = true;
				} catch (Exception e) {
				}
			}
		};

		timeOut = false;
		timerClose.schedule(closeWaiting, 10 * 1000); // 10 sec
		System.out.println("Started: " + s);
		Socket socket = null;
		Message.waitMessage(GetIp.getIP());
		try {
			// ��������� �� ��� ���, ���� �� ��������� ����������:
			socket = s.accept();
			timerClose.cancel();
		} catch (Exception e) {
			s.close();
			socket.close();
		} finally {
			Message.closeWaitMessage();
			System.out.println("Finally");
			if (!timeOut) {
				s.close();
			} else {
				return null;
			}
		}
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream())), true);
		return socket;
	}

	public static void sendRequestClient(Socket socket, String data) {
		if (socket != null && !socket.isClosed()) {
			// ����� ������������� Output ������������� PrintWriter'��.
			out.println(data);
		}
	}

	public static String getRequestClient(Socket socket) throws IOException {
		if (socket != null && !socket.isClosed()) {
			return in.readLine();
		} else {
			return null;
		}
	}
}