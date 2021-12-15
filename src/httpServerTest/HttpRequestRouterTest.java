package httpServerTest;

import httpServer.*;
import org.junit.Assert;
import org.junit.Test;

public class HttpRequestRouterTest {

    @Test
    public void routeCreatesHttpResponse() {
        HttpRequest req = new HttpRequest("GET /hello HTTP/1.1");
        HttpRequestRouter router = new HttpRequestRouter();
        HttpResponse res = router.route(req);
        Assert.assertNull(res.content);
        Assert.assertEquals(1, res.headers.size());
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals(HttpStatusCode.NotFound, res.statusCode);

        router.addController("/hello", (GetController) r -> new HttpResponse(HttpStatusCode.OK, "Hello Content"));

        res = router.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("13", res.headers.get("Content-Length"));
        Assert.assertEquals("Hello Content", res.content);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

        req = new HttpRequest("GET /goodbye HTTP/1.1");
        router.addController("/goodbye", (GetController) r -> new HttpResponse(HttpStatusCode.InternalServerError, "An error message"));
        res = router.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("16", res.headers.get("Content-Length"));
        Assert.assertEquals("An error message", res.content);
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);

        req = new HttpRequest("GET /abcdefg HTTP/1.1");
        router.addController("*", (GetController) r -> new HttpResponse(HttpStatusCode.OK, "Default Route"));
        res = router.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("13", res.headers.get("Content-Length"));
        Assert.assertEquals("Default Route", res.content);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

        req = new HttpRequest("POST /abcdefg HTTP/1.1");
        res = router.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertNull(res.content);
        Assert.assertEquals(HttpStatusCode.MethodNotAllowed, res.statusCode);

        router.addController("/postNote",
                (PostController) r -> new HttpResponse(HttpStatusCode.OK, "Completed POST Request"));
        req = new HttpRequest("POST /postNote HTTP/1.1");
        res = router.route(req);
        Assert.assertNotNull(res.headers.get("Date"));
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("22", res.headers.get("Content-Length"));
        Assert.assertEquals("Completed POST Request", res.content);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

        class multiPurposeController implements PostController, GetController {
            public HttpResponse get(HttpRequest request)
            { return new HttpResponse(HttpStatusCode.OK, "Multipurpose GET"); }
            public HttpResponse post(HttpRequest request)
            { return new HttpResponse(HttpStatusCode.Accepted, "Multipurpose POST"); }
        }

        router.addController("/multiPurpose", new multiPurposeController());
        req = new HttpRequest("POST /multiPurpose HTTP/1.1");
        res = router.route(req);
        Assert.assertEquals("Multipurpose POST", res.content);
        Assert.assertEquals(HttpStatusCode.Accepted, res.statusCode);

        req = new HttpRequest("GET /multiPurpose HTTP/1.1");
        res = router.route(req);
        Assert.assertEquals("Multipurpose GET", res.content);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
    }
}
