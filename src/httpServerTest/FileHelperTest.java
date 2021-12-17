package httpServerTest;

import httpServer.FileHelper;
import org.junit.Assert;
import org.junit.Test;

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
}
