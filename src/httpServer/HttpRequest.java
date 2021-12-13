package httpServer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public final HttpMethod method;
    public final String uri;
    public final String protocol;
    public final String userAgent;
    public final String host;
    public final String acceptLanguage;

    public HttpRequest(InputStream stream) {
        Map<String, String> socketInfo = tryParseStream(stream);
        method = parseHttpMethod(socketInfo.get("Method"));
        uri = socketInfo.get("URI");
        protocol = socketInfo.get("Protocol");
        userAgent = socketInfo.get("User-Agent");
        host = socketInfo.get("Host");
        acceptLanguage = socketInfo.get("Accept-Language");
    }

    private Map<String, String> tryParseStream(InputStream stream) {
        HashMap<String, String> data = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            parseHeader(data, in);
            parseBody(data, in);
        } catch (Exception ignored) { }
        return data;
    }

    private void parseHeader(HashMap<String, String> data, BufferedReader reader) throws IOException {
        String[] header = reader.readLine().split("\\s+");
        if (header.length > 0) data.put("Method", header[0]);
        if (header.length > 1) data.put("URI", header[1]);
        if (header.length > 2) data.put("Protocol", header[2]);
    }

    private void parseBody(HashMap<String, String> data, BufferedReader reader) throws IOException {
        String s;
        while ((s = reader.readLine()) != null) {
            if (s.isEmpty()) break;
            String[] pair = s.split(":\\s+");
            data.put(pair[0], pair[1]);
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
