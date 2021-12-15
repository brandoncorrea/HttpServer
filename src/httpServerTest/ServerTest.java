package httpServerTest;

import httpServer.*;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {
    @Test
    public void newServer() {
        Server server = new Server(80, "root/directory", new HttpRequestRouter());
        Assert.assertEquals(80, server.port);
        Assert.assertEquals("root/directory", server.root);
        Assert.assertTrue(server.shutdown());
    }
}
