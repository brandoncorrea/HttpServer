package httpServerTest;

import httpServer.*;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {
    @Test
    public void newServer() {
        Server server = new Server(80, new HttpRequestRouter());
        Assert.assertEquals(80, server.port);
        Assert.assertTrue(server.shutdown());

        server = new Server(80, r -> new HttpResponse(200));
        Assert.assertEquals(80, server.port);
        Assert.assertTrue(server.shutdown());
    }
}
