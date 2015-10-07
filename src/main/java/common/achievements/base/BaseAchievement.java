package common.achievements.base;

import common.FileNamespace;
import common.achievements.Achievement;
import common.achievements.AchievementStatus;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
import utils.ImageUtils;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class BaseAchievement implements Achievement {

    protected boolean isInitialRun = false;
    protected boolean wasCompleted = false;
    protected Logger log = Logger.getLogger(getClass());
    private String tooltip = "This achievement is currently unavailable";
    private String imagePath = FileNamespace.DEFAULT_ACHIEVEMENT_IMAGE;

    @Override
    public AchievementStatus retest() {
        return null;
    }

    @Override
    public AchievementStatus getStatus() {
        return null;
    }

    private void notifySuccess() {
        printSuccessMessage();
        if (!isInitialRun) {
            Platform.runLater(() -> {
                Notifications.create().hideAfter(Duration.seconds(4.0)).title("Achievement!").graphic(new ImageView(ImageUtils.loadJavaFXImage(imagePath))).text("Achievement \"" + tooltip + "\" completed!").show();
            });
        }
    }

    private void notifyFail() {
        printFailMessage();
        if (!isInitialRun) {
            Platform.runLater(() -> {
                WritableImage writableImage = ImageUtils.copyImage(ImageUtils.loadJavaFXImage(imagePath), ImageUtils.ImageRenderType.GRAYSCALE);
                Notifications.create().hideAfter(Duration.seconds(4.0)).title("Achievement!").graphic(new ImageView(writableImage)).text("Achievement \"" + tooltip + "\" missed!").show();
            });
        }
    }

    private void printSuccessMessage() {
        log.info("Achievement " + getClass().getSimpleName() + " completed!");
    }

    private void printFailMessage() {
        log.info("Achievement " + getClass().getSimpleName() + " missed!");
    }

    protected boolean calcResult() {
        return false;
    }

    @Override
    public boolean isWasCompleted() {
        return wasCompleted;
    }

    @Override
    public boolean isCompleted() {
        boolean result = calcResult();
        if (!wasCompleted && result) {
            notifySuccess();
        } else if (wasCompleted && !result) {
            notifyFail();
        }
        wasCompleted = result;
        return result;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void setImagePath(String path) {
        this.imagePath = path;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }
}
