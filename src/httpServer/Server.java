package httpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final HttpResponseWriter httpWriter = new HttpResponseWriter();
    public final int port;
    private final Map<String, Map<HttpMethod, ApiHandler>> routes = new HashMap<>();

    public Server(int port) { this.port = port; }

    public void listen() throws IOException {
        ServerSocket server = new ServerSocket(port);
        while (true) processRequest(server.accept());
    }

    public void addRoute(String uri, HttpMethod method, ApiHandler handler) {
        Map<HttpMethod, ApiHandler> route = routes.computeIfAbsent(uri, k -> new HashMap<>());
        route.put(method, handler);
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
            Map<HttpMethod, ApiHandler> route = routes.get(key);
            if (route == null) continue;
            ApiHandler handler = route.get(request.method);
            if (handler == null)
                return new HttpResponse(HttpStatusCode.MethodNotAllowed);
            return handler.respond(request);
        }

        return new HttpResponse(HttpStatusCode.NotFound);
    }

    private void tryCloseClient(Socket client) {
        try { client.close(); }
        catch (IOException ignored) { }
    }
}
