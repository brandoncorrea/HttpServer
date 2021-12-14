package httpServerTest;

import httpServer.HttpMethod;
import httpServer.HttpRequest;
import org.junit.Assert;
import org.junit.Test;
import java.io.InputStream;

public class HttpRequestTest {

    private InputStream newInputStream(String content) {
        return new InputStream() {
            private final byte[] bytes = content.getBytes();
            private int index = 0;

            @Override
            public int read() {
                if (index >= bytes.length) return -1;
                return bytes[index++];
            }
        };
    }

    @Test
    public void newHttpRequestUsingStringContent() {
        HttpRequest req = new HttpRequest("");
        Assert.assertNull(req.protocol);
        Assert.assertNull(req.method);
        Assert.assertNull(req.uri);
        Assert.assertEquals(0, req.body.length);
        Assert.assertTrue(req.headers.isEmpty());

        req = new HttpRequest("GET");
        Assert.assertEquals(HttpMethod.GET, req.method);
        req = new HttpRequest("GET /");
        Assert.assertEquals(HttpMethod.GET, req.method);
        Assert.assertEquals("/", req.uri);
        req = new HttpRequest("GET / HTTP/1.1");
        Assert.assertEquals(HttpMethod.GET, req.method);
        Assert.assertEquals("/", req.uri);
        Assert.assertEquals("HTTP/1.1", req.protocol);

        req = new HttpRequest("POST /hello HTTP/1.1\r\nContent-Type: application/json");
        Assert.assertEquals(HttpMethod.POST, req.method);
        Assert.assertEquals("/hello", req.uri);
        Assert.assertEquals("HTTP/1.1", req.protocol);
        Assert.assertEquals("application/json", req.headers.get("Content-Type"));

        req = new HttpRequest("GET / HTTP/1.1\r\nContent-Type: application/json\r\n\r\nsome json\r\ncontent\r\nlast line");
        Assert.assertEquals(HttpMethod.GET, req.method);
        Assert.assertEquals("/", req.uri);
        Assert.assertEquals("HTTP/1.1", req.protocol);
        Assert.assertEquals("application/json", req.headers.get("Content-Type"));
        Assert.assertEquals("some json", req.body[0]);
        Assert.assertEquals("content", req.body[1]);
        Assert.assertEquals("last line", req.body[2]);
    }

    @Test
    public void newHttpRequestWithMissingStatusInfo() {
        InputStream stream = newInputStream("");
        HttpRequest request = new HttpRequest(stream);
        Assert.assertNull(request.method);
        Assert.assertNull(request.uri);
        Assert.assertNull(request.protocol);

        stream = newInputStream("GET");
        request = new HttpRequest(stream);
        Assert.assertEquals(HttpMethod.GET, request.method);
        Assert.assertNull(request.uri);
        Assert.assertNull(request.protocol);

        stream = newInputStream("GET /hellos");
        request = new HttpRequest(stream);
        Assert.assertEquals(HttpMethod.GET, request.method);
        Assert.assertEquals("/hellos", request.uri);
        Assert.assertNull(request.protocol);
    }

    @Test
    public void newHttpRequestWithBadMethod() {
        InputStream stream = newInputStream("NOTAMETHOD /hello HTTP/1.1");
        HttpRequest request = new HttpRequest(stream);
        Assert.assertNull(request.method);
        Assert.assertEquals("/hello", request.uri);
        Assert.assertEquals("HTTP/1.1", request.protocol);
    }

    @Test
    public void newHttpRequest() {
        String[][] statuses = {
                {"GET", "/hello.txt", "HTTP/1.1"},
                {"POST", "/Heyo/goodbye.pdf", "HTTP/2.0"}
        };

        for (String[] status : statuses) {
            InputStream stream = newInputStream(status[0] + " " + status[1] + " " + status[2]);
            HttpRequest request = new HttpRequest(stream);
            Assert.assertEquals(HttpMethod.valueOf(status[0]), request.method);
            Assert.assertEquals(status[1], request.uri);
            Assert.assertEquals(status[2], request.protocol);
        }
    }

    @Test
    public void requestContainsUserAgent() {
        String[] agents = {
                "curl/7.16.3 libcurl/7.16.3 OpenSSL/0.9.7l zlib/1.2.3",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15"
        };

        String status = "GET / HTTP/1.1\r\n";
        for (String agent : agents) {
            InputStream stream = newInputStream(status + "User-Agent: " + agent);
            HttpRequest request = new HttpRequest(stream);
            Assert.assertEquals(HttpMethod.GET, request.method);
            Assert.assertEquals("/", request.uri);
            Assert.assertEquals("HTTP/1.1", request.protocol);
            Assert.assertEquals(agent, request.headers.get("User-Agent"));
        }
    }

    @Test
    public void requestContainsHost() {
        String[] hosts = {
                "www.example.com",
                "google.com"
        };

        String status = "GET / HTTP/1.1\r\n";
        for (String host : hosts) {
            InputStream stream = newInputStream(status + "Host: " + host);
            HttpRequest request = new HttpRequest(stream);
            Assert.assertEquals(HttpMethod.valueOf("GET"), request.method);
            Assert.assertEquals("/", request.uri);
            Assert.assertEquals("HTTP/1.1", request.protocol);
            Assert.assertEquals(host, request.headers.get("Host"));
        }
    }

    @Test
    public void requestContainsAcceptLanguage() {
        String[] langs = {
                "en, mi",
                "en, du",
                "wookie"
        };

        String status = "GET / HTTP/1.1\r\n";
        for (String lang : langs) {
            InputStream stream = newInputStream(status + "Accept-Language: " + lang);
            HttpRequest request = new HttpRequest(stream);
            Assert.assertEquals(HttpMethod.valueOf("GET"), request.method);
            Assert.assertEquals("/", request.uri);
            Assert.assertEquals("HTTP/1.1", request.protocol);
            Assert.assertEquals(lang, request.headers.get("Accept-Language"));
        }
    }

    @Test
    public void requestParsesBodyLines() {
        String content = "GET / HTTP/1.1\r\n";
        HttpRequest req = new HttpRequest(newInputStream(content));
        Assert.assertEquals(0, req.body.length);

        content = "GET / HTTP/1.1\r\n\r\nnumber=1";
        req = new HttpRequest(newInputStream(content));
        Assert.assertEquals("number=1", req.body[0]);

        content = "GET / HTTP/1.1\r\n\r\nnumber=1\r\nchipotle";
        req = new HttpRequest(newInputStream(content));
        Assert.assertEquals("number=1", req.body[0]);
        Assert.assertEquals("chipotle", req.body[1]);

        content = "GET / HTTP/1.1\r\n\r\nchipotle\r\nnumber=1";
        req = new HttpRequest(newInputStream(content));
        Assert.assertEquals("chipotle", req.body[0]);
        Assert.assertEquals("number=1", req.body[1]);
    }
}
