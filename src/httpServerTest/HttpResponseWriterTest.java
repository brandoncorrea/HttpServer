package httpServerTest;

import httpServer.HttpResponse;
import httpServer.HttpResponseWriter;
import httpServer.HttpStatusCode;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpResponseWriterTest {
    @Test
    public void newHttpResponseWriter() {
        new HttpResponseWriter();
    }

    @Test
    public void writeStatusCode() throws IOException {
        HttpResponse res = new HttpResponse(HttpStatusCode.OK);
        res.headers.put("Date", "date content");
        testWriter(res,"HTTP/1.1 200 OK\r\nDate: date content\r\n");

        res.headers.put("Random-Header", "Some random content");
        testWriter(res, "HTTP/1.1 200 OK\r\n" +
                "Random-Header: Some random content\r\n" +
                "Date: date content\r\n");

        res.headers.put("Third-Item", "More text");
        testWriter(res, "HTTP/1.1 200 OK\r\n" +
                "Third-Item: More text\r\n" +
                "Random-Header: Some random content\r\n" +
                "Date: date content\r\n");

        res = new HttpResponse(HttpStatusCode.InternalServerError);
        res.headers.put("Date", "dummy date");
        testWriter(res, "HTTP/1.1 500 Internal Server Error\r\n" +
                "Date: dummy date\r\n");

        res = new HttpResponse(HttpStatusCode.NotFound, "404 Page Not Found");
        res.headers.put("Date", "Another dummy date");
        testWriter(res, "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 18\r\n" +
                "Date: Another dummy date\r\n" +
                "Content-Type: text/plain\r\n\r\n" +
                "404 Page Not Found");

        res = new HttpResponse(HttpStatusCode.InternalServerError, "Error Message Here");
        res.headers.put("Date", "Another dummy date");
        testWriter(res, "HTTP/1.1 500 Internal Server Error\r\n" +
                "Content-Length: 18\r\n" +
                "Date: Another dummy date\r\n" +
                "Content-Type: text/plain\r\n\r\n" +
                "Error Message Here");
    }

    private void testWriter(HttpResponse response, String expected) throws IOException {
        HttpResponseWriter writer = new HttpResponseWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out, response);
        Assert.assertEquals(expected, out.toString());
    }
}
