import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteHandler {
    public static Map<Pattern, Map<String, BiConsumer<List<String>, PrintWriter>>> getRoutes() {
        Map<Pattern, Map<String, BiConsumer<List<String>, PrintWriter>>> routes = new HashMap();
        Pattern rootPattern = Pattern.compile("/");
        
        routes.computeIfAbsent(rootPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleRoot);

        Pattern echoPattern = Pattern.compile("^/echo/(.+)$");
        
        routes.computeIfAbsent(echoPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleEcho);

        // write a pattern for the /user-agent path
        Pattern userAgentPattern = Pattern.compile("/user-agent");

        routes.computeIfAbsent(userAgentPattern, k -> new HashMap<>()).put("GET", RouteHandler::handleUserAgent);

        return routes;
    }

    private static void handleUserAgent(List<String> request, PrintWriter out) {
        // Get the user agent header from the request
        String userAgent = request.stream()
                .filter(line -> line.startsWith("User-Agent"))
                .findFirst()
                .map(line -> line.split(": ")[1])
                .orElse("Unknown");
        out.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + userAgent.length() + "\r\n\r\n" + userAgent);
        out.flush();
    }

    private static void handleEcho(List<String> request, PrintWriter out) {
        // Get the echo path from the request
        Matcher matcher = Pattern.compile("^/echo/(.+)$").matcher(request.get(1));
        String echoPath = matcher.group(0);
        // Send the http response with the echoPath as the body of the response
        out.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + echoPath.length() + "\r\n\r\n" + echoPath);
        out.flush();
    }


    private static void handleRoot(List<String> request, PrintWriter out) {
        out.print("HTTP/1.1 200 OK\r\n\r\n");
        out.flush();
    }
}
