import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final Map<Pattern, Map<String, BiConsumer<HttpRequest, PrintWriter>>> routes = new ConcurrentHashMap<>(RouteHandler.getRoutes());

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

        // print the request line
        System.out.println("Requestline: " + requestLine);

        // Read headers
        String headerLine;
        while (!(headerLine = reader.readLine()).isEmpty()) {
            String[] headerParts = headerLine.split(": ");
            if (headerParts.length == 2)
                request.headers.put(headerParts[0], headerParts[1]);
        }

        // print the headers
        System.out.println("Headers: " + request.headers);

        int contentLength = Integer.parseInt(request.headers.get("Content-Length"));
        System.out.println("Content-Length: " + contentLength);
        char[] content = new char[contentLength];
        reader.read(content, 0, contentLength);
        System.out.println("Read content");
        request.body = content;

        // print the body
        System.out.println("Body: " + new String(content));
        return request;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {


            HttpRequest request = parseRequest(in);

            if (request == null) {
                out.print("HTTP/1.1 400 Bad Request\r\n\r\n");
                out.flush();
                return;
            }

            System.out.println("Routing request");

            // Check if the resource is in the routes
            routeRequest(request, out);

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void routeRequest(HttpRequest request, PrintWriter out) {
        // parse the first line of the request to find the resource requested
        System.out.println("Routing request");
        String method = request.method;
        String resource = request.path;
        for (Map.Entry<Pattern, Map<String, BiConsumer<HttpRequest, PrintWriter>>> entry : routes.entrySet()) {
            Matcher matcher = entry.getKey().matcher(resource);
            if (matcher.matches()) {
                Map<String, BiConsumer<HttpRequest, PrintWriter>> methodRoutes = entry.getValue();
                if (methodRoutes != null) {
                    System.out.println("routing to " + resource + " with method " + method);
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