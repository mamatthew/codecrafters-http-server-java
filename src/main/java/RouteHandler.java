import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteHandler {
    public static Map<Pattern, Map<String, BiConsumer<Matcher, PrintWriter>>> getRoutes() {
        Map<Pattern, Map<String, BiConsumer<Matcher, PrintWriter>>> routes = new HashMap();
        Pattern rootPattern = Pattern.compile("/");
        
        routes.computeIfAbsent(rootPattern, k -> new HashMap<>()).put("GET", (path, out) -> handleRoot(path, out));

        Pattern echoPattern = Pattern.compile("^/echo/(.+)$");
        
        routes.computeIfAbsent(echoPattern, k -> new HashMap<>()).put("GET", (path, out) -> handleEcho(path, out));
        return routes;
    }

    private static void handleEcho(Matcher matcher, PrintWriter out) {
        String echoPath = matcher.group(1);
        // Send the http response with the echoPath as the body of the response
        out.print("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 3\r\n\r\n" + echoPath);
        out.flush();
    }


    private static void handleRoot(Matcher matcher, PrintWriter out) {
        out.print("HTTP/1.1 200 OK\r\n\r\n");
        out.flush();
    }
}
