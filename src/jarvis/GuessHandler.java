package jarvis;

import httpServer.ApiHandler;
import httpServer.HttpRequest;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class GuessHandler implements ApiHandler {
    private final String filePath;
    public GuessHandler(String filePath) { this.filePath = filePath; }

    public HttpResponse respond(HttpRequest request) {
        try {
            HttpResponse res = FileHelper.fileResponse(filePath);
            if (requiresCookie(request))
                res.headers.put("Set-Cookie", "session_id=" + UUID.randomUUID());
            return res;
        } catch (IOException ignored) {
            return new HttpResponse(HttpStatusCode.InternalServerError, "Failed to load resource");
        }
    }

    private boolean requiresCookie(HttpRequest request) {
        String cookieHeader = request.headers.get("Cookie");
        if (cookieHeader == null)
            return true;
        for (String cookie : cookieHeader.split(";"))
            if (Objects.equals(cookie.substring(0, 11), "session_id="))
                return false;
        return true;
    }
}
