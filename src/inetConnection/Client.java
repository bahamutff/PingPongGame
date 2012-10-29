package inetConnection;

import java.net.*;
import java.io.*;

public class Client {
	public static void main(String[] args) throws IOException {
		// �������� null � getByName(), �������
		// ����������� IP ����� "��������� ��������"
		// ��� ������������ �� ������ ��� ����:
		InetAddress addr = InetAddress.getByName(null);
		// �������������, �� ������ ������������
		// ����� ��� ���:
		// InetAddress addr =
		// InetAddress.getByName("127.0.0.1");
		// InetAddress addr =
		// InetAddress.getByName("localhost");
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
		// �������� ��� � ���� try-finally, �����
		// ���� ���������, ��� ����� ���������:
		try {
			System.out.println("socket = " + socket);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// ����� ������������� Output ������������� PrintWriter'��.
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			for (int i = 0; i < 10; i++) {
				out.println(i);
				String str = in.readLine();
				int data = Integer.parseInt(str);
				System.out.println(data);
			}
			out.println(-1);
		} finally {
			System.out.println("closing...");
			socket.close();
		}
	}
} // /:~
