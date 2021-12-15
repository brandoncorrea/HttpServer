package jarvis;

import httpServer.*;

import java.io.IOException;

public class FileController implements GetController {
    private final String filePath;

    public FileController(String filePath) { this.filePath = filePath; }

    public HttpResponse get(HttpRequest request) {
        try {
            return FileHelper.fileResponse(filePath);
        } catch (IOException ex) {
            String content = "An error occurred while retrieving the resource.";
            return new HttpResponse(HttpStatusCode.InternalServerError, content);
        }
    }
}
