package httpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketWorker implements Runnable {
    private final Socket socket;
    private final String root;
    private final HttpResponseWriter writer = new HttpResponseWriter();
    private final HttpRequestRouter router;

    public SocketWorker(Socket socket,
                        String root,
                        HttpRequestRouter router) {
        this.socket = socket;
        this.root = root;
        this.router = router;
    }

    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            HttpRequest request = new HttpRequest(in);
            writer.write(out, router.route(request));
            in.close();
            out.close();
        } catch (IOException ex) {
            tryCloseClient(socket);
        }
    }

    private void tryCloseClient(Socket client) {
        try { client.close(); }
        catch (IOException ignored) { }
    }
}
