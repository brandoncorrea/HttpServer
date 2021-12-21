package httpServer;

import java.io.*;
import java.util.Map;

public class HttpResponseWriter {

    private HttpResponseWriter() { }

    public static void write(OutputStream out, Map<String, Object> response) throws IOException {
        writeStatus(out, (int)response.get("status"));
        writeHeaders(out, HttpResponse.headers(response));
        writeBody(out, HttpResponse.body(response));
        out.flush();
    }

    private static void writeStatus(OutputStream out, int statusCode) throws IOException {
        String format = "HTTP/1.1 %s %s\r\n";
        String description = HttpStatusCode.description(statusCode);
        writeFormat(out, format, statusCode, description);
    }

    private static void writeHeaders(OutputStream out, Map<String, String> headers) throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet())
            writeFormat(out, "%s: %s\r\n", entry.getKey(), entry.getValue());
    }

    private static void writeBody(OutputStream out, Object body) throws IOException {
        if (body == null) return;
        byte[] bytes = (byte[])body;
        if (bytes.length > 0)
            out.write("\r\n".getBytes());
        out.write(bytes);
    }

    private static void writeFormat(OutputStream out, String format, Object... args) throws IOException {
        out.write(String.format(format, args).getBytes());
    }
}
