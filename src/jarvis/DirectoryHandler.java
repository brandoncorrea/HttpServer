package jarvis;

import httpServer.ApiHandler;
import httpServer.HttpRequest;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DirectoryHandler implements ApiHandler {
    private final String root;
    public DirectoryHandler(String root) { this.root = root; }

    public HttpResponse respond(HttpRequest request) {
        String uri = request.uri.replaceAll("[/]+$", "");
        String path = root + uri;
        File file = new File(path);
        if (uri.contains(".."))
            return new HttpResponse(HttpStatusCode.Forbidden, "Cannot request parent directory");
        if (!file.exists())
            return new HttpResponse(HttpStatusCode.NotFound, "Path Not Found");
        if (new File(path).isDirectory())
            return buildDirectoryList(uri);
        return tryRequestFile(path);
    }

    private HttpResponse tryRequestFile(String path) {
        try {
            return FileHelper.fileResponse(path);
        } catch (Exception ignored) {
            return new HttpResponse(HttpStatusCode.InternalServerError, "Failed to load file.");
        }
    }

    private HttpResponse buildDirectoryList(String uri) {
        List<String> paths = getDirectories(uri);
        HttpResponse res = new HttpResponse(HttpStatusCode.OK, buildHtmlList(uri, paths));
        res.headers.put("Content-Type", "text/html");
        return res;
    }

    private List<String> getDirectories(String uri) {
        List<String> paths = Arrays.asList(new File(root + uri).list());
        paths.sort(String::compareTo);
        return paths;
    }

    private String buildHtmlList(String uri, List<String> paths) {
        StringBuilder builder = new StringBuilder();
        builder.append("<ul><li><a href=\"" + (Objects.equals(uri, "/") ? "" : uri) + "/..\">..</a></li>\r\n");
        for (String path : paths)
            builder.append("\t<li><a href=\"")
                    .append(uri)
                    .append(Objects.equals(uri, "/") ? "" : "/")
                    .append(path)
                    .append("\">")
                    .append(path)
                    .append("</a></li>\r\n");

        return builder.append("</ul>\r\n").toString();
    }
}
