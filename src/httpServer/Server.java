package httpServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public final int port;
    private final HttpRequestRouter router;
    private final TimedThreadPoolExecutor executioner = new TimedThreadPoolExecutor(100, 30000);

    public Server(int port, HttpRequestRouter router) {
        this.port = port;
        this.router = router;
    }

    public void listen() throws IOException {
        ServerSocket server = new ServerSocket(port);
        while (true)
            executioner.submit(new SocketWorker(server.accept(), router));
    }

    public boolean shutdown() {
        return executioner.shutdown(3000);
    }
}
