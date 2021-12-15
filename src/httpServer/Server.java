package httpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final HttpResponseWriter httpWriter = new HttpResponseWriter();
    public final int port;
    private final Map<String, ControllerBase> controllers = new HashMap<>();

    public Server(int port) { this.port = port; }

    public void listen() throws IOException {
        ServerSocket server = new ServerSocket(port);
        while (true) processRequest(server.accept());
    }

    public void addController(String uri, ControllerBase controller) {
        controllers.put(uri, controller);
    }

    public void processRequest(Socket client) {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            HttpRequest request = new HttpRequest(in);
            httpWriter.write(out, route(request));
            in.close();
            out.close();
        } catch (IOException ex) {
            tryCloseClient(client);
        }
    }

    public HttpResponse route(HttpRequest request) {
        String[] keys = { request.uri, "*" };
        for (String key : keys) {
            ControllerBase controller = controllers.get(key);
            if (controller == null) continue;
            if (request.method == HttpMethod.GET && controller instanceof GetController)
                return ((GetController)controller).get(request);
            else if (controller instanceof PostController)
                return ((PostController)controller).post(request);
            return new HttpResponse(HttpStatusCode.MethodNotAllowed);
        }

        return new HttpResponse(HttpStatusCode.NotFound);
    }

    private void tryCloseClient(Socket client) {
        try { client.close(); }
        catch (IOException ignored) { }
    }
}
