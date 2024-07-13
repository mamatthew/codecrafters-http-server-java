import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Server {

    private final int port;
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.port = serverSocket.getLocalPort();
    }

    public void start() {
        System.out.println("Server started on port " + port);
        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connected");
                handleClientRequest(clientSocket);

            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }

        }
    }

    private void handleClientRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Prepare the response
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";

        // Send the response
        out.print(httpResponse);
        out.flush();

        // Close streams
        in.close();
        out.close();
    }
}
