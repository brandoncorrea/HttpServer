package jarvisTest;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.FileHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileHelperTest {

    private void testContentType(String expected, String filePath) {
        Assert.assertEquals(expected, FileHelper.getContentType(filePath));
    }

    @Test
    public void determinesFileContentType() {
        testContentType("text/plain", "unknown.abcdefg");
        testContentType("text/plain", "fake.doc.type.hahahaha");
        testContentType("text/plain", "textfile.txt");
        testContentType("text/plain", "file with spaces.txt");
        testContentType("text/html", "htmlFile.html");
        testContentType("text/html", "html with spaces.html");
        testContentType("text/html", "CAPITALIZED.HTML");
        testContentType("image/gif", "meme.gif");
        testContentType("image/gif", "anotherMeme.gif");
        testContentType("application/pdf", "document1.pdf");
        testContentType("application/pdf", "document2.pdf");
        testContentType("image/png", "someIcon.png");
        testContentType("image/png", "aTinyImage.PNG");
        testContentType("image/jpeg", "chocolateMountain.jpg");
        testContentType("image/jpeg", "anotherJpgImage.jpg");
        testContentType("image/jpeg", "imageWithTheE.jpeg");
    }

    @Test
    public void createsResponseForText() throws IOException {
        String[] paths = {
                "src/resources/public/starWars/episode1.txt",
                "src/resources/public/starWars/episode2.txt"
        };

        for (String path : paths) {
            HttpResponse res = FileHelper.fileResponse(HttpStatusCode.OK, path);
            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
            Assert.assertEquals("text/plain", res.headers.get("Content-Type"));
            Assert.assertEquals(String.valueOf(res.contentBytes.length), res.headers.get("Content-Length"));

            File file = new File(path);
            byte [] expected  = new byte [(int) file.length()];
            new BufferedInputStream(new FileInputStream(file)).read(expected);
            Assert.assertArrayEquals(expected, res.contentBytes);
        }

        HttpResponse res = FileHelper.fileResponse(HttpStatusCode.InternalServerError, "src/resources/public/memes/dwight.gif");
        Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
        Assert.assertEquals("image/gif", res.headers.get("Content-Type"));
    }
}
