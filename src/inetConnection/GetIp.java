package inetConnection;

import java.net.*;
import java.io.*;

public class GetIp {

	public static String getIP() {
		/*
		 * try { // InetAddress ip = InetAddress.getLocalHost().getByName( /*
		 * InetAddress.getLocalHost().getHostName()); // return ip.toString();
		 * return InetAddress.getLocalHost().getHostAddress(); } catch
		 * (UnknownHostException e) { } ; return null; }
		 */
		String result = null;
		try {
			BufferedReader reader = null;
			try {
				result = "int IP:"
						+ InetAddress.getLocalHost().getHostAddress();
				URL url = new URL("http://myip.by/");
				InputStream inputStream = null;
				inputStream = url.openStream();
				reader = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder allText = new StringBuilder();
				char[] buff = new char[1024];

				int count = 0;
				while ((count = reader.read(buff)) != -1) {
					allText.append(buff, 0, count);
				}
				// Строка содержащая IP имеет следующий вид
				// <a href="whois.php?127.0.0.1">whois 127.0.0.1</a>
				Integer indStart = allText.indexOf("\">whois ");
				Integer indEnd = allText.indexOf("</a>", indStart);

				String ipAddress = new String(allText.substring(indStart + 8,
						indEnd));
				if (ipAddress.split("\\.").length == 4) { // минимальная
															// (неполная)
					// проверка что выбранный текст является ip адресом.
					result += "  ext IP:" + ipAddress;
				}
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}