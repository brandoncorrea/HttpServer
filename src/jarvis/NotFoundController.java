package jarvis;

import httpServer.*;

import java.io.IOException;

public class NotFoundController implements GetController {
    private final String filePath;

    public NotFoundController(String filePath) { this.filePath = filePath; }

    public HttpResponse get(HttpRequest request) {
        try {
            return FileHelper.fileResponse(HttpStatusCode.NotFound, filePath);
        } catch (IOException ex) {
            return new HttpResponse(HttpStatusCode.NotFound, "404 - Not Found");
        }
    }
}
