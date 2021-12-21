package httpServerTest;

import httpServer.HttpResponse;
import httpServer.HttpResponseWriter;
import httpServer.HttpStatusCode;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpResponseWriterTest {
    @Test
    public void writeStatusCode() throws IOException {
        Map<String, Object> res = HttpResponse.create(HttpStatusCode.OK);
        HttpResponse.headers(res).put("Date", "date content");
        testWriter(res,"HTTP/1.1 200 OK\r\nDate: date content\r\n");

        HttpResponse.headers(res).put("Random-Header", "Some random content");
        testWriter(res, "HTTP/1.1 200 OK\r\n" +
                "Random-Header: Some random content\r\n" +
                "Date: date content\r\n");

        HttpResponse.headers(res).put("Third-Item", "More text");
        testWriter(res, "HTTP/1.1 200 OK\r\n" +
                "Third-Item: More text\r\n" +
                "Random-Header: Some random content\r\n" +
                "Date: date content\r\n");

        res = HttpResponse.create(HttpStatusCode.InternalServerError);
        HttpResponse.headers(res).put("Date", "dummy date");
        testWriter(res, "HTTP/1.1 500 Internal Server Error\r\n" +
                "Date: dummy date\r\n");

        res = HttpResponse.create(HttpStatusCode.NotFound, "404 Page Not Found");
        HttpResponse.headers(res).put("Date", "Another dummy date");
        testWriter(res, "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 18\r\n" +
                "Date: Another dummy date\r\n" +
                "Content-Type: text/plain\r\n\r\n" +
                "404 Page Not Found");

        res = HttpResponse.create(HttpStatusCode.InternalServerError, "Error Message Here");
        HttpResponse.headers(res).put("Date", "Another dummy date");
        testWriter(res, "HTTP/1.1 500 Internal Server Error\r\n" +
                "Content-Length: 18\r\n" +
                "Date: Another dummy date\r\n" +
                "Content-Type: text/plain\r\n\r\n" +
                "Error Message Here");

        res = HttpResponse.create(HttpStatusCode.OK, "Hello!".getBytes());
        HttpResponse.headers(res).put("Date", "fake date");
        testWriter(res, "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 6\r\n" +
                "Date: fake date\r\n\r\n" +
                "Hello!");
    }

    private void testWriter(Map<String, Object> response, String expected) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpResponseWriter.write(out, response);
        Assert.assertEquals(expected, out.toString());
    }
}
