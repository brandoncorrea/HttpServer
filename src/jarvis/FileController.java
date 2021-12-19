package jarvis;

import httpServer.*;

import java.io.IOException;

public class FileController {

    public static HttpResponse get(String filePath) {
        try {
            return new HttpFileResponse(filePath);
        } catch (IOException ex) {
            String content = "An error occurred while retrieving the resource.";
            return new HttpResponse(HttpStatusCode.InternalServerError, content);
        }
    }
}
