package jarvis;

import httpServer.HttpRequestRouter;
import httpServer.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            runServer(readConfiguration(new CommandArguments(args)));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static Configuration readConfiguration(CommandArguments args) {
        Configuration config = new Configuration("configuration.properties");
        if (args.port > 0)
            config.set("DefaultPort", String.valueOf(args.port));
        if (args.root != null)
            config.set("DefaultRootDirectory", args.root);
        if (config.getInt("DefaultPort") == 0)
            config.set("DefaultPort", "80");
        if (config.getString("DefaultRootDirectory") == null)
            config.set("DefaultRootDirectory", System.getProperty("user.dir"));
        return config;
    }

    private static void runServer(Configuration config) throws IOException {
        GuessingGameRepository gameRepo = new GuessingGameRepository(config);
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController(config.getString("HelloEndpoint"), new FileController(config.getString("HelloPage")));
        router.addController(config.getString("PingEndpoint"), new PingController(config));
        router.addController(config.getString("GuessEndpoint"), new GuessController(config, gameRepo));
        router.addController("*", new DirectoryController(config));
        Server server = new Server(config.getInt("DefaultPort"), router);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        System.out.println("Starting Server on localhost:" + server.port);
        System.out.println("Root Directory: " + config.getString("DefaultRootDirectory"));
        server.listen();
    }
}
