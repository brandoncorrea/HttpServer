package httpServerTest;

import httpServer.CommandArguments;
import org.junit.Assert;
import org.junit.Test;

public class CommandArgumentsTest {

    @Test
    public void newCommandArgumentsWithDefaultValues() {
        String[][] testArgs = {
                {},
                {"-p", "80"},
                {"-r", System.getProperty("user.dir")},
                {"-r", System.getProperty("user.dir"), "-p", "80"},
                {"-p", "80", "-r", System.getProperty("user.dir")},
        };

        for (String[] args : testArgs)
            testNewCommandArguments(args, 80, System.getProperty("user.dir"));

        testArgs = new String[][] {
                {"-r", "Some/Directory/Path", "-p", "8000"},
                {"-p", "8000", "-r", "Some/Directory/Path"}
        };

        for (String[] args : testArgs)
            testNewCommandArguments(args, 8000, "Some/Directory/Path");
    }

    private void testNewCommandArguments(String[] commandLineArgs, int port, String root) {
        CommandArguments args = new CommandArguments(commandLineArgs);
        Assert.assertEquals(port, args.port);
        Assert.assertEquals(root, args.root);
    }

    @Test
    public void initializesWithNonDefaultPortNumber() {
        String[] ports = {"3000", "8080"};
        for (String port : ports) {
            CommandArguments args = new CommandArguments(new String[] {"-p", port});
            Assert.assertEquals(Integer.parseInt(port), args.port);
        }
    }

    @Test
    public void initializeWithNonDefaultRootDirectory() {
        String[] dirs = {"NewDirectory", "/Some/Other/Directory/Path"};
        for (String dir : dirs) {
            CommandArguments args = new CommandArguments(new String[] {"-r", dir});
            Assert.assertEquals(dir, args.root);
        }
    }

    @Test
    public void invalidArgumentsThrowExceptions() {
        testInvalidArguments(new String[] {"-p"}, "Port number not specified");
        testInvalidArguments(new String[] {"-r"}, "Root directory not specified");
        testInvalidArguments(new String[] {"chicken"}, "chicken is not an argument");
        testInvalidArguments(new String[] {"beef"}, "beef is not an argument");
        testInvalidArguments(new String[] {"chicken", "80"}, "chicken is not an argument");
        testInvalidArguments(new String[] {"-p", "abcdefg"}, "Invalid Port: abcdefg");
        testInvalidArguments(new String[] {"-p", "Hellos!"}, "Invalid Port: Hellos!");
        testInvalidArguments(new String[] {"-p", "80", "-r"}, "Root directory not specified");
        testInvalidArguments(new String[] {"-r", "/My/Dir", "-p"}, "Port number not specified");
        testInvalidArguments(new String[] {"-r", "/My/Dir", "chicken"}, "chicken is not an argument");
        testInvalidArguments(new String[] {"-r", "/My/Dir", "beef"}, "beef is not an argument");
        testInvalidArguments(new String[] {"-r", "/My/Dir", "-p", "8080", "fifth"}, "Too many arguments");
        testInvalidArguments(new String[] {"-r", "/My/Dir", "-p", "8080", "fifth", "sixth"}, "Too many arguments");
    }

    private void testInvalidArguments(String[] args, String message) {
        try {
            new CommandArguments(args);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals(message, ex.getMessage());
        }
    }
}
