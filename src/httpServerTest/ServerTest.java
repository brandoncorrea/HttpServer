package httpServerTest;

import httpServer.*;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {
    @Test
    public void newServer() {
        Server server = new Server(80, r -> HttpResponse.create(200));
        Assert.assertEquals(80, server.port);
        Assert.assertTrue(server.shutdown());
    }
}
