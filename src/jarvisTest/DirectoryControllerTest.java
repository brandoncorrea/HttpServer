package jarvisTest;

import httpServer.*;
import jarvis.DirectoryController;
import jarvis.FileHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DirectoryControllerTest {
    private final String indexPath = "src/resources/index.html";
    private HttpRequest newHttpRequest(String uri) {
        return new HttpRequest(
                new ByteArrayInputStream(
                        ("GET " + uri + " HTTP/1.1").getBytes()));
    }

    @Test
    public void newDirectoryHandler() {
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath);

        HttpRequest req = newHttpRequest("/");
        HttpResponse res = handler.get(req);

        String expected = "<ul><li><a href=\"/..\">..</a></li>\r\n";
        List<String> dirs = Arrays.asList(new File(root).list());
        dirs.sort(String::compareTo);
        for (String f : dirs)
            expected += "\t<li><a href=\"/" + f + "\">" + f + "</a></li>\r\n";
        expected += "</ul>\r\n";

        Assert.assertTrue(res.content.contains(expected));
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
        Assert.assertEquals("text/html", res.headers.get("Content-Type"));
    }

    @Test
    public void newDirectoryHandlerFromSeparateRoot() {
        String root = System.getProperty("user.dir") + "src";
        DirectoryController handler = new DirectoryController(root, indexPath);
        HttpRequest req = newHttpRequest("/");
        HttpResponse res = handler.get(req);
        Assert.assertFalse(res.content.contains("<a href=\"/src\">"));
    }

    @Test
    public void requestForDirectoryPathResultsInSubdirectory() {
        String[] uris = {"/src", "/src/"};
        for (String uri : uris) {
            String root = System.getProperty("user.dir");
            DirectoryController handler = new DirectoryController(root, indexPath);
            HttpRequest req = newHttpRequest(uri);

            HttpResponse res = handler.get(req);
            Assert.assertFalse(res.content.contains("<a href=\"/src\">"));

            String expected = "<ul><li><a href=\"/src/..\">..</a></li>\r\n";
            List<String> dirs = Arrays.asList(new File(root + "/src").list());
            dirs.sort(String::compareTo);
            for (String f : dirs)
                expected += "\t<li><a href=\"/src/" + f + "\">" + f + "</a></li>\r\n";
            expected += "</ul>\r\n";

            Assert.assertTrue(res.content.contains(expected));
        }
    }

    @Test
    public void cannotRequestParentDirectory() {
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath);
        String[] uris = {"/..", "..", "/somewhere/over/../the/rainbow"};
        for (String uri : uris) {
            HttpRequest req = newHttpRequest(uri);
            HttpResponse res = handler.get(req);
            Assert.assertEquals(HttpStatusCode.Forbidden, res.statusCode);
            Assert.assertEquals("Cannot request parent directory", res.content);
        }
    }

    @Test
    public void resultsInNotFoundForNonExistentFile() {
        String[] paths = {"/not/a/path", "/fake/path.html"};
        for (String path : paths) {
            DirectoryController handler = new DirectoryController(System.getProperty("user.dir"), indexPath);
            HttpRequest req = newHttpRequest(path);
            HttpResponse res = handler.get(req);
            Assert.assertEquals(HttpStatusCode.NotFound, res.statusCode);
            Assert.assertTrue(res.content.contains("Path Not Found"));
        }
    }

    @Test
    public void fileRequestResultsInFileContent() throws IOException {
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath);
        HttpRequest req = newHttpRequest("/README.md");
        HttpResponse res = handler.get(req);
        Assert.assertEquals(FileHelper.readFile("README.md"), res.content);
    }

    @Test
    public void badHtmlFileReturnsServerError() {
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, "not/a/path.html");
        HttpRequest req = newHttpRequest("/");
        HttpResponse res = handler.get(req);
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
        Assert.assertEquals("Failed to load resource", res.content);
    }
}
