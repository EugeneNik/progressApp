package common.achievements.base;

import common.FileNamespace;
import common.achievements.Achievement;
import common.achievements.AchievementStatus;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import utils.ImageUtils;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class BaseAchievement implements Achievement {

    protected boolean isInitialRun = false;
    protected boolean wasCompleted = false;
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
            Notifications.create().hideAfter(Duration.seconds(4.0)).title("Achievement!").graphic(new ImageView(ImageUtils.loadJavaFXImage(imagePath))).text("Achievement \"" + tooltip + "\" completed!").show();
        }
    }

    private void notifyFail() {
        printFailMessage();
        if (!isInitialRun) {
            WritableImage writableImage = ImageUtils.copyImage(ImageUtils.loadJavaFXImage(imagePath), ImageUtils.ImageRenderType.GRAYSCALE);
            Notifications.create().hideAfter(Duration.seconds(4.0)).title("Achievement!").graphic(new ImageView(writableImage)).text("Achievement \"" + tooltip + "\" missed!").show();
        }
    }

    private void printSuccessMessage() {
        System.out.println("Achievement " + getClass().getSimpleName() + " completed!");
    }

    private void printFailMessage() {
        System.out.println("Achievement " + getClass().getSimpleName() + " missed!");
    }

    protected boolean calcResult() {
        return false;
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
