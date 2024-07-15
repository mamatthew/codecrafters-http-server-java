import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPServer {

    private final int port;
    private final ServerSocket serverSocket;
    private static final Map<Pattern, Map<String, BiConsumer<Matcher, PrintWriter>>> routes = RouteHandler.getRoutes();

    public HTTPServer(ServerSocket serverSocket) {
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
        String method = requestLine[0];
        String resource = requestLine[1];

        // Check if the resource is in the routes
        routeRequest(method, resource, out);

        // Close streams
        in.close();
        out.close();
    }

    private static void routeRequest(String method, String path, PrintWriter out) {
        for (Map.Entry<Pattern, Map<String, BiConsumer<Matcher, PrintWriter>>> entry : routes.entrySet()) {
            Matcher matcher = entry.getKey().matcher(path);
            if (matcher.matches()) {
                Map<String, BiConsumer<Matcher, PrintWriter>> methodRoutes = entry.getValue();
                if (methodRoutes != null) {
                    methodRoutes.getOrDefault(method, HTTPServer::handleNotFound).accept(matcher, out);
                } else {
                    handleNotFound(null, out);
                }
                return;
            }
        }
        handleNotFound(null, out);
    }


    private static void handleNotFound(Matcher matcher, PrintWriter printWriter) {
        printWriter.print("HTTP/1.1 404 Not Found\r\n\r\n");
        printWriter.flush();
    }
}
