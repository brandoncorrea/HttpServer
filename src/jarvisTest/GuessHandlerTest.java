package jarvisTest;

import httpServer.ApiHandler;
import httpServer.HttpRequest;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.GuessHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class GuessHandlerTest {
    ApiHandler handler = new GuessHandler("src/resources/guess.html");

    @Test
    public void generatesRandomSessionIdGuids() {
        HttpRequest req = new HttpRequest("GET / HTTP/1.1");
        HttpResponse res = handler.respond(req);
        String guid1 = res.headers.get("Set-Cookie").substring(11);
        res = handler.respond(req);
        String guid2 = res.headers.get("Set-Cookie").substring(11);
        Assert.assertNotEquals(guid1, guid2);
        Assert.assertFalse(guid1.isEmpty());
        Assert.assertFalse(guid2.isEmpty());

        UUID.fromString(guid1);
        UUID.fromString(guid2);
    }

    @Test
    public void doesNotSetCookieIfAlreadySet() {
        HttpRequest req = new HttpRequest("GET / HTTP/1.1");
        HttpResponse res = handler.respond(req);
        String sessionId = res.headers.get("Set-Cookie").substring(11);

        req = new HttpRequest("GET / HTTP/1.1\r\nCookie: session_id=" + sessionId);
        res = handler.respond(req);
        Assert.assertNull(res.headers.get("Set-Cookie"));

        req = new HttpRequest("GET / HTTP/1.1\r\nCookie: bleh=" + sessionId);
        res = handler.respond(req);
        Assert.assertNotNull(res.headers.get("Set-Cookie"));
        Assert.assertNotEquals(sessionId, res.headers.get("Set-Cookie").substring(11));
    }

    @Test
    public void serverErrorWhenFileNotFound() {
        ApiHandler handler = new GuessHandler("not/a/path.html");
        HttpResponse res = handler.respond(new HttpRequest("GET / HTTP/1.1"));
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
        Assert.assertEquals("Failed to load resource", res.content);
    }
}
