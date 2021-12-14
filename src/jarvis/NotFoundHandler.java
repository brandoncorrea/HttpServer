package jarvis;

import httpServer.ApiHandler;
import httpServer.HttpRequest;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

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
        } catch (Exception ex) {
            return new HttpResponse(HttpStatusCode.NotFound, "404 - Not Found");
        }
    }
}
