package jarvisTest;

import httpServer.*;
import jarvis.Configuration;
import jarvis.DirectoryController;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            Map<String, Object> res = controller.get(req);

            String expected = "<a href=\"/..\">..</a>\r\n";
            List<String> dirs = Arrays.asList(new File(root).list());
            dirs.sort(String::compareTo);
            for (String f : dirs)
                expected += "\t<a href=\"/" + f + "\">" + f + "</a>\r\n";

            Assert.assertTrue(new String(HttpResponse.body(res)).contains(expected));
            Assert.assertEquals(HttpStatusCode.OK, res.get("status"));
            Assert.assertEquals("text/html", HttpResponse.headers(res).get("Content-Type"));
        }
    }

    @Test
    public void newDirectoryHandlerFromSeparateRoot() {
        String root = System.getProperty("user.dir") + "/src";
        DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
        HttpRequest req = newHttpRequest("/");
        Map<String, Object> res = handler.get(req);
        Assert.assertEquals(HttpStatusCode.OK, res.get("status"));
        Assert.assertFalse(new String(HttpResponse.body(res)).contains("<a href=\"/src\">"));
    }

    @Test
    public void requestForDirectoryPathResultsInSubdirectory() {
        String[] uris = {"/src", "/src/"};
        for (String uri : uris) {
            String root = System.getProperty("user.dir");
            DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
            HttpRequest req = newHttpRequest(uri);

            Map<String, Object> res = handler.get(req);
            Assert.assertFalse(Arrays.toString(HttpResponse.body(res)).contains("<a href=\"/src\">"));

            String expected = "<a href=\"/src/..\">..</a>\r\n";
            List<String> dirs = Arrays.asList(new File(root + "/src").list());
            dirs.sort(String::compareTo);
            for (String f : dirs)
                expected += "\t<a href=\"/src/" + f + "\">" + f + "</a>\r\n";

            Assert.assertTrue(new String(HttpResponse.body(res)).contains(expected));
        }
    }

    @Test
    public void cannotRequestParentDirectory() {
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
        String[] uris = {"/..", "..", "/somewhere/over/../the/rainbow"};
        for (String uri : uris) {
            HttpRequest req = newHttpRequest(uri);
            Map<String, Object> res = handler.get(req);
            Assert.assertEquals(HttpStatusCode.Forbidden, res.get("status"));
            Assert.assertArrayEquals("Cannot request parent directory".getBytes(), HttpResponse.body(res));
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
                Map<String, Object> res = controller.get(req);
                Assert.assertEquals(HttpStatusCode.NotFound, res.get("status"));
                Assert.assertArrayEquals(notFoundFile, HttpResponse.body(res));
            }
        }
    }

    @Test
    public void fileRequestResultsInFileContent() throws IOException {
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
        HttpRequest req = newHttpRequest("/README.md");
        Map<String, Object> res = handler.get(req);
        Assert.assertArrayEquals(FileHelper.readFileBytes("README.md"), HttpResponse.body(res));
    }

    @Test
    public void replacesEncodedSpacesInFilePath() throws IOException {
        String path = "/src/resources/public/documents/GOF%20Design%20Patterns.pdf";
        String root = System.getProperty("user.dir");
        DirectoryController handler = new DirectoryController(root, indexPath, notFoundPath);
        HttpRequest req = newHttpRequest(path);
        Map<String, Object> res = handler.get(req);

        Assert.assertEquals(HttpStatusCode.OK, res.get("status"));

        File file = new File(root + path.replace("%20", " "));
        byte[] data = new byte[(int)file.length()];
        new BufferedInputStream(new FileInputStream(file)).read(data);
        Assert.assertArrayEquals(data, HttpResponse.body(res));
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
            Map<String, Object> res = controller.get(req);
            Assert.assertEquals(HttpStatusCode.InternalServerError, res.get("status"));
            Assert.assertArrayEquals("Failed to load resource".getBytes(), HttpResponse.body(res));
        }
    }
}
