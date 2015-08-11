package utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by nikiforov on 11.08.2015.
 */
public class FileUtils {

    public static boolean createIfNotExist(String name, boolean isDirectory) {
        try {
            File file = new File(name);
            if (!file.exists()) {
                if (isDirectory) {
                    file.mkdir();
                } else {
                    file.createNewFile();
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void createDirectoryChain(String fileName) {
        String [] fileNameDirectories = fileName.split("/");
        StringBuilder pathBuilder = new StringBuilder("");
        for (String file : fileNameDirectories) {
            pathBuilder.append(file);
            createIfNotExist(pathBuilder.toString(), !pathBuilder.toString().contains("."));
            pathBuilder.append("/");
        }
    }
}
