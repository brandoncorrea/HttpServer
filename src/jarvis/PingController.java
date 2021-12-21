package jarvis;

import httpServer.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PingController {
    private final SimpleDateFormat dateFormatter;
    private final String filePath;
    private final int pingSleepMs;

    public PingController(String pattern, String filePath) {
        dateFormatter = new SimpleDateFormat(pattern);
        this.filePath = filePath;
        pingSleepMs = 1000;
    }

    public PingController(Configuration config) {
        dateFormatter = new SimpleDateFormat(config.getString("PingTimeFormat"));
        filePath = config.getString("PingPage");
        pingSleepMs = config.getInt("PingSleepMS");
    }

    public Map<String, Object> get(HttpRequest request) {
        try {
            sleep();
            Map<String, Object> res = HttpResponse.create(HttpStatusCode.OK, FileHelper.readFile(filePath).replace("{{timestamp}}", now()));
            HttpResponse.headers(res).put("Content-Type", "text/html");
            return res;
        } catch(Exception ignored) {
            return HttpResponse.create(HttpStatusCode.InternalServerError, "Failed to load resource");
        }
    }

    private String now() { return dateFormatter.format(new Date()); }

    private void sleep() {
        try { Thread.sleep(pingSleepMs); }
        catch (Exception ignored) { }
    }
}
