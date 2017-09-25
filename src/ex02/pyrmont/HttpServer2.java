package ex02.pyrmont;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * ��������
 * @author Administrator
 *
 */
public class HttpServer2 {

  /**
   * shutdown command��
   * �������http://192.168.146.1:8080/SHUTDOWN���Թر�serversocket
   */
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  /**
   * the shutdown command received
   * serversocket�Ƿ�ر��˱�־λ
   */
  private boolean shutdown = false;

  public static void main(String[] args) {
    HttpServer2 server = new HttpServer2();
    server.await();
  }

  /**
   * ���������serverSocket������������ip=192.168.146.1��port=8080��
   * ������serverSocket֮�󣬷���������� socket = serverSocket.accept();
   * ֱ�����˷��ʸ������Ķ˿���Դ���߹رո�serverSocket
   * 
   */
  public void await() {
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("192.168.146.1"));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // Loop waiting for a request
    while (!shutdown) {
      Socket socket = null;
      InputStream input = null;
      OutputStream output = null;
      try {
        socket = serverSocket.accept();
        input = socket.getInputStream(); //��ȡ�ͻ���������
        output = socket.getOutputStream(); //��ȡ�ͻ�������������

        // create Request object and parse
        Request request = new Request(input); //���ݿͻ��� ����������requet
        request.parse(); //�����������Դ

        // create Response object
        Response response = new Response(output);
        response.setRequest(request);

        //check if this is a request for a servlet or a static resource
        //a request for a servlet begins with "/servlet/"
        if (request.getUri().startsWith("/servlet/")) {
          ServletProcessor2 processor = new ServletProcessor2();
          processor.process(request, response);
        }
        else {
          StaticResourceProcessor processor = new StaticResourceProcessor();
          processor.process(request, response);
        }

        // Close the socket
        socket.close();
        //check if the previous URI is a shutdown command
        shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
      }
      catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
