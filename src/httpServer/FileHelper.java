package httpServer;

import java.io.*;

public class FileHelper {
    public static byte[] readFileBytes(String path) throws IOException {
        File file = new File(path);
        byte[] data  = new byte [(int) file.length()];
        new FileInputStream(file).read(data);
        return data;
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
        else if (path.endsWith(".mp3"))
            return "audio/mpeg";
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
