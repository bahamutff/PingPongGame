package testChat;

import java.net.*;
import java.util.Scanner;
import java.io.*;

class ClientChat implements Runnable {
	static PrintWriter out;
	static BufferedReader in;
	static Scanner scIn;

	ClientChat() {
		Thread T2 = new Thread(this);
		T2.start();
	}

	public static Socket createClient() throws IOException {
		InetAddress addr = InetAddress.getByName(null);
		Socket socket = null;
		System.out.println("addr = " + addr);
		boolean serverFound = false;
		while (!serverFound) {
			try {
				socket = new Socket(addr, 8080);
				serverFound = true;
			} catch (ConnectException e) {
			} finally {
			}
		}
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream())), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		scIn = new Scanner(in);
		return socket;
	}

	public static void sendMessageServer(Socket socket, String data)
			throws IOException {
		if (!socket.isClosed()) {
			out.println(data);
		}
	}

	public static String getMessageServer(Socket socket) throws IOException {
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
		// TODO Auto-generated method stub
		socket = createClient();
		ClientChat chat = new ClientChat();
		while (true) {
			if (scIn.hasNextLine()) {
				try {
					getData = getMessageServer(socket);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
			sendMessageServer(socket, message);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Scanner sc = new Scanner(input);
		while (true) {
			if (sc.hasNextLine()) {
				message = sc.nextLine();
				try {
					sendMessageServer(socket, message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
