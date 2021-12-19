package jarvisTest;

import httpServer.FileHelper;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.FileController;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class FileControllerTest {
    @Test
    public void newFileHandler() throws IOException {
        String[] paths = {
                "src/resources/hello.html",
                "src/resources/notFound.html",
                "src/resources/index.html"
        };

        for (String path : paths) {
            HttpResponse res = FileController.get(path);
            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
            Assert.assertArrayEquals(FileHelper.readFileBytes(path), res.contentBytes);
            Assert.assertEquals("text/html", res.headers.get("Content-Type"));
        }
    }

    @Test
    public void loadsNonHtmlFile() throws IOException {
        String path = "HttpServer.iml";
        HttpResponse res = FileController.get(path);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
        Assert.assertArrayEquals(FileHelper.readFileBytes(path), res.contentBytes);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
    }

    @Test
    public void respondFailsToReadFile() {
        HttpResponse res = FileController.get("not/a/file.html");
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
        Assert.assertEquals("An error occurred while retrieving the resource.", res.content);
    }
}
