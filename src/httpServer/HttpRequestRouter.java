package httpServer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HttpRequestRouter {

    Map<String, Map<HttpMethod, Function<HttpRequest, HttpResponse>>> controllers = new HashMap<>();

    public void addController(String uri, HttpMethod method, Function<HttpRequest, HttpResponse> controller) {
        Map<HttpMethod, Function<HttpRequest, HttpResponse>> methods = controllers.computeIfAbsent(uri, k -> new HashMap<>());
        methods.put(method, controller);
    }

    public HttpResponse route(HttpRequest request) {
        String[] uris = { request.uri, "*" };
        for (String uri : uris) {
            Map<HttpMethod, Function<HttpRequest, HttpResponse>> controller = controllers.get(uri);
            if (controller == null) continue;
            if (!controller.containsKey(request.method))
                return new HttpResponse(HttpStatusCode.MethodNotAllowed);

            HttpResponse response = controller.get(request.method).apply(request);
            if (request.method == HttpMethod.HEAD)
                return removeBody(response);
            return response;
        }

        return new HttpResponse(HttpStatusCode.NotFound);
    }

    private HttpResponse removeBody(HttpResponse response) {
        return new HttpResponse(response.statusCode, response.headers);
    }
}
