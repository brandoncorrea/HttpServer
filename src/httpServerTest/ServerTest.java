package httpServerTest;

import httpServer.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

public class ServerTest {
    private HttpRequest newHttpRequest(HttpMethod method, String uri) {
        String content = String.format("%s %s HTTP/1.1", method, uri);
        ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
        return new HttpRequest(stream);
    }

    @Test
    public void newServer() {
        Server server = new Server(80);
        Assert.assertEquals(80, server.port);
    }

    @Test
    public void routeCreatesHttpResponse() {
        HttpRequest req = newHttpRequest(HttpMethod.GET, "/hello");
        Server server = new Server(80);
        HttpResponse res = server.route(req);
        Assert.assertNull(res.content);
        Assert.assertEquals(1, res.headers.size());
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals(HttpStatusCode.NotFound, res.statusCode);

        server.addController("/hello", (GetController) r -> new HttpResponse(HttpStatusCode.OK, "Hello Content"));

        res = server.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("13", res.headers.get("Content-Length"));
        Assert.assertEquals("Hello Content", res.content);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

        req = newHttpRequest(HttpMethod.GET, "/goodbye");
        server.addController("/goodbye", (GetController) r -> new HttpResponse(HttpStatusCode.InternalServerError, "An error message"));
        res = server.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("16", res.headers.get("Content-Length"));
        Assert.assertEquals("An error message", res.content);
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);

        req = newHttpRequest(HttpMethod.GET, "/abcdefg");
        server.addController("*", (GetController) r -> new HttpResponse(HttpStatusCode.OK, "Default Route"));
        res = server.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("13", res.headers.get("Content-Length"));
        Assert.assertEquals("Default Route", res.content);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

        req = newHttpRequest(HttpMethod.POST, "/abcdefg");
        res = server.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertNull(res.content);
        Assert.assertEquals(HttpStatusCode.MethodNotAllowed, res.statusCode);

        server.addController("/postNote",
                (PostController) r -> new HttpResponse(HttpStatusCode.OK, "Completed POST Request"));
        req = newHttpRequest(HttpMethod.POST, "/postNote");
        res = server.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("22", res.headers.get("Content-Length"));
        Assert.assertEquals("Completed POST Request", res.content);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

        class multiPurposeController implements PostController, GetController {
            public HttpResponse get(HttpRequest request)
            { return new HttpResponse(HttpStatusCode.OK, "Multipurpose GET"); }
            public HttpResponse post(HttpRequest request)
            { return new HttpResponse(HttpStatusCode.Accepted, "Multipurpose POST"); }
        }

        server.addController("/multiPurpose", new multiPurposeController());
        req = newHttpRequest(HttpMethod.POST, "/multiPurpose");
        res = server.route(req);
        Assert.assertEquals("Multipurpose POST", res.content);
        Assert.assertEquals(HttpStatusCode.Accepted, res.statusCode);

        req = newHttpRequest(HttpMethod.GET, "/multiPurpose");
        res = server.route(req);
        Assert.assertEquals("Multipurpose GET", res.content);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
    }

    @Test
    public void socketClosesOnIOException() {
        // Throws on Output Stream
        Socket socket = new Socket() {
            private boolean open = true;
            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException();
            }
            @Override
            public void close() { open = false; }
            @Override
            public boolean isClosed() { return !open; }
        };

        Server server = new Server(80);
        server.processRequest(socket);
        Assert.assertTrue(socket.isClosed());

        // Throws on Output Stream
        socket = new Socket() {
            private boolean open = true;
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(new byte[0]);
            }
            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new IOException();
            }
            @Override
            public void close() { open = false; }
            @Override
            public boolean isClosed() { return !open; }
        };

        server = new Server(80);
        server.processRequest(socket);
        Assert.assertTrue(socket.isClosed());
    }

    @Test
    public void processRequestWritesResponseToOutput() throws IOException {
        Server server = new Server(80);
        server.addController("/hello", (GetController) r -> new HttpResponse(HttpStatusCode.OK, "Hello!"));
        Socket socket = new Socket() {
            private final InputStream in = new ByteArrayInputStream(
                    "GET /hello HTTP/1.1".getBytes());
            private final OutputStream out = new ByteArrayOutputStream();
            private boolean open = true;

            @Override
            public InputStream getInputStream() { return in; }
            @Override
            public OutputStream getOutputStream() { return out; }
            @Override
            public boolean isClosed() { return !open; }
            @Override
            public void close() { open = false; }
        };

        server.processRequest(socket);
        ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
        String output = out.toString();
        Assert.assertFalse(socket.isClosed());
        Assert.assertEquals("HTTP/1.1 200 OK\r\n", output.substring(0, 17));
        Assert.assertEquals("\r\n\r\nHello!\r\n", output.substring(output.length() - 12));
    }
}
