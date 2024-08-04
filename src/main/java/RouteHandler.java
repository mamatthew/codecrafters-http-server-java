import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class RouteHandler {
    public static Map<Pattern, Map<String, BiConsumer<List<String>, PrintWriter>>> getRoutes() {
        Map<Pattern, Map<String, BiConsumer<List<String>, PrintWriter>>> routes = new HashMap();
        Pattern rootPattern = Pattern.compile("/");
        
        routes.computeIfAbsent(rootPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleRoot);

        Pattern echoPattern = Pattern.compile("^/echo/(.+)$");
        
        routes.computeIfAbsent(echoPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleEcho);

        Pattern userAgentPattern = Pattern.compile("/user-agent");

        routes.computeIfAbsent(userAgentPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleUserAgent);

        Pattern filePattern = Pattern.compile("/files/(.+)");

        routes.computeIfAbsent(filePattern, k -> new HashMap<>()).put("GET", RouteHandler::handleFile);

        return routes;
    }

    private static void handleFile(List<String> strings, PrintWriter printWriter) {
        String path = strings.get(0).split(" ")[1].substring(6);
        try {
            String content = FileUtil.readFile(path);
            printWriter.print("HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + content.length() + "\r\n\r\n" + content);
        } catch (Exception e) {
            printWriter.print("HTTP/1.1 404 Not Found\r\n\r\n");
        }
        printWriter.flush();
    }

    private static void handleUserAgent(List<String> request, PrintWriter out) {
        String userAgent = request.stream()
                .filter(line -> line.startsWith("User-Agent"))
                .findFirst()
                .map(line -> line.split(": ")[1])
                .orElse("Unknown");
        out.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + userAgent.length() + "\r\n\r\n" + userAgent);
        out.flush();
    }

    private static void handleEcho(List<String> request, PrintWriter out) {
        String echoPath = request.get(0).split(" ")[1].substring(6);
        out.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + echoPath.length() + "\r\n\r\n" + echoPath);
        out.flush();
    }


    private static void handleRoot(List<String> request, PrintWriter out) {
        out.print("HTTP/1.1 200 OK\r\n\r\n");
        out.flush();
    }
}
