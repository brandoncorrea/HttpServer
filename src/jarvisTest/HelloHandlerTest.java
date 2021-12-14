package jarvisTest;

import httpServer.ApiHandler;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.FileHelper;
import jarvis.HelloHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class HelloHandlerTest {
    @Test
    public void newHelloHandler() throws IOException {
        String[] paths = {
                "src/resources/hello.html",
                "src/resources/notFound.html"
        };

        for (String path : paths) {
            ApiHandler handler = new HelloHandler(path);
            HttpResponse res = handler.respond(null);
            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
            Assert.assertEquals(FileHelper.readFile(path), res.content);
            Assert.assertEquals("text/html", res.headers.get("Content-Type"));
        }
    }

    @Test
    public void respondFailsToReadFile() {
        ApiHandler handler = new HelloHandler("not/a/file.html");
        HttpResponse res = handler.respond(null);
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
        Assert.assertEquals("An error occurred while retrieving the resource.", res.content);
    }
}
