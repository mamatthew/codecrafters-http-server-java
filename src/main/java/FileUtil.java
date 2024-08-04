import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

    private static String directoryPath = "./";

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

    public static void setDirectoryPath(String path) {
        directoryPath = path;
    }
}