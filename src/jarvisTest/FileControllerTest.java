package jarvisTest;

import httpServer.FileHelper;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.FileController;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileControllerTest {
    @Test
    public void newFileHandler() throws IOException {
        String[] paths = {
                "src/resources/hello.html",
                "src/resources/notFound.html",
                "src/resources/index.html"
        };

        for (String path : paths) {
            Map<String, Object> res = FileController.get(path);
            Assert.assertEquals(HttpStatusCode.OK, res.get("status"));
            Assert.assertArrayEquals(FileHelper.readFileBytes(path), HttpResponse.body(res));
            Assert.assertEquals("text/html", HttpResponse.headers(res).get("Content-Type"));
        }
    }

    @Test
    public void loadsNonHtmlFile() throws IOException {
        String path = "HttpServer.iml";
        Map<String, Object> res = FileController.get(path);
        Assert.assertEquals(HttpStatusCode.OK, res.get("status"));
        Assert.assertArrayEquals(FileHelper.readFileBytes(path), HttpResponse.body(res));
        Assert.assertEquals("text/plain", HttpResponse.headers(res).get("Content-Type"));
    }

    @Test
    public void respondFailsToReadFile() {
        Map<String, Object> res = FileController.get("not/a/file.html");
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.get("status"));
        Assert.assertArrayEquals("An error occurred while retrieving the resource.".getBytes(), HttpResponse.body(res));
    }
}
