package jarvis;

import httpServer.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DirectoryController implements GetController {
    private final String root;
    private final String htmlPagePath;
    private final String notFoundPath;

    public DirectoryController(String root, String htmlPage, String notFoundPage) {
        this.root = root;
        htmlPagePath = htmlPage;
        notFoundPath = notFoundPage;
    }

    public DirectoryController(Configuration config) {
        root = config.getString("DefaultRootDirectory");
        htmlPagePath = config.getString("HomePage");
        notFoundPath = config.getString("NotFoundPage");
    }

    public HttpResponse get(HttpRequest request) {
        try {
            String uri = request.uri
                    .replaceAll("[/]+$", "")
                    .replace("%20", " ");
            String path = root + uri;
            File file = new File(path);
            if (uri.contains(".."))
                return new HttpResponse(HttpStatusCode.Forbidden, "Cannot request parent directory");
            if (!file.exists())
                return FileHelper.fileResponse(HttpStatusCode.NotFound, notFoundPath);
            if (file.isDirectory())
                return buildDirectoryList(uri);
            return tryRequestFile(path);
        } catch (Exception ignored) {
            return new HttpResponse(HttpStatusCode.InternalServerError, "Failed to load resource");
        }
    }

    private HttpResponse tryRequestFile(String path) {
        try {
            return FileHelper.fileResponse(path);
        } catch (Exception ignored) {
            return new HttpResponse(HttpStatusCode.InternalServerError, "Failed to load file.");
        }
    }

    private HttpResponse buildDirectoryList(String uri) throws IOException {
        List<String> paths = getDirectories(uri);
        HttpResponse res = new HttpResponse(HttpStatusCode.OK, buildHtmlContent(uri, paths));
        res.headers.put("Content-Type", "text/html");
        return res;
    }

    private List<String> getDirectories(String uri) {
        List<String> paths = Arrays.asList(new File(root + uri).list());
        paths.sort(String::compareTo);
        return paths;
    }

    private String buildHtmlContent(String uri, List<String> paths) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href=\"" + (Objects.equals(uri, "/") ? "" : uri) + "/..\">..</a>\r\n");
        for (String path : paths)
            builder.append("\t<a href=\"")
                    .append(uri)
                    .append(Objects.equals(uri, "/") ? "" : "/")
                    .append(path)
                    .append("\">")
                    .append(path)
                    .append("</a>\r\n");

        return FileHelper
                .readFile(htmlPagePath)
                .replace("{{listings}}", builder.toString());
    }
}
