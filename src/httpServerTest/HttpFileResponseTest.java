package httpServerTest;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import httpServer.HttpFileResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class HttpFileResponseTest {

    @Test
    public void createsResponseForText() throws IOException {
        String[] paths = {
                "src/resources/public/starWars/episode1.txt",
                "src/resources/public/starWars/episode2.txt"
        };

        for (String path : paths) {
            Map<String, Object> res = HttpFileResponse.create(HttpStatusCode.OK, path);
            Assert.assertEquals(HttpStatusCode.OK, res.get("status"));
            Assert.assertEquals("text/plain", HttpResponse.headers(res).get("Content-Type"));
            Assert.assertEquals(String.valueOf(HttpResponse.body(res).length), HttpResponse.headers(res).get("Content-Length"));

            File file = new File(path);
            byte [] expected  = new byte [(int) file.length()];
            new BufferedInputStream(new FileInputStream(file)).read(expected);
            Assert.assertArrayEquals(expected, HttpResponse.body(res));
        }

        Map<String, Object> res = HttpFileResponse.create(HttpStatusCode.InternalServerError, "src/resources/public/memes/dwight.gif");
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.get("status"));
        Assert.assertEquals("image/gif", HttpResponse.headers(res).get("Content-Type"));
    }
}
