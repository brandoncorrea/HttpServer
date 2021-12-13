package httpServerTest;

import httpServer.HttpMethod;
import httpServer.HttpRequest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class HttpRequestTest {

    private InputStream newInputStream(String content) {
        return new InputStream() {
            private final byte[] bytes = content.getBytes();
            private int index = 0;

            @Override
            public int read() throws IOException {
                if (index >= bytes.length) return -1;
                return bytes[index++];
            }
        };
    }

    @Test
    public void newHttpRequestWithMissingHeaderInfo() {
        InputStream stream = newInputStream("");
        HttpRequest request = new HttpRequest(stream);
        Assert.assertNull(request.method);
        Assert.assertNull(request.uri);
        Assert.assertNull(request.protocol);
        Assert.assertNull(request.userAgent);
        Assert.assertNull(request.host);
        Assert.assertNull(request.acceptLanguage);

        stream = newInputStream("GET");
        request = new HttpRequest(stream);
        Assert.assertEquals(HttpMethod.GET, request.method);
        Assert.assertNull(request.uri);
        Assert.assertNull(request.protocol);
        Assert.assertNull(request.userAgent);
        Assert.assertNull(request.host);
        Assert.assertNull(request.acceptLanguage);

        stream = newInputStream("GET /hellos");
        request = new HttpRequest(stream);
        Assert.assertEquals(HttpMethod.GET, request.method);
        Assert.assertEquals("/hellos", request.uri);
        Assert.assertNull(request.protocol);
        Assert.assertNull(request.userAgent);
        Assert.assertNull(request.host);
        Assert.assertNull(request.acceptLanguage);
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
        String[][] headers = {
                {"GET", "/hello.txt", "HTTP/1.1"},
                {"POST", "/Heyo/goodbye.pdf", "HTTP/2.0"}
        };

        for (String[] header : headers) {
            InputStream stream = newInputStream(header[0] + " " + header[1] + " " + header[2]);
            HttpRequest request = new HttpRequest(stream);
            Assert.assertEquals(HttpMethod.valueOf(header[0]), request.method);
            Assert.assertEquals(header[1], request.uri);
            Assert.assertEquals(header[2], request.protocol);
            Assert.assertNull(request.userAgent);
            Assert.assertNull(request.host);
            Assert.assertNull(request.acceptLanguage);
        }
    }

    @Test
    public void requestContainsUserAgent() {
        String[] agents = {
                "curl/7.16.3 libcurl/7.16.3 OpenSSL/0.9.7l zlib/1.2.3",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15"
        };

        String header = "GET / HTTP/1.1\r\n";
        for (String agent : agents) {
            InputStream stream = newInputStream(header + "User-Agent: " + agent);
            HttpRequest request = new HttpRequest(stream);
            Assert.assertEquals(HttpMethod.valueOf("GET"), request.method);
            Assert.assertEquals("/", request.uri);
            Assert.assertEquals("HTTP/1.1", request.protocol);
            Assert.assertEquals(agent, request.userAgent);
            Assert.assertNull(request.host);
            Assert.assertNull(request.acceptLanguage);
        }
    }

    @Test
    public void requestContainsHost() {
        String[] hosts = {
                "www.example.com",
                "google.com"
        };

        String header = "GET / HTTP/1.1\r\n";
        for (String host : hosts) {
            InputStream stream = newInputStream(header + "Host: " + host);
            HttpRequest request = new HttpRequest(stream);
            Assert.assertEquals(HttpMethod.valueOf("GET"), request.method);
            Assert.assertEquals("/", request.uri);
            Assert.assertEquals("HTTP/1.1", request.protocol);
            Assert.assertEquals(host, request.host);
            Assert.assertNull(request.userAgent);
            Assert.assertNull(request.acceptLanguage);
        }
    }

    @Test
    public void requestContainsAcceptLanguage() {
        String[] langs = {
                "en, mi",
                "en, du",
                "wookie"
        };

        String header = "GET / HTTP/1.1\r\n";
        for (String lang : langs) {
            InputStream stream = newInputStream(header + "Accept-Language: " + lang);
            HttpRequest request = new HttpRequest(stream);
            Assert.assertEquals(HttpMethod.valueOf("GET"), request.method);
            Assert.assertEquals("/", request.uri);
            Assert.assertEquals("HTTP/1.1", request.protocol);
            Assert.assertNull(request.host);
            Assert.assertNull(request.userAgent);
            Assert.assertEquals(lang, request.acceptLanguage);
        }
    }
}
