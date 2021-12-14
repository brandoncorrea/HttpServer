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
        if (lines.length > 0) {
            Map<String, String> status = parseStatusLine(lines[0]);
            method = parseHttpMethod(status.get("Method"));
            uri = status.get("URI");
            protocol = status.get("Protocol");
        }
        else {
            method = null;
            uri = null;
            protocol = null;
        }

        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            String[] pair = lines[i++].split(":\\s+");
            headers.put(pair[0], pair[1]);
        }

        i++;
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
        tryParseHeaders(reader);
        body = tryParseBody(reader);
    }

    private Map<String, String> tryParseStatusLine(BufferedReader reader) {
        try {
            return parseStatusLine(reader.readLine());
        } catch(Exception ignored) {
            return new HashMap<>();
        }
    }

    private Map<String, String> parseStatusLine(String status) {
        Map<String, String> data = new HashMap<>();
        String[] parts = status.split("\\s+");
        if (parts.length > 0) data.put("Method", parts[0]);
        if (parts.length > 1) data.put("URI", parts[1]);
        if (parts.length > 2) data.put("Protocol", parts[2]);
        return data;
    }

    private void tryParseHeaders(BufferedReader reader) {
        try {
            String s;
            while ((s = reader.readLine()) != null) {
                if (s.isEmpty()) return;
                String[] pair = s.split(":\\s+");
                headers.put(pair[0], pair[1]);
            }
        } catch(IOException ignored) { }
    }

    private String[] tryParseBody(BufferedReader reader) {
        try {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                content.append(line).append("\n");
            return content.toString().split("\\n");
        } catch (IOException ignored) {
            return new String[0];
        }
    }

    private HttpMethod parseHttpMethod(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (Exception e) {
            return null;
        }
    }
}
