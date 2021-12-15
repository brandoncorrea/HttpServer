package httpServer;

public interface PostController extends ControllerBase {
    HttpResponse post(HttpRequest request);
}
