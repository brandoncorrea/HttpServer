package jarvis;

import httpServer.ApiHandler;
import httpServer.HttpRequest;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PingHandler implements ApiHandler {
    private final SimpleDateFormat dateFormatter;

    public PingHandler(String pattern) {
        dateFormatter = new SimpleDateFormat(pattern);
    }

    public HttpResponse respond(HttpRequest request) {
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
