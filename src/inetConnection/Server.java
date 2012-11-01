package inetConnection;

import java.io.*;
import java.net.*;

public class Server {
	// �������� ���� ��� �������� 1-1024:
	public static final int PORT = 8080;
	static BufferedReader in;
	static PrintWriter out;

	public static Socket createServer() throws IOException {
		ServerSocket s = new ServerSocket(PORT);
		System.out.println("Started: " + s);
		Socket socket = null;
		try {
			// ��������� �� ��� ���, ���� �� ��������� ����������:
			socket = s.accept();
		} catch (ConnectException e) {
			s.close();
			socket.close();
			// !!!!!!!!!!!!!!
		} finally {
			s.close();
		}
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream())), true);
		return socket;
	}

	public static void sendIntClient(Socket socket, int data)
			throws IOException {
		// ����� ������������� Output ������������� PrintWriter'��.
		out.println(data);
	}

	public static int getIntClient(Socket socket) throws IOException {
		int data = 0;
		try {
			String str = in.readLine();
			data = Integer.parseInt(str);
		} catch (ConnectException e) {
			socket.close();
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		}
		return data;
	}

	public static char getRequestClient(Socket socket) throws IOException {
		char data = 0;
		try {
			String str = in.readLine();
			data = str.charAt(0);
		} catch (ConnectException e) {
			socket.close();
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		}
		return data;
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