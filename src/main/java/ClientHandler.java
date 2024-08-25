import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final Map<Pattern, Map<String, BiConsumer<HttpRequest, OutputStream>>> routes = new ConcurrentHashMap<>(RouteHandler.getRoutes());

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private HttpRequest parseRequest(BufferedReader reader) throws IOException {
        HttpRequest request = new HttpRequest();

        // Read the request line
        String requestLine = reader.readLine();
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3)
            return null;
        request.method = requestParts[0];
        request.path = requestParts[1];
        request.version = requestParts[2];

        // Read headers
        String headerLine;
        while (!(headerLine = reader.readLine()).isEmpty()) {
            String[] headerParts = headerLine.split(": ");
            if (headerParts.length == 2)
                request.headers.put(headerParts[0], headerParts[1]);
        }

        if (!request.headers.containsKey("Content-Length"))
            return request;

        int contentLength = Integer.parseInt(request.headers.get("Content-Length"));
        char[] content = new char[contentLength];
        reader.read(content, 0, contentLength);
        request.body = content;

        return request;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream();) {


            HttpRequest request = parseRequest(in);

            if (request == null) {
                out.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes("UTF-8"));
                return;
            }

            System.out.println("Routing request");

            // Check if the resource is in the routes
            routeRequest(request, out);

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void routeRequest(HttpRequest request, OutputStream out) {
        // parse the first line of the request to find the resource requested
        String method = request.method;
        String resource = request.path;
        for (Map.Entry<Pattern, Map<String, BiConsumer<HttpRequest, OutputStream>>> entry : routes.entrySet()) {
            Matcher matcher = entry.getKey().matcher(resource);
            if (matcher.matches()) {
                Map<String, BiConsumer<HttpRequest, OutputStream>> methodRoutes = entry.getValue();
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