package jarvis;

import httpServer.HttpResponse;
import httpServer.HttpStatusCode;

import java.io.*;

public class FileHelper {
    private FileHelper() { }

    public static HttpResponse fileResponse(String path) throws IOException {
        return fileResponse(HttpStatusCode.OK, path);
    }

    public static HttpResponse fileResponse(int statusCode, String path) throws IOException {
        File file = new File(path);
        byte[] data  = new byte [(int) file.length()];
        new FileInputStream(file).read(data);
        HttpResponse res = new HttpResponse(statusCode, data);
        res.headers.put("Content-Type", getContentType(path));
        res.headers.put("Content-Length", String.valueOf(data.length));
        return res;
    }

    public static String getContentType(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".gif"))
            return "image/gif";
        else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
            return "image/jpeg";
        else if (path.endsWith(".png"))
            return "image/png";
        else if (path.endsWith(".pdf"))
            return "application/pdf";
        else if (path.endsWith(".html"))
            return "text/html";
        return "text/plain";
    }

    public static String readFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = reader.read()) >= 0)
            builder.append((char)c);
        reader.close();
        return builder.toString();
    }
}
