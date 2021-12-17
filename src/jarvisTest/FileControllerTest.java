package jarvisTest;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import httpServer.FileHelper;
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
            FileController handler = new FileController(path);
            HttpResponse res = handler.get(null);
            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
            Assert.assertArrayEquals(FileHelper.readFile(path).getBytes(), res.contentBytes);
            Assert.assertEquals("text/html", res.headers.get("Content-Type"));
        }
    }

    @Test
    public void loadsNonHtmlFile() throws IOException {
        String path = "HttpServer.iml";
        FileController handler = new FileController(path);
        HttpResponse res = handler.get(null);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
        Assert.assertArrayEquals(FileHelper.readFile(path).getBytes(), res.contentBytes);
        Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
    }

    @Test
    public void respondFailsToReadFile() {
        FileController handler = new FileController("not/a/file.html");
        HttpResponse res = handler.get(null);
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
        Assert.assertEquals("An error occurred while retrieving the resource.", res.content);
    }
}
