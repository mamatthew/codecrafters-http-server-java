import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    String method;
    String path;
    String version;
    Map<String, String> headers;
    char[] body;

    public HttpRequest() {
        headers = new HashMap<>();
    }
}