package jarvis;

import httpServer.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DirectoryController {
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

    public Map<String, Object> get(HttpRequest request) {
        try {
            String uri = request.uri
                    .replaceAll("[/]+$", "")
                    .replace("%20", " ");
            String path = root + uri;
            File file = new File(path);
            if (uri.contains(".."))
                return HttpResponse.create(HttpStatusCode.Forbidden, "Cannot request parent directory");
            if (!file.exists())
                return HttpFileResponse.create(HttpStatusCode.NotFound, notFoundPath);
            if (file.isDirectory())
                return buildDirectoryList(uri);
            return tryRequestFile(path);
        } catch (Exception ignored) {
            return HttpResponse.create(HttpStatusCode.InternalServerError, "Failed to load resource");
        }
    }

    private Map<String, Object> tryRequestFile(String path) {
        try {
            return HttpFileResponse.create(path);
        } catch (Exception ignored) {
            return HttpResponse.create(HttpStatusCode.InternalServerError, "Failed to load file.");
        }
    }

    private Map<String, Object> buildDirectoryList(String uri) throws IOException {
        Map<String, Object> res = HttpResponse.create(HttpStatusCode.OK, buildHtmlContent(uri));
        HttpResponse.headers(res).put("Content-Type", "text/html");
        return res;
    }

    private String buildHtmlContent(String uri) throws IOException {
        return FileHelper
                .readFile(htmlPagePath)
                .replace("{{listings}}", buildListings(uri));
    }

    private String buildListings(String uri) {
        StringBuilder builder = new StringBuilder();
        builder.append(previousDirectoryTag(uri));
        for (String path : getDirectories(uri))
            appendAnchorTag(builder, uri, path);
        return builder.toString();
    }

    private String previousDirectoryTag(String uri) {
        if (Objects.equals(uri, "/")) uri = "";
        return "<a href=\"" + uri + "/..\">..</a>\r\n";
    }

    private List<String> getDirectories(String uri) {
        List<String> paths = Arrays.asList(new File(root + uri).list());
        paths.sort(String::compareTo);
        return paths;
    }

    private void appendAnchorTag(StringBuilder builder, String uri, String path) {
        builder.append("\t<a href=\"")
                .append(uri)
                .append(Objects.equals(uri, "/") ? "" : "/")
                .append(path)
                .append("\">")
                .append(path)
                .append("</a>\r\n");
    }
}
