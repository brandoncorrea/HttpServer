package httpServerTest;

import httpServer.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseTest {
    @Test
    public void newHttpResponse() {
        int[] statusCodes = { 200, 400 };
        for (int code : statusCodes) {
            HttpResponse res = new HttpResponse(code);
            Assert.assertEquals(code, res.statusCode);
            Assert.assertNotNull(res.headers.get("Date"));
        }
    }

    @Test
    public void newHttpResponseWithContent() {
        HttpResponse res = new HttpResponse(200, "Some Text");
        Assert.assertEquals(200, res.statusCode);
        Assert.assertEquals("Some Text", res.content);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("9", res.headers.get("Content-Length"));
        Assert.assertNotNull(res.headers.get("Date"));

        res = new HttpResponse(500, "An error message");
        Assert.assertEquals(500, res.statusCode);
        Assert.assertEquals("An error message", res.content);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("16", res.headers.get("Content-Length"));
    }

    @Test
    public void newHttpResponseWithHeaders() {
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "text/html");
        }};

        HttpResponse res = new HttpResponse(200, headers);
        Assert.assertEquals(200, res.statusCode);
        Assert.assertNull(res.content);
        Assert.assertEquals("text/html", res.headers.get("Content-Type"));
        Assert.assertNotNull(res.headers.get("Date"));

        headers.put("Content-Type", "application/json");
        res = new HttpResponse(300, headers);
        Assert.assertEquals(300, res.statusCode);
        Assert.assertNull(res.content);
        Assert.assertEquals("application/json", res.headers.get("Content-Type"));

        Assert.assertNotSame(headers, res.headers);
        headers.put("Content-Type", "text/plain");
        Assert.assertEquals("application/json", res.headers.get("Content-Type"));

        headers.put("Accept-Ranges", "bytes");
        res = new HttpResponse(200, headers);
        Assert.assertEquals(200, res.statusCode);
        Assert.assertNull(res.content);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("bytes", res.headers.get("Accept-Ranges"));

        headers.put("Date", "2021-11-13T00:01:02.123");
        res = new HttpResponse(200, headers);
        Assert.assertEquals("2021-11-13T00:01:02.123", res.headers.get("Date"));
    }

    @Test
    public void newHttpResponseWithContentAndHeaders() {
        Map<String, String> headers = new HashMap<>();
        HttpResponse res = new HttpResponse(200, headers, "Some content here");
        Assert.assertEquals(200, res.statusCode);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("17", res.headers.get("Content-Length"));
        Assert.assertEquals("Some content here", res.content);
        Assert.assertNotNull(res.headers.get("Date"));

        res = new HttpResponse(300, headers, "Blah blah blah");
        Assert.assertEquals(300, res.statusCode);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("14", res.headers.get("Content-Length"));
        Assert.assertNull(res.headers.get("Accept-Ranges"));
        Assert.assertEquals("Blah blah blah", res.content);

        headers.put("Accept-Ranges", "bytes");
        res = new HttpResponse(200, headers, "Blah blah blah");
        Assert.assertEquals("bytes", res.headers.get("Accept-Ranges"));

        headers.put("Content-Length", "42");
        res = new HttpResponse(200, headers, "Not 42 bytes");
        Assert.assertEquals("42", res.headers.get("Content-Length"));

        headers.put("Content-Type", "application/json");
        res = new HttpResponse(200, headers, "json content");
        Assert.assertEquals("application/json", res.headers.get("Content-Type"));

        headers.put("Date", "2022-12-31T23:59:59.999");
        res = new HttpResponse(200, headers);
        Assert.assertEquals("2022-12-31T23:59:59.999", res.headers.get("Date"));
    }
}
