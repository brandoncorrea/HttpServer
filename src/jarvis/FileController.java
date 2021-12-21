package jarvis;

import httpServer.*;

import java.io.IOException;
import java.util.Map;

public class FileController {

    public static Map<String, Object> get(String filePath) {
        try {
            return HttpFileResponse.create(filePath);
        } catch (IOException ex) {
            String content = "An error occurred while retrieving the resource.";
            return HttpResponse.create(HttpStatusCode.InternalServerError, content);
        }
    }
}
