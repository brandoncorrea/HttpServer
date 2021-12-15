package jarvis;

import httpServer.CommandArguments;
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
        Server server = new Server(args.port);
        server.addController("/hello", new FileController("src/resources/hello.html"));
        server.addController("/ping", new PingController("HH:mm:ss", "src/resources/ping.html"));
        server.addController("/guess", new GuessController("src/resources/guess.html", gameRepo));
        server.addController("*", new DirectoryController(args.root, "src/resources/index.html"));
        server.listen();
    }
}
