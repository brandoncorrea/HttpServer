package httpServerTest;

import httpServer.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

public class SocketWorkerTest {

    @Test
    public void socketClosesOnIOException() {
        // Throws on Output Stream
        Socket socket = new Socket() {
            private boolean open = true;
            public InputStream getInputStream() throws IOException {
                throw new IOException();
            }
            public void close() { open = false; }
            public boolean isClosed() { return !open; }
        };

        new SocketWorker(socket,new HttpRequestRouter()).run();
        Assert.assertTrue(socket.isClosed());

        // Throws on Output Stream
        socket = new Socket() {
            private boolean open = true;
            public InputStream getInputStream() {
                return new ByteArrayInputStream(new byte[0]);
            }
            public OutputStream getOutputStream() throws IOException {
                throw new IOException();
            }
            public void close() { open = false; }
            public boolean isClosed() { return !open; }
        };

        new SocketWorker(socket, new HttpRequestRouter()).run();
        Assert.assertTrue(socket.isClosed());
    }

    @Test
    public void processRequestWritesResponseToOutput() throws IOException {
        Socket socket = new Socket() {
            private final InputStream in = new ByteArrayInputStream(
                    "GET /hello HTTP/1.1".getBytes());
            private final OutputStream out = new ByteArrayOutputStream();
            private boolean open = true;

            public InputStream getInputStream() { return in; }
            public OutputStream getOutputStream() { return out; }
            public boolean isClosed() { return !open; }
            public void close() { open = false; }
        };

        HttpRequestRouter router = new HttpRequestRouter();
        router.addController("/hello", HttpMethod.GET, r -> new HttpResponse(HttpStatusCode.OK, "Hello!"));
        new SocketWorker(socket, router).run();

        ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
        String output = out.toString();
        Assert.assertFalse(socket.isClosed());
        Assert.assertEquals("HTTP/1.1 200 OK\r\n", output.substring(0, 17));
        Assert.assertEquals("\r\n\r\nHello!", output.substring(output.length() - 10));
    }
}
