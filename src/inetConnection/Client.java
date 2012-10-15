package inetConnection;

import java.net.*;
import java.io.*;

public class Client {
	public static void main(String[] args) throws IOException {
		// ѕередаем null в getByName(), получа€
		// специальный IP адрес "локальной заглушки"
		// дл€ тестировани€ на машине без сети:
		InetAddress addr = InetAddress.getByName(null);
		// јльтернативно, вы можете использовать
		// адрес или им€:
		// InetAddress addr =
		// InetAddress.getByName("127.0.0.1");
		// InetAddress addr =
		// InetAddress.getByName("localhost");
		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, Server.PORT);
		// ѕомещаем все в блок try-finally, чтобы
		// быть уверенным, что сокет закроетс€:
		try {
			System.out.println("socket = " + socket);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// ¬ывод автоматически Output быталкиваетс€ PrintWriter'ом.
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			for (int i = 0; i < 10; i++) {
				out.println("howdy " + i);
				String str = in.readLine();
				System.out.println(str);
			}
			out.println("END");
		} finally {
			System.out.println("closing...");
			socket.close();
		}
	}
} // /:~
