package httpServerTest;

import httpServer.HttpRequestRouter;
import httpServer.SocketManager;
import org.junit.Test;

public class SocketManagerTest {
    @Test
    public void newSocketManager() {
        new SocketManager(new HttpRequestRouter());
    }
}
