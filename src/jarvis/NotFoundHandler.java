package jarvis;

import httpServer.ApiHandler;
import httpServer.HttpRequest;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

import java.io.IOException;

public class NotFoundHandler implements ApiHandler {
    private final String filePath;

    public NotFoundHandler(String filePath) {
        this.filePath = filePath;
    }

    public HttpResponse respond(HttpRequest request) {
        try {
            HttpResponse res = new HttpResponse(
                    HttpStatusCode.NotFound,
                    FileHelper.readFile(filePath));
            res.headers.put("Content-Type", "text/html");
            return res;
        } catch (IOException ex) {
            return new HttpResponse(HttpStatusCode.NotFound, "404 - Not Found");
        }
    }
}
