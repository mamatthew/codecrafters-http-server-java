import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HTTPServer {

    private final int port;
    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;

    public HTTPServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.port = serverSocket.getLocalPort();
        this.threadPool = Executors.newFixedThreadPool(10); // Adjust the pool size as needed
    }

    public void start() {
        System.out.println("Server started on port " + port);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                threadPool.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }

    public static void handleNotFound(HttpRequest request, OutputStream out) {
        try {
            out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes("UTF-8"));
            out.flush();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}