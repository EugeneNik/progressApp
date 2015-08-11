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
}
