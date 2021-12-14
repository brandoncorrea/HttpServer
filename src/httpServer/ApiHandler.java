package httpServer;

public interface ApiHandler {
    HttpResponse respond(HttpRequest request);
}
