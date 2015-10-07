package utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * Created by DARIA on 19.04.2015.
 */
public class ImageUtils {

    public enum ImageRenderType {
        NORMAL,
        BRIGHTER,
        DARKER,
        SATURATE,
        DESATURATE,
        GRAYSCALE,
        INVERT
    }

    public static Image loadJavaFXImage(String file) {
        InputStream is = ImageUtils.class.getResourceAsStream(file);
        Image image = new Image(is);
        return image;
    }

    public static WritableImage copyImage(Image src, ImageRenderType op) {

        PixelReader pixelReader = src.getPixelReader();
        WritableImage dest
                = new WritableImage(
                (int) src.getWidth(),
                (int) src.getHeight());
        PixelWriter pixelWriter = dest.getPixelWriter();

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);

                if (op != null) {
                    switch (op) {
                        case NORMAL:
                            break;
                        case BRIGHTER:
                            color = color.brighter();
                            break;
                        case DARKER:
                            color = color.darker();
                            break;
                        case SATURATE:
                            color = color.saturate();
                            break;
                        case DESATURATE:
                            color = color.desaturate();
                            break;
                        case GRAYSCALE:
                            color = color.grayscale();
                            break;
                        case INVERT:
                            color = color.invert();
                            break;
                    }
                }

                pixelWriter.setColor(x, y, color);
            }
        }

        return dest;
    }
}
