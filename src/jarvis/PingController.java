package jarvis;

import httpServer.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PingController implements GetController {
    private final SimpleDateFormat dateFormatter;
    private final String filePath;

    public PingController(String pattern, String filePath) {
        dateFormatter = new SimpleDateFormat(pattern);
        this.filePath = filePath;
    }

    public HttpResponse get(HttpRequest request) {
        try {
            sleep();
            HttpResponse res = new HttpResponse(HttpStatusCode.OK, FileHelper.readFile(filePath).replace("{{timestamp}}", now()));
            res.headers.put("Content-Type", "text/html");
            return res;
        } catch(Exception ignored) {
            return new HttpResponse(HttpStatusCode.InternalServerError, "Failed to load resource");
        }
    }

    private String now() { return dateFormatter.format(new Date()); }

    private void sleep() {
        try { Thread.sleep(1000); }
        catch (Exception ignored) { }
    }
}
