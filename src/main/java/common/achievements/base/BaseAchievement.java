package common.achievements.base;

import common.achievements.Achievement;
import common.achievements.AchievementStatus;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class BaseAchievement implements Achievement {

    protected boolean wasCompleted = false;

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
        return result;
    }
}
