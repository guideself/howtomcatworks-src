package ex01.pyrmont;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Test {

	public static void main(String[] args) {
		requestClient();
	}

	public static void requestClient(){
		Socket socket;
		try {
			socket = new Socket("192.168.146.1", 8080);
			OutputStream os = socket.getOutputStream();
			boolean autoflush = true;
			PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(socket.getInputstream()));// send
			out.println("GET /index.jsp HTTP/1.1");
			out.println("Host: localhost:8080");
			out.println("Connection: Close");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
