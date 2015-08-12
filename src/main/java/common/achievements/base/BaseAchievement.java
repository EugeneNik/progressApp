package common.achievements.base;

import common.FileNamespace;
import common.achievements.Achievement;
import common.achievements.AchievementStatus;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class BaseAchievement implements Achievement {

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

    protected void printSuccessMessage() {
        System.out.println("Achievement " + getClass().getSimpleName() + " completed!");
    }

    protected void printFailMessage() {
        System.out.println("Achievement " + getClass().getSimpleName() + " missed!");
    }

    protected boolean calcResult() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        boolean result = calcResult();
        if (!wasCompleted && result) {
            printSuccessMessage();
        } else if (wasCompleted && !result) {
            printFailMessage();
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
