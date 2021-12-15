package httpServer;

public interface GetController extends ControllerBase {
    HttpResponse get(HttpRequest request);
}
