package inetConnection;

import java.net.*;
import java.io.*;

import screens.GameConstants.*;
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

	public static void sendRequestServer(Socket socket, char data)
			throws IOException {
		if (!socket.isClosed()) {
			out.println(data);
		}
	}

	public static void sendIntServer(Socket socket, int data)
			throws IOException {
		if (!socket.isClosed()) {
			// ¬ывод автоматически Output выталкиваетс€ PrintWriter'ом.
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

	// методы передачи и получени€ необходимых данных с сервера
	public static Coord getBall(Socket socket) throws IOException {
		sendRequestServer(socket, 'b'); // символ 'b' означает запрос
		// координат м€ча
		int x = getIntServer(socket);
		System.out.println(x);
		int y = getIntServer(socket);
		System.out.println(y);
		Coord ball = new Coord(x, y);
		return ball;
	}

	public static int getServerY(Socket socket) throws IOException {
		sendRequestServer(socket, 'p'); // символ 'p' означает запрос
		// y координаты соперника
		int y = getIntServer(socket);
		return y;
	}

	public static void sendClientY(Socket socket, int y) throws IOException {
		sendRequestServer(socket, 'i'); // символ 'i' означает запрос на
										// передачу
		// y координаты игрока
		sendIntServer(socket, y);
	}

	public static Score getScore(Socket socket) throws IOException {
		sendRequestServer(socket, 's'); // символ 's' означает запрос
		// текущего счета
		int scoreClient = getIntServer(socket);
		int scoreServer = getIntServer(socket);
		Score score = new Score(scoreClient, scoreServer);
		return score;
	}

	public static void sendServe(Socket socket) throws IOException {
		sendRequestServer(socket, 'g');
	}

} // /:~
