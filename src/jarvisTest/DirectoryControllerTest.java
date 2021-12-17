package jarvisTest;

import httpServer.*;
import jarvis.Configuration;
import jarvis.DirectoryController;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class DirectoryControllerTest {
    private final String indexPath = "src/resources/index.html";
    private final String notFoundPath = "src/resources/notFound.html";
    private HttpRequest newHttpRequest(String uri) {
        return new HttpRequest(
                new ByteArrayInputStream(
                        ("GET " + uri + " HTTP/1.1").getBytes()));
    }

    @Test
    public void newDirectoryHandler() {
        String root = System.getProperty("user.dir");
        Configuration config = new Configuration();
        config.set("DefaultRootDirectory", root);
        config.set("HomePage", indexPath);
        config.set("NotFoundPage", notFoundPath);
        DirectoryController[] controllers = {
                new DirectoryController(root, indexPath, notFoundPath),
                new DirectoryController(config)
        };

        for (DirectoryController controller : controllers) {
            HttpRequest req = newHttpRequest("/");
            HttpResponse res = controller.get(req);

            String expected = "<a href=\"/..\">..</a>\r\n";
            List<String> dirs = Arrays.asList(new File(root).list());
            dirs.sort(String::compareTo);
            for (String f : dirs)
                expected += "\t<a href=\"/" + f + "\">" + f + "</a>\r\n";

            Assert.assertTrue(res.content.contains(expected));
            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
            Assert.assertEquals("text/html", res.headers.get("Content-Type"));
        }
    }

    @Test
    public void newDirectoryHandlerFromSeparateRoot() {
        String root = System.getProperty("user.dir") + "/src";
        DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
        HttpRequest req = newHttpRequest("/");
        HttpResponse res = handler.get(req);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
        Assert.assertFalse(res.content.contains("<a href=\"/src\">"));
    }

    @Test
    public void requestForDirectoryPathResultsInSubdirectory() {
        String[] uris = {"/src", "/src/"};
        for (String uri : uris) {
            String root = System.getProperty("user.dir");
            DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
            HttpRequest req = newHttpRequest(uri);

            HttpResponse res = handler.get(req);
            Assert.assertFalse(res.content.contains("<a href=\"/src\">"));

            String expected = "<a href=\"/src/..\">..</a>\r\n";
            List<String> dirs = Arrays.asList(new File(root + "/src").list());
            dirs.sort(String::compareTo);
            for (String f : dirs)
                expected += "\t<a href=\"/src/" + f + "\">" + f + "</a>\r\n";

            Assert.assertTrue(res.content.contains(expected));
        }
    }

    @Test
    public void cannotRequestParentDirectory() {
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
        String[] uris = {"/..", "..", "/somewhere/over/../the/rainbow"};
        for (String uri : uris) {
            HttpRequest req = newHttpRequest(uri);
            HttpResponse res = handler.get(req);
            Assert.assertEquals(HttpStatusCode.Forbidden, res.statusCode);
            Assert.assertEquals("Cannot request parent directory", res.content);
        }
    }

    @Test
    public void resultsInNotFoundForNonExistentFile() throws IOException {
        String[] paths = {"/not/a/path", "/fake/path.html"};
        byte[] notFoundFile = FileHelper.readFileBytes("src/resources/notFound.html");
        for (String path : paths) {
            Configuration config = new Configuration();
            config.set("DefaultRootDirectory", System.getProperty("user.dir"));
            config.set("HomePage", indexPath);
            config.set("NotFoundPage", notFoundPath);
            DirectoryController[] controllers = {
                    new DirectoryController(System.getProperty("user.dir"), indexPath, notFoundPath),
                    new DirectoryController(config)
            };

            for (DirectoryController controller : controllers) {
                HttpRequest req = newHttpRequest(path);
                HttpResponse res = controller.get(req);
                Assert.assertEquals(HttpStatusCode.NotFound, res.statusCode);
                Assert.assertArrayEquals(notFoundFile, res.contentBytes);
            }
        }
    }

    @Test
    public void fileRequestResultsInFileContent() throws IOException {
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
        HttpRequest req = newHttpRequest("/README.md");
        HttpResponse res = handler.get(req);
        Assert.assertArrayEquals(FileHelper.readFileBytes("README.md"), res.contentBytes);
    }

    @Test
    public void replacesEncodedSpacesInFilePath() throws IOException {
        String path = "/src/resources/public/documents/GOF%20Design%20Patterns.pdf";
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
        HttpRequest req = newHttpRequest(path);
        HttpResponse res = handler.get(req);

        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

        File file = new File(root + path.replace("%20", " "));
        byte[] data = new byte[(int)file.length()];
        new BufferedInputStream(new FileInputStream(file)).read(data);
        Assert.assertArrayEquals(data, res.contentBytes);
    }

    @Test
    public void badHtmlFileReturnsServerError() {
        String root = System.getProperty("user.dir");
        Configuration config = new Configuration();
        config.set("DefaultRootDirectory", root);
        config.set("HomePage", "not/a/path.html");
        DirectoryController[] controllers = {
                new DirectoryController(root, "not/a/path.html", notFoundPath),
                new DirectoryController(config)
        };
        for (DirectoryController controller : controllers) {
            HttpRequest req = newHttpRequest("/");
            HttpResponse res = controller.get(req);
            Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
            Assert.assertEquals("Failed to load resource", res.content);
        }
    }
}
