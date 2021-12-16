package jarvis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private final Map<String, String> settings = new HashMap<>();

    public Configuration(String filePath) {
        try {
            parseConfig(filePath);
        } catch (Exception ignored) { }
    }

    private void parseConfig(String filePath) throws IOException {
        String content = FileHelper.readFile(filePath);
        String[] lines = content.split("\\r\\n|(?!\\r)\\n|\\r(?!\\n)");
        for (String line : lines)
            addSetting(line);
    }

    private void addSetting(String line) {
        String[] parts = line.split("=");
        if (parts.length == 0) return;
        String key = parts[0].trim();
        if (key.isEmpty() || key.startsWith("#")) return;
        if (parts.length == 1)
            settings.put(key, "");
        else
            settings.put(key, parts[1]);
    }

    public String getString(String name) { return settings.get(name); }

    public int getInt(String name) {
        try {
            return Integer.parseInt(settings.get(name));
        } catch(Exception exception) {
            return 0;
        }
    }

    public void set(String name, String value) { settings.put(name, value); }
}
