import java.io.IOException;
import java.net.ServerSocket;

public class Main {

  public final static int PORT = 4221;

  public static void main(String[] args) {

    if (args.length > 0 && args[0].equals("--directory")) {
        if (args.length > 1) {
            FileUtil.setDirectoryPath(args[1]);
        } else {
            System.out.println("Directory path not provided");
        }
    }

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
