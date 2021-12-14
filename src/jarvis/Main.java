package jarvis;

import httpServer.CommandArguments;
import httpServer.HttpMethod;
import httpServer.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            runServer(args);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void runServer(String[] args) throws IOException {
        Server server = new Server(new CommandArguments(args).port);
        server.addRoute("/hello", HttpMethod.GET, new FileHandler("src/resources/hello.html"));
        server.addRoute("/ping", HttpMethod.GET, new PingHandler("HH:mm:ss"));
        server.addRoute("/", HttpMethod.GET, new FileHandler("src/resources/index.html"));
        server.addRoute("*", HttpMethod.GET, new NotFoundHandler("src/resources/notFound.html"));
        server.listen();
    }
}
