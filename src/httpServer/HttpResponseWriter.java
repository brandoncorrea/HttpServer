package httpServer;

import java.io.*;
import java.util.Map;

public class HttpResponseWriter {

    public void write(OutputStream out, HttpResponse response) throws IOException {
        writeStatus(out, response.statusCode);
        writeHeaders(out, response.headers);
        if (response.contentBytes.length > 0)
            out.write("\r\n".getBytes());
        out.write(response.contentBytes);
        out.flush();
    }

    private void writeStatus(OutputStream out, int statusCode) throws IOException {
        String format = "HTTP/1.1 %s %s\r\n";
        String description = HttpStatusCode.description(statusCode);
        writeFormat(out, format, statusCode, description);
    }

    private void writeHeaders(OutputStream out, Map<String, String> headers) throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet())
            writeFormat(out, "%s: %s\r\n", entry.getKey(), entry.getValue());
    }

    private void writeFormat(OutputStream out, String format, Object... args) throws IOException {
        out.write(String.format(format, args).getBytes());
    }
}
