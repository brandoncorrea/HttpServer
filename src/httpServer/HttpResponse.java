package httpServer;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    public final int statusCode;
    public final String content;
    public final Map<String, String> headers;

    public HttpResponse(int statusCode) {
        this.statusCode = statusCode;
        content = null;
        headers = new HashMap<>();
    }

    public HttpResponse(int statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
        headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("Content-Length", String.valueOf(content.getBytes().length));
    }

    public HttpResponse(int statusCode, Map<String, String> headers) {
        this.statusCode = statusCode;
        content = null;
        this.headers = new HashMap<>(headers);
    }

    public HttpResponse(int statusCode, Map<String, String> headers, String content) {
        this.statusCode = statusCode;
        this.headers = new HashMap<String, String>() {{
            put("Content-Type", "text/plain");
            put("Content-Length", String.valueOf(content.getBytes().length));
        }};
        this.headers.putAll(headers);
        this.content = content;
    }
}
