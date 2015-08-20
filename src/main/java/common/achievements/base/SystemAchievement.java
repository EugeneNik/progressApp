package common.achievements.base;

import common.achievements.AchievementStatus;
import common.achievements.custom.BooleanAchievementStatus;

/**
 * Created by nikiforov on 12.08.2015.
 */
public abstract class SystemAchievement extends BaseAchievement {

    public SystemAchievement() {
        this.isInitialRun = true;
        retest();
        this.isInitialRun = false;
    }

    @Override
    public AchievementStatus retest() {
        return new BooleanAchievementStatus(isCompleted());
    }
}
