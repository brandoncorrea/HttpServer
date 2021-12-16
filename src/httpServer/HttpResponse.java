package httpServer;

import java.text.SimpleDateFormat;
import java.util.*;

public class HttpResponse {
    public final int statusCode;
    public final String content;
    public final byte[] contentBytes;
    public final Map<String, String> headers;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    public HttpResponse(int statusCode) {
        this.statusCode = statusCode;
        content = null;
        contentBytes = new byte[0];
        headers = new HashMap<String, String>() {{
            put("Date", getFormattedDate());
        }};
    }

    public HttpResponse(int statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
        contentBytes = content.getBytes();
        headers = new HashMap<String, String>() {{
            put("Date", getFormattedDate());
            put("Content-Type", "text/plain");
            put("Content-Length", String.valueOf(contentBytes.length));
        }};
    }

    public HttpResponse(int statusCode, byte[] bytes) {
        this.statusCode = statusCode;
        this.content = null;
        contentBytes = new byte[bytes.length];
        System.arraycopy(bytes, 0, contentBytes, 0, bytes.length);
        this.headers = new HashMap<String, String>(){{
            put("Content-Length", String.valueOf(contentBytes.length));
            put("Date", getFormattedDate());
        }};
    }

    public HttpResponse(int statusCode, Map<String, String> headers) {
        this(statusCode);
        this.headers.putAll(headers);
    }

    public HttpResponse(int statusCode, Map<String, String> headers, String content) {
        this(statusCode, content);
        this.headers.putAll(headers);
    }

    private String getFormattedDate() { return dateFormat.format(new Date()); }
}
