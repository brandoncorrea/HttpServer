package jarvis;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;

public class FileHelper {
    private FileHelper() { }

    public static HttpResponse fileResponse(String path) throws IOException {
        return fileResponse(HttpStatusCode.OK, path);
    }

    public static HttpResponse fileResponse(int statusCode, String path) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(path));
        HttpResponse res = new HttpResponse(statusCode, FileHelper.readFile(path));
        String contentType = URLConnection.guessContentTypeFromStream(in);
        if (contentType == null)
            contentType = "text/plain";
        res.headers.put("Content-Type", contentType);
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
