package jarvisTest;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.FileHelper;
import jarvis.PingController;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PingControllerTest {
    @Test
    public void newPingHandler() throws IOException {
        String[] patterns = {"HH:mm:ss", "yyyy-MM-dd HH:mm:ss"};
        String filePath = "src/resources/ping.html";
        String fileContent = FileHelper.readFile(filePath);
        for (String pattern : patterns) {
            PingController handler = new PingController(pattern, filePath);

            long before = System.currentTimeMillis();
            HttpResponse res = handler.get(null);
            long after = System.currentTimeMillis();
            long diff = after - before;
            Assert.assertTrue(1000 < diff && diff < 1100);

            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

            String formattedDate = new SimpleDateFormat(pattern).format(new Date());
            Assert.assertEquals(fileContent.replace("{{timestamp}}", formattedDate), res.content);
            Assert.assertEquals("text/html", res.headers.get("Content-Type"));
        }
    }

    @Test
    public void badFileReturnsServerError() {
        PingController handler = new PingController("HH:mm:ss", "nott/a/path.html");
        HttpResponse res = handler.get(null);
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
        Assert.assertEquals("Failed to load resource", res.content);
    }
}
