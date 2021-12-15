package jarvis;

import httpServer.CommandArguments;
import httpServer.HttpRequestRouter;
import httpServer.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            runServer(new CommandArguments(args));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void runServer(CommandArguments args) throws IOException {
        GuessingGameRepository gameRepo = new GuessingGameRepository();
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController("/hello", new FileController("src/resources/hello.html"));
        router.addController("/ping", new PingController("HH:mm:ss", "src/resources/ping.html"));
        router.addController("/guess", new GuessController("src/resources/guess.html", gameRepo));
        router.addController("*", new DirectoryController(args.root, "src/resources/index.html"));
        Server server = new Server(args.port, router);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.listen();
    }
}
