package httpServer;

import java.io.IOException;
import java.util.Map;

public final class HttpFileResponse {

    public static Map<String, Object> create(String path) throws IOException {
        return create(HttpStatusCode.OK, path);
    }

    public static Map<String, Object> create(int status, String path) throws IOException {
        Map<String, Object> res = HttpResponse.create(status, FileHelper.readFileBytes(path));
        HttpResponse.headers(res).put("Content-Type", FileHelper.getContentType(path));
        return res;
    }
}
