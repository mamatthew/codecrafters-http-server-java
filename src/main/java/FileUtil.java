import java.io.*;
import java.util.zip.GZIPOutputStream;

public class FileUtil {

    public static String directoryPath = "./";

    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(directoryPath + filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            content.deleteCharAt(content.length() - 1);
        } catch (IOException e) {
            throw new IOException("File not found");
        }
        return content.toString();
    }

    // add method to write content to a file given a path and content
    public static void writeFile(String filePath, String content) {
        // create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // write code to write content to a file
        File file = new File(directoryPath + filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public static void writeFile(String filePath, char[] content) {
        // create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // write code to write content to a file
        File file = new File(directoryPath + filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }
    }

    public static void setDirectoryPath(String path) {
        directoryPath = path;
    }

    public static byte[] compress(String echoPath) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = null;
        try {
            gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(echoPath.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipOutputStream != null) {
                try {
                    gzipOutputStream.close();  // This ensures that all data is properly flushed and the stream is closed.
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }
        return hex.toString();
    }

}