package jarvis;

import httpServer.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PingController implements GetController {
    private final SimpleDateFormat dateFormatter;

    public PingController(String pattern) {
        dateFormatter = new SimpleDateFormat(pattern);
    }

    public HttpResponse get(HttpRequest request) {
        sleep();
        HttpResponse res = new HttpResponse(HttpStatusCode.OK, "<h1>Ping</h1>\r\n<p>" + now() + "</p>");
        res.headers.put("Content-Type", "text/html");
        return res;
    }

    private String now() { return dateFormatter.format(new Date()); }

    private void sleep() {
        try { Thread.sleep(1000); }
        catch (Exception ignored) { }
    }
}
