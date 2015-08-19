package common.achievements;

/**
 * Created by nikiforov on 11.08.2015.
 */
public interface Achievement {
    boolean isCompleted();

    boolean isWasCompleted();

    String getTooltip();

    void setTooltip(String tooltip);

    void setImagePath(String path);

    String getImagePath();

    AchievementStatus retest();

    AchievementStatus getStatus();
}
