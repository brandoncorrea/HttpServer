package jarvisTest;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.FileHelper;
import jarvis.NotFoundController;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class NotFoundControllerTest {

    @Test
    public void newNotFoundHandler() throws IOException {
        String[] paths = {
                "src/resources/notFound.html",
                "src/resources/hello.html"
        };

        for (String path : paths) {
            NotFoundController handler = new NotFoundController(path);
            HttpResponse res = handler.get(null);
            Assert.assertEquals(HttpStatusCode.NotFound, res.statusCode);
            Assert.assertEquals("text/html", res.headers.get("Content-Type"));
            Assert.assertEquals(FileHelper.readFile(path), res.content);
        }
    }

    @Test
    public void plainTextWhenFileFailsToOpen() {
        NotFoundController handler = new NotFoundController("fake/file/path.html");
        HttpResponse res = handler.get(null);
        Assert.assertEquals(HttpStatusCode.NotFound, res.statusCode);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
        Assert.assertEquals("404 - Not Found", res.content);
    }

}
