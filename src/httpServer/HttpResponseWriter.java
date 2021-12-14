package httpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponseWriter {

    public void write(OutputStream out, HttpResponse response) throws IOException {
        writeStatus(out, response);
        writeHeaders(out, response);
        writeContent(out, response);
        out.flush();
    }

    private void writeStatus(OutputStream out, HttpResponse response) throws IOException {
        String format = "HTTP/1.1 %s %s\r\n";
        String description = HttpStatusCode.description(response.statusCode);
        writeFormat(out, format, response.statusCode, description);
    }

    private void writeHeaders(OutputStream out, HttpResponse response) throws IOException {
        for (Map.Entry<String, String> entry : response.headers.entrySet())
            writeFormat(out, "%s: %s\r\n", entry.getKey(), entry.getValue());
    }

    private void writeContent(OutputStream out, HttpResponse response) throws IOException {
        if (response.content != null)
            writeFormat(out, "\r\n%s", response.content);
    }

    private void writeFormat(OutputStream out, String format, Object... args) throws IOException {
        out.write(String.format(format, args).getBytes());
    }
}
