package inetConnection;

import java.net.*;
import java.io.*;

import screens.GameConstants.*;

public class Client {
	static PrintWriter out;
	static BufferedReader in;

	public static Socket createClient() throws IOException {
		InetAddress addr = InetAddress.getByName(null);
		Socket socket = null;
		System.out.println("addr = " + addr);
		boolean serverFound = false;
		while (!serverFound) {
			try {
				socket = new Socket(addr, Server.PORT);
				serverFound = true;
			} catch (ConnectException e) {
			} finally {
			}
		}
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream())), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return socket;
	}

	public static void sendRequestServer(Socket socket, char data)
			throws IOException {
		out.println(data);
	}

	public static void sendIntServer(Socket socket, int data)
			throws IOException {
		// ����� ������������� Output ������������� PrintWriter'��.
		out.println(data);
	}

	public static int getIntServer(Socket socket) throws IOException {
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

	// ������ �������� � ��������� ����������� ������ � �������
	public static Coord getBall(Socket socket) throws IOException {
		sendRequestServer(socket, 'b'); // ������ 'b' �������� ������
		// ��������� ����
		int x = getIntServer(socket);
		System.out.println(x);
		int y = getIntServer(socket);
		System.out.println(y);
		Coord ball = new Coord(x, y);
		return ball;
	}

	public static int getServerY(Socket socket) throws IOException {
		sendRequestServer(socket, 'p'); // ������ 'p' �������� ������
		// y ���������� ���������
		int y = getIntServer(socket);
		return y;
	}

	public static void sendClientY(Socket socket, int y) throws IOException {
		sendRequestServer(socket, 'i'); // ������ 'i' �������� ������ ��
										// ��������
		// y ���������� ������
		sendIntServer(socket, y);
	}

	public static Score getScore(Socket socket) throws IOException {
		sendRequestServer(socket, 's'); // ������ 's' �������� ������
		// �������� �����
		int scoreClient = getIntServer(socket);
		int scoreServer = getIntServer(socket);
		Score score = new Score(scoreClient, scoreServer);
		return score;
	}

} // /:~
