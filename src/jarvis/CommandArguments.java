package jarvis;

import java.util.Objects;

public class CommandArguments {
    public final int port;
    public final String root;

    public CommandArguments(String[] args) {
        validateArguments(args);
        port = getPort(args);
        root = getRootDirectory(args);
    }

    private String getRootDirectory(String[] args) {
        for (int i = 0; i < args.length - 1; i += 2)
            if (Objects.equals(args[i], "-r"))
                return args[i + 1];
        return null;
    }

    private int getPort(String[] args) {
        for (int i = 0; i < args.length - 1; i += 2)
            if (Objects.equals(args[i], "-p"))
                return parsePort(args[i + 1]);
        return 0;
    }

    private void validateArguments(String[] args) {
        if (args.length > 4)
            throw new IllegalArgumentException("Too many arguments");
        for (int i = 0; i < args.length; i += 2)
            validatePairAtIndex(args, i);
    }

    private void validatePairAtIndex(String[] args, int index) {
        if (args.length > index &&
                !Objects.equals(args[index], "-p") &&
                !Objects.equals(args[index], "-r"))
            throw new IllegalArgumentException(args[index] + " is not an argument");
        if (args.length != index + 1) return;
        if (Objects.equals(args[index], "-p"))
            throw new IllegalArgumentException("Port number not specified");
        if (Objects.equals(args[index], "-r"))
            throw new IllegalArgumentException("Root directory not specified");
    }

    private int parsePort(String port) {
        try {
            int parsedPort = Integer.parseInt(port);
            if (parsedPort <= 0) throw new IllegalArgumentException();
            return parsedPort;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid Port: " + port);
        }
    }
}
