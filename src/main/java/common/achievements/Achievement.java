package common.achievements;

/**
 * Created by nikiforov on 11.08.2015.
 */
public interface Achievement {
    boolean isCompleted();

    AchievementStatus retest();

    AchievementStatus getStatus();
}