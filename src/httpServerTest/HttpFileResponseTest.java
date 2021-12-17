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

public class HttpFileResponseTest {

    @Test
    public void createsResponseForText() throws IOException {
        String[] paths = {
                "src/resources/public/starWars/episode1.txt",
                "src/resources/public/starWars/episode2.txt"
        };

        for (String path : paths) {
            HttpResponse res = new HttpFileResponse(HttpStatusCode.OK, path);
            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
            Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
            Assert.assertEquals(String.valueOf(res.contentBytes.length), res.headers.get("Content-Length"));

            File file = new File(path);
            byte [] expected  = new byte [(int) file.length()];
            new BufferedInputStream(new FileInputStream(file)).read(expected);
            Assert.assertArrayEquals(expected, res.contentBytes);
        }

        HttpResponse res = new HttpFileResponse(HttpStatusCode.InternalServerError, "src/resources/public/memes/dwight.gif");
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
        Assert.assertEquals("image/gif", res.headers.get("Content-Type"));
    }
}
