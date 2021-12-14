package httpServer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class HttpResponse {
    public final int statusCode;
    public final String content;
    public final Map<String, String> headers;

    public HttpResponse(int statusCode) {
        this.statusCode = statusCode;
        content = null;

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        headers = new HashMap<String, String>() {{
            put("Date", getFormattedDate());
        }};
    }

    public HttpResponse(int statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
        headers = new HashMap<String, String>() {{
            put("Date", getFormattedDate());
            put("Content-Type", "text/plain");
            put("Content-Length", String.valueOf(content.getBytes().length));
        }};
    }

    public HttpResponse(int statusCode, Map<String, String> headers) {
        this.statusCode = statusCode;
        content = null;
        this.headers = new HashMap<String, String>() {{
            put("Date", getFormattedDate());
        }};
        this.headers.putAll(headers);
    }

    public HttpResponse(int statusCode, Map<String, String> headers, String content) {
        this.statusCode = statusCode;
        this.headers = new HashMap<String, String>() {{
            put("Date", getFormattedDate());
            put("Content-Type", "text/plain");
            put("Content-Length", String.valueOf(content.getBytes().length));
        }};
        this.headers.putAll(headers);
        this.content = content;
    }

    private String getFormattedDate() {
        String format = "EEE, dd MMM yyyy HH:mm:ss z";
        return new SimpleDateFormat(format).format(new Date());
    }
}
