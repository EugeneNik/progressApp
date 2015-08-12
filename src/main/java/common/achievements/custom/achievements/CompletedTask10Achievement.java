package common.achievements.custom.achievements;

import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTask10Achievement extends CompletedTaskAchievement {

    public CompletedTask10Achievement(Task task) {
        super(task);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 10;
    }
}
