import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final Map<Pattern, Map<String, BiConsumer<List<String>, PrintWriter>>> routes = RouteHandler.getRoutes();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Read the HTTP request from the client
            List<String> request = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                request.add(line);
                System.out.println(line);
            }

            // Check if the resource is in the routes
            routeRequest(request, out);

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void routeRequest(List<String> request, PrintWriter out) {
        // parse the first line of the request to find the resource requested
        String[] requestLine = request.get(0).split(" ");
        String method = requestLine[0];
        String resource = requestLine[1];
        for (Map.Entry<Pattern, Map<String, BiConsumer<List<String>, PrintWriter>>> entry : routes.entrySet()) {
            Matcher matcher = entry.getKey().matcher(resource);
            if (matcher.matches()) {
                Map<String, BiConsumer<List<String>, PrintWriter>> methodRoutes = entry.getValue();
                if (methodRoutes != null) {
                    methodRoutes.getOrDefault(method, HTTPServer::handleNotFound).accept(request, out);
                } else {
                    HTTPServer.handleNotFound(null, out);
                }
                return;
            }
        }
        HTTPServer.handleNotFound(null, out);
    }
}