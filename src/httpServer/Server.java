package httpServer;

import java.io.*;
import java.net.ServerSocket;

public class Server {
    public final int port;
    public final String root;
    private final HttpRequestRouter router;
    private final TimedThreadPoolExecutor executioner = new TimedThreadPoolExecutor(100, 30000);

    public Server(int port, String root, HttpRequestRouter router) {
        this.port = port;
        this.root = root;
        this.router = router;
    }

    public void listen() throws IOException {
        ServerSocket server = new ServerSocket(port);
        while (true)
            executioner.submit(new SocketWorker(server.accept(), root, router));
    }

    public boolean shutdown() {
        return executioner.shutdown(3000);
    }
}
