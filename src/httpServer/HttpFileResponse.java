package httpServer;

import java.io.IOException;

public class HttpFileResponse extends HttpResponse {

    public HttpFileResponse(String path) throws IOException {
        this(HttpStatusCode.OK, path);
    }

    public HttpFileResponse(int statusCode, String path) throws IOException {
        super(statusCode, FileHelper.readFileBytes(path));
        headers.put("Content-Type", FileHelper.getContentType(path));
    }
}
