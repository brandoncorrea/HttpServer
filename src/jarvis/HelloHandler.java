package jarvis;

import httpServer.ApiHandler;
import httpServer.HttpRequest;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

import java.io.IOException;

public class HelloHandler implements ApiHandler {
    private final String filePath;

    public HelloHandler(String filePath) { this.filePath = filePath; }

    public HttpResponse respond(HttpRequest request) {
        try {
            return FileHelper.fileResponse(filePath);
        } catch (IOException ex) {
            String content = "An error occurred while retrieving the resource.";
            return new HttpResponse(HttpStatusCode.InternalServerError, content);
        }
    }
}
