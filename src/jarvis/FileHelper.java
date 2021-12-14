package jarvis;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileHelper {
    private FileHelper() { }

    public static HttpResponse fileResponse(String path) throws IOException {
        return fileResponse(HttpStatusCode.OK, path);
    }

    public static HttpResponse fileResponse(int statusCode, String path) throws IOException {
        HttpResponse res = new HttpResponse(statusCode, FileHelper.readFile(path));
        res.headers.put("Content-Type", "text/html");
        return res;
    }

    public static String readFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder builder = new StringBuilder();
        String s;
        while ((s = reader.readLine()) != null)
            builder.append(s);
        reader.close();
        return builder.toString();
    }
}
