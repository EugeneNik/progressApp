package common.achievements;

import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTask20Achievement extends CompletedTaskAchievement {

    public CompletedTask20Achievement(Task task) {
        super(task);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 20;
    }
}
