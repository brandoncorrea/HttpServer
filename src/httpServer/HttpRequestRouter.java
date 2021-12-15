package httpServer;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestRouter {

    private final Map<String, ControllerBase> controllers = new HashMap<>();

    public void addController(String uri, ControllerBase controller) {
        controllers.put(uri, controller);
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
}
