package jarvisTest;

import httpServer.ApiHandler;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.FileHelper;
import jarvis.NotFoundHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class NotFoundHandlerTest {

    @Test
    public void newNotFoundHandler() throws IOException {
        String[] paths = {
                "src/resources/notFound.html",
                "src/resources/hello.html"
        };

        for (String path : paths) {
            ApiHandler handler = new NotFoundHandler(path);
            HttpResponse res = handler.respond(null);
            Assert.assertEquals(HttpStatusCode.NotFound, res.statusCode);
            Assert.assertEquals("text/html", res.headers.get("Content-Type"));
            Assert.assertEquals(FileHelper.readFile(path), res.content);
        }
    }

    @Test
    public void plainTextWhenFileFailsToOpen() {
        ApiHandler handler = new NotFoundHandler("fake/file/path.html");
        HttpResponse res = handler.respond(null);
        Assert.assertEquals(HttpStatusCode.NotFound, res.statusCode);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("404 - Not Found", res.content);
    }

}
