package common.achievements.custom.achievements;

import common.achievements.AchievementStatus;
import common.achievements.base.SystemAchievement;
import common.achievements.custom.BooleanAchievementStatus;

/**
 * Created by Евгений on 20.08.2015.
 */
public class MinutesOnlineAchievement extends SystemAchievement {

    @Override
    public AchievementStatus retest() {
        return new BooleanAchievementStatus(isCompleted());
    }
}
