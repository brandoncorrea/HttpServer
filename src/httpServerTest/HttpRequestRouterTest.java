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
        Map<String, Map<HttpMethod, Function<HttpRequest, HttpResponse>>> router = new HashMap<>();
        Map<HttpMethod, Function<HttpRequest, HttpResponse>> controller = new HashMap<>();
        controller.put(HttpMethod.GET, r -> new HttpResponse(200));
        router.put("/hello", controller);
        HttpRequest req = new HttpRequest("GET /hello HTTP/1.1");
        HttpResponse res = HttpRequestRouter.route(req, router);
        Assert.assertEquals(200, res.statusCode);

        controller = new HashMap<>();
        controller.put(HttpMethod.POST, r -> new HttpResponse(300));
        router.put("/goodbye", controller);
        req = new HttpRequest("POST /goodbye HTTP/1.1");
        res = HttpRequestRouter.route(req, router);
        Assert.assertEquals(300, res.statusCode);
    }

    @Test
    public void routeRespondsWithControllerResponse() {
        HttpRequestRouter router = new HttpRequestRouter();
        testHttpRequestRouter(router, HttpMethod.GET, "/hello", null, HttpStatusCode.NotFound);
        testHttpRequestRouter(router, HttpMethod.HEAD, "/hello", null, HttpStatusCode.NotFound);
        router.addController("/hello", HttpMethod.GET, r -> new HttpResponse(HttpStatusCode.OK, "Hello Content"));
        testHttpRequestRouter(router, HttpMethod.GET, "/hello", "Hello Content", HttpStatusCode.OK);
        testHttpRequestRouter(router, HttpMethod.POST, "/hello", null, HttpStatusCode.MethodNotAllowed);
        router.addController("/goodbye", HttpMethod.GET, r -> new HttpResponse(HttpStatusCode.InternalServerError, "An error message"));
        testHttpRequestRouter(router, HttpMethod.GET, "/goodbye", "An error message", HttpStatusCode.InternalServerError);
    }

    @Test
    public void routeCreatesHttpResponse() {
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController("/multiPurpose", HttpMethod.GET, r -> new HttpResponse(HttpStatusCode.OK, "Multipurpose GET"));
        router.addController("/multiPurpose", HttpMethod.POST, r -> new HttpResponse(HttpStatusCode.Accepted, "Multipurpose POST"));
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
                HttpMethod.POST, r -> new HttpResponse(HttpStatusCode.OK, "Completed POST Request"));
        testHttpRequestRouter(router, HttpMethod.POST, "/postNote", "Completed POST Request", HttpStatusCode.OK);
    }

    @Test
    public void dispatchesToDefaultRouteIfNotFound() {
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController("*", HttpMethod.GET, r -> new HttpResponse(HttpStatusCode.OK, "Default Route"));
        testHttpRequestRouter(router, HttpMethod.GET, "/abcdefg", "Default Route", HttpStatusCode.OK);
    }

    private void testHttpRequestRouter(HttpRequestRouter router, HttpMethod method, String route, String expectedContent, int expectedStatus) {
        HttpRequest req = new HttpRequest(String.format("%s %s HTTP/1.1", method, route));
        HttpResponse res = router.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        if (expectedContent == null) {
            Assert.assertEquals(1, res.headers.size());
            Assert.assertNull(res.content);
            Assert.assertEquals(0, res.contentBytes.length);
        } else {
            Assert.assertEquals(String.valueOf(expectedContent.length()), res.headers.get("Content-Length"));
            Assert.assertEquals(3, res.headers.size());
            Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
            Assert.assertEquals(expectedContent, res.content);
            Assert.assertArrayEquals(expectedContent.getBytes(), res.contentBytes);
        }
        Assert.assertEquals(expectedStatus, res.statusCode);
    }
}
