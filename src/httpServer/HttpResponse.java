package httpServer;

import java.text.SimpleDateFormat;
import java.util.*;

public final class HttpResponse {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    public static Map<String, String> headers(Map<String, Object> response) {
        return (Map<String, String>)response.get("headers");
    }

    public static byte[] body(Map<String, Object> response) {
        if (response.containsKey("body"))
            return (byte[])response.get("body");
        return new byte[0];
    }

    public static Map<String, Object> create(int status) {
        return new HashMap<String, Object>() {{
            put("status", status);
            put("headers", new HashMap<String, String>() {{
                put("Date", dateFormat.format(new Date()));
            }});
        }};
    }

    public static Map<String, Object> create(int status, String content) {
        Map<String, Object> res = create(status, content.getBytes());
        headers(res).put("Content-Type", "text/plain");
        return res;
    }

    public static Map<String, Object> create(int status, byte[] body) {
        Map<String, Object> res = create(status);
        headers(res).put("Content-Length", String.valueOf(body.length));
        byte[] bytes = new byte[body.length];
        System.arraycopy(body, 0, bytes, 0, bytes.length);
        res.put("body", bytes);
        return res;
    }

    public static Map<String, Object> create(int statusCode, Map<String, String> headers) {
        Map<String, Object> res = create(statusCode);
        headers(res).putAll(headers);
        return res;
    }

    public static Map<String, Object> create(int statusCode, Map<String, String> headers, String content) {
        Map<String, Object> res = create(statusCode, content);
        headers(res).putAll(headers);
        return res;
    }
}
