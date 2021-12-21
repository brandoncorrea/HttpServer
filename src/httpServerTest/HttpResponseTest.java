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
            Map<String, Object> res = HttpResponse.create(code);
            Assert.assertEquals(code, res.get("status"));
            Assert.assertNotNull(HttpResponse.headers(res).get("Date"));
            Assert.assertNull(res.get("body"));
        }
    }

    @Test
    public void newHttpResponseWithContent() {
        Object[][] options = new Object[][] {
                { 200, "Some Test" },
                { 500, "An error message" }
        };

        for (Object[] responseOptions : options) {
            int status = (int)responseOptions[0];
            String content = responseOptions[1].toString();
            Map<String, Object> res = HttpResponse.create(status, content);
            Assert.assertArrayEquals(content.getBytes(), HttpResponse.body(res));
            Assert.assertEquals("text/plain", HttpResponse.headers(res).get("Content-Type"));
            Assert.assertEquals(String.valueOf(content.length()), HttpResponse.headers(res).get("Content-Length"));
            Assert.assertNotNull(HttpResponse.headers(res).get("Date"));
        }
    }

    @Test
    public void newHttpResponseWithHeaders() {
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "text/html");
        }};

        Map<String, Object> res = HttpResponse.create(200, headers);
        Assert.assertEquals(200, res.get("status"));
        Assert.assertNull(res.get("body"));
        Assert.assertEquals("text/html", HttpResponse.headers(res).get("Content-Type"));
        Assert.assertNotNull(HttpResponse.headers(res).get("Date"));
        Assert.assertNull(res.get("body"));

        headers.put("Content-Type", "application/json");
        res = HttpResponse.create(300, headers);
        Assert.assertEquals(300, res.get("status"));
        Assert.assertNull(res.get("body"));
        Assert.assertEquals("application/json", HttpResponse.headers(res).get("Content-Type"));

        Assert.assertNotSame(headers, HttpResponse.headers(res));
        headers.put("Content-Type", "text/plain");
        Assert.assertEquals("application/json", HttpResponse.headers(res).get("Content-Type"));

        headers.put("Accept-Ranges", "bytes");
        res = HttpResponse.create(200, headers);
        Assert.assertEquals(200, res.get("status"));
        Assert.assertNull(res.get("body"));
        Assert.assertEquals("text/plain", HttpResponse.headers(res).get("Content-Type"));
        Assert.assertEquals("bytes", HttpResponse.headers(res).get("Accept-Ranges"));

        headers.put("Date", "2021-11-13T00:01:02.123");
        res = HttpResponse.create(200, headers);
        Assert.assertEquals("2021-11-13T00:01:02.123", HttpResponse.headers(res).get("Date"));
    }

    @Test
    public void newHttpResponseWithContentAndHeaders() {
        Map<String, String> headers = new HashMap<>();
        Map<String, Object> res = HttpResponse.create(200, headers, "Some content here");
        Assert.assertEquals(200, res.get("status"));
        Assert.assertEquals("text/plain", HttpResponse.headers(res).get("Content-Type"));
        Assert.assertEquals("17", HttpResponse.headers(res).get("Content-Length"));
        Assert.assertArrayEquals("Some content here".getBytes(), HttpResponse.body(res));
        Assert.assertNotNull(HttpResponse.headers(res).get("Date"));

        res = HttpResponse.create(300, headers, "Blah blah blah");
        Assert.assertEquals(300, res.get("status"));
        Assert.assertEquals("text/plain", HttpResponse.headers(res).get("Content-Type"));
        Assert.assertEquals("14", HttpResponse.headers(res).get("Content-Length"));
        Assert.assertNull(HttpResponse.headers(res).get("Accept-Ranges"));
        Assert.assertArrayEquals("Blah blah blah".getBytes(), HttpResponse.body(res));

        headers.put("Accept-Ranges", "bytes");
        res = HttpResponse.create(200, headers, "Blah blah blah");
        Assert.assertEquals("bytes", HttpResponse.headers(res).get("Accept-Ranges"));

        headers.put("Content-Length", "42");
        res = HttpResponse.create(200, headers, "Not 42 bytes");
        Assert.assertEquals("42", HttpResponse.headers(res).get("Content-Length"));

        headers.put("Content-Type", "application/json");
        res = HttpResponse.create(200, headers, "json content");
        Assert.assertEquals("application/json", HttpResponse.headers(res).get("Content-Type"));

        headers.put("Date", "2022-12-31T23:59:59.999");
        res = HttpResponse.create(200, headers);
        Assert.assertEquals("2022-12-31T23:59:59.999", HttpResponse.headers(res).get("Date"));
    }

    @Test
    public void newHttpResponseWithByteArray() {
        Map<String, Object> res = HttpResponse.create(200, new byte[0]);
        Assert.assertEquals(200, res.get("status"));
        Assert.assertArrayEquals(new byte[0], HttpResponse.body(res));
        Assert.assertEquals("0", HttpResponse.headers(res).get("Content-Length"));
        Assert.assertNotNull(HttpResponse.headers(res).get("Date"));

        byte[] responseData = {0, 4, 3, 7, 3};
        res = HttpResponse.create(300, responseData);
        Assert.assertEquals(300, res.get("status"));
        Assert.assertEquals("5", HttpResponse.headers(res).get("Content-Length"));
        Assert.assertNotNull(HttpResponse.headers(res).get("Date"));
        Assert.assertArrayEquals(responseData, HttpResponse.body(res));

        responseData[3] = 5;
        Assert.assertNotEquals(responseData[3], HttpResponse.body(res)[3]);
    }
}
