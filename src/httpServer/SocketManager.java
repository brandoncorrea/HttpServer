package httpServer;

import java.net.Socket;

public class SocketManager {
    private final HttpResponseWriter writer = new HttpResponseWriter();
    private final HttpRequestRouter router;

    public SocketManager(HttpRequestRouter router) {
        this.router = router;
    }

    public void enqueueSocket(Socket socket) {
        new SocketWorker(socket, router, writer).run();
    }
}
