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
            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException();
            }
            @Override
            public void close() { open = false; }
            @Override
            public boolean isClosed() { return !open; }
        };

        new SocketWorker(socket, new HttpRequestRouter(), new HttpResponseWriter()).run();
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

        new SocketWorker(socket, new HttpRequestRouter(), new HttpResponseWriter()).run();
        Assert.assertTrue(socket.isClosed());
    }

    @Test
    public void processRequestWritesResponseToOutput() throws IOException {
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

        HttpRequestRouter router = new HttpRequestRouter();
        router.addController("/hello", (GetController) r -> new HttpResponse(HttpStatusCode.OK, "Hello!"));
        new SocketWorker(socket, router, new HttpResponseWriter()).run();

        ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
        String output = out.toString();
        Assert.assertFalse(socket.isClosed());
        Assert.assertEquals("HTTP/1.1 200 OK\r\n", output.substring(0, 17));
        Assert.assertEquals("\r\n\r\nHello!\r\n", output.substring(output.length() - 12));
    }
}
