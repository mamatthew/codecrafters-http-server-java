import java.io.IOException;
import java.net.ServerSocket;

public class Main {

  public final static int PORT = 4221;

  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

     try {
       ServerSocket serverSocket = new ServerSocket(PORT);

       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);

       HTTPServer server = new HTTPServer(serverSocket);
       server.start();

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
