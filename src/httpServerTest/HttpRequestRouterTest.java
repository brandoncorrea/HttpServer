package httpServerTest;

import httpServer.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HttpRequestRouterTest {

    @Test
    public void routesUsingMapOfControllers() {
        Map<String, Map<HttpMethod, Function<HttpRequest, Map<String, Object>>>> router = new HashMap<>();
        Map<HttpMethod, Function<HttpRequest, Map<String, Object>>> controller = new HashMap<>();
        controller.put(HttpMethod.GET, r -> HttpResponse.create(200));
        router.put("/hello", controller);
        HttpRequest req = new HttpRequest("GET /hello HTTP/1.1");
        Map<String, Object> res = HttpRequestRouter.route(req, router);
        Assert.assertEquals(200, res.get("status"));

        controller = new HashMap<>();
        controller.put(HttpMethod.POST, r -> HttpResponse.create(300));
        router.put("/goodbye", controller);
        req = new HttpRequest("POST /goodbye HTTP/1.1");
        res = HttpRequestRouter.route(req, router);
        Assert.assertEquals(300, res.get("status"));
    }

    @Test
    public void routeRespondsWithControllerResponse() {
        HttpRequestRouter router = new HttpRequestRouter();
        testHttpRequestRouter(router, HttpMethod.GET, "/hello", null, HttpStatusCode.NotFound);
        testHttpRequestRouter(router, HttpMethod.HEAD, "/hello", null, HttpStatusCode.NotFound);
        router.addController("/hello", HttpMethod.GET, r -> HttpResponse.create(HttpStatusCode.OK, "Hello Content"));
        testHttpRequestRouter(router, HttpMethod.GET, "/hello", "Hello Content", HttpStatusCode.OK);
        testHttpRequestRouter(router, HttpMethod.POST, "/hello", null, HttpStatusCode.MethodNotAllowed);
        router.addController("/goodbye", HttpMethod.GET, r -> HttpResponse.create(HttpStatusCode.InternalServerError, "An error message"));
        testHttpRequestRouter(router, HttpMethod.GET, "/goodbye", "An error message", HttpStatusCode.InternalServerError);
    }

    @Test
    public void routeCreatesHttpResponse() {
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController("/multiPurpose", HttpMethod.GET, r -> HttpResponse.create(HttpStatusCode.OK, "Multipurpose GET"));
        router.addController("/multiPurpose", HttpMethod.POST, r -> HttpResponse.create(HttpStatusCode.Accepted, "Multipurpose POST"));
        testHttpRequestRouter(
                router,
                HttpMethod.POST,
                "/multiPurpose",
                "Multipurpose POST",
                HttpStatusCode.Accepted);
        testHttpRequestRouter(
                router,
                HttpMethod.GET,
                "/multiPurpose",
                "Multipurpose GET",
                HttpStatusCode.OK);
    }

    @Test
    public void performsPostRequest() {
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController("/postNote",
                HttpMethod.POST, r -> HttpResponse.create(HttpStatusCode.OK, "Completed POST Request"));
        testHttpRequestRouter(router, HttpMethod.POST, "/postNote", "Completed POST Request", HttpStatusCode.OK);
    }

    @Test
    public void dispatchesToDefaultRouteIfNotFound() {
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController("*", HttpMethod.GET, r -> HttpResponse.create(HttpStatusCode.OK, "Default Route"));
        testHttpRequestRouter(router, HttpMethod.GET, "/abcdefg", "Default Route", HttpStatusCode.OK);
    }

    private void testHttpRequestRouter(HttpRequestRouter router, HttpMethod method, String route, String expectedContent, int expectedStatus) {
        HttpRequest req = new HttpRequest(String.format("%s %s HTTP/1.1", method, route));
        Map<String, Object> res = router.route(req);
        Assert.assertNotNull(HttpResponse.headers(res).get("Date"));
        if (expectedContent == null) {
            Assert.assertEquals(1, HttpResponse.headers(res).size());
            Assert.assertNull(res.get("body"));
        } else {
            Assert.assertEquals(String.valueOf(expectedContent.length()), HttpResponse.headers(res).get("Content-Length"));
            Assert.assertEquals(3, HttpResponse.headers(res).size());
            Assert.assertEquals("text/plain", HttpResponse.headers(res).get("Content-Type"));
            Assert.assertArrayEquals(expectedContent.getBytes(), HttpResponse.body(res));
        }
        Assert.assertEquals(expectedStatus, res.get("status"));
    }
}
