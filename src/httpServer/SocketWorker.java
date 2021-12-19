package httpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.function.Function;

public class SocketWorker implements Runnable {
    private final Socket socket;
    private final Function<HttpRequest, HttpResponse> router;

    public SocketWorker(Socket socket, Function<HttpRequest, HttpResponse> router) {
        this.socket = socket;
        this.router = router;
    }

    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            HttpRequest request = new HttpRequest(in);
            HttpResponseWriter.write(out, router.apply(request));
            in.close();
            out.close();
        } catch (IOException ex) {
            tryCloseClient(socket);
        }
    }

    private static void tryCloseClient(Socket client) {
        try { client.close(); }
        catch (IOException ignored) { }
    }
}
