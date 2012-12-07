package inetConnection;

import java.io.*;
import java.net.*;
import java.util.TimerTask;

import message.Message;
import inetConnection.GetIp;

public class Server {
	// ¬ыбираем порт вне пределов 1-1024:
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
			// Ѕлокирует до тех пор, пока не возникнет соединение:
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
			// ¬ывод автоматически Output выталкиваетс€ PrintWriter'ом.
			out.println(data);
		}
	}

	public static void sendRequestClient(Socket socket, char data)
			throws IOException {
		if (socket != null && !socket.isClosed()) {
			// ¬ывод автоматически Output выталкиваетс€ PrintWriter'ом.
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
		if (socket != null) {
			String str = in.readLine();
			char data = 0;
			if (str != null) {
				data = str.charAt(0);
				return data;
			} else {
				return data;
			}
		} else {
			return 0;
		}
	}

	// методы передачи и получени€ необходимых данных с клиента
	public static void sendBall(Socket socket, int x, int y) throws IOException {
		sendIntClient(socket, x);
		sendIntClient(socket, y);
	}

	public static int getClientY(Socket socket) throws IOException {
		return getIntClient(socket);
	}

	public static void serverUp(Socket socket) {
		try {
			sendRequestClient(socket, 'u');
		} catch (IOException e) {
		}
	}

	public static void serverDown(Socket socket) {
		try {
			sendRequestClient(socket, 'd');
		} catch (IOException e) {
		}
	}

	public static void startNewGame(Socket socket) {
		try {
			Server.sendRequestClient(socket, 'n');
		} catch (IOException e) {
		}
	}

	public static void serverExit(Socket socket) {
		try {
			Server.sendRequestClient(socket, 'e');
		} catch (IOException e) {
		}
	}

	public static void sendServe(Socket socket) {
		try {
			sendRequestClient(socket, 'g');
		} catch (IOException e) {
		}
	}

	public static void sendDir(Socket socket, char dir) {
		try {
			sendRequestClient(socket, dir);
		} catch (IOException e) {
		}
	}

} // /:~