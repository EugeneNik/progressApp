package common.achievements;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class BooleanAchievementStatus implements AchievementStatus{
    private boolean value;

    public BooleanAchievementStatus(boolean value) {
        this.value = value;
    }
}
