package httpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.function.Function;

public class Server {
    public final int port;
    private final Function<HttpRequest, Map<String, Object>> router;
    private final TimedThreadPoolExecutor executioner = new TimedThreadPoolExecutor(100, 30000);

    public Server(int port, Function<HttpRequest, Map<String, Object>> router) {
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
