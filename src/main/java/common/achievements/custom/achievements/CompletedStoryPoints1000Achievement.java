package common.achievements.custom.achievements;

import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedStoryPoints1000Achievement extends CompletedStoryPointsAchievement{
    public CompletedStoryPoints1000Achievement(Task task) {
        super(task);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 1000.0;
    }
}
