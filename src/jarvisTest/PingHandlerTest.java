package jarvisTest;

import httpServer.ApiHandler;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.PingHandler;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PingHandlerTest {
    @Test
    public void newPingHandler() {
        String[] patterns = {"HH:mm:ss", "yyyy-MM-dd HH:mm:ss"};
        for (String pattern : patterns) {
            ApiHandler handler = new PingHandler(pattern);

            long before = System.currentTimeMillis();
            HttpResponse res = handler.respond(null);
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
