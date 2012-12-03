package inetConnection;

import java.net.*;
import java.io.*;

import screens.GameConstants.*;

import java.util.Scanner;
import java.util.TimerTask;

import message.Message;
import menu.Menu;

public class Client {
	static PrintWriter out;
	static BufferedReader in;
	// Work timer
	static int timerUpdate;
	static boolean timeOut;

	public static Socket createClient() throws IOException {
		// Timer update
		final java.util.Timer timerSearchUpdate = new java.util.Timer();
		TimerTask update = new TimerTask() {
			public void run() {
				timerUpdate--;
				if (timerUpdate >= 0) {
					Message.updateSearchMessage(timerUpdate);
				}
			}
		};

		// Time search
		final java.util.Timer timerSearch = new java.util.Timer();
		TimerTask closeSearch = new TimerTask() {
			public void run() {
				Message.closeSearchMessage();
				timerSearchUpdate.cancel();
				timerSearch.cancel();
				timeOut = true;
			}
		};

		Message.searchMessage();
		InetAddress addr = InetAddress.getByName(Menu.IP);
		Socket socket = null;
		System.out.println("addr = " + addr);
		boolean serverFound = false;
		// Timer search
		timeOut = false;
		timerSearch.schedule(closeSearch, 20 * 1000); // 20 sec
		// Timer update
		timerUpdate = 20;
		timerSearchUpdate.schedule(update, 0, 1 * 1000); // 1 sec
		while (!serverFound) {
			if (timeOut) {
				socket = null;
				return socket;
			}
			try {
				socket = new Socket(addr, Server.PORT);
				serverFound = true;
			} catch (Exception e) {
			} finally {
			}
		}
		Message.closeSearchMessage();
		timerSearchUpdate.cancel();
		timerSearch.cancel();
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream())), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return socket;
	}

	public static char getRequestServer(Socket socket) throws IOException {
		String str = in.readLine();
		char data = 0;
		if (str != null) {
			data = str.charAt(0);
			return data;
		} else {
			return data;
		}
	}

	public static void sendRequestServer(Socket socket, char data)
			throws IOException {
		if (!socket.isClosed()) {
			out.println(data);
		}
	}

	public static void sendIntServer(Socket socket, int data)
			throws IOException {
		if (!socket.isClosed()) {
			// ����� ������������� Output ������������� PrintWriter'��.
			out.println(data);
		}
	}

	public static int getIntServer(Socket socket) throws IOException {
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

	// ������ �������� � ��������� ����������� ������ � �������
	public static Coord getBall(Socket socket) throws IOException {
		sendRequestServer(socket, 'b'); // ������ 'b' �������� ������
		// ��������� ����
		int x = getIntServer(socket);
		int y = getIntServer(socket);
		Coord ball = new Coord(x, y);
		return ball;
	}

	public static int getServerY(Socket socket) throws IOException {
		sendRequestServer(socket, 'p'); // ������ 'p' �������� ������
		// y ���������� ���������
		int y = getIntServer(socket);
		return y;
	}

	public static void clientUp(Socket socket) {
		try {
			sendRequestServer(socket, 'u'); // ������ 'u' - ������ �� ��������
											// �����
		} catch (Exception e) {
		}
	}

	public static void clientDown(Socket socket) {
		try {
			sendRequestServer(socket, 'd'); // ������ 'd' - ������ �� ��������
											// ����
		} catch (IOException e) {
		}
	}

	public static void sendServe(Socket socket) throws IOException {
		sendRequestServer(socket, 'g');
	}

	public static void clientExit(Socket socket) {
		try {
			sendRequestServer(socket, 'e');
		} catch (IOException e) {
		}
	}

} // /:~
