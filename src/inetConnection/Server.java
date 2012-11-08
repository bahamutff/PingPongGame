package inetConnection;

import java.io.*;
import java.net.*;

import screens.Message;

public class Server {
	// ¬ыбираем порт вне пределов 1-1024:
	public static final int PORT = 8080;
	static BufferedReader in;
	static PrintWriter out;

	public static Socket createServer() throws IOException {
		ServerSocket s = new ServerSocket(PORT);
		System.out.println("Started: " + s);
		Socket socket = null;
		try {
			Message.waitMessage();
			// Ѕлокирует до тех пор, пока не возникнет соединение:
			socket = s.accept();
			Message.closeWaitMessage();
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
		// ¬ывод автоматически Output выталкиваетс€ PrintWriter'ом.
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

	// методы передачи и получени€ необходимых данных с клиента
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