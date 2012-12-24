package testChat;

import java.io.*;
import java.net.*;
import java.util.*;

class ServerChat implements Runnable {
	// ¬ыбираем порт вне пределов 1-1024:
	public static final int PORT = 8080;
	static BufferedReader in;
	static PrintWriter out;
	static Scanner scIn;

	ServerChat() {
		Thread T = new Thread(this);
		T.start();
	}

	public static Socket createServer() throws IOException {
		final ServerSocket s = new ServerSocket(PORT);
		System.out.println("Started: " + s);
		Socket socket = null;
		try {
			// Ѕлокирует до тех пор, пока не возникнет соединение:
			socket = s.accept();
		} catch (ConnectException e) {
			s.close();
			socket.close();
			// !!!!!!!!!!!!!!
		} finally {
		}
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream())), true);
		scIn = new Scanner(in);
		return socket;
	}

	public static void sendMessageClient(Socket socket, String data)
			throws IOException {
		if (!socket.isClosed()) {
			// ¬ывод автоматически Output выталкиваетс€ PrintWriter'ом.
			out.println(data);
		}
	}

	public static String getMessageClient(Socket socket) throws IOException {
		String data = null;
		if (!socket.isClosed()) {
			try {
				data = in.readLine();
			} catch (Exception e) {
				socket.close();
			}
			return data;
		} else {
			return "NULL";
		}
	}

	static Socket socket;
	static String getData;

	public static void main(String[] args) throws IOException {
		try {
			socket = createServer();
			ServerChat chat = new ServerChat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			if (scIn.hasNextLine()) {
				try {
					getData = getMessageClient(socket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(getData);
			}
		}
	}

	@Override
	public void run() {
		getData = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		String message = null;
		try {
			sendMessageClient(socket, message);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Scanner sc = new Scanner(input);
		while (true) {
			if (sc.hasNextLine()) {
				message = sc.nextLine();
				try {
					sendMessageClient(socket, message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
