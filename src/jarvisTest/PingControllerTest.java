package jarvisTest;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.PingController;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PingControllerTest {
    @Test
    public void newPingHandler() {
        String[] patterns = {"HH:mm:ss", "yyyy-MM-dd HH:mm:ss"};
        for (String pattern : patterns) {
            PingController handler = new PingController(pattern);

            long before = System.currentTimeMillis();
            HttpResponse res = handler.get(null);
            long after = System.currentTimeMillis();
            long diff = after - before;
            Assert.assertTrue(1000 < diff && diff < 1100);

            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

            String formattedDate = new SimpleDateFormat(pattern).format(new Date());
            String expected = "<h1>Ping</h1>\r\n<p>" + formattedDate + "</p>";
            Assert.assertEquals(expected, res.content);
            Assert.assertEquals("text/html", res.headers.get("Content-Type"));
        }
    }
}
