package jarvis;

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
        Configuration config = new Configuration("configuration.properties");
        GuessingGameRepository gameRepo = new GuessingGameRepository(config);
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController(config.getString("HelloEndpoint"), new FileController(config.getString("HelloPage")));
        router.addController(config.getString("PingEndpoint"), new PingController(config));
        router.addController(config.getString("GuessEndpoint"), new GuessController(config, gameRepo));
        router.addController("*", new DirectoryController(args.root, "src/resources/index.html", "src/resources/notFound.html"));
        Server server = new Server(args.port, router);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        System.out.println("Starting Server on localhost:" + args.port);
        System.out.println("Root Directory: " + args.root);
        server.listen();
    }
}
