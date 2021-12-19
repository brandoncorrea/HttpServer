package jarvis;

import httpServer.HttpMethod;
import httpServer.HttpRequestRouter;
import httpServer.Server;

import java.io.IOException;
import java.util.TimeZone;

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
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        GuessingGameRepository gameRepo = new GuessingGameRepository(config);
        HttpRequestRouter router = new HttpRequestRouter();
        router.addController(config.getString("HelloEndpoint"), HttpMethod.GET, r -> FileController.get(config.getString("HelloPage")));
        router.addController(config.getString("PingEndpoint"), HttpMethod.GET, r -> new PingController(config).get(r));
        router.addController(config.getString("GuessEndpoint"), HttpMethod.GET, r -> new GuessController(config, gameRepo).get(r));
        router.addController(config.getString("GuessEndpoint"), HttpMethod.POST, r -> new GuessController(config, gameRepo).post(r));
        router.addController("*", HttpMethod.GET, r -> new DirectoryController(config).get(r));
        Server server = new Server(config.getInt("DefaultPort"), router);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        System.out.println("Starting Server on localhost:" + server.port);
        System.out.println("Root Directory: " + config.getString("DefaultRootDirectory"));
        server.listen();
    }
}
