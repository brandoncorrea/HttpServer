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
            if (isGetOperation(request, controller))
                return performGetRequest(request, (GetController) controller);
            else if (controller instanceof PostController)
                return ((PostController)controller).post(request);
            return new HttpResponse(HttpStatusCode.MethodNotAllowed);
        }

        return new HttpResponse(HttpStatusCode.NotFound);
    }

    private HttpResponse performGetRequest(HttpRequest request, GetController controller) {
        HttpResponse res = controller.get(request);
        if (request.method == HttpMethod.GET)
            return res;
        return new HttpResponse(res.statusCode, res.headers);
    }

    private boolean isGetOperation(HttpRequest request, ControllerBase controller) {
        boolean isGetMethod = request.method == HttpMethod.GET || request.method == HttpMethod.HEAD;
        return isGetMethod && controller instanceof GetController;
    }
}
