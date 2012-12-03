package inetConnection;

import java.net.*;
import java.io.*;

public class GetIp {

	public static String getIP() {
		try {
			InetAddress ip = InetAddress.getLocalHost().getByName(
					InetAddress.getLocalHost().getHostName());
			return ip.toString();
		} catch (UnknownHostException e) {
		}
		;
		return null;
	}

}