package httpServer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HttpRequestRouter {

    Map<String, Map<HttpMethod, Function<HttpRequest, Map<String, Object>>>> controllers = new HashMap<>();

    public void addController(String uri, HttpMethod method, Function<HttpRequest, Map<String, Object>> controller) {
        Map<HttpMethod, Function<HttpRequest, Map<String, Object>>> methods = controllers.computeIfAbsent(uri, k -> new HashMap<>());
        methods.put(method, controller);
    }

    public Map<String, Object> route(HttpRequest request) {
        return route(request, controllers);
    }

    public static Map<String, Object> route(HttpRequest request, Map<String, Map<HttpMethod, Function<HttpRequest, Map<String, Object>>>> controllers) {
        String[] uris = { request.uri, "*" };
        for (String uri : uris) {
            Map<HttpMethod, Function<HttpRequest, Map<String, Object>>> controller = controllers.get(uri);
            if (controller == null) continue;
            if (!controller.containsKey(request.method))
                return HttpResponse.create(HttpStatusCode.MethodNotAllowed);

            Map<String, Object> response = controller.get(request.method).apply(request);
            if (request.method == HttpMethod.HEAD)
                response.remove("body");
            return response;
        }

        return HttpResponse.create(HttpStatusCode.NotFound);
    }
}
