package ex02.pyrmont;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * 测试用例
 * @author Administrator
 *
 */
public class HttpServer2 {

  /**
   * shutdown command，
   * 比如访问http://192.168.146.1:8080/SHUTDOWN可以关闭serversocket
   */
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  /**
   * the shutdown command received
   * serversocket是否关闭了标志位
   */
  private boolean shutdown = false;

  public static void main(String[] args) {
    HttpServer2 server = new HttpServer2();
    server.await();
  }

  /**
   * 创建服务端serverSocket，蹲守在主机ip=192.168.146.1，port=8080上
   * 创建了serverSocket之后，服务就阻塞在 socket = serverSocket.accept();
   * 直到有人访问该主机的端口资源或者关闭该serverSocket
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
        input = socket.getInputStream(); //获取客户端请求流
        output = socket.getOutputStream(); //获取客户端请求输入流

        // create Request object and parse
        Request request = new Request(input); //根据客户端 请求流创建requet
        request.parse(); //解析请求的资源

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
