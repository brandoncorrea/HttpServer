package httpServer;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpRequest {
    public final HttpMethod method;
    public final String uri;
    public final String protocol;
    public final Map<String, String> headers = new HashMap<>();
    public final String[] body;

    public HttpRequest(String content) {
        Iterator<String> lines = Arrays.stream(content.split("\\r\\n")).iterator();
        Map<String, String> status = tryParseStatusLine(lines);
        method = parseHttpMethod(status.get("Method"));
        uri = status.get("URI");
        protocol = status.get("Protocol");
        readHeader(lines);
        body = readBody(lines);
    }

    public HttpRequest(InputStream stream) {
        Iterator<String> lines = new BufferedReader(new InputStreamReader(stream)).lines().iterator();
        Map<String, String> status = tryParseStatusLine(lines);
        method = parseHttpMethod(status.get("Method"));
        uri = status.get("URI");
        protocol = status.get("Protocol");
        readHeader(lines);
        body = readBody(lines);
    }

    private Map<String, String> tryParseStatusLine(Iterator<String> lines) {
        Map<String, String> data = new HashMap<>();
        try {
            String[] parts = lines.next().split("\\s+");
            if (parts.length > 0) data.put("Method", parts[0]);
            if (parts.length > 1) data.put("URI", parts[1]);
            if (parts.length > 2) data.put("Protocol", parts[2]);
            return data;
        } catch(Exception ignored) { }
        return data;
    }

    private void readHeader(Iterator<String> lines) {
        while (lines.hasNext()) {
            String line = lines.next();
            if (line.isEmpty()) break;
            String[] pair = line.split(":\\s+");
            headers.put(pair[0], pair[1]);
        }
    }

    private String[] readBody(Iterator<String> lines) {
        StringBuilder remaining = new StringBuilder();
        while (lines.hasNext())
            remaining.append(lines.next()).append("\n");
        String content = remaining.toString();
        if (content.isEmpty())
            return new String[0];
        else
            return content.split("\\n");
    }

    private HttpMethod parseHttpMethod(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (Exception e) {
            return null;
        }
    }
}
