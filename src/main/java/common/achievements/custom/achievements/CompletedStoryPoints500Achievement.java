package common.achievements.custom.achievements;

import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedStoryPoints500Achievement extends CompletedStoryPointsAchievement {
    public CompletedStoryPoints500Achievement(Task task) {
        super(task);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 500.0;
    }
}
