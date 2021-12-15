package httpServer;

import java.io.*;
import java.net.ServerSocket;

public class Server {
    public final int port;
    private final SocketManager manager;

    public Server(int port, HttpRequestRouter router) {
        this.port = port;
        this.manager = new SocketManager(router);
    }

    public void listen() throws IOException {
        ServerSocket server = new ServerSocket(port);
        while (true) manager.enqueueSocket(server.accept());
    }
}
