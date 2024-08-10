import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class RouteHandler {
    public static Map<Pattern, Map<String, BiConsumer<HttpRequest, PrintWriter>>> getRoutes() {
        Map<Pattern, Map<String, BiConsumer<HttpRequest, PrintWriter>>> routes = new HashMap();
        Pattern rootPattern = Pattern.compile("/");
        
        routes.computeIfAbsent(rootPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleRoot);

        Pattern echoPattern = Pattern.compile("^/echo/(.+)$");
        
        routes.computeIfAbsent(echoPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleEcho);

        Pattern userAgentPattern = Pattern.compile("/user-agent");

        routes.computeIfAbsent(userAgentPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleUserAgent);

        Pattern filePattern = Pattern.compile("/files/(.+)");

        routes.computeIfAbsent(filePattern, k -> new HashMap<>()).put("GET", RouteHandler::handleGetFile);

        routes.computeIfAbsent(filePattern, k -> new HashMap<>()).put("POST", RouteHandler::handlePostFile);

        return routes;
    }

    private static void handleGetFile(HttpRequest request, PrintWriter printWriter) {
        String path = request.path.substring(7);
        System.out.println("Path: " + path);
        try {
            String content = FileUtil.readFile(path);
            printWriter.print("HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + content.length() + "\r\n\r\n" + content);
        } catch (Exception e) {
            printWriter.print("HTTP/1.1 404 Not Found\r\n\r\n");
        }
        printWriter.flush();
    }

    private static void handlePostFile(HttpRequest request, PrintWriter printWriter) {
        String path = request.path.substring(7);
        // write code to get the content from the request body
        // and write it to the file
        char[] content = request.body;

        try {
            FileUtil.writeFile(path, content);
            printWriter.print("HTTP/1.1 201 Created\r\n\r\n");
        } catch (Exception e) {
            printWriter.print("HTTP/1.1 500 Internal Server Error\r\n\r\n");
        }



    }

    private static void handleUserAgent(HttpRequest request, PrintWriter out) {
        String userAgent = request.headers.get("User-Agent");
        out.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + userAgent.length() + "\r\n\r\n" + userAgent);
        out.flush();
    }

    private static void handleEcho(HttpRequest request, PrintWriter out) {
        System.out.println("Echoing");
        String echoPath = request.path.substring(6);
        String encoding = "";

        if (request.headers.get("Accept-Encoding").equals("gzip")) {
            encoding = "Content-Encoding: gzip";
        }
        if (encoding.length() != 0) {
            System.out.println("Encoding: " + encoding);
            out.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + echoPath.length() + "\r\n" + encoding + "\r\n\r\n" + echoPath);
        } else {
            System.out.println("No encoding");
            out.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + echoPath.length() + "\r\n\r\n" + echoPath);
        }
        out.flush();
    }


    private static void handleRoot(HttpRequest request, PrintWriter out) {
        out.print("HTTP/1.1 200 OK\r\n\r\n");
        out.flush();
    }
}
