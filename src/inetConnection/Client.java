package inetConnection;

import java.net.*;
import java.io.*;
import java.util.TimerTask;

import message.Message;

public class Client {
	static PrintWriter out;
	static BufferedReader in;
	// Таймеры
	static int timerUpdate;
	static boolean timeOut;

	public static Socket createClient() throws IOException {
		// Таймер обновления
		final java.util.Timer timerSearchUpdate = new java.util.Timer();
		TimerTask update = new TimerTask() {
			public void run() {
				timerUpdate--;
				if (timerUpdate >= 0) {
					Message.updateSearchMessage(timerUpdate);
				}
			}
		};

		// Таймер поиска
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
		InetAddress addr = InetAddress.getByName(menu.Menu.IP);
		Socket socket = null;
		System.out.println("addr = " + addr);
		boolean serverFound = false;
		// Таймаут для поиска
		timeOut = false;
		timerSearch.schedule(closeSearch, 20 * 1000); // 20 sec
		// Таймаут для обновления
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

	public static String getRequestServer(Socket socket) throws IOException {
		if (socket != null && !socket.isClosed()) {
			return in.readLine();
		} else {
			return null;
		}
	}

	public static void sendRequestServer(Socket socket, String data) {
		if (socket != null && !socket.isClosed()) {
			out.println(data);
		}
	}
}