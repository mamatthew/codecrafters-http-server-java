import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

        // Read the HTTP request from the client
        List<String> request = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            request.add(line);
            System.out.println(line);
        }
        // parse the first line of the request to find the resource requested
        String[] requestLine = request.get(0).split(" ");
        String resource = requestLine[1];

        String httpResponse;
        if (resource.equals("/")) {
            httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
        } else {
            httpResponse = "HTTP/1.1 404 Not Found\\r\\n\\r\\n";
        }

        // Send the response
        out.print(httpResponse);
        out.flush();

        // Close streams
        in.close();
        out.close();
    }
}
