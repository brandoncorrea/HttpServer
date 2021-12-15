package httpServer;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public final HttpMethod method;
    public final String uri;
    public final String protocol;
    public final Map<String, String> headers = new HashMap<>();
    public final String[] body;

    public HttpRequest(String content) {
        String[] lines = content.split("\\r\\n");
        Map<String, String> status = tryParseStatusLine(lines);
        method = parseHttpMethod(status.get("Method"));
        uri = status.get("URI");
        protocol = status.get("Protocol");
        int i = 1;
        while (i < lines.length) {
            String line = lines[i++];
            if (line.isEmpty()) break;
            String[] pair = line.split(":\\s+");
            headers.put(pair[0], pair[1]);
        }

        if (i < lines.length)
            body = Arrays.copyOfRange(lines, i, lines.length);
        else
            body = new String[0];
    }

    public HttpRequest(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Map<String, String> status = tryParseStatusLine(reader);
        method = parseHttpMethod(status.get("Method"));
        uri = status.get("URI");
        protocol = status.get("Protocol");
        fillHeaders(reader);
        body = readBody(reader);
    }

    private Map<String, String> tryParseStatusLine(String[] lines) {
        if (lines.length > 0)
            return parseStatusParts(lines[0]);
        return new HashMap<>();
    }

    private Map<String, String> tryParseStatusLine(BufferedReader reader) {
        try {
            return parseStatusParts(reader.readLine());
        } catch(Exception ignored) {
            return new HashMap<>();
        }
    }

    private Map<String, String> parseStatusParts(String s) {
        Map<String, String> data = new HashMap<>();
        String[] parts = s.split("\\s+");
        if (parts.length > 0) data.put("Method", parts[0]);
        if (parts.length > 1) data.put("URI", parts[1]);
        if (parts.length > 2) data.put("Protocol", parts[2]);
        return data;
    }

    private void fillHeaders(BufferedReader reader) {
        try {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] pair = line.split(":\\s+");
                headers.put(pair[0], pair[1]);
            }
        } catch (IOException ignored) { }
    }

    private String[] readBody(BufferedReader reader) {
        try {
            int length = getContentLength();
            if (length == 0) return new String[0];
            char[] content = new char[length];
            reader.read(content);
            return String.valueOf(content).split("\\r\\n");
        } catch (IOException ignored) { }
        return new String[0];
    }

    private int getContentLength() {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) return 0;
        return Integer.parseInt(contentLength);
    }

    private HttpMethod parseHttpMethod(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (Exception e) {
            return null;
        }
    }
}
