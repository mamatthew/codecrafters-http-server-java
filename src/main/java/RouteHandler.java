import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class RouteHandler {
    public static Map<Pattern, Map<String, BiConsumer<HttpRequest, OutputStream>>> getRoutes() {
        Map<Pattern, Map<String, BiConsumer<HttpRequest, OutputStream>>> routes = new HashMap();
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

    private static void handleGetFile(HttpRequest request, OutputStream out) {
        String path = request.path.substring(7);
        System.out.println("Path: " + path);
        try {
            String content = FileUtil.readFile(path);
            String response = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + content.length() + "\r\n\r\n" + content;
            out.write(response.getBytes("UTF-8"));
        } catch (Exception e) {
            try {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void handlePostFile(HttpRequest request, OutputStream out) {
        String path = request.path.substring(7);
        // write code to get the content from the request body
        // and write it to the file
        char[] content = request.body;

        try {
            FileUtil.writeFile(path, content);
            out.write("HTTP/1.1 201 Created\r\n\r\n".getBytes("UTF-8"));
        } catch (Exception e) {
            try {
                out.write("HTTP/1.1 500 Internal Server Error\r\n\r\n".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }



    }

    private static void handleUserAgent(HttpRequest request, OutputStream out) {
        String userAgent = request.headers.get("User-Agent");
        String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + userAgent.length() + "\r\n\r\n" + userAgent;
        try {
            out.write(response.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleEcho(HttpRequest request, OutputStream out) {
        String echoPath = request.path.substring(6);
        System.out.println("Echo path: " + echoPath);
        String encoding = "";

        String acceptEncoding = request.headers.get("Accept-Encoding");
        if (acceptEncoding != null && acceptEncoding.contains("gzip")){
            encoding = "Content-Encoding: gzip";
        }

        if (encoding != "") {
            byte[] compressedPath = FileUtil.compress(echoPath);
            // use OutputStream to write the compressedPath instead of PrintWriter
            try {
                out.write(("HTTP/1.1 200 OK\r\n" + encoding + "\r\n" + "Content-Type: text/plain\r\nContent-Length: " + compressedPath.length + "\r\n" + encoding + "\r\n\r\n").getBytes("UTF-8"));
                out.write(compressedPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                out.write(("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + echoPath.length() + "\r\n\r\n" + echoPath).getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void handleRoot(HttpRequest request, OutputStream out) {
        try {
            out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
