package jarvisTest;

import httpServer.FileHelper;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.Configuration;
import jarvis.PingController;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PingControllerTest {

    private void testPingController(String fileContent, String pattern, PingController controller) {
        long before = System.currentTimeMillis();
        Map<String, Object> res = controller.get(null);
        long after = System.currentTimeMillis();
        long diff = after - before;
        Assert.assertTrue(1000 < diff && diff < 1100);

        Assert.assertEquals(HttpStatusCode.OK, res.get("status"));

        String formattedDate = new SimpleDateFormat(pattern).format(new Date());
        Assert.assertArrayEquals(fileContent.replace("{{timestamp}}", formattedDate).getBytes(), HttpResponse.body(res));
        Assert.assertEquals("text/html", HttpResponse.headers(res).get("Content-Type"));
    }

    @Test
    public void newPingHandler() throws IOException {
        String[] patterns = {"HH:mm:ss", "yyyy-MM-dd HH:mm:ss"};
        String filePath = "src/resources/ping.html";
        String fileContent = FileHelper.readFile(filePath);
        for (String pattern : patterns) {
            testPingController(fileContent, pattern, new PingController(pattern, filePath));
            Configuration config = new Configuration();
            config.set("PingTimeFormat", pattern);
            config.set("PingSleepMS", "1000");
            config.set("PingPage", "src/resources/ping.html");
            testPingController(fileContent, pattern, new PingController(config));
        }
    }

    @Test
    public void badFileReturnsServerError() {
        PingController handler = new PingController("HH:mm:ss", "nott/a/path.html");
        Map<String, Object> res = handler.get(null);
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.get("status"));
        Assert.assertArrayEquals("Failed to load resource".getBytes(), HttpResponse.body(res));

        Configuration config = new Configuration();
        config.set("PingTimeFormat", "HH:mm:ss");
        config.set("PingSleepMS", "1000");
        config.set("PingPage", "nott/a/path.html");
        res = new PingController(config).get(null);
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.get("status"));
        Assert.assertArrayEquals("Failed to load resource".getBytes(), HttpResponse.body(res));
    }
}
