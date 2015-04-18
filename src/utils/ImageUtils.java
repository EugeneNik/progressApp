package utils;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by DARIA on 19.04.2015.
 */
public class ImageUtils {

    public static Image loadJavaFXImage(String file) {
        Image image = null;
        try {
            InputStream is = new FileInputStream(file);
            image = new Image(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //createException(Exceptions.UnsupportedSkinException, getLanguage(), JavaFXUtils.class, e);
        }
        return image;
    }
}
