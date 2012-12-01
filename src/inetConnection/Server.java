package inetConnection;

import java.io.*;
import java.net.*;
import java.util.TimerTask;

import message.Message;

public class Server {
	// �������� ���� ��� �������� 1-1024:
	public static final int PORT = 8080;
	static BufferedReader in;
	static PrintWriter out;
	static boolean timeOut;

	public static Socket createServer() throws IOException {
		final ServerSocket s = new ServerSocket(PORT);

		// Timer for close waitnig client
		final java.util.Timer timerClose = new java.util.Timer();
		TimerTask closeWaiting = new TimerTask() {
			public void run() {
				System.out.println("KO");
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
		Message.waitMessage();
		try {
			// ��������� �� ��� ���, ���� �� ��������� ����������:
			socket = s.accept();
			timerClose.cancel();
		} catch (Exception e) {
			s.close();
			socket.close();
			// !!!!!!!!!!!!!!
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

	public static void sendIntClient(Socket socket, int data)
			throws IOException {
		if (!socket.isClosed()) {
			// ����� ������������� Output ������������� PrintWriter'��.
			out.println(data);
		}
	}

	public static int getIntClient(Socket socket) throws IOException {
		int data = 0;
		if (!socket.isClosed()) {
			try {
				String str = in.readLine();
				data = Integer.parseInt(str);
			} catch (Exception e) {
				socket.close();
				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			}
			return data;
		} else {
			return 0;
		}
	}

	public static char getRequestClient(Socket socket) throws IOException {
		char data = 0;
		if (!socket.isClosed()) {
			try {
				String str = in.readLine();
				data = str.charAt(0);
			} catch (Exception e) {
				socket.close();
				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			}
			return data;
		} else {
			return 0;
		}
	}

	// ������ �������� � ��������� ����������� ������ � �������
	public static void sendBall(Socket socket, int x, int y) throws IOException {
		sendIntClient(socket, x);
		sendIntClient(socket, y);
	}

	public static int getClientY(Socket socket) throws IOException {
		return getIntClient(socket);
	}

	public static void sendServerY(Socket socket, int y) throws IOException {
		sendIntClient(socket, y);
	}

	public static void sendScore(Socket socket, int scoreClient, int scoreServer)
			throws IOException {
		sendIntClient(socket, scoreClient);
		sendIntClient(socket, scoreServer);
	}

} // /:~